package edu.isi.misd.scanner.network.glore.utils;

import Jama.Matrix;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import edu.isi.misd.scanner.network.types.base.DoubleType;
import edu.isi.misd.scanner.network.types.base.MatrixType;
import edu.isi.misd.scanner.network.types.base.MatrixRowType;

/**
 *
 */
public class GloreUtils 
{
    /**
     *
     * @param matrix
     * @param w
     * @param d
     * @return
     */
    public static String matrixToString(Matrix matrix, int w, int d)
    {
        String matrixStr = "";        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            matrix.print(pw, w, d);
            matrixStr = sw.getBuffer().toString();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                sw.close();            
                pw.close();
            } catch (Exception e) {
                System.err.println(e);                
            }
        }
        return matrixStr;
    }

    /**
     *
     * @param matrix
     * @return
     */
    public static MatrixType convertMatrixToMatrixType(Matrix matrix)
    {
        ArrayList<MatrixRowType> matrixRowsList = new ArrayList<MatrixRowType>();        
        double[][] matrixArray = matrix.getArray();
        
        for (int i = 0; i < matrixArray.length; i++) 
        {
            ArrayList<DoubleType> columnList = 
                new ArrayList<DoubleType>();
            for (int j = 0; j < matrixArray[i].length; j++) {
                DoubleType columnType = new DoubleType();
                columnType.setValue(Double.valueOf(matrixArray[i][j]));
                columnType.setName(Integer.toString(j));
                columnList.add(columnType);
            }
            MatrixRowType matrixRow = new MatrixRowType();
            matrixRow.getMatrixColumn().addAll(columnList);
            matrixRowsList.add(matrixRow);
        }
        
        MatrixType matrixType = new MatrixType();        
        matrixType.getMatrixRow().addAll(matrixRowsList);
        return matrixType;    
    }
    
    public static Matrix convertMatrixTypeToMatrix(MatrixType baseMatrixType)
    {
        List<MatrixRowType> rowArrays = baseMatrixType.getMatrixRow();
        ArrayList<List<Double>> doubleList = new ArrayList<List<Double>>();
        
        for (MatrixRowType rowArray : rowArrays) {
            ArrayList doubles = new ArrayList();            
            for (DoubleType column : rowArray.getMatrixColumn()) {
                doubles.add(column.getValue());             
            }
            doubleList.add(doubles);            
        }
        
        Matrix matrix = new Matrix(two_dim_list_to_arr(doubleList));
        return matrix;
    }
    
    /* Returns the absolute maximum of the elements in the two dimensional
       array matrix. */
    /**
     *
     * @param matrix
     * @return
     */
    public static double max_abs(double[][] matrix) 
    {
        int i,j;
        boolean set = false;
        double max = 0;

        // iterate through matrix
        for (i = 0; i < matrix.length; i++) 
        {
            for (j = 0; j < matrix[i].length; j++) 
            {
                // maintain absolute max number found
                if (!set) {
                    max = Math.abs(matrix[i][j]);
                    set = true;
                }
                else if (Math.abs(matrix[i][j]) > max) {
                    max = Math.abs(matrix[i][j]);
                }
            }
        }
        return max;
    }

    /* Convert a 2D ArrayList of Doubles into a 2D array of doubles. */
    /**
     *
     * @param V
     * @return
     */
    public static double[][] two_dim_list_to_arr(List<List<Double>>V) 
    {
        // allocate part of the array
        double[][] A = new double[V.size()][];
        int i;

        // allocate and convert rows of the ArrayList
        for (i = 0; i < V.size(); i++) {
            A[i] = one_dim_list_to_arr(V.get(i));
        }

        // return 2D array
        return A;
    }

    /* Convert a ArrayList of Doubles into an array of doubles. */
    /**
     *
     * @param V
     * @return
     */
    public static double[] one_dim_list_to_arr(List<Double> V) 
    {
        int size = V.size();
        int i;
        double[] A = new double[size];

        for (i = 0; i < size; i++) {
            A[i] = (V.get(i)).doubleValue();
        }

        return A;
    }

    /* Set each element of the 2D double array to e^a where a is the value of
       an element. */
    /**
     *
     * @param A
     */
    public static void exp(double[][] A) 
    {
        int i,j;
        for (i = 0; i < A.length; i++) {
            for (j = 0; j < A[i].length; j++) {
                A[i][j] = Math.exp(A[i][j]);
            }
        }
    }

    /* Set each element of the 2D double array to 1 + a where a is the value of
       an element. */
    /**
     *
     * @param A
     */
    public static void add_one(double[][] A) 
    {
        int i,j;
        for (i = 0; i < A.length; i++) {
            for (j = 0; j < A[i].length; j++) {
                A[i][j] = 1 + A[i][j];
            }
        }
    }

    /* Set each element of the 2D double array to 1/a where a is the value of
       an element. */
    /**
     *
     * @param A
     */
    public static void div_one(double[][] A) 
    {
        int i,j;
        for (i = 0; i < A.length; i++) {
            for (j = 0; j < A[i].length; j++) {
                A[i][j] = 1.0 / A[i][j];
            }
        }
    }

    /* Given an array of length n, returns an n by n matrix M where
       M[i][j] = A[i] if i = j and 0 otherwise. */
    /**
     *
     * @param A
     * @return
     */
    public static Matrix diag(double[] A) 
    {
        int n = A.length;
        int i;

        Matrix M = new Matrix(n, n, 0.0);
        for (i = 0; i < n; i++) {
            M.set(i,i,A[i]);
        }
        return M;
    }     
}