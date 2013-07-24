package edu.isi.misd.scanner.network.modules.worker.processors.glore;

import Jama.Matrix;
import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ConfigUtils;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.glore.utils.GloreUtils;
import edu.isi.misd.scanner.network.types.glore.GloreData;
import edu.isi.misd.scanner.network.types.glore.GloreLogisticRegressionRequest;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.isi.misd.scanner.network.types.regression.LogisticRegressionInputParameters;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
        try {    
            exchange.getIn().setBody(this.executeAnalysis(exchange)); 
        }
        catch (Exception e) {
            RuntimeException rtex = 
                new RuntimeException(
                    "Unhandled exception during GLORE processing. Caused by [" + 
                    e.toString() + "]");
            ErrorUtils.setHttpError(exchange, rtex, 500);
            removeState(exchange);
        }                
    }
    /**
     * Performs the statistical analysis (logistic regression) using GLORE.
     * 
     * @param exchange The current exchange.
     * @return The formatted response.
     * @throws Exception 
     */
    protected GloreLogisticRegressionRequest executeAnalysis(Exchange exchange) 
        throws Exception
    {
        GloreLogisticRegressionRequest request = 
            (GloreLogisticRegressionRequest)exchange.getIn().getBody(
                GloreLogisticRegressionRequest.class);
        
        GloreData data = request.getGloreData();
        if (data == null) {
            data = new GloreData();
            request.setGloreData(data);
        }
        
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

                state.dataLoaded = true;            
            }
        }
        
        if ("computeCovarianceMatrix".equalsIgnoreCase(data.getState()))
        {
            log.info("Compute covariance matrix");
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
        } 
        else if ("computeSDMatrix".equalsIgnoreCase(data.getState()))
        {
            state.cov_matrix = 
                GloreUtils.convertMatrixTypeToMatrix(
                    data.getCovarianceMatrix());
            log.info("Covariance matrix: " +
                GloreUtils.matrixToString(state.cov_matrix, 8, 6));
            
            log.info("Compute SD matrix");            
            state.SD = new Matrix(1, state.columns);
            for (int i = 0; i < state.columns; i++) {
                state.SD.set(0, i, Math.sqrt(state.cov_matrix.get(i,i)));
            }
            
            log.info("SD matrix:" + GloreUtils.matrixToString(state.SD, 8, 6));            
            data.setSDMatrix(GloreUtils.convertMatrixToMatrixType(state.SD));
            removeState(exchange);
        }            
        else 
        {
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
            log.info("Beta: " + GloreUtils.matrixToString(state.beta, 8, 8));         
        }
        return request;
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
            new ArrayList<Integer>();
        ArrayList<String> independentVariableNames = new ArrayList(
            request.getLogisticRegressionInput().getInputParameters().getIndependentVariableName());

        String dependentVariableName = 
            request.getLogisticRegressionInput().getInputParameters().getDependentVariableName();
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
                log.info("Warning: no line is read, empty file...");
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
    }
}