import fourier.PeakPicking;
import fourier.FrequencySpectrum;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PhyphoxData {
	// File write format
	public static String separator = "\t"; //The separator, typically "," or "\t"
	public static char decimalPoint = '.'; //The separator, typically "," or "\t"

	public PhyphoxData (String data_name, int numberOfPeaks, DBAccess dbAccess, int Phone_index) throws InterruptedException, IOException {

		DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
		format.applyPattern("0.000000000E0");
		DecimalFormatSymbols dfs = format.getDecimalFormatSymbols();
		dfs.setDecimalSeparator(decimalPoint);
		format.setDecimalFormatSymbols(dfs);
		format.setGroupingUsed(false);

		String rawDataPath = "./Results/";
		String writtenPath = "./Results/Java_Phyphox/";

		String filename = rawDataPath + data_name + ".csv";
		DataProcessFormat d = new DataProcessFormat(filename);

		double[][] data = d.getData();
		int aux = data[0].length;

		long[] timeinter = new long[aux];
		double[] x_accelerations_raw = new double[aux];    
		double[] y_accelerations_raw = new double[aux];
		double[] z_accelerations_raw = new double[aux];

		int secondsMeasuring = 30;
		int samplingRate = 100;
		int lengthOfDataset		= samplingRate * secondsMeasuring;

		for(int i = 0; i < aux; i ++) {
			timeinter[i] = (long)data[0][i];
		}

		x_accelerations_raw = data[1];
		y_accelerations_raw = data[2];
		z_accelerations_raw = data[3];

		// Re-sampling data to a defined sampling frequency
		samplingData samplingacc = new samplingData();
		double[] x_accelerations = samplingacc.getSamplingData(x_accelerations_raw, timeinter, samplingRate, aux, lengthOfDataset);     	
		double[] y_accelerations = samplingacc.getSamplingData(y_accelerations_raw, timeinter, samplingRate, aux, lengthOfDataset);     	
		double[] z_accelerations = samplingacc.getSamplingData(z_accelerations_raw, timeinter, samplingRate, aux, lengthOfDataset);


		baseLineCorrection base=new baseLineCorrection();
		double[] x_accelerations_bl = base.getBaseLineCorrection(x_accelerations);
		double[] y_accelerations_bl = base.getBaseLineCorrection(y_accelerations);
		double[] z_accelerations_bl = base.getBaseLineCorrection(z_accelerations);
		
		// Create Database for acceleration of Phone data
		for(int k = 0; k < lengthOfDataset; k++){
			double[] datasetData = {Phone_index, (0.01*k), x_accelerations_bl[k], y_accelerations_bl[k], z_accelerations_bl[k]};
			dataFormat dataSet = new dataFormat(datasetData,true);
			// write incoming data into database using DBAccess class
			dbAccess.insertPhyphoxData(dataSet);
		}


		// Extending length of vector to a power of 2 (due to FFT)
		powerOf2Extension extendedVector = new powerOf2Extension();
		double [] x_accelerations_ext = extendedVector.nextPow2vector(x_accelerations_bl);
		double [] y_accelerations_ext = extendedVector.nextPow2vector(y_accelerations_bl);
		double [] z_accelerations_ext = extendedVector.nextPow2vector(z_accelerations_bl);

		// Calculating the frequency spectrum of the stored data
		double deltaT = 1/(double)samplingRate;
		FrequencySpectrum 	fSpec_x = new FrequencySpectrum(x_accelerations_ext, deltaT);      
		FrequencySpectrum 	fSpec_y = new FrequencySpectrum(y_accelerations_ext, deltaT);      
		FrequencySpectrum 	fSpec_z = new FrequencySpectrum(z_accelerations_ext, deltaT);      

		// Performing the Peak picking of the frequency spectrum
		PeakPicking pp_x = new PeakPicking(numberOfPeaks, fSpec_x);
		PeakPicking pp_y = new PeakPicking(numberOfPeaks, fSpec_y);
		PeakPicking pp_z = new PeakPicking(numberOfPeaks, fSpec_z);

		int [] 	detectedPeaks_x = pp_x.getPeaks();
		int [] 	detectedPeaks_y = pp_y.getPeaks();
		int [] 	detectedPeaks_z = pp_z.getPeaks();

		double[] freqs_x = new double[detectedPeaks_x.length];
		double[] freqs_y = new double[detectedPeaks_y.length];
		double[] freqs_z = new double[detectedPeaks_z.length];

		double[] amplitudes_x = new double[detectedPeaks_x.length];
		double[] amplitudes_y = new double[detectedPeaks_y.length];
		double[] amplitudes_z = new double[detectedPeaks_z.length];

		for(int i=0; i < detectedPeaks_x.length; i++) {
			freqs_x[i] = fSpec_x.getFrequencies()[detectedPeaks_x[i]];
			freqs_y[i] = fSpec_y.getFrequencies()[detectedPeaks_y[i]];
			freqs_z[i] = fSpec_z.getFrequencies()[detectedPeaks_z[i]];

			amplitudes_x[i] = fSpec_x.getAmplitudeSpectrum()[detectedPeaks_x[i]];
			amplitudes_y[i] = fSpec_x.getAmplitudeSpectrum()[detectedPeaks_x[i]];
			amplitudes_z[i] = fSpec_x.getAmplitudeSpectrum()[detectedPeaks_x[i]];
		}
		
		for(int i = 0; i < numberOfPeaks; i++){
			double[] data1 = {i+1, Phone_index, freqs_x[i], freqs_y[i], freqs_z[i]};
        	dataFormat dataSet1 = new dataFormat(data1,false);
        	dbAccess.insertPhyphoxPeak(dataSet1);
		}
		
		System.out.println(data_name + ":");
		for(int i=0; i < detectedPeaks_x.length; i++) {
			System.out.println("Peak Frequency " + i + ":" + format.format(freqs_x[i]) + " Amplitude: " + format.format(amplitudes_x[i]));
		}

		// Storing data into a file in ./Results/
		//----------------------------------------------------------------------------------------------
		FileWriter writer = new FileWriter(writtenPath + data_name + "_rewrite" + ".csv");
		writer.write("Time (s)" + "\t");
		writer.write("Acceleration x (m/s^2)" + separator);
		writer.write("Acceleration y (m/s^2)" + separator);
		writer.write("Acceleration z (m/s^2)");
		writer.write("\n");

		for(int k = 0; k < lengthOfDataset; k++){
			writer.write( (0.01*k) + separator);
			writer.write(format.format(x_accelerations_bl[k])  + separator);
			writer.write(format.format(y_accelerations_bl[k])  + separator);
			writer.write(format.format(z_accelerations_bl[k]) + "\n");
			
		}
		writer.write("\n \n \n");


		writer.flush();
		writer.close();
	}

}
