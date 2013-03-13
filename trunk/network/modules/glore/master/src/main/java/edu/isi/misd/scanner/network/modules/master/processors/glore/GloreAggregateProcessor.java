package edu.isi.misd.scanner.network.modules.master.processors.glore;

import Jama.Matrix;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.glore.utils.GloreUtils;
import edu.isi.misd.scanner.network.types.glore.GloreData;
import edu.isi.misd.scanner.network.types.glore.GloreResultData;
import edu.isi.misd.scanner.network.types.glore.GloreLogisticRegressionRequest;
import edu.isi.misd.scanner.network.types.glore.GloreLogisticRegressionResponse;
import java.util.ArrayList;
import java.util.List;

import edu.isi.misd.scanner.network.types.regression.Coefficient;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionInputParameters;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionOutput;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
/**
 *
 */
public class GloreAggregateProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(GloreAggregateProcessor.class);
    
    /**
     *
     */
    public static final double epsilon = Math.pow(10.0, -6.0);
            
    /**
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception
    {
        List<GloreLogisticRegressionRequest> gloreRequestList = 
            getGloreRequestList(exchange);
        if (gloreRequestList.isEmpty()) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("Null aggregate results"), 500);           
        }
        
        GloreLogisticRegressionRequest request = gloreRequestList.get(0);
        GloreData gloreData = request.getGloreData();
        if (gloreData == null) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("Null Glore state data"), 500);           
        }

        // replace the getFeatures with the standardized input parameters
        LogisticRegressionInputParameters params = request.getLogisticRegressionInput().getInputParameters();
        ArrayList<String> independentVariables = new ArrayList(params.getIndependentVariableName());
        int features = independentVariables.size()+1;
//        int features = gloreData.getFeatures();

        int iter = gloreData.getIteration();
        Matrix beta0, beta1;
        
        if ("computeCovarianceMatrix".equalsIgnoreCase(gloreData.getState()))
        {
            log.info("Compute covariance matrix");
            ArrayList<double[][]> D_values = new ArrayList<double[][]>();
            for (GloreLogisticRegressionRequest gloreRequest : gloreRequestList)
            { 
                Matrix D = 
                    GloreUtils.convertMatrixTypeToMatrix(
                        gloreRequest.getGloreData().getD());
                D_values.add(D.getArray());
            }              
            Matrix temp_b = new Matrix(row_sums_two_dim(D_values,features));
            Matrix temp_c = diag(0.0000001, features);

            temp_b = temp_b.plus(temp_c);
            Matrix cov_matrix = temp_b.inverse();
  
            log.info("Covariance matrix:" + 
                GloreUtils.matrixToString(cov_matrix, 8, 6));
            
            gloreData.setCovarianceMatrix(
                GloreUtils.convertMatrixToMatrixType(cov_matrix));
            gloreData.setState("computeSDMatrix"); 

        } 
        else if ("computeSDMatrix".equalsIgnoreCase(gloreData.getState()))
        {
            log.info("Compute SD matrix");
            
            Matrix cov_matrix = 
                GloreUtils.convertMatrixTypeToMatrix(
                    gloreData.getCovarianceMatrix());
            
            Matrix SD = new Matrix(1, features);
            for (int i = 0; i < features; i++) {
                SD.set(0,i, Math.sqrt(cov_matrix.get(i,i)));
            }          
            log.info("SD Matrix: " + GloreUtils.matrixToString(SD, 8, 6));

            gloreData.setSDMatrix(GloreUtils.convertMatrixToMatrixType(SD));
            gloreData.setState("complete");
                   
            // prepare final results
            GloreResultData resultData = new GloreResultData();
            resultData.setBeta(gloreData.getBeta());
            resultData.setCovarianceMatrix(gloreData.getCovarianceMatrix());
            resultData.setSDMatrix(gloreData.getSDMatrix());
            exchange.getIn().setBody(resultData);              
            
            // signal complete
            exchange.setProperty("status", "complete");


            // prepare GLORE outputs
            GloreLogisticRegressionResponse gloreResponse = new GloreLogisticRegressionResponse();
            LogisticRegressionResponse response = new LogisticRegressionResponse();
            response.setDataSetID("GLORE server");
            response.setInput(request.getLogisticRegressionInput());
            response.setOutput(new LogisticRegressionOutput());

            List<Coefficient> target= response.getOutput().getCoefficient();
            Matrix fBeta =  GloreUtils.convertMatrixTypeToMatrix(gloreData.getBeta());

            // set intercept
            Coefficient coefficient = new Coefficient();
            coefficient.setB(fBeta.get(0,0));
            coefficient.setSE(SD.get(0,0));
            coefficient.setTStatistics(fBeta.get(0,0)/SD.get(0,0));
            coefficient.setDegreeOfFreedom(1);
            coefficient.setPValue(0);
            coefficient.setName("Intercept");
            target.add(coefficient);

            //set the rest of the attributes
            for (int i=1; i<fBeta.getColumnPackedCopy().length;i++ )
            {
                coefficient = new Coefficient();
                coefficient.setB(fBeta.get(i,0));
                coefficient.setSE(SD.get(0,i));
                coefficient.setTStatistics(fBeta.get(i,0)/SD.get(0,i));
                coefficient.setDegreeOfFreedom(1);
                coefficient.setPValue(0);
                coefficient.setName(independentVariables.get(i-1));
                target.add(coefficient);
            }

            gloreResponse.setLogisticRegressionResponse(response);

//            return gloreResponse;
            return;
        }            
        else 
        {
            if (iter == 0) {
                log.info("Primary iteration phase");  
                // init beta vectors
                beta0 = new Matrix(features, 1, -1.0);
                beta1 = new Matrix(features, 1, 0.0);                  
                log.info("Initial iteration value: " + 
                    GloreUtils.max_abs((beta1.minus(beta0)).getArray()));                
                gloreData.setState("iteration");
            } else {                              
                beta1 = 
                    GloreUtils.convertMatrixTypeToMatrix(gloreData.getBeta());                  
            }
            
            beta0 = beta1.copy(); 
            /* beta1<-beta0+solve(rowSums(d,dims=2)+
               diag(0.0000001,m))%*%(rowSums(e,dims=2)) */
            ArrayList<double[]> E_values = new ArrayList<double[]>();
            for (GloreLogisticRegressionRequest gloreRequest : gloreRequestList)
            { 
                Matrix E = 
                    GloreUtils.convertMatrixTypeToMatrix(
                        gloreRequest.getGloreData().getE());
                double[] e = new double[features];
                for (int i = 0; i < features; i++) {
                    e[i] = E.get(i,0);           
                }                
                E_values.add(e);
            } 
            Matrix temp_a = new Matrix(row_sums_one_dim(E_values, features)); 
            
            ArrayList<double[][]> D_values = new ArrayList<double[][]>();
            for (GloreLogisticRegressionRequest gloreRequest : gloreRequestList) 
            { 
                Matrix D = 
                    GloreUtils.convertMatrixTypeToMatrix(
                        gloreRequest.getGloreData().getD());
                D_values.add(D.getArray());
            }  
            Matrix temp_b = new Matrix(row_sums_two_dim(D_values, features));
            
            Matrix temp_c = diag(0.0000001, features);
            temp_b = temp_b.plus(temp_c);
            temp_b = temp_b.inverse();
            temp_b = temp_b.times(temp_a);
            beta1 = beta0.plus(temp_b);

            log.info("beta1:" + GloreUtils.matrixToString(beta1, 10, 12));

            gloreData.setBeta(
                GloreUtils.convertMatrixToMatrixType(beta1));             

            log.info("Iteration " + iter + " value: " + 
                GloreUtils.max_abs((beta1.minus(beta0)).getArray()));
           
            if (iter > 0) {
                if (GloreUtils.max_abs(
                    (beta1.minus(beta0)).getArray()) < epsilon) 
                {  
                    log.info("Iteration final value: " + 
                        GloreUtils.max_abs((beta1.minus(beta0)).getArray()));                    
                    gloreData.setState("computeCovarianceMatrix");        
                }                                 
            }
            gloreData.setIteration(iter + 1);                
        }

        exchange.getIn().setBody(request);        
    }




    private List<GloreLogisticRegressionRequest> 
        getGloreRequestList(Exchange exchange)
        throws Exception
    {
        ArrayList<String> resultsInput = 
            exchange.getIn().getBody(ArrayList.class);
        ArrayList<GloreLogisticRegressionRequest> resultsOutput = 
            new ArrayList<GloreLogisticRegressionRequest>();
        
        for (Object result : resultsInput) 
        {            
            GloreLogisticRegressionRequest request = 
                (GloreLogisticRegressionRequest)MessageUtils.convertTo(
                    GloreLogisticRegressionRequest.class, result, exchange);
            resultsOutput.add(request);
        }
        return resultsOutput; 
    }

	/* Return a one dimensional array that is the sum of the E.length
	   one dimensional vectors. */
	private double[][] row_sums_one_dim(List<double[]> E, int features) 
    {
	    int i, j;
	    double [][] sums;
	    sums = new double[features][1];

	    // init sums
	    for (i = 0; i < features; i++) {
            sums[i][0] = 0.0;
	    }
	    // for each response, add its contribution to sums
	    for (i = 0; i < E.size(); i++) {
            for (j = 0; j < features; j++) {
                sums[j][0] = sums[j][0] + E.get(i)[j];
            }
	    }
	    return sums;
	}

	private double[][] row_sums_two_dim(List<double[][]> D, int features)
    {
	    int i,j,k;
	    double[][] sums;
	    sums = new double[features][features];
	    // init sums
	    for (i = 0; i < features; i++) {
            for (j = 0; j < features; j++) {
                sums[i][j] = 0;
            }
	    }
	    // for each client, add its contribution to sums
	    for (i = 0; i < D.size(); i++) {
            for (j = 0; j < features; j++) {
                for (k = 0; k < features; k++) {
                sums[j][k] = sums[j][k] + D.get(i)[j][k];
                }
            }
	    }
	    return sums;
	}

	/* Returns an n by n matrix where the diagonal entries are v and the
	   other entries are 0 */
	private Matrix diag(double v, int n) 
    {
	    int i;
	    double[][] A = new double[n][n];
	    for (i = 0; i < n; i++) {
            A[i][i] = v;
	    }
	    return new Matrix(A);
    }    
}
