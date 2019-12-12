import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBAccess {
	// attributes to define connection parameters to the SQL server
		private String db_url = "";
		private String driver = "";
		private Connection connection = null;
		private Statement statement = null;

		// constructor
		// needs information about database URL and specific driver
		// driver is loaded instantly after initiate the DBAccess-object
		public DBAccess(String db_url, String driver) {
			this.db_url = db_url;
			this.driver = driver;
			loadDriver();
			try
			{
				connection = DriverManager.getConnection(db_url, "root", "");
				statement = connection.createStatement();
				
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
			public void clear(String databaseName) {

				try {
					// building up connection
					// here without password
					

					// change operation: create statement
					statement = connection.createStatement();

					// act operation:
					// data-sets are wrote into database

					statement.executeUpdate("DELETE FROM " + databaseName);

					// close connection

				} catch (SQLException e) {
					e.printStackTrace();
				}
				// catch failure
			}

		// write data into database
		public void insertData(dataFormat dataSet, String databaseName) {

			try {
				// building up connection
				// here without password
				

				// change operation: create statement
				//Statement statement = connection.createStatement();

				// act operation:
				// data-sets are wrote into database
				double[] accelerations = dataSet.getAcceleration();
				double accel_x = accelerations[0];
				double accel_y = accelerations[1];
				double accel_z = accelerations[2];
				
				statement.executeUpdate("INSERT INTO " + databaseName + " VALUES (" + dataSet.getSensor()+ ", " + dataSet.getTime() + ", " + accel_x + ", " + accel_y + ", " + accel_z + ")");

				// close connection

			} catch (SQLException e) {
				e.printStackTrace();
			}
			// catch failure
		}
}
