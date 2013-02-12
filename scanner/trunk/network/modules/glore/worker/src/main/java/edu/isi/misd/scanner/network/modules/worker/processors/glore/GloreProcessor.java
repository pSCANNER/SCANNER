package edu.isi.misd.scanner.network.modules.worker.processors.glore;

import Jama.Matrix;
import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ConfigUtils;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.glore.utils.GloreUtils;
import edu.isi.misd.scanner.network.types.glore.GloreData;
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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class GloreProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(GloreProcessor.class); 
        
    /**
     *
     */
    public static Map<String, Object> dataStore = new HashMap<String, Object>();

    /**
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {   
        try {    
            exchange.getIn().setBody(this.executeAnalysis(exchange)); 
        }
        catch (Exception e) {
            ErrorUtils.setHttpError(exchange, e, 500);
        }                
    }
    
    private GloreData executeAnalysis(Exchange exchange) 
        throws Exception
    {
        GloreData data = 
            (GloreData)exchange.getIn().getBody(GloreData.class); 
        GloreStateData state = getState(exchange);
        
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
        return data;
    }

    /**
     *
     * @param exchange
     * @throws Exception
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
                exchange, BaseConstants.WORKER_INPUT_DIR) + "/glore/lr";  
        
        // access the file
        File file = new File(baseInputDir,fileName);        
        file_stream = new FileInputStream(file);
        file_in = new DataInputStream(file_stream);
        file_br = new BufferedReader(new InputStreamReader(file_in));

        // read file and populate X and Y matrices
        state.rows = 0;
        // get rid of the first line
        if ((file_line = file_br.readLine()) != null){
            // do nothing
        }
        else{
            log.info("Warning: no line is read...");
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
            for (int i = 0; i < line_tokens.length - 1; i++) {
                state.xrow.add(new Double(line_tokens[i]));
            }
            state.Xv.add(state.xrow);
            state.Yv.add(new Double(line_tokens[line_tokens.length-1]));
        }

        state.dataLoaded = true;
        // close input stream
        file_in.close();
        log.info("Successfully loaded file: " + file.getAbsolutePath());
    }
    
    /**
     *
     * @param exchange
     * @return
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
     *
     * @param exchange
     * @return
     */
    protected static GloreStateData removeState(Exchange exchange)
    {
        String key = 
            exchange.getFromEndpoint().getEndpointKey() + "/id/" +
            (String)exchange.getIn().getHeader(BaseConstants.ID);
            return (GloreStateData)dataStore.remove(key);
    }
    
    /**
     *
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
