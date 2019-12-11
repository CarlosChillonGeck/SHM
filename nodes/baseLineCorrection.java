
public class baseLineCorrection {

	double[] getBaseLineCorrection(double dataAcc []){         
		double[] acc1= dataAcc;
		double sum = 0;
		
		for(int i=0;i<acc1.length;i++){
			sum=acc1[i]+sum; 
			}	
		sum=sum/acc1.length;		

		for(int i=0;i<acc1.length;i++){
			acc1[i]=acc1[i]-sum; 
			}	
		
		return acc1;
		}

}
