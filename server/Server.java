import java.net.Socket;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/* This part of the code runs into the server (laptop)
The step for running the software properly are written along the code
see lines: 22, 

The data collected by the nodes is going to be stored in the folder ./Results/
with the name of the current time in milliseconds
*/
public class Server {
	
	private static String rawDataPath = "./Results/";

	@SuppressWarnings("unused")
	public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException{		

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
		double timeStamp[] = new double[lengthOfDataset]; // Time Stamp
		double[][] BLAccelerationData = new double[3][lengthOfDataset];
		double[][] BLAccelerationData2 = new double[3][lengthOfDataset];
        double[][]	detFrequencies	= new double[numberOfNodes][numberOfPeaks];
        double[][]	detFrequencies2	= new double[numberOfNodes][numberOfPeaks];		

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
        System.out.println("Press enter for sending parameters to nodes");
        System.in.read();

        for(int node = 0; node < numberOfNodes; node++){
        	waitingTime=(numberOfNodes-node)*waitingTime0;
    		OUT[node].writeInt(samplingFrequency);
    		OUT[node].writeInt(numberOfPeaks);
    		OUT[node].writeInt(secondsmeasuring);
    		OUT[node].writeInt(direction);
    		OUT[node].writeLong(waitingTime);
            OUT[node].flush();
            Thread.sleep(waitingTime0);
        }
         
        System.out.println("\nInput parameters transmitted\n"
        		+ "--------------------");
        
        /* Receiving acceleration data
        This is only for research purposes, ideally, only the results of the analysis 
        should be transmitted*/
        for(int node = 0; node < numberOfNodes; node++){
        	System.out.println("Waiting acceleration data from node  " + (node + 1));
            for(int i = 0; i < lengthOfDataset; i++){
            	timeStamp[i] = IN[node].readDouble();
            	BLAccelerationData[0][i] = IN[node].readDouble(); // reading Acc. data from the nodes
            	BLAccelerationData[1][i] = IN[node].readDouble();
            	BLAccelerationData[2][i] = IN[node].readDouble();
            	BLAccelerationData2[0][i] = IN[node].readDouble();
            	BLAccelerationData2[1][i] = IN[node].readDouble();
            	BLAccelerationData2[2][i] = IN[node].readDouble();
            	if(i % 20 == 0) System.out.println(i+1 + "/" + lengthOfDataset);
	        	}
            System.out.println("================ Acceleration data node  " + (node + 1) + " received ================= ");
        	}
        
        // Receiving results of analysis
        for(int node = 0; node < numberOfNodes; node++){
        	System.out.println("Waiting frequencies from node  " + (node + 1));
	        for(int i = 0; i < numberOfPeaks; i++){
	        	System.out.println("Frequency  " + (i + 1));
	        	detFrequencies[node][i] = IN[node].readDouble(); // reading Acc. data from the nodes
	        	detFrequencies2[node][i] = IN[node].readDouble();
	        	}
        }
        
        // Storing data into a file in ./Results/
        long date=System.currentTimeMillis();
      	FileWriter writer = new FileWriter(rawDataPath + Long.toString(date) + "_Acc.csv");
      	writer.write("Time Stamp,");
      	writer.write("Sensor 1_X,");
      	writer.write("Sensor 1_Y,");
      	writer.write("Sensor 1_Z,");
      	writer.write("Sensor 2_X,");
      	writer.write("Sensor 2_Y,");
      	writer.write("Sensor 2_Z,");
      	writer.write("\n");
      			
      	for(int node = 0; node < numberOfNodes; node++){
	      	for(int k = 0; k < lengthOfDataset; k++){
	      		writer.write(timeStamp[k]  + ",");
	      		writer.write(BLAccelerationData[0][k]  + ",");
	      		writer.write(BLAccelerationData[1][k]  + ",");
	      		writer.write(BLAccelerationData[2][k]  + ",");
	      		writer.write(BLAccelerationData2[0][k]  + ",");
	      		writer.write(BLAccelerationData2[1][k]  + ",");
	      		writer.write(BLAccelerationData2[2][k]  + "\n");      		
	      	    }
      	writer.write("\n \n \n");
      	}
      	
      	for(int node = 0; node < numberOfNodes; node++){
	      	for(int k = 0; k < numberOfPeaks; k++){
	      		writer.write(detFrequencies[node][k]  + "	");
	      		writer.write(detFrequencies2[node][k]  + "\n");      		
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
	        	System.out.print("Peak " + (i+1) + "\t" + detFrequencies[node][i] + "\t\t");
				System.out.println(detFrequencies2[node][i]);
			}
      	}
     
        for(int node = 0; node < numberOfNodes; node++){
        	IN[node].close();
            OUT[node].close();
            ss[node].close(); 
      	    }
  
	}

}
