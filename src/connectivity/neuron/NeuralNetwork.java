package connectivity.neuron;

import java.util.ArrayList;




public class NeuralNetwork {


	private final static float BIAS_VALUE = 1.0f;


	private Layer input;
	private Layer output;

	private ArrayList<Layer> neural_layers;
	private Neuron bias;


	private ValueSet input_values;
	private ValueSet output_values;
	
	
	public NeuralNetwork(){
		clear();
	}


	public void clear(){

		neural_layers = new ArrayList<Layer>();				
		bias = new Neuron();
		bias.setValue(BIAS_VALUE);	
		input_values = new ValueSet();
		output_values = new ValueSet();

	}


	public void run(ValueSet in){

		input_values = in;
		input.setValues(in, Layer.ON_FEWER_SET_ZERO);
		fire();


		output.fillValueSet(output_values);
		
	}

	
	public void fire(){

		for(Layer l : neural_layers){
			l.fire();
		}

	}

	
	/**
	 * 
	 * @param inputs
	 * @param hidden
	 * @param outputs
	 * @param inputActivator
	 * @param hiddenActivator
	 * @param outputActivator
	 * @throws ClassNotFoundException
	 */
	public void buildNetwork(int inputs, int[] hidden, int outputs, int inputActivator, int hiddenActivator, int outputActivator) throws ClassNotFoundException{

		clear();

		output_values.setSize(outputs);
		input_values.setSize(inputs);
		// create the input layer
		input = new Layer();		

		for(int i = 0; i < inputs; i++){
			input.add(new Neuron(ActivatorFactory.retrieve(inputActivator)));
		}

		//neural_layers.add(input);


		// create the hidden layers
		for(int i = 0; i < hidden.length; i++){

			Layer hiddenLayer = new Layer(bias);

			for(int h = 0; h < hidden[i]; h++){

				hiddenLayer.add(new Neuron(ActivatorFactory.retrieve(hiddenActivator)));
			}

			neural_layers.add(hiddenLayer);
		}


		// create the output layer
		output = new Layer(bias);		

		for(int i = 0; i < outputs; i++){

			output.add(new Neuron(ActivatorFactory.retrieve(outputActivator)));
		}

		neural_layers.add(output);


		if(!neural_layers.isEmpty()){

			input.connectToChildLayer(neural_layers.get(0));

			// bind synapses
			for(int i = 0; i < neural_layers.size()-1; i++){

				neural_layers.get(i).connectToChildLayer(neural_layers.get(i+1));		
			}
		}

	}

	
	/**
	 * Scrambles all the synaptic weights randomly
	 */
	public void scrambleWeights(){

		for(Synapse s : bias.getOutputSynapses()){
			s.setWeight((float)(2.0f*Math.random())-1.0f);
		}

		for(Neuron n : input.getNeurons()){
			for(Synapse s : n.getOutputSynapses()){
				s.setWeight((float)(2.0f*Math.random())-1.0f);
			}
		}

		for(Layer l : neural_layers){

			for(Neuron n : l.getNeurons()){
				for(Synapse s : n.getOutputSynapses()){
					s.setWeight((float)(2.0f*Math.random())-1.0f);
				}
			}
		}
	}


	/**
	 * Returns the neural network input layer
	 * @return
	 */
	public Layer getInputLayer(){
		return input;
	}
	
	
	/**
	 * Returns the neural network output layer
	 * @return
	 */
	public Layer getOutputLayer(){
		return output;
	}
	
	
	/**
	 * Returns a list with the hidden and output layers
	 * @return
	 */
	public ArrayList<Layer> getLayers(){
		return neural_layers;
	}
	
	
	/**
	 * Returns the input
	 * @return
	 */
	public ValueSet getInputValueSet(){
		return input_values;
	}
	
	
	/**
	 * Returns the output
	 * @return
	 */
	public ValueSet getOutputValueSet(){
		return output_values;
	}
	
	
	/**
	 * Returns the bias neuron
	 * @return
	 */
	public Neuron getBias(){
		return bias;
	}
	
	
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder(input.toString()+"\n");
		
		
		for(Layer l : neural_layers){
			str.append(l+"\n");
		}
		
		return str.toString();
	}



}
