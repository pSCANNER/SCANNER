/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.modules.worker.processors.glore; 

import Jama.Matrix;
import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ConfigUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.glore.utils.GloreUtils;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
import edu.isi.misd.scanner.network.types.glore.GloreData;
import edu.isi.misd.scanner.network.types.glore.GloreLogisticRegressionRequest;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class does the work of the "Client" in the GLORE network model.  
 * It calculates a portion of the logistic regression and sends the results
 * back to the "Server" (Master) node, where those results are combined with 
 * the results from other nodes.  The master node then sends back a new set of
 * refined variables and the process begins again, repeating until the epsilon
 * convergence (or maximum number of iterations) is reached.
 * 
 * @author Xiaoqian Jiang 
 * @author Mike D'Arcy 
 */
public class GloreProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(GloreProcessor.class); 
        
    public static Map<String, Object> dataStore = new HashMap<String, Object>();

    /**
     * Camel {@link org.apache.camel.Processor} implementation,
     * invokes {@link GloreProcessor#executeAnalysis(org.apache.camel.Exchange)}.
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {    
        exchange.getIn().setBody(this.executeAnalysis(exchange));        
    }
    /**
     * Performs the statistical analysis (logistic regression) using GLORE.
     * 
     * @param exchange The current exchange.
     * @return The formatted response.
     */
    protected ServiceResponse executeAnalysis(Exchange exchange) 
    {
        // Create the service response object
        ServiceResponse response = 
            new ServiceResponse();           
        
        try
        {            
            GloreLogisticRegressionRequest request = 
                (GloreLogisticRegressionRequest)exchange.getIn().getBody(
                    GloreLogisticRegressionRequest.class);

            GloreData data = request.getGloreData();
            if (data == null) {
                data = new GloreData();
                request.setGloreData(data);
            }
        
            // Create the service response metadata object
            ServiceResponseMetadata responseMetadata = 
                MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.PROCESSING,
                    "The GLORE computational request is in process."); 
            
            GloreStateData state = getState(exchange);        
            synchronized(this)
            {
                if (!state.dataLoaded) 
                {
                    readDataFile(exchange);
                    state.beta = new Matrix(state.columns, 1, 0.0);
                    state.cov_matrix = new Matrix(state.columns, state.columns);

                    // convert data into arrays to be passed to Matrix's constructor
                    state.Xa = GloreUtils.two_dim_list_to_arr(state.Xv);
                    state.Ya = GloreUtils.one_dim_list_to_arr(state.Yv);

                    // create X and Y matrices
                    state.X = new Matrix(state.Xa);
                    state.Y = new Matrix(state.Ya, state.Ya.length);  

                    data.setColumns(state.columns);
                    data.setRows(state.rows);

                    state.dataLoaded = true;                  
                }
            }

            if ("computeCovarianceMatrix".equalsIgnoreCase(data.getState()))
            {
                if (log.isDebugEnabled()) {
                    log.debug("Compute covariance matrix");
                }
                state.beta = 
                    GloreUtils.convertMatrixTypeToMatrix(data.getBeta());            
                state.hat_beta = state.beta.copy();

                state.P = (state.X.times(-1)).times(state.hat_beta);
                GloreUtils.exp(state.P.getArray());
                GloreUtils.add_one(state.P.getArray());
                GloreUtils.div_one(state.P.getArray());

                state.W = state.P.copy();
                state.W.timesEquals(-1.0);
                GloreUtils.add_one(state.W.getArray());
                state.W.arrayTimesEquals(state.P);
                state.W = state.W.transpose();
                state.W = GloreUtils.diag(state.W.getArray()[0]);

                state.D = ((state.X.transpose()).times(state.W)).times(state.X); 
                data.setD(GloreUtils.convertMatrixToMatrixType(state.D));
                responseMetadata.setRequestStateDetail(
                    "The GLORE computational request is in process." + 
                    "  Current state: " + data.getState());
            } 
            else if ("computeSDMatrix".equalsIgnoreCase(data.getState()))
            {
                state.cov_matrix = 
                    GloreUtils.convertMatrixTypeToMatrix(
                        data.getCovarianceMatrix());
                if (log.isDebugEnabled()) {                
                    log.debug("Covariance matrix: " +
                        GloreUtils.matrixToString(state.cov_matrix, 8, 6));
                }

                if (log.isDebugEnabled()) {                
                    log.debug("Compute SD matrix");            
                }
                state.SD = new Matrix(1, state.columns);
                for (int i = 0; i < state.columns; i++) {
                    state.SD.set(0, i, Math.sqrt(state.cov_matrix.get(i,i)));
                }

                // compute the M and STD since SD matrix is only computed 
                // when model converges, and therefore, only once
                calMandSTD(state);
                data.getM().addAll(
                    Arrays.asList(ArrayUtils.toObject(state.Ma)));
                if (log.isDebugEnabled()) {                
                    log.debug("M: " + data.getM());              
                }
                data.getSTD().addAll(
                    Arrays.asList(ArrayUtils.toObject(state.STDa)));
                if (log.isDebugEnabled()) {                
                    log.debug("STD: " + data.getSTD()); 
                }
                if (log.isDebugEnabled()) {                
                    log.debug("SD matrix:" + 
                        GloreUtils.matrixToString(state.SD, 8, 6));            
                }
                data.setSDMatrix(GloreUtils.convertMatrixToMatrixType(state.SD));
                removeState(exchange);
                
                responseMetadata.setRequestState(
                    ServiceRequestStateType.COMPLETE);
                responseMetadata.setRequestStateDetail(
                    "GLORE computation completed successfully at iteration: " + 
                    data.getIteration());
            }                       
            else 
            {
                if (log.isDebugEnabled()) {                
                    log.debug("Compute beta value");
                }
                if (data.getBeta() != null) {
                    state.beta = 
                        GloreUtils.convertMatrixTypeToMatrix(data.getBeta());
                }
                // P <- 1 + exp(-x%*%beta1)
                state.P = (state.X.times(-1)).times(state.beta);
                GloreUtils.exp(state.P.getArray());
                GloreUtils.add_one(state.P.getArray());
                GloreUtils.div_one(state.P.getArray());

                // w = diag(c(p*(1-p)))
                state.W = state.P.copy();
                state.W.timesEquals(-1.0);
                GloreUtils.add_one(state.W.getArray());
                state.W.arrayTimesEquals(state.P);
                state.W = state.W.transpose();
                state.W = GloreUtils.diag(state.W.getArray()[0]);

                // d <- t(x)%*%w%*%x
                state.D = ((state.X.transpose()).times(state.W)).times(state.X);
                // e <- t(x)%*%(y-p)
                state.E = 
                    (state.X.transpose()).times(state.Y.plus(state.P.uminus()));

                // D.print(10,3);
                // E.print(10,3);

                data.setD(GloreUtils.convertMatrixToMatrixType(state.D));
                data.setE(GloreUtils.convertMatrixToMatrixType(state.E));

                // print beta for this iteration
                if (log.isDebugEnabled()) {                      
                    log.debug("Beta: " + GloreUtils.matrixToString(state.beta, 8, 8));     
                }
                responseMetadata.setRequestStateDetail(
                    "The GLORE computational request is in process." + 
                    "  Current state: " + data.getState() + 
                    " " + data.getIteration());                   
            }

            
            // Create the service response data object
            ServiceResponseData responseData = 
                new ServiceResponseData();  
            responseData.setAny(request);
            // Initialize the service response with response data and response metadata
            response.setServiceResponseData(responseData);
            response.setServiceResponseMetadata(responseMetadata);
        }
        catch (Exception e) 
        {    
            response.setServiceResponseMetadata(
                MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.ERROR,
                    "Unhandled exception during GLORE processing. Caused by [" + 
                    e.toString() + "]"));    
            removeState(exchange); 
            log.warn("Unhandled exception during GLORE processing: ", e);
        }
        return response;         
    }

    /**
     *  Reads the specified input file stored as the value of the 
     * {@link BaseConstants#DATASOURCE} header and stores it in a 
     * {@link HashMap} containing the {@link GloreStateData}.  This is done on 
     * a per-transaction basis, so each incoming transaction will have its own
     * runtime copy of {@link GloreStateData}.
     */
    protected void readDataFile(Exchange exchange) throws Exception
    {
        FileInputStream file_stream;
        DataInputStream file_in;
        BufferedReader file_br;
        String file_line;
        String[] line_tokens;   

        GloreStateData state = getState(exchange);
        
        // locate the specified input file
        String fileName = 
            (String)exchange.getIn().getHeader(BaseConstants.DATASOURCE);
        if (fileName == null) {
            FileNotFoundException fnf = 
                new FileNotFoundException("A null file name was specified");
            throw fnf;
        }        
        String baseInputDir = 
            ConfigUtils.getBaseInputDir(
                exchange, BaseConstants.WORKER_INPUT_DIR_PROPERTY);  
        
        // access the file
        File file = new File(baseInputDir,fileName);        
        file_stream = new FileInputStream(file);
        file_in = new DataInputStream(file_stream);
        file_br = new BufferedReader(new InputStreamReader(file_in));


        // Obtain the independent and dependent variables
        GloreLogisticRegressionRequest request =
                (GloreLogisticRegressionRequest)exchange.getIn().getBody(
                        GloreLogisticRegressionRequest.class);

        ArrayList<Integer> independentVariableColumnsSelected = 
            new ArrayList<>();
        ArrayList<String> independentVariableNames = new ArrayList<>(
            request.getLogisticRegressionInput().
                getInputParameters().getIndependentVariableName());

        String dependentVariableName = 
            request.getLogisticRegressionInput().
                getInputParameters().getDependentVariableName();
        if (log.isDebugEnabled()) {
            log.debug("--##-- DependentVariableName="+dependentVariableName);
        }

        try
        {
            // read file and populate X and Y matrices
            state.rows = 0;
            int targetVar = -1;
            // parse the first line to match with the arrary list of dependent and independent variables
            if ((file_line = file_br.readLine()) != null){
                line_tokens = file_line.split(",");
                for (int i = 0; i < line_tokens.length; i++) {
                   // if dependent variable is matched
                    if (log.isDebugEnabled()) {
                        log.debug("--**-- headerVariable[" + i+"]="+line_tokens[i]);
                    }
                    if (line_tokens[i].equals(dependentVariableName))
                    {
                          targetVar = i;
                          continue;
                    }
                    else
                    {
                        if (independentVariableNames.contains(line_tokens[i])) {
                            independentVariableColumnsSelected.add(i);
                        }
                    }
                }
            }
            else{
                log.debug("Warning: no line is read, empty file...");
            }

            while ((file_line = file_br.readLine()) != null) 
            {
                // update number of rows
                state.rows = state.rows + 1;
                line_tokens = file_line.split(",");

                // detect number of columns in data file
                if (state.columns == -1) {
                    state.columns = line_tokens.length;
                }
                // line in data file does not match dimensions
                else if (state.columns != line_tokens.length) {
                    throw new RuntimeException(
                        "ERROR: data file dimensions don't " +
                        "match on line " + state.rows + ".");
                }

                // populate data structures with data
                state.xrow = new ArrayList<Double>();
                state.xrow.add(1.0);
                for (int i = 0; i < line_tokens.length ; i++) {
                    if (independentVariableColumnsSelected.contains(i)) {
                        state.xrow.add(new Double(line_tokens[i]));
                    }
                }
                state.Xv.add(state.xrow);
                state.Yv.add(new Double(line_tokens[targetVar]));
            }

            // update state.columns to match the total number of columns being
            // analyzed, and not the total number of columns present in the file...
            // the total number of columns is the number of independent variables 
            // specified, plus one for the dependent variable
            state.columns = independentVariableNames.size() + 1; 
        }
        finally
        {
            // close input stream
            file_br.close();
        }
        log.info("Successfully loaded file: " + file.getAbsolutePath());
    }
    
    /**
     * Gets the state from the internal data store {@link HashMap}. 
     * If the state does not exist, a new {@link GloreStateData} object is created.
     */
    protected static GloreStateData getState(Exchange exchange)
    {
        String key = 
            exchange.getFromEndpoint().getEndpointKey() + "/id/" +
            (String)exchange.getIn().getHeader(BaseConstants.ID);
        GloreStateData state = (GloreStateData)dataStore.get(key);
        if (state == null) {
            state = new GloreStateData();
            dataStore.put(key, state);
        }
        return state;
    }
    
    /**
     * Remove the state from the internal data store.  This is done at the end 
     * of processing or on a processing error.
     */
    protected static GloreStateData removeState(Exchange exchange)
    {
        String key = 
            exchange.getFromEndpoint().getEndpointKey() + "/id/" +
            (String)exchange.getIn().getHeader(BaseConstants.ID);
            return (GloreStateData)dataStore.remove(key);
    }


    /* Return a one dimensional array that is the sum of the E.length
        one dimensional vectors. */
    private static double[] row_sums(double[][] E, int m, int n) {

        int i, j;
        double sums[] = new double[m];
        // init sums
        for (i = 0; i < m; i++) {
            sums[i] = 0.0;
        }
        // Add each elements in a column to sums
        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                sums[j] = sums[j] + E[i][j];
            }
        }
        return sums;
    }


    /* Return a one dimensional array that is the sum of square difference
        one dimensional vectors. */
    private static double[] row_squared_sum(double[][] E, double[] M, int m, int n) {

        int i, j;
        double sums[] = new double[m];
        // init sums
        for (i = 0; i < m; i++) {
            sums[i] = 0.0;
        }
        // Add each elements in a column to sums
        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                sums[j] = sums[j]+ Math.pow( E[i][j],2);
            }
        }
        return sums;
    }


    /**
     * This function calculate the mean and std of attributes.
     */

    protected static void calMandSTD(GloreStateData state)
    {
        // get the M vector
        state.Ma = row_sums(state.Xa, state.columns, state.rows);

        // get the STD vector
        state.STDa = row_squared_sum(state.Xa, state.Ma, state.columns, state.rows);

    }
    
    /**
     * This class contains all of the runtime variables used during the GLORE
     * logistic regression analysis.
     */
    protected static class GloreStateData
    {
        // data structures used to hold the client data read in from files
        public ArrayList<List<Double> > Xv = new ArrayList<List<Double> >();
        public ArrayList<Double> Yv = new ArrayList<Double>();
        public ArrayList<Double> xrow;

        // data structures used to hold client data for matrix constructors
        public double[][] Xa;
        public double[] Ya;

        public Matrix beta;
        public Matrix hat_beta;
        public Matrix cov_matrix, SD;
        public Matrix X, Y;
        public Matrix P, W, D, E;    

        // number of columns and rows in data file
        public int columns = -1;
        public int rows;
        // is the data loaded?
        public boolean dataLoaded = false;

        // attribute mean, attribute variance
        public double[] Ma;
        public double[] STDa;


    }
}
