import java.io.FileWriter;
import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import daq.ADXL345;

/* Class defined for sampling data from the accelerometers as fast as possible and then  
 * re-sampling the data according to a given sampling frequency
 * 
 * Remark: Here, the maximum amplitude of the accelerations should be defined and the maximum sampling from the nodes
 * see lines: 33 and 39!
 * 
 * Created by Stalin Ibanez 
 * last version: 2019-07-10
 */
public class nodesDataCollection {

	double[][] getDataCollection(int seconds, int samplingFrequency, int direction) throws IOException, UnsupportedBusNumberException{         
		
		// Input variables
		int secondsMeasuring = seconds;
		int samplingRate = samplingFrequency;
        int axis = direction; // 0, 1, or 2
        
        // Configuration of accelerometers
        ADXL345 adxl345 = new ADXL345(I2CBus.BUS_1, ADXL345.ADXL345_ADDRESS_ALT_LOW); //Sensor 1
        ADXL345 adxl345_1d = new ADXL345(I2CBus.BUS_1, ADXL345.ADXL345_ADDRESS_ALT_HIGH); //Sensor 2 with extra cable 
        adxl345.setup();
        adxl345_1d.setup();
        
        // Step 2: Defining the maximum amplitude of the acceleration sensor 1: 2G, 4G, 8G        
        adxl345.writeRange(ADXL345.ADXL345_RANGE_8G);
        adxl345_1d.writeRange(ADXL345.ADXL345_RANGE_8G);
        adxl345.writeFullResolution(true);
        adxl345_1d.writeFullResolution(true);
        
        // step 3: Define here the maximum sampling rate possible by the sensors
        adxl345.writeRate(ADXL345.ADXL345_RATE_1600);
        adxl345_1d.writeRate(ADXL345.ADXL345_RATE_1600);

        float scalingFactor = adxl345.getScalingFactor();
        int factorMeasuringpoints = 400; //variable related with the size of the vector when sampling as fast as possible (empirical)
        int lengthOfDatasetRaw	= factorMeasuringpoints * secondsMeasuring;
        int lengthOfDataset		= samplingRate * secondsMeasuring;
        
        short[] raw = new short[3]; //Each value of the vector represent one direction of the acceleration
        short[] raw_1d = new short[3];
        double[] x_accelerations_s1_raw = new double[lengthOfDatasetRaw];
        double[] x_accelerations_s2_raw = new double[lengthOfDatasetRaw];
        double[] y_accelerations_s1_raw = new double[lengthOfDatasetRaw];
        double[] y_accelerations_s2_raw = new double[lengthOfDatasetRaw];
        double[] z_accelerations_s1_raw = new double[lengthOfDatasetRaw];
        double[] z_accelerations_s2_raw = new double[lengthOfDatasetRaw];

        long[] timeinter = new long[lengthOfDatasetRaw];
        double timestep = (1000/(double)samplingRate);

        double[] x_accelerations_s1 = new double[lengthOfDataset];
        double[] x_accelerations_s2 = new double[lengthOfDataset];
        double[] y_accelerations_s1 = new double[lengthOfDataset];
        double[] y_accelerations_s2 = new double[lengthOfDataset];
        double[] z_accelerations_s1 = new double[lengthOfDataset];
        double[] z_accelerations_s2 = new double[lengthOfDataset];
        double[][] accelerations = new double[7][lengthOfDataset];
        
        // sampling as fast as possible
        int aux = 0;        
        long t0 = System.nanoTime();
        while ( ((System.nanoTime()-t0)/1000000) < secondsMeasuring*1000) {
        	adxl345.readRawAcceleration(raw);
        	adxl345_1d.readRawAcceleration(raw_1d);
        	x_accelerations_s1_raw[aux] = (double) raw[0]*scalingFactor;
        	x_accelerations_s2_raw[aux] = (double) raw_1d[0]*scalingFactor;
        	y_accelerations_s1_raw[aux] = (double) raw[1]*scalingFactor;
        	y_accelerations_s2_raw[aux] = (double) raw_1d[1]*scalingFactor;
        	z_accelerations_s1_raw[aux] = (double) raw[2]*scalingFactor;
        	z_accelerations_s2_raw[aux] = (double) raw_1d[2]*scalingFactor;
        	timeinter[aux] = System.nanoTime(); 
        	aux = aux+1;
 		}
        
        // Storing Data in RPI - not necessary in real implementations
        long date=System.currentTimeMillis();
        FileWriter writer = new FileWriter("/home/pi/Desktop/Data/"+"RawAcc" + Long.toString(date) + ".txt");
	    for(int k = 0; k < aux; k++){
	    	writer.write(timeinter[k]+ "\t" + x_accelerations_s1_raw[k] + "\t" +x_accelerations_s2_raw[k]+ "\t" + y_accelerations_s1_raw[k] + "\t" +y_accelerations_s2_raw[k]+ "\t" + z_accelerations_s1_raw[k] + "\t" +z_accelerations_s2_raw[k] + "\n");
	    }
	    writer.flush();
      	writer.close();
      	
      	for (int i = 0; i < lengthOfDataset; i++) {
      		accelerations[0][i] = timestep * i;
      	}
      	
      	// Re-sampling data to a defined sampling frequency
      	samplingData samplingacc = new samplingData();
      	x_accelerations_s1 = samplingacc.getSamplingData(x_accelerations_s1_raw, timeinter, samplingRate, aux, lengthOfDataset);
      	x_accelerations_s2 = samplingacc.getSamplingData(x_accelerations_s2_raw, timeinter, samplingRate, aux, lengthOfDataset);
      	y_accelerations_s1 = samplingacc.getSamplingData(y_accelerations_s1_raw, timeinter, samplingRate, aux, lengthOfDataset);
      	y_accelerations_s2 = samplingacc.getSamplingData(y_accelerations_s2_raw, timeinter, samplingRate, aux, lengthOfDataset);
      	z_accelerations_s1 = samplingacc.getSamplingData(z_accelerations_s1_raw, timeinter, samplingRate, aux, lengthOfDataset);
      	z_accelerations_s2 = samplingacc.getSamplingData(z_accelerations_s2_raw, timeinter, samplingRate, aux, lengthOfDataset);

      	// Storing data into a matrix
      	accelerations[1]=x_accelerations_s1;
      	accelerations[2]=x_accelerations_s2;
      	accelerations[3]=y_accelerations_s1;
      	accelerations[4]=y_accelerations_s2;
      	accelerations[5]=z_accelerations_s1;
      	accelerations[6]=z_accelerations_s2;
      	
		return accelerations;
		}	
}
