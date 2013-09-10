package edu.isi.misd.scanner.network.modules.master.processors.glore;

import Jama.Matrix;
import edu.isi.misd.scanner.network.base.master.processors.BaseAggregateProcessor;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.glore.utils.GloreUtils;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.base.ServiceResponses;
import edu.isi.misd.scanner.network.types.glore.GloreData;
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
 * This class does the work of the "Server" in the GLORE network model.  It uses
 * a looping Dynamic Routing Slip and simple state machine to iterate over partial
 * aggregate results from the GLORE computational (worker) nodes until the Beta 
 * value is less than the target Epsilon value, after which the final states 
 * are processed in sequence.  Upon success, the result of the distributed 
 * computation is collated in to a single response message and returned to the 
 * client.
 */
public class GloreAggregateProcessor extends BaseAggregateProcessor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(GloreAggregateProcessor.class);
    
    private static final double epsilon = Math.pow(10.0, -6.0);
    private static final int max_iter = 20;
    
    /**
     * Camel {@link org.apache.camel.Processor} implementation -- 
     * the majority of the work is handled in this function implementation.
     */
    @Override
    public void process(Exchange exchange) throws Exception
    {
        // creates the aggregate ServiceResponses object
        super.process(exchange);
        
        // fail fast - treating GLORE as transactional for now,
        // so if any part of the request fails, the entire request fails.
        List<ServiceResponse> errorResponses = getGloreErrorList(exchange);
        if (!errorResponses.isEmpty()) 
        {
            ServiceResponses responses = new ServiceResponses();                     
            for (ServiceResponse errorResponse : errorResponses) {      
                responses.getServiceResponse().add(errorResponse);
            }
            exchange.getIn().setBody(responses);  
            
            // signal complete
            exchange.setProperty("status", "complete");           
            return;            
            
        }
        
        List<GloreLogisticRegressionRequest> gloreRequestList = 
            getGloreRequestList(exchange);
        if (gloreRequestList.isEmpty()) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("Null Glore aggregate results"), 500);
            return;
        }
        
        GloreLogisticRegressionRequest request = gloreRequestList.get(0);
        GloreData gloreData = request.getGloreData();
        if (gloreData == null) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("Null Glore state data"), 500);
            return;
        }

        // replace the getFeatures with the standardized input parameters
        LogisticRegressionInputParameters params = 
            request.getLogisticRegressionInput().getInputParameters();
        ArrayList<String> independentVariables = 
            new ArrayList(params.getIndependentVariableName());
        int features = independentVariables.size()+1;

        int iter = gloreData.getIteration();
        Matrix beta0, beta1;
        
        if ("computeCovarianceMatrix".equalsIgnoreCase(gloreData.getState()))
        {
            if (log.isDebugEnabled()) {
                log.debug("Compute covariance matrix");
            }
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
  
            if (log.isDebugEnabled()) {
                log.debug("Covariance matrix:" + 
                    GloreUtils.matrixToString(cov_matrix, 8, 6));
            }
            
            gloreData.setCovarianceMatrix(
                GloreUtils.convertMatrixToMatrixType(cov_matrix));
            gloreData.setState("computeSDMatrix"); 

        } 
        else if ("computeSDMatrix".equalsIgnoreCase(gloreData.getState()))
        {
            if (log.isDebugEnabled()) {            
                log.debug("Compute SD matrix");
            }
            
            Matrix cov_matrix = 
                GloreUtils.convertMatrixTypeToMatrix(
                    gloreData.getCovarianceMatrix());
            
            Matrix SD = new Matrix(1, features);
            for (int i = 0; i < features; i++) {
                SD.set(0,i, Math.sqrt(cov_matrix.get(i,i)));
            }   
            if (log.isDebugEnabled()) {            
                log.debug("SD Matrix: " + GloreUtils.matrixToString(SD, 8, 6));
            }
            gloreData.setState("complete");

            // prepare GLORE outputs            
            GloreLogisticRegressionResponse gloreResponse =
                new GloreLogisticRegressionResponse();
            LogisticRegressionResponse lrResponse =
                new LogisticRegressionResponse();
            // need to decide how best to handle the DataSetID
            //response.setDataSetID("GLORE server");  
            lrResponse.setInput(request.getLogisticRegressionInput());
            lrResponse.setOutput(new LogisticRegressionOutput());

            List<Coefficient> target = 
                lrResponse.getOutput().getCoefficient();
            Matrix fBeta = 
                GloreUtils.convertMatrixTypeToMatrix(gloreData.getBeta());

            // set intercept
            Coefficient coefficient = new Coefficient();
            coefficient.setB(GloreUtils.df(fBeta.get(0,0)));
            coefficient.setSE(GloreUtils.df(SD.get(0,0)));
            coefficient.setTStatistics(GloreUtils.df(fBeta.get(0,0)/SD.get(0,0)));
            coefficient.setDegreeOfFreedom(1);
            coefficient.setPValue(GloreUtils.df(ztest(fBeta.get(0,0)/SD.get(0,0))));
            coefficient.setName("Intercept");
            target.add(coefficient);

            //set the rest of the attributes
            for (int i=1; i<fBeta.getColumnPackedCopy().length;i++ )
            {
                coefficient = new Coefficient();
                coefficient.setB(GloreUtils.df(fBeta.get(i,0)));
                coefficient.setSE(GloreUtils.df(SD.get(0,i)));
                coefficient.setTStatistics(GloreUtils.df(fBeta.get(i,0)/SD.get(0,i)));
                coefficient.setDegreeOfFreedom(1);
                coefficient.setPValue(GloreUtils.df(ztest(fBeta.get(i,0)/SD.get(0,i))));
                coefficient.setName(independentVariables.get(i-1));
                target.add(coefficient);
            }         
            gloreResponse.setLogisticRegressionResponse(lrResponse);
            
            // format the service response objects and set as the result body
            ServiceResponseData responseData = new ServiceResponseData();           
            responseData.setAny(gloreResponse);            
            ServiceResponse response = new ServiceResponse();
            response.setServiceResponseData(responseData);            
            ServiceResponses responses = new ServiceResponses();              
            responses.getServiceResponse().add(response);
            exchange.getIn().setBody(responses);   
            
            // signal complete
            exchange.setProperty("status", "complete");           
            return;
        }            
        else 
        {
            if (iter == 0) {
                if (log.isDebugEnabled()) {                
                    log.debug("Primary iteration phase");  
                }
                // init beta vectors
                beta0 = new Matrix(features, 1, -1.0);
                beta1 = new Matrix(features, 1, 0.0);   
                if (log.isDebugEnabled()) {                
                    log.debug("Initial iteration value: " + 
                        GloreUtils.max_abs((beta1.minus(beta0)).getArray()));                
                }
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
            if (log.isDebugEnabled()) {
                log.debug("beta1:" + GloreUtils.matrixToString(beta1, 10, 12));
            }

            gloreData.setBeta(
                GloreUtils.convertMatrixToMatrixType(beta1));             
            if (log.isDebugEnabled()) {
                log.debug("Iteration " + iter + " value: " + 
                    GloreUtils.max_abs((beta1.minus(beta0)).getArray()));
            }
           
            if (iter > 0) {
                if ((GloreUtils.max_abs(
                    (beta1.minus(beta0)).getArray()) < epsilon) || 
                    (iter >= max_iter))
                {  
                    if (iter >= max_iter) {
                        log.info(
                            "Hit maximum number of iterations (" + max_iter  + 
                            ") without converging, iterations stopped.");
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Iteration final value: " + 
                            GloreUtils.max_abs((beta1.minus(beta0)).getArray()));    
                    }
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
        ServiceResponses serviceResponses = 
            exchange.getIn().getBody(ServiceResponses.class);   
        if (serviceResponses == null) {
            throw new NullPointerException(
                "ServiceResponses aggregate structure was null");
        }        
        List<ServiceResponse> resultsInput = 
            serviceResponses.getServiceResponse();     
        
        ArrayList<GloreLogisticRegressionRequest> resultsOutput = 
            new ArrayList<GloreLogisticRegressionRequest>();        
        for (ServiceResponse result : resultsInput) 
        {
            ServiceResponseData responseData = result.getServiceResponseData();
            if (responseData != null) {
                GloreLogisticRegressionRequest request = 
                        (GloreLogisticRegressionRequest)MessageUtils.convertTo(
                            GloreLogisticRegressionRequest.class,              
                            responseData.getAny(),
                            exchange);
                resultsOutput.add(request);
            } else {
                log.warn("A ServiceResponse was missing the expected ServiceResponseData");
            }
        }        
        return resultsOutput; 
    }

    private List<ServiceResponse> getGloreErrorList(Exchange exchange)
        throws Exception
    {
        ServiceResponses serviceResponses = 
            exchange.getIn().getBody(ServiceResponses.class);   
        if (serviceResponses == null) {
            throw new NullPointerException(
                "ServiceResponses aggregate structure was null");
        }        
        List<ServiceResponse> resultsInput = 
            serviceResponses.getServiceResponse(); 
        
        ArrayList<ServiceResponse> errors = 
            new ArrayList<ServiceResponse>();         
        for (ServiceResponse result : resultsInput) 
        {
                ServiceResponseMetadata status = 
                    result.getServiceResponseMetadata();
                if (ServiceRequestStateType.ERROR.equals(
                    status.getRequestState())) {
                        errors.add(result);
                }
        }
        return errors;         
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

    /* some additional statistical function to calculate normCDF*/

    private static final double coefficient = 2 / Math.sqrt(Math.PI);

    /**
     * Calculates probability that normal random variable is less than x
     * @param x - input value
     * @return - Probability that normal random variable is less than x
     */
    private static double getCDF(double x) {
        double tolerance = 1e-15;
        int maxCycles = 100;
        return 0.5 * (1.0 + getERF(x/Math.sqrt(2.0), tolerance, maxCycles));
    }

    /**
     * Estimates error function value for x
     * @param x - input value
     * @return - error function value for x
     */
    private static double getERF(double x, double tolerance, int maxCycles) {
        int n = 1;
        double nextCorrection = tolerance + 1;
        double erf = x;
        while((n <= maxCycles) && (Math.abs(nextCorrection) > tolerance)) {
            nextCorrection = Math.pow(x, 2*n + 1)/(factorial(n)*(2*n + 1));
            if((n % 2) == 1) {
                erf -= nextCorrection;
            }
            else {
                erf += nextCorrection;
            }
            n++;
        }
        erf = erf * coefficient;
        return erf;
    }

    /**
     * Calculates n factorial.
     * @param n - input value
     * @return
     */
    private static double factorial(int n) {
        double result = 1.0;
        for(int i=2; i<=n; i++) {
            result *= i;
        }
        return result;
    }

    private static double ztest(double v)
    {
        return 2*(1-getCDF(Math.abs(v)));
    }

}
