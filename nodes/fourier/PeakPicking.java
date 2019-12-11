/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fourier;

import java.io.FileWriter;
import java.io.IOException;


public class PeakPicking {
    
    /**
     * Number of candidate peaks to be detected.
     */
    protected int numOfPeaks;
    
    /**
     * Frequencies of the FFT with step: f_step = (sampling rate)/(length of data set)
     */
    protected double[] freqs;
    
    /**
     * Fourier amplitude spectrum of the FFT.
     */
    protected double[] amplitudes;
    
    /**
     * Frequencies at the candidate peaks.
     */
    protected double[] frequenciesAtPeaks;

    public PeakPicking(int peaks, FrequencySpectrum fSpec) throws IOException
    {
        numOfPeaks = peaks;
        freqs = fSpec.getFrequencies();
        amplitudes = fSpec.getAmplitudeSpectrum();
    }    
    
    /**
     * Retrieves the detected candidate peaks.
     * @return - indices of the detected candidate peaks
     * @throws InterruptedException 
     */
    public int [] getPeaks() throws InterruptedException
    {
        return detectPeaks();
    }
    
    /**
     * Detects candidate peaks. The detection is based on finding the peak that
     * corresponds to the maximum amplitude and, subsequently, on removing the respective
     * amplitude, along with amplitudes within a range of the selected peak, 
     * while repeating the detection until the predefined number of candidate peaks 
     * is selected. The removal of amplitudes in the vicinity of the selected peak
     * aims at avoiding detecting false positives, since the amplitudes around a 
     * peak might also assume high values that may not be regarded as modal 
     * peaks. The tolerance for this removal has been set to -+5 amplitudes around
     * the selected peak.
     * @return - indices of detected candidate peaks
     * @throws InterruptedException 
     */
    private int [] detectPeaks() throws InterruptedException
    {
        int [] peaksDetected = new int [numOfPeaks];
        int amplitudeIndices[] = new int[amplitudes.length];
        for (int i = 0; i < amplitudes.length; i++){
            amplitudeIndices [i] = i;
        }
        frequenciesAtPeaks = new double [numOfPeaks];
        
        for (int i = 0; i < numOfPeaks; i++){
        double tempMax = 0;
        int tempPeak = 0; 
            for (int j = 0; j < amplitudes.length; j++){
                if (amplitudes [j] > tempMax){
                    tempMax = amplitudes[j];
                    tempPeak = j;
                }
            }
            if (i == 0){
                peaksDetected [i] = tempPeak;
                frequenciesAtPeaks[i] = freqs[tempPeak];
            }else{
                peaksDetected [i] = (int) amplitudeIndices[tempPeak];
                frequenciesAtPeaks[i] = freqs[(int) amplitudeIndices[tempPeak]];
            }
            
            int cropSpectrumLeft;
            int cropSpectrumRight;
            int offset = 5;
            
            if (tempPeak - offset < 0){
                cropSpectrumLeft = tempPeak;
            }else{
                cropSpectrumLeft = offset;
            }
            
            if (tempPeak + offset > amplitudes.length - 1){
                cropSpectrumRight = amplitudes.length - tempPeak - 1;
            }else{
                cropSpectrumRight = offset;
            }            

            double amplitudes2[] = new double[amplitudes.length - cropSpectrumLeft - cropSpectrumRight];
            int amplitudeIndices2 [] = new int[amplitudeIndices.length - cropSpectrumLeft - cropSpectrumRight];


            int k = 0;
            for (int j = 0; j < amplitudes.length; j++) {
                if (j >= tempPeak - cropSpectrumLeft && j <= tempPeak + cropSpectrumRight) {
                    continue;
                }
                amplitudes2[k] = amplitudes[j];
                amplitudeIndices2 [k] = amplitudeIndices [j];
                k++;
            }
            amplitudes = new double[amplitudes2.length];
            amplitudeIndices = new int [amplitudeIndices2.length];
            System.arraycopy(amplitudes2, 0, amplitudes, 0, amplitudes2.length);
            System.arraycopy(amplitudeIndices2, 0, amplitudeIndices, 0, amplitudeIndices2.length);
        }
        Thread.sleep(2000);
        return peaksDetected;
    }

}

        
    
