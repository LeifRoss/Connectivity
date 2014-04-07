package connectivity.neuron;

public class ValueSet {


		private static final int STANDARD_SIZE = 1;
		
		private int size;
		private float[] values;
		
		
		public ValueSet(float ...v){
			size = v.length;
			values = v;			
		}
		
		public ValueSet(){
			clear();		
		}
		
		
		public void clear(){
			
			size = STANDARD_SIZE;
			values = new float[size];		
		}
		
		
		public int size(){
			return size;
		}
		
		public float getValue(int i){
			return values[i];
		}

		public void setValue(int i, float value){
			values[i] = value;
		}

		public void setSize(int s){
			
			float[] buffer = new float[s];
			
			int minidx = Math.min(s, values.length);
			
			for(int i = 0; i < minidx; i++){
				buffer[i] = values[i];
			}
			
			size = s;
			values = buffer;
		}
		
		
		public static float distance(ValueSet a, ValueSet b){
			
			int minidx = Math.min(a.size(), b.size());
			float dV = 0;
			
			for(int i = 0; i < minidx; i++){
				
				float dI = a.getValue(i) - b.getValue(i);				
				dV += (dI*dI);				
			}
			
		
			return (float)Math.sqrt(dV);
		}
		
		@Override
		public String toString(){
			
			StringBuilder str = new StringBuilder();
			
			for(Float f : values){
				str.append("["+f+"]");
			}
			
			return str.toString();
		}
	
}
