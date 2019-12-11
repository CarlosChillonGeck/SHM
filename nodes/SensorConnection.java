
import java.io.IOException;

import daq.ADXL345;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class SensorConnection {
	
	

	private ADXL345 sensor;
	float scalingFactor;
    short[] raw = new short[3];
	

	/**
	 * Initiating bus connection to sensor
	 * 
	 * @param bus  use 0 for BUS_1 and 1 for BUS_2
	 * @throws IOException
	 * @throws UnsupportedBusNumberException
	 */
	public SensorConnection(int bus) throws IOException, UnsupportedBusNumberException {
		
		if (bus == 0) {
			
			 sensor = new ADXL345(I2CBus.BUS_1, ADXL345.ADXL345_ADDRESS_ALT_LOW);
		        sensor.setup();
		        if (!sensor.verifyDeviceID()) 
		        {
		            throw new IOException("Failed to verify ADXL345 BUS 1 device ID (53)");
		        }
		        
		        sensor.writeRange(ADXL345.ADXL345_RANGE_2G);
		        sensor.writeFullResolution(true);
		        sensor.writeRate(ADXL345.ADXL345_RATE_100);
		        scalingFactor = sensor.getScalingFactor();
				        
		} 
		
		if (bus == 1) {
			
			//TODO implementation for BUS_2
			ADXL345 sensor = new ADXL345(I2CBus.BUS_1, ADXL345.ADXL345_ADDRESS_ALT_HIGH);
			
			if (!sensor.verifyDeviceID()) {
	            throw new IOException("Failed to verify ADXL345 BUS 1device ID (1d)");
	            
	        }
			
			
			sensor.writeRange(ADXL345.ADXL345_RANGE_4G);
	        sensor.writeFullResolution(true);
	        sensor.writeRate(ADXL345.ADXL345_RATE_100);
	        scalingFactor = sensor.getScalingFactor();
	        
	        
	        
			
		}
       
		
	}

	/**
	 * reading Acceleration-Data in g [ 9.81 m*s^-1 ]
	 * 
	 * 
	 * @return a double array: x stored at [0] , y stored at[1] , z = stored at[2]
	 * @throws IOException
	 */
	public double[] readAcceleration() throws IOException {
		
		sensor.readRawAcceleration(raw);
		double acc[] = {
				(double) raw[0] * scalingFactor,
				(double) raw[0] * scalingFactor,
				(double) raw[0] * scalingFactor,
		};
		return acc;
	}
	
	
	
	

}
