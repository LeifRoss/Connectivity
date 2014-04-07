package connectivity.neuron;

public class ActivatorFactory {


	public static final int SIGMOID_ACTIVATOR = 0;
	public static final int TAHN_ACTIVATOR = 1;
	public static final int BOOLEAN_ACTIVATOR = 2;
	public static final int LINEAR_ACTIVATOR = 3;


	private static Activator[] premade_activator = new Activator[]{
		new SigmoidActivator(),
		new TahnActivator(),
		new BooleanActivator(),
		new LinearActivator()
	};


	/**
	 * Creates a new activator function with the specified function input parameter
	 * @param type
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Activator create(int type) throws ClassNotFoundException{

		switch(type){

		case SIGMOID_ACTIVATOR:
			return new SigmoidActivator();

		case TAHN_ACTIVATOR:
			return new TahnActivator();	

		case BOOLEAN_ACTIVATOR:
			return new BooleanActivator();

		case LINEAR_ACTIVATOR:
			return new LinearActivator();			

		}


		throw new ClassNotFoundException("Could create requested activator");
	}


	/**
	 * Retrieves a premade activator function with the specified function input parameter
	 * @param type
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Activator retrieve(int type) throws ClassNotFoundException{

		if(premade_activator == null || type < 0 || type >= premade_activator.length){
			throw new ClassNotFoundException("Could not find requested activator");
		}

		return premade_activator[type];
	}





	//////////////////////////////// Activator templates //////////////////////////////////////	

	static class SigmoidActivator implements Activator{

		@Override
		public float calculateActivator(float sum) {

			return (float) (1.0f/(1.0f+Math.exp(-sum)));
		}

		@Override
		public String toString(){
			return "Sigmoid";
		}

		@Override
		public float calculateInverse(float sum) {
			
			return (float)-Math.log((1.0f-sum)/sum);
		}
	}


	static class BooleanActivator implements Activator{

		@Override
		public float calculateActivator(float sum) {

			return (sum >= 0.0f ? 1.0f : -1.0f);
		}	

		@Override
		public String toString(){
			return "Boolean";
		}

		@Override
		public float calculateInverse(float sum) {
			
			return (sum >= 0.0f ? 1.0f : -1.0f);
		}
	}

	static class TahnActivator implements Activator{

		@Override
		public float calculateActivator(float sum) {

			return (float)Math.tanh(sum);
		}	

		@Override
		public String toString(){
			return "Tahn";
		}

		@Override
		public float calculateInverse(float sum) {

			return (float)((Math.log(1f + sum) - Math.log(1f - sum))/2f);
		}
	}

	static class LinearActivator implements Activator{

		@Override
		public float calculateActivator(float sum) {

			return sum;
		}	

		@Override
		public String toString(){
			return "Linear";
		}

		@Override
		public float calculateInverse(float sum) {
			// TODO Auto-generated method stub
			return sum;
		}
	}




}
