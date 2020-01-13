import java.net.Socket;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;
import java.util.Scanner;


/* This part of the code runs into the server (laptop)
The step for running the software properly are written along the code
see lines: 22, 

The data collected by the nodes is going to be stored in the folder ./Results/
with the name of the current time in milliseconds
*/
public class Server {
	
	private static String rawDataPath = "./Results/";
	
	public static final String DB_URL = "jdbc:mysql://localhost/accelerometer";
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static String IPAddress = "192.168.1.102:8080";
	// always check if the right driver is installed!
	

	@SuppressWarnings("unused")
	public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException{		
		Phyphox.clear(IPAddress);
		
		// Step 1: define input parameters (Pay attention to number the of nodes)
		// ===== Input Parameters ==========//
		int secondsmeasuring = 30;			// [s]        
		int samplingFrequency = 100;		// [Hz] (Divisor of 1000, i.e. 25, 50, 100, 125, 200, and 250) higher are not possible when 2 sensor nodes are used
		int numberOfNodes = 1;				// sensor nodes to be used
		int numberOfPeaks = 2;				// expected peaks from the Peak Picking analysis
		int direction = 2;					// 0 = x direction ; 1 = Y direction ; 2 = Z direction
		// ============================== //
		
		// Variables related with the acceleration data
		int lengthOfDataset = samplingFrequency * secondsmeasuring;
		long timeStamp[] = new long[lengthOfDataset]; // Time Stamp
		double[][] BLAccelerationData = new double[3][lengthOfDataset];
		double[][] BLAccelerationData2 = new double[3][lengthOfDataset];
        double[][]	detFrequencies_x	= new double[numberOfNodes][numberOfPeaks];
        double[][]	detFrequencies_x2	= new double[numberOfNodes][numberOfPeaks];
        double[][]	detFrequencies_y	= new double[numberOfNodes][numberOfPeaks];
        double[][]	detFrequencies_y2	= new double[numberOfNodes][numberOfPeaks];
        double[][]	detFrequencies_z	= new double[numberOfNodes][numberOfPeaks];
        double[][]	detFrequencies_z2	= new double[numberOfNodes][numberOfPeaks];

        // Starting communication with the nodes
        System.out.println("Server is running and waiting for connection");
        ServerSocket 		ss[]	= new ServerSocket[numberOfNodes];
        Socket 				s[]		= new Socket[numberOfNodes];
        DataOutputStream 	OUT[] 	= new DataOutputStream[numberOfNodes];
        DataInputStream 	IN[] 	= new DataInputStream[numberOfNodes];
        long waitingTime0 = 3000;
        long waitingTime  = 0;
        
        for(int node = 0; node < numberOfNodes; node++){
        	int numberConn = 1234+node;
        	System.out.println("Connecting node " + numberConn );
        	ss[node] = new ServerSocket(numberConn);
    		s[node] = ss[node].accept();
            OUT[node] 	= new DataOutputStream	(s[node].getOutputStream());
            IN[node] 	= new DataInputStream	(s[node].getInputStream());
        	System.out.println("Node " + numberConn + " connected" );    
        }		
        
        // Sending input parameters to the sensor nodes
        System.out.println("Press key in Experiment Condition:");
        Scanner sc = new Scanner(System.in);
        String condition = sc.nextLine();
        
        DBAccess dbAccess = new DBAccess(DB_URL, DB_DRIVER, condition);
        
        // Sending input parameters to the sensor nodes
        System.out.println("Press enter for sending parameters to nodes");
        System.in.read();
        
    	Phyphox.start(IPAddress);
    	long Phyphox_startTime = System.currentTimeMillis();
    	
        for(int node = 0; node < numberOfNodes; node++){
        	//waitingTime=(numberOfNodes-node)*waitingTime0;
    		OUT[node].writeInt(samplingFrequency);
    		OUT[node].writeInt(numberOfPeaks);
    		OUT[node].writeInt(secondsmeasuring);
    		OUT[node].writeInt(direction);
    		OUT[node].writeLong(waitingTime);
            OUT[node].flush();
            //Thread.sleep(waitingTime0);
        }
        
		
        System.out.println("\nInput parameters transmitted\n"
        		+ "--------------------");
        
        /* Receiving acceleration data
        This is only for research purposes, ideally, only the results of the analysis 
        should be transmitted*/
        for(int node = 0; node < numberOfNodes; node++){
        	System.out.println("Waiting acceleration data from node  " + (node + 1));
            for(int i = 0; i < lengthOfDataset; i++){
            	timeStamp[i] = IN[node].readLong();
            	BLAccelerationData[0][i] = IN[node].readDouble(); // reading Acc. data from the nodes
            	BLAccelerationData[1][i] = IN[node].readDouble();
            	BLAccelerationData[2][i] = IN[node].readDouble();
            	
            	double[] data = {1, timeStamp[i], BLAccelerationData[0][i], BLAccelerationData[1][i],BLAccelerationData[2][i]};
    			dataFormat dataSet = new dataFormat(data,true);
    			// write incoming data into database using DBAccess class
    			dbAccess.insertData(dataSet);
    			
            	BLAccelerationData2[0][i] = IN[node].readDouble();
            	BLAccelerationData2[1][i] = IN[node].readDouble();
            	BLAccelerationData2[2][i] = IN[node].readDouble();
            	
            	double[] data2 = {2, timeStamp[i], BLAccelerationData2[0][i], BLAccelerationData2[1][i],BLAccelerationData2[2][i]};
    			dataFormat dataSet2 = new dataFormat(data2,true);
    			// write incoming data into database using DBAccess class
    			dbAccess.insertData(dataSet2);
    			
            	if(i % 20 == 0) System.out.println(i+1 + "/" + lengthOfDataset);
	        	}
            System.out.println("================ Acceleration data node  " + (node + 1) + " received ================= ");
        	}
        
        // Receiving results of analysis
        for(int node = 0; node < numberOfNodes; node++){
        	System.out.println("Waiting frequencies from node  " + (node + 1));
	        for(int i = 0; i < numberOfPeaks; i++){
	        	System.out.println("Frequency  " + (i + 1));
	        	detFrequencies_x[node][i] = IN[node].readDouble(); // reading Acc. data from the nodes
	        	detFrequencies_y[node][i] = IN[node].readDouble();
	        	detFrequencies_z[node][i] = IN[node].readDouble();
	        	double[] data1 = {i+1, 1, detFrequencies_x[node][i], detFrequencies_y[node][i], detFrequencies_z[node][i]};
	        	dataFormat dataSet1 = new dataFormat(data1,false);
	        	dbAccess.insertPeak(dataSet1);
	        	
	        	detFrequencies_x2[node][i] = IN[node].readDouble();	        	
	        	detFrequencies_y2[node][i] = IN[node].readDouble();	        	
	        	detFrequencies_z2[node][i] = IN[node].readDouble();
	        	double[] data2 = {i+1, 2, detFrequencies_x2[node][i], detFrequencies_y2[node][i], detFrequencies_z2[node][i]};
	        	dataFormat dataSet2 = new dataFormat(data2,false);
	        	dbAccess.insertPeak(dataSet2);
	        	}
        }
        
        Phyphox.save(IPAddress);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");
        String phy_startTime = sdf.format(new Date(Phyphox_startTime));
        System.out.println("Phyphox start time: " + phy_startTime);
        
        // Storing data into a file in ./Results/
      	FileWriter writer = new FileWriter(rawDataPath + dbAccess.getAccName() + ".csv");
      	writer.write("Time Stamp" + "\t");
      	writer.write("Sensor 1_X" + "\t");
      	writer.write("Sensor 1_Y" + "\t");
      	writer.write("Sensor 1_Z" + "\t");
      	writer.write("Sensor 2_X" + "\t");
      	writer.write("Sensor 2_Y" + "\t");
      	writer.write("Sensor 2_Z");
      	writer.write("\n");
      	
      	SimpleDateFormat st = new SimpleDateFormat("HH:mm:ss.SSS");
      	
      	for(int node = 0; node < numberOfNodes; node++){
	      	for(int k = 0; k < lengthOfDataset; k++){
	      		String t = st.format(new Date(timeStamp[k]));
	      		writer.write( t + "\t");
	      		writer.write(BLAccelerationData[0][k]  + "\t");
	      		writer.write(BLAccelerationData[1][k]  + "\t");
	      		writer.write(BLAccelerationData[2][k]  + "\t");
	      		writer.write(BLAccelerationData2[0][k]  + "\t");
	      		writer.write(BLAccelerationData2[1][k]  + "\t");
	      		writer.write(BLAccelerationData2[2][k]  + "\n");      		
	      	    }
      	writer.write("\n \n \n");
      	}
      	
      	writer.flush();
      	writer.close();
      	
        
      	// Final output of the console
        System.out.println("\nAcceleration-data written into " + rawDataPath);
        System.out.print("\nReceived Frequencies: \n" );

      	for(int node = 0; node < numberOfNodes; node++){
      		System.out.println("\nSensor Node " + (node + 1) );
      		System.out.println("\tSensor 1\tSensor 2");
	        for (int i = 0; i < numberOfPeaks; i++) {
	        	System.out.print("Peak x" + (i+1) + "\t" + detFrequencies_x[node][i] + "\t\t");
				System.out.println(detFrequencies_x2[node][i]);
				System.out.print("Peak y" + (i+1) + "\t" + detFrequencies_y[node][i] + "\t\t");
				System.out.println(detFrequencies_y2[node][i]);
				System.out.print("Peak z" + (i+1) + "\t" + detFrequencies_z[node][i] + "\t\t");
				System.out.println(detFrequencies_z2[node][i]);
			}
      	}
      	
      	System.out.println("Phyphox_" + dbAccess.getAccName() + ".csv");
        for(int node = 0; node < numberOfNodes; node++){
        	IN[node].close();
            OUT[node].close();
            ss[node].close(); 
      	    }
        
        
	}

}
