import java.text.SimpleDateFormat;
import java.util.Date;

public class dataFormat {

	// defining Data pack to write into database

	private String time;
	private int sensor;
	private double accel_x;
	private double accel_y;
	private double accel_z;
	private double peak_x;
	private double peak_y;
	private double peak_z;
	private int index;
	SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");    


	public dataFormat(double[] data, boolean Acc) {
		if (Acc) {
			this.sensor = (int)data[0];
			this.time = sdf.format(new Date((long)data[1]));
			this.accel_x = data[2];
			this.accel_y = data[3];
			this.accel_z = data[4];
		}
		else {
			this.index = (int)data[0];
			this.sensor = (int)data[1];
			this.peak_x = data[2];
			this.peak_y = data[3];
			this.peak_z = data[4];
		}
	}

	public double[] getAcceleration() {
		double[] accelerations = new double[3];
		accelerations[0] = this.accel_x;
		accelerations[1] = this.accel_y;
		accelerations[2] = this.accel_z;
		return accelerations;
	}
	
	public double[] getPeak() {
		double[] peaks = new double[3];
		peaks[0] = this.peak_x;
		peaks[1] = this.peak_y;
		peaks[2] = this.peak_z;
		return peaks;
	}
	
	public String getTime() {
		return this.time;
	}

	public double getSensor() {
		return this.sensor;
	}
	
	public int getIndex() {
		return this.index;
	}

}
