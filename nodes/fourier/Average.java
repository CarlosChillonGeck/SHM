/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fourier;

public class Average {
    
    /**
     * Vector 1 for averaging.
     */
    private double [] vector1;
    
    /**
     * Vector 2 for averaging.
     */
    private double [] vector2;
    
    public Average(double vec [])
    {
        vector1 = vec;
    }
    
    public Average(double vec1 [], double [] vec2)
    {
        vector1 = vec1;
        vector2 = vec2;
    }    
    
    /**
     * Retrieves the average of a single vector.
     * @return - average of a single vector
     */
    public double getAverage()
    {
        return computeAverage();
    }
    
    /**
     * Retrieves the average vector of two vectors.
     * @return - average vector of two vectors
     */
    public double [] getAverageVector()
    {
        return computeAverageVector();
    }
    
    /**
     * Computes the average of a vector: M = Sum(A[i])/N (i = 0,1,2...,N)
     * @return - average of a single vector
     */
    private double computeAverage(){
        double ave;
        double sum = 0; 
        for (int i = 0; i < vector1.length; i++){
            sum += vector1 [i];
        }
        ave = sum / (double) vector1.length;
        return ave;
    }
    
    /**
     * Computes the average of two vectors: M[i] = 1/2*(A1[i] + A2[i])
     * @return - average of two vectors
     */
    private double [] computeAverageVector(){
        double [] ave = new double [vector1.length];
        for (int i = 0; i < vector1.length; i++){
            ave [i] = 0.5 * (vector1[i] + vector2[i]);
        }
        return ave;
    }
}
