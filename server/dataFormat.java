

public class dataFormat {
	
	// defining Data pack to write into database
	
	private double time;
	private int sensor;
	private double accel_x;
	private double accel_y;
	private double accel_z;

	public dataFormat(double[] data) {
		this.sensor = (int)data[0];
		this.time = data[1];
		this.accel_x = data[2];
		this.accel_y = data[3];
		this.accel_z = data[4];
	}

	public double[] getAcceleration() {
		double[] accelerations = new double[3];
		accelerations[0] = this.accel_x;
		accelerations[1] = this.accel_y;
		accelerations[2] = this.accel_z;
		return accelerations;
	}

	public double getTime() {
		return this.time;
	}
	
	public double getSensor() {
		return this.sensor;
	}

}
