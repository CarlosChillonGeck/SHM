
public class powerOf2Extension {

	int nextPow2Value(int value){
		int highestOneBit = Integer.highestOneBit(value);
		if (value == highestOneBit) {
		    value = value;
		    return value;
		}		
		value = highestOneBit << 1;
		return value;
		}
		
	double[] nextPow2vector(double dataVector []){         
		double[] Vector = dataVector;
		int value = Vector.length;
		int highestOneBit = Integer.highestOneBit(value);

		if (value == highestOneBit) {
		    value = value;
		    return Vector;
		}
		
		value = highestOneBit << 1;
		double[] NewVector = new double[value];
		
		for (int i = 0; i < Vector.length; i++){
            NewVector[i] = Vector[i];
        }
		
		return NewVector;
		}
		
}
