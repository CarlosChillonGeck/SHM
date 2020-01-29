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
	
	public static String serverIP = "192.168.61.112"; 
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
        long[] timeStamp = new long[lengthOfDataset]; // Time Stamp
        double[] x_accelerations_s1_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S1
        double[] x_accelerations_s2_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S2
        double[] y_accelerations_s1_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S1
        double[] y_accelerations_s2_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S2
        double[] z_accelerations_s1_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S1
        double[] z_accelerations_s2_bl = new double[lengthOfDataset]; // Sampled corrected acceleration S2
        
        long timestep = (long)(1000/samplingRate);
        
        // Collecting data from sensors (raw collected data will be stored in the RPI, folder ./Data/)
        nodesDataCollection getAccData = new nodesDataCollection();
        long t0 = getAccData.getDataCollection(accelerations, secondsMeasuring, samplingRate, direction);
        x_accelerations_s1 = accelerations[0];
        x_accelerations_s2 = accelerations[1];
        y_accelerations_s1 = accelerations[2];
        y_accelerations_s2 = accelerations[3];
        z_accelerations_s1 = accelerations[4];
        z_accelerations_s2 = accelerations[5];

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
    	double [] y_accelerations_s1_bl_ext = extendedVector.nextPow2vector(y_accelerations_s1_bl);
    	double [] y_accelerations_s2_bl_ext = extendedVector.nextPow2vector(y_accelerations_s2_bl);
    	double [] z_accelerations_s1_bl_ext = extendedVector.nextPow2vector(z_accelerations_s1_bl);
    	double [] z_accelerations_s2_bl_ext = extendedVector.nextPow2vector(z_accelerations_s2_bl);
    	
        // Calculating the frequency spectrum of the stored data
    	double deltaT = 1/(double)samplingRate;
        FrequencySpectrum 	fSpec_x = new FrequencySpectrum(x_accelerations_s1_bl_ext, deltaT);      
        FrequencySpectrum 	fSpec_x2 = new FrequencySpectrum(x_accelerations_s2_bl_ext, deltaT);
        FrequencySpectrum 	fSpec_y = new FrequencySpectrum(y_accelerations_s1_bl_ext, deltaT);      
        FrequencySpectrum 	fSpec_y2 = new FrequencySpectrum(y_accelerations_s2_bl_ext, deltaT);
        FrequencySpectrum 	fSpec_z = new FrequencySpectrum(z_accelerations_s1_bl_ext, deltaT);      
        FrequencySpectrum 	fSpec_z2 = new FrequencySpectrum(z_accelerations_s2_bl_ext, deltaT);
        // Performing the Peak picking of the frequency spectrum
        PeakPicking 		pp_x 	  = new PeakPicking(numberOfPeaks, fSpec_x);
        PeakPicking 		pp_x2 	  = new PeakPicking(numberOfPeaks, fSpec_x2);
        PeakPicking 		pp_y 	  = new PeakPicking(numberOfPeaks, fSpec_y);
        PeakPicking 		pp_y2 	  = new PeakPicking(numberOfPeaks, fSpec_y2);
        PeakPicking 		pp_z 	  = new PeakPicking(numberOfPeaks, fSpec_z);
        PeakPicking 		pp_z2 	  = new PeakPicking(numberOfPeaks, fSpec_y2);
        int [] 				detectedPeaks_x = pp_x.getPeaks();
        int [] 				detectedPeaks_x2 = pp_x2.getPeaks();
        int [] 				detectedPeaks_y = pp_y.getPeaks();
        int [] 				detectedPeaks_y2 = pp_y2.getPeaks();
        int [] 				detectedPeaks_z = pp_z.getPeaks();
        int [] 				detectedPeaks_z2 = pp_z2.getPeaks();
        
