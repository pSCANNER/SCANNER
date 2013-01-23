/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.misd.scanner.network.glore.utils;

import Jama.Matrix;
import edu.isi.misd.scanner.network.types.glore.DoubleArray;
import edu.isi.misd.scanner.network.types.glore.MatrixType;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
     * @param matrixType
     * @return
     */
    public static Matrix convertMatrixTypeToMatrix(MatrixType matrixType)
    {
        List<DoubleArray> doubleArrays = matrixType.getDoubles();
        ArrayList<List<Double>> doubleList = new ArrayList<List<Double>>();
        
        for (DoubleArray doubleArray : doubleArrays) {
            doubleList.add(doubleArray.getDouble());
        }
        
        Matrix matrix = new Matrix(two_dim_list_to_arr(doubleList));
        return matrix;
    }

    /**
     *
     * @param matrix
     * @return
     */
    public static MatrixType convertMatrixToMatrixType(Matrix matrix) 
    {
        ArrayList<DoubleArray> doubleArraysList = new ArrayList<DoubleArray>();        
        double[][] matrixArray = matrix.getArray();
        
        for (int i = 0; i < matrixArray.length; i++) 
        {
            ArrayList<Double> doubleList = new ArrayList<Double>();
            for (int j = 0; j < matrixArray[i].length; j++) {
                doubleList.add(Double.valueOf(matrixArray[i][j]));
            }
            DoubleArray doubleArray = new DoubleArray();
            doubleArray.getDouble().addAll(doubleList);
            doubleArraysList.add(doubleArray);
        }
        
        MatrixType matrixType = new MatrixType();        
        matrixType.getDoubles().addAll(doubleArraysList);
        return matrixType;
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
