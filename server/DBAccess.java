import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DBAccess {
	// attributes to define connection parameters to the SQL server
		private String db_url = "";
		private String driver = "";
		private Connection connection = null;
		private Statement statement = null;
		long date=System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY_MM_DD_HH_mm");  
		private String Acc_databaseName = "_Acc_" + sdf.format(new Date((long)date));
		private String Peak_databaseName = "_Peak_" + sdf.format(new Date((long)date));
		private String Phyphox_Acc_databaseName = "_Phone_Acc_" + sdf.format(new Date((long)date));
		private String Phyphox_Peak_databaseName = "_Phone_Peak_" + sdf.format(new Date((long)date));

		// constructor
		// needs information about database URL and specific driver
		// driver is loaded instantly after initiate the DBAccess-object
		public DBAccess(String db_url, String driver, String condition) {
			this.db_url = db_url;
			this.driver = driver;
			loadDriver();
			try
			{
				connection = DriverManager.getConnection(db_url, "root", "");
				statement = connection.createStatement();
				
				Acc_databaseName = condition + Acc_databaseName;
				Peak_databaseName = condition + Peak_databaseName;
				Phyphox_Acc_databaseName = condition + Phyphox_Acc_databaseName;
				Phyphox_Peak_databaseName = condition + Phyphox_Peak_databaseName;
				// Create new table
			    String sql_acc = "CREATE TABLE " +
			    			Acc_databaseName +
		                   " (Sensor int, " + 
		                   " Time TIMESTAMP(3), " + 
		                   " Acc_x varchar(15), " +
		                   " Acc_y varchar(15), " + 
		                   " Acc_z varchar(15))";
			    
			 // Create new table
			    String sql_peak = "CREATE TABLE " +
			    			Peak_databaseName +
		                   " (idx INTEGER not NULL," +
		                   " Sensor int, " + 
		                   " Peak_x varchar(15), " +
		                   " Peak_y varchar(15), " + 
		                   " Peak_z varchar(15))";
			    
			 // Create new table
			    String sql_phyphox_acc = "CREATE TABLE " +
			    			Phyphox_Acc_databaseName +
		                   " (Phone int, " + 
		                   " Time int, " + 
		                   " Acc_x varchar(15), " +
		                   " Acc_y varchar(15), " + 
		                   " Acc_z varchar(15))";
			    
			 // Create new table
			    String sql_phyphox_peak = "CREATE TABLE " +
			    			Phyphox_Peak_databaseName +
		                   " (idx INTEGER not NULL," +
		                   " Phone int, " + 
		                   " Peak_x varchar(15), " +
		                   " Peak_y varchar(15), " + 
		                   " Peak_z varchar(15))";
			    
			    statement.executeUpdate(sql_acc);
			    statement.executeUpdate(sql_peak);
			    statement.executeUpdate(sql_phyphox_acc);
			    statement.executeUpdate(sql_phyphox_peak);
			    
			    System.out.println("Database: " + Acc_databaseName + " created");
			    System.out.println("Database: " + Peak_databaseName + " created");
			    System.out.println("Database: " + Phyphox_Acc_databaseName + " created");
			    System.out.println("Database: " + Phyphox_Peak_databaseName + " created");
			    
				
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		// driver load
		private void loadDriver() {

			try {
				// driver load
				Class.forName(driver).newInstance();

			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			} catch (InstantiationException ie) {
				ie.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			}
			// catching the failures
		}
		
		// write data into database
			public void clear() {

				try {
					// building up connection
					// here without password

					// change operation: create statement
					statement = connection.createStatement();

					// act operation:
					// data-sets are wrote into database

					statement.executeUpdate("DELETE FROM " + Acc_databaseName);

					// close connection

				} catch (SQLException e) {
					e.printStackTrace();
				}
				// catch failure
			}

		// write data into database
		public void insertData(dataFormat dataSet) {

			try {
				// building up connection
				// here without password
			

				// act operation:
				// data-sets are wrote into database
				String[] accelerations = dataSet.getAcceleration();
				String accel_x = accelerations[0];
				String accel_y = accelerations[1];
				String accel_z = accelerations[2];
				
				statement.executeUpdate("INSERT INTO " + Acc_databaseName + " VALUES (" + dataSet.getSensor()+ ", '" + dataSet.getTime() + "', " + accel_x + ", " + accel_y + ", " + accel_z + ")");

				// close connection

			} catch (SQLException e) {
				e.printStackTrace();
			}
			// catch failure
		}
		
		// write data into database
		public void insertPeak(dataFormat dataSet) {

			try {
				// building up connection
				// here without password
			

				// act operation:
				// data-sets are wrote into database
				String[] peaks = dataSet.getPeak();
				String peak_x = peaks[0];
				String peak_y = peaks[1];
				String peak_z = peaks[2];
				
				statement.executeUpdate("INSERT INTO " + Peak_databaseName + " VALUES (" + dataSet.getIndex() + ", " + dataSet.getSensor()+ ", "  + peak_x + ", " + peak_y + ", " + peak_z + ")");

				// close connection

			} catch (SQLException e) {
				e.printStackTrace();
			}
			// catch failure
		}
		
		// write data into database
		public void insertPhyphoxData(dataFormat dataSet) {

			try {
				// building up connection
				// here without password
			

				// act operation:
				// data-sets are wrote into database
				String[] accelerations = dataSet.getAcceleration();
				String accel_x = accelerations[0];
				String accel_y = accelerations[1];
				String accel_z = accelerations[2];
				
				statement.executeUpdate("INSERT INTO " + Phyphox_Acc_databaseName + " VALUES (" + dataSet.getSensor()+ ", '" + dataSet.getTime() + "', " + accel_x + ", " + accel_y + ", " + accel_z + ")");

				// close connection

			} catch (SQLException e) {
				e.printStackTrace();
			}
			// catch failure
		}
		
		// write data into database
		public void insertPhyphoxPeak(dataFormat dataSet) {

			try {
				// building up connection
				// here without password
			

				// act operation:
				// data-sets are wrote into database
				String[] peaks = dataSet.getPeak();
				String peak_x = peaks[0];
				String peak_y = peaks[1];
				String peak_z = peaks[2];
				
				statement.executeUpdate("INSERT INTO " + Phyphox_Peak_databaseName + " VALUES (" + dataSet.getIndex() + ", " + dataSet.getSensor()+ ", "  + peak_x + ", " + peak_y + ", " + peak_z + ")");

				// close connection

			} catch (SQLException e) {
				e.printStackTrace();
			}
			// catch failure
		}
		
		public String getAccName() {
			return this.Acc_databaseName;
		}
		
		public String getPeakName() {
			return this.Peak_databaseName;
		}
		
		public String getPhyphoxAccName() {
			return this.Phyphox_Acc_databaseName;
		}
		
		public String getPhyphoxPeakName() {
			return this.Phyphox_Peak_databaseName;
		}
}
