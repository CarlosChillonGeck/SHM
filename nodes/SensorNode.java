import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;

import com.pi4j.io.i2c.I2CFactory;
import fourier.PeakPicking;
import fourier.FrequencySpectrum;

/* This part of the code must be copied to an USB-drive that will be plugged into the nodes
The step for running the software properly are written along the code
see lines: 24, 31 (more definitions are inside class nodesDataCollection)
*/
public class SensorNode {
	static int 		samplingRate;     	// [Hz]
	static int 		lengthOfDatasetRaw; // [n]
	static int 		lengthOfDataset; 	// [n]
	static int 		numberOfPeaks;		// [n]
	static int 		secondsMeasuring;	// [s]
	static int 		direction;			// [s]
	static double 	acceleration[];		// [x-acceleration, y-acceleration, z-acceleration][in g]		
	
	public static String serverIP = "192.168.43.12"; 
	/* Step 1: Define here the IP address of the server (Laptop)
	in windows: windows key + R -> cmd -> write ipconfig -> copy IPv4 in the serverIP value
	*/
	
    public static void main(String[] args) throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException, ClassNotFoundException {
    	// Initiating communication to the server:
    	Socket s = new Socket(serverIP,1234);
    	/* Step 2: Define here the socket. In case of one node: 1234;
    	In case of multiple nodes: 1234, 1235, 1236, and so on
    	One value for each node
    	*/
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        DataInputStream in = new DataInputStream(s.getInputStream());        
        
        //Receiving setup parameters
        samplingRate 		= in.readInt();
        numberOfPeaks		= in.readInt();
        secondsMeasuring	= in.readInt();
        direction			= in.readInt();
        long waitingTime 	= in.readLong();
        
        // for coordinating measurements
        Thread.sleep(waitingTime);
        
        // Definition of variables
        int lengthOfDataset		= samplingRate * secondsMeasuring; //Length of the result vector
        double[] x_accelerations_s1 = new double[lengthOfDataset]; // Sampled Raw acceleration S1
        double[] x_accelerations_s2 = new double[lengthOfDataset]; // Sampled Raw acceleration S2
        double[] y_accelerations_s1 = new double[lengthOfDataset]; // Sampled Raw acceleration S1
        double[] y_accelerations_s2 = new double[lengthOfDataset]; // Sampled Raw acceleration S2
        double[] z_accelerations_s1 = new double[lengthOfDataset]; // Sampled Raw acceleration S1
        double[] z_accelerations_s2 = new double[lengthOfDataset]; // Sampled Raw acceleration S2
        double[][] accelerations = new double[6][lengthOfDataset]; // Sampled raw acceleration S1 & S2
        double[] x_accelerations_s1_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S1
        double[] x_accelerations_s2_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S2
        double[] y_accelerations_s1_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S1
        double[] y_accelerations_s2_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S2
        double[] z_accelerations_s1_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S1
        double[] z_accelerations_s2_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S2
        
        // Collecting data from sensors (raw collected data will be stored in the RPI, folder ./Data/)
        nodesDataCollection getAccData = new nodesDataCollection();
        accelerations = getAccData.getDataCollection(secondsMeasuring, samplingRate, direction);
        x_accelerations_s1 = accelerations[0];
        x_accelerations_s2 = accelerations[1];
        y_accelerations_s1 = accelerations[0];
        y_accelerations_s2 = accelerations[1];
        z_accelerations_s1 = accelerations[0];
        z_accelerations_s2 = accelerations[1];

        // Removing offset by subtracting the mean value, i.e. baseline correction
    	baseLineCorrection base=new baseLineCorrection();
    	x_accelerations_s1_bl = base.getBaseLineCorrection(x_accelerations_s1);
    	x_accelerations_s2_bl = base.getBaseLineCorrection(x_accelerations_s2);
    	y_accelerations_s1_bl = base.getBaseLineCorrection(y_accelerations_s1);
    	y_accelerations_s2_bl = base.getBaseLineCorrection(y_accelerations_s2);
    	z_accelerations_s1_bl = base.getBaseLineCorrection(z_accelerations_s1);
    	z_accelerations_s2_bl = base.getBaseLineCorrection(z_accelerations_s2);
        
    	// Extending length of vector to a power of 2 (due to FFT)
    	powerOf2Extension extendedVector = new powerOf2Extension();
    	double [] x_accelerations_s1_bl_ext = extendedVector.nextPow2vector(x_accelerations_s1_bl);
    	double [] x_accelerations_s2_bl_ext = extendedVector.nextPow2vector(x_accelerations_s2_bl);
    	
    	// Saving raw data without filtering and sampling (for comparison reasons)
    	long date=System.currentTimeMillis();
        FileWriter writer = new FileWriter("/home/pi/Desktop/Data/"+"FFTData" + Long.toString(date) + ".txt");
	    for(int k = 0; k < x_accelerations_s1_bl_ext.length; k++){
	    	writer.write(x_accelerations_s1_bl_ext[k] + "\t" +x_accelerations_s2_bl_ext[k] + "\n");
	    }
	    writer.flush();
      	writer.close();
      	// end writing
    	
        // Calculating the frequency spectrum of the stored data
    	double deltaT = 1/(double)samplingRate;
        FrequencySpectrum 	fSpec = new FrequencySpectrum(x_accelerations_s1_bl_ext, deltaT);      
        FrequencySpectrum 	fSpec2 = new FrequencySpectrum(x_accelerations_s2_bl_ext, deltaT);
        // Performing the Peak picking of the frequency spectrum
        PeakPicking 		pp 	  = new PeakPicking(numberOfPeaks, fSpec);
        PeakPicking 		pp2 	  = new PeakPicking(numberOfPeaks, fSpec2);
        int [] 				detectedPeaks = pp.getPeaks();
        int [] 				detectedPeaks2 = pp2.getPeaks();
        
        // Extracting and saving frequencies and amplitudes (comparison reasons)
        double [] freqs = fSpec.getFrequencies();
        double [] amplitudes = fSpec.getAmplitudeSpectrum();
    	date=System.currentTimeMillis();
        FileWriter writer2 = new FileWriter("/home/pi/Desktop/Data/"+"AmplitudesAndFreq" + Long.toString(date) + ".txt");
	    for(int k = 0; k < amplitudes.length; k++){
	    	writer2.write(freqs[k] + "\t" +amplitudes[k] + "\n");
	    }
	    for(int k = 0; k < detectedPeaks.length; k++){
	    	writer2.write(detectedPeaks[k] + "\t" +detectedPeaks2[k] + "\n");
	    }
	    writer2.flush();
      	writer2.close();
      	// end writing

        // transmitting acceleration-data to the server
    	for (int i = 0; i < lengthOfDataset; i++) {
    		//System.out.println("sending" + i + x_accelerations_s1_bl[i]);
    		out.writeDouble(x_accelerations_s1_bl[i]);	
    		out.writeDouble(y_accelerations_s1_bl[i]);
    		out.writeDouble(z_accelerations_s1_bl[i]);	
    		out.writeDouble(x_accelerations_s2_bl[i]);
    		out.writeDouble(y_accelerations_s2_bl[i]);	
    		out.writeDouble(z_accelerations_s2_bl[i]);
    		out.flush();
		}
    	    	
    	//transmitting the detected frequencies to the server
    	for (int i = 0; i < detectedPeaks.length; i++) {
        	double freq1 = (double)detectedPeaks[i]*((double)samplingRate/2)/((double)x_accelerations_s1_bl_ext.length/2);
        	double freq2 = (double)detectedPeaks2[i]*((double)samplingRate/2)/((double)x_accelerations_s2_bl_ext.length/2);
        	System.out.println(freq1+"\t" + freq2);   
    		out.writeDouble(freq1);
    		out.writeDouble(freq2);
    		out.flush();
    	}
    	
    	// closing communication
    	s.close();
    	out.close();
    	in.close();    	
    }
}