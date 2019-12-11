

package fourier;

import java.lang.Math;

public class FrequencySpectrum {
    
    /**
     * Acceleration data set to be transformed.
     */
    private double [] inputData;
    
    /**
     * Time step in [s].
     */
    private final double DELTA_T;
    
    /**
     * Size of the frequency spectrum.
     */
    private int numOfFrequencyValues;
    
    /**
     * Overlap between windows while performing spectrum averaging.
     */
    private int overlap = 0;
    
    /**
     * Determines the size of the window: windowSize = 2^(powerOfTwoForAveraging).
     */
    private int powerOfTwoForAveraging = 0;
    
    /**
     * Size of the window used for averaging.
     */
    private int windowSize = 0;
    
    /**
     * Number of segments (windows) created if spectrum is averaged.
     */
    private int segments = 0;

    public FrequencySpectrum(double[] inData, double dt) 
    {
        inputData = inData;
        DELTA_T = dt;
        numOfFrequencyValues = inputData.length/2 + 1;
    }
    
    /**
     * Sets the overlap between windows for averaging.
     * @param overlapFactor - number of data points overlapping
     * between two successive windows
     */
    public void setOverlap(int overlapFactor)
    {
        overlap = overlapFactor;
    }
    
    /**
     * Sets the power of two for determining the window size for
     * averaging. Default value "0" indicates no averaging. If a value
     * is selected, the spectrum computation parameters are set anew by
     * calling the "setNewParameters()" method.
     * @param powOfTwo - power of two determining the size of the window
     * to be used for averaging
     */
    public void setPowerOfTwoForAveraging(int powOfTwo){
        powerOfTwoForAveraging = powOfTwo;
        if (powerOfTwoForAveraging != 0){
            setNewParameters();
        }
    }
    
    /**
     * Retrieves the complex Fourier values of the transformed acceleration
     * data set.
     * @return - FFT transform of acceleration data set
     */
    public Complex [] getFFTValues()
    {
        if (powerOfTwoForAveraging == 0){
            return computeFFTvalues();
        }else{
            return computeAverageFFT();
        }    
    }

    /**
     * Retrieves the frequencies of the FFT.
     * @return - array holding the frequencies of the FFT
     */
    public double[] getFrequencies() 
    {
        return computeFrequencies();
    }

    /**
     * Retrieves the Fourier amplitudes.
     * @return - array holding the amplitudes of the FFT
     */
    public double[] getAmplitudeSpectrum() {
        if (powerOfTwoForAveraging == 0){
            return computeAmplitudeSpectrum();
        }else{
            return computeAverageAmplitudeSpectrum();
        }
    }

    /**
     * Retrieves the Fourier phase angles.
     * @return - array holding the phase angles of the FFT
     */
    public double[] getPhaseSpectrum() {
        if (powerOfTwoForAveraging == 0) {
            return computePhaseSpectrum();
        } else {
            return computeAveragePhaseSpectrum();
        }
    }

    /**
     * Sets the parameters of the FFT computation anew, if a power of two for
     * averaging is selected. Calculates and assings: the window size, the
     * window segments, and the number of frequency values for spectrum
     * averaging.
     */
    private void setNewParameters(){
        int totalSize = inputData.length;
        this.windowSize = (int) Math.pow(2, powerOfTwoForAveraging);
        System.out.println("window size: " + this.windowSize);
        int a = 1;
        int windowLastIndex = this.windowSize - 1;
        while (windowLastIndex < totalSize - 1){
            System.out.println("window last index: " + windowLastIndex);
            windowLastIndex += this.windowSize - overlap;
            a++;
            System.out.println("a = " + a);
        }
        this.segments = a;
        System.out.println("Segments: " + this.segments);
        this.numOfFrequencyValues = windowSize/2;
    }


    /**
     * Performs the FFT of the acceleration data set.
     * @return - array of complex FFT values
     */
    private Complex[] computeFFTvalues() {

        int numValsPow;
        int numVals;
        numValsPow = (int) Math.floor(Math.log(inputData.length) / Math.log(2));//consider adding 1 and pad with zeros (see Matlab)
        numVals = (int) Math.pow(2, numValsPow);
        Complex[] tempX = new Complex[numVals];
        for (int i = 0; i < numVals; i++) {
            tempX[i] = new Complex(inputData[i], 0);
        }
        Complex[] fftVals = FFT.fft(tempX);
        return fftVals;
    }

    /**
     * Computes the Fourier amplitudes of the FFT as per: A = (FFT(re)^2 + FFT(im)^2)^(1/2)
     * @return - the Fourier amplitudes of the FFT
     */
    private double[] computeAmplitudeSpectrum() {
        Complex fftValues [] = computeFFTvalues();
        double[] specData = new double[numOfFrequencyValues];
        for (int i = 0; i < numOfFrequencyValues; i++) {
            specData[i] = fftValues[i].abs();
        }
        return specData;
    }
    
    /**
     * Computes the Fourier phases of the FFT as per: U = atan(FFT(im)/FFT(re))
     * @return - the Fourier phase angles of the FFT
     */
    private double[] computePhaseSpectrum() {
        Complex fftValues [] = computeFFTvalues();
        double[] phaseSpecData = new double[numOfFrequencyValues];
        for (int i = 0; i < numOfFrequencyValues; i++) {
            phaseSpecData[i] = Math.atan2(fftValues[i].im(), fftValues[i].re());
        }
        return phaseSpecData;
    }

