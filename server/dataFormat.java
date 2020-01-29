import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class dataFormat {

	// defining Data pack to write into database

	private String time;
	private int sensor;
	private String accel_x;
	private String accel_y;
	private String accel_z;
	private String peak_x;
	private String peak_y;
	private String peak_z;
	private int index;
	SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");

	DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
	public static char decimalPoint = '.'; //The separator, typically "," or "\t"



	public dataFormat(double[] data, boolean Acc) {

		format.applyPattern("0.000000000E0");
		DecimalFormatSymbols dfs = format.getDecimalFormatSymbols();
		dfs.setDecimalSeparator(decimalPoint);
		format.setDecimalFormatSymbols(dfs);
		format.setGroupingUsed(false);

		if (Acc) {
			this.sensor = (int)data[0];
			this.time = sdf.format(new Date((long)data[1]));
			this.accel_x = format.format(data[2]);
			this.accel_y = format.format(data[3]);
			this.accel_z = format.format(data[4]);
		}
		else {
			this.index = (int)data[0];
			this.sensor = (int)data[1];
			this.peak_x = format.format(data[2]);
			this.peak_y = format.format(data[3]);
			this.peak_z = format.format(data[4]);
		}
	}

	public String[] getAcceleration() {
		String[] accelerations = new String[3];
		accelerations[0] = this.accel_x;
		accelerations[1] = this.accel_y;
		accelerations[2] = this.accel_z;
		return accelerations;
	}

	public String[] getPeak() {
		String[] peaks = new String[3];
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