//        // Extracting and saving frequencies and amplitudes (comparison reasons)
//        double [] freqs_x = fSpec_x.getFrequencies();
//        double [] amplitudes_x = fSpec_x.getAmplitudeSpectrum();
//        double [] freqs_y = fSpec_y.getFrequencies();
//        double [] amplitudes_y = fSpec_y.getAmplitudeSpectrum();
//        double [] freqs_z = fSpec_z.getFrequencies();
//        double [] amplitudes_z = fSpec_z.getAmplitudeSpectrum();
        
        long date=System.currentTimeMillis();
    	date=System.currentTimeMillis();
//        FileWriter writer2 = new FileWriter("/home/pi/Desktop/Data/"+"AmplitudesAndFreq" + Long.toString(date) + ".txt");
//        writer2.write("Frequency x"+ "\t" + "Amplitude x" + "\t" + "Frequency y" + "\t" + "Amplitude y" + "\t" + "Frequency z"+ "\t" + "Amplitude z" + "\n");
//        for(int k = 0; k < amplitudes_x.length; k++){
//	    	writer2.write(freqs_x[k] + "\t" +amplitudes_x[k] + "\t" + freqs_y[k] + "\t" +amplitudes_y[k] + "\t" + freqs_z[k] + "\t" +amplitudes_z[k] + "\n");
//	    }
//        writer2.write("Detected Peak x S1"+ "\t" + "Detected Peak x S2" + "\t" + "Detected Peak y S1"+ "\t" + "Detected Peak y S2" + "\n" + "Detected Peak z S1"+ "\t" + "Detected Peak z S2" + "\n");
//	    for(int k = 0; k < detectedPeaks_x.length; k++){
//	    	writer2.write(detectedPeaks_x[k] + "\t" +detectedPeaks_x2[k] + "\t" + detectedPeaks_y[k] + "\t" +detectedPeaks_y2[k] + "\t" + detectedPeaks_z[k] + "\t" +detectedPeaks_z2[k] + "\n");
//	    }
//	    writer2.flush();
//	    writer2.close();
////      // end writing
    	
    	// Create TimeStamp
    	for (int i = 0; i < lengthOfDataset; i++) {
    		timeStamp[i] = t0 + (timestep * i);
    	}
    	
        // transmitting acceleration-data to the server
    	for (int i = 0; i < lengthOfDataset; i++) {
    		out.writeLong(timeStamp[i]);
    		out.writeDouble(x_accelerations_s1_bl[i]);	
    		out.writeDouble(y_accelerations_s1_bl[i]);
    		out.writeDouble(z_accelerations_s1_bl[i]);	
    		out.writeDouble(x_accelerations_s2_bl[i]);
    		out.writeDouble(y_accelerations_s2_bl[i]);	
    		out.writeDouble(z_accelerations_s2_bl[i]);
    		out.flush();
		}
    	    	
//    	//transmitting the detected frequencies to the server
    	for (int i = 0; i < detectedPeaks_x.length; i++) {
        	double freq_x1 = (double)detectedPeaks_x[i]*((double)samplingRate/2)/((double)x_accelerations_s1_bl_ext.length/2);
        	double freq_x2 = (double)detectedPeaks_x2[i]*((double)samplingRate/2)/((double)x_accelerations_s2_bl_ext.length/2);  
        	double freq_y1 = (double)detectedPeaks_y[i]*((double)samplingRate/2)/((double)y_accelerations_s1_bl_ext.length/2);
        	double freq_y2 = (double)detectedPeaks_y2[i]*((double)samplingRate/2)/((double)y_accelerations_s2_bl_ext.length/2);  
        	double freq_z1 = (double)detectedPeaks_z[i]*((double)samplingRate/2)/((double)z_accelerations_s1_bl_ext.length/2);
        	double freq_z2 = (double)detectedPeaks_z2[i]*((double)samplingRate/2)/((double)z_accelerations_s2_bl_ext.length/2);  
        	out.writeDouble(freq_x1);
    		out.writeDouble(freq_x2);
    		out.writeDouble(freq_y1);
    		out.writeDouble(freq_y2);
    		out.writeDouble(freq_z1);
    		out.writeDouble(freq_z2);
    		out.flush();
    	}
    	
    	// closing communication
    	s.close();
    	out.close();
    	in.close();    	
    }
}