    /**
     * Computes the frequencies of the FFT with step: f_step = (sampling rate)/(length of data set)
     * @return - the frequencies of the FFT
     */
    private double[] computeFrequencies() {

        double Fs;
        Fs = (double) 1 / DELTA_T;
        double[] freqVals = new double[numOfFrequencyValues];

        for (int i = 0; i < numOfFrequencyValues; i++) {
            freqVals[i] = Fs / 2 * (double) i / (double) (numOfFrequencyValues - 1);
        }
        return freqVals;
    }
    
    /**
     * Averages the FFT with respect to the specified window size, segments, and 
     * overlap.
     * @return - averaged complex FFT series
     */
    private Complex [] computeAverageFFT(){
        double [] averageOfRealParts = new double[windowSize/2];
        double [] averageOfImaginaryParts = new double[windowSize/2];
        Complex [] averagedFFT = new Complex [windowSize/2];
        Complex [][] segmentFFT = new Complex [segments][windowSize/2];
        double window [] = new double[windowSize];
        double totalInputData [] = new double [inputData.length];
        System.arraycopy(inputData, 0, totalInputData, 0, inputData.length);
        int startPointForCopying = 0;
        for(int i = 0; i < segments; i++){
            System.arraycopy(totalInputData, startPointForCopying, window, 0, windowSize);
            this.inputData = window;
            segmentFFT [i] = computeFFTvalues();
            if (i == 0){
                startPointForCopying += this.windowSize - 1 - overlap;
            }else{
                startPointForCopying += this.windowSize - overlap;
            }
        }
        for (int i = 0; i < windowSize/2; i++){
            double [] realPartsAtCurrentFrequency = new double [segments];
            double [] imaginaryPartsAtCurrentFrequency = new double [segments];
            for (int j = 0; j < segments; j++){
                realPartsAtCurrentFrequency [j] = segmentFFT[j][i].re();
                imaginaryPartsAtCurrentFrequency[j] = segmentFFT[j][i].im();
            }
            averageOfRealParts[i] = (new Average(realPartsAtCurrentFrequency)).getAverage();
            averageOfImaginaryParts[i] = (new Average(imaginaryPartsAtCurrentFrequency)).getAverage();
            averagedFFT[i] = new Complex(averageOfRealParts[i],averageOfImaginaryParts[i]);
        }
        this.inputData = totalInputData;
        return averagedFFT;
    }    
    
     /**
     * Averages the Fourier amplitude spectrum with respect to the specified window 
     * size, segments, and overlap.
     * @return - averaged Fourier amplitude spectrum
     */
    private double[] computeAverageAmplitudeSpectrum(){
        double [] averagedAmplitudeSpectrum = new double [windowSize/2];
        double [][] segmentAmplitudeSpectra = new double [segments][windowSize/2];
        double window [] = new double[windowSize];
        double totalInputData [] = new double [inputData.length];
        System.arraycopy(inputData, 0, totalInputData, 0, inputData.length); 
        int startPointForCopying = 0;
        for(int i = 0; i < segments; i++){
            System.out.println("i = " + i + " starting point: " + startPointForCopying);
            System.arraycopy(totalInputData, startPointForCopying, window, 0, windowSize);
            this.inputData = window;
            segmentAmplitudeSpectra [i] = computeAmplitudeSpectrum();
            if (i == 0){
                startPointForCopying += this.windowSize - 1 - overlap;
            }else{
                startPointForCopying += this.windowSize - overlap;
            }
        }
        for (int i = 0; i < windowSize/2; i++){
            double [] amplitudesAtCurrentFrequency = new double [segments];
            for (int j = 0; j < segments; j++){
                amplitudesAtCurrentFrequency [j] = segmentAmplitudeSpectra[j][i];
            }
            averagedAmplitudeSpectrum[i] = (new Average(amplitudesAtCurrentFrequency)).getAverage();
        }
        this.inputData = totalInputData;
        return averagedAmplitudeSpectrum;
    }
    
    /**
     * Averages the Fourier phase spectrum with respect to the specified window 
     * size, segments, and overlap.
     * @return - averaged Fourier phase spectrum
     */
    private double[] computeAveragePhaseSpectrum(){
        
        double [] averagedPhaseSpectrum = new double [windowSize/2];
        double [][] segmentPhaseSpectra = new double [segments][windowSize/2];
        double window [] = new double[windowSize];
        double totalInputData [] = new double [inputData.length];
        System.arraycopy(inputData, 0, totalInputData, 0, inputData.length);
        int startPointForCopying = 0;
        for(int i = 0; i < segments; i++){
            System.arraycopy(totalInputData, startPointForCopying, window, 0, windowSize);
            this.inputData = window;
            segmentPhaseSpectra [i] = computePhaseSpectrum();
            if (i == 0){
                startPointForCopying += this.windowSize - 1 - overlap;
            }else{
                startPointForCopying += this.windowSize - overlap;
            }
        }
        for (int i = 0; i < windowSize/2; i++){
            double [] phasesAtCurrentFrequency = new double [segments];
            for (int j = 0; j < segments; j++){
                phasesAtCurrentFrequency [j] = segmentPhaseSpectra[j][i];
            }
            averagedPhaseSpectrum[i] = (new Average(phasesAtCurrentFrequency)).getAverage();
        }
        this.inputData = totalInputData;
        return averagedPhaseSpectrum;
    }
     
}
