/*
 * This class re-sampled data based on a given sampling frequency
 * Along the vector of time, the time that correspond to the given sampling is searched,
 * then, if the time is not exactly the one needed, interpolation is performed, for finding 
 * the value a the specific needed point
 * 
 * Created by Stalin Ibanez 
 * last version: 2019-07-10
 */

public class samplingData {

	double[] getSamplingData(double dataAcc [], long dataTime [], int samplingFrequency, int points, int lengthOfDataset){         
		double[] accraw= dataAcc;
		long[] time= dataTime;
		int sampling = samplingFrequency;
		int vectorlength = points;
		double mstime;
		double m1;
		double acc1 = 0;
		double[] timemili= new double[vectorlength];	
				
		double timestep = (1000/(double)sampling);
		//int totalpoints = (int)(accraw[vectorlength]/timestep);
		int totalpoints = lengthOfDataset;
		
		double[] accsampled = new double[totalpoints];
		
		for(int j=1;j<vectorlength;j++){
			timemili[j]=(time[j]-time[0]);
			timemili[j]=timemili[j]/1000000;
			}
		
		accsampled[0]=accraw[0];
		for(int i=1; i < totalpoints ; i++){
			mstime=timestep*(double)i;
			for(int j=0;j<vectorlength;j++){
				if (mstime <= timemili[j] ) {
					m1 = (accraw[j]-accraw[j-1])/(timemili[j]-timemili[j-1]);
					acc1 = accraw[j-1] + m1*(mstime-timemili[j-1]);
					break;
				}
			}
			accsampled[i]=acc1;			
		}	
		
		return accsampled;
		}	
}
