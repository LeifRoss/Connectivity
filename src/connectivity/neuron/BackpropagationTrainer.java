package connectivity.neuron;

public class BackpropagationTrainer implements Trainer {

	
	private NeuralNetwork network;
	
	public BackpropagationTrainer(NeuralNetwork network){		
		this.network = network;
	}
	
	@Override
	public float train(ValueSet in, ValueSet desired, float learningRate) {
		
		
		
		network.run(in);	

		network.getOutputLayer().calculateError(desired);

		// calculate error from hidden layer neurons
		for(int i = network.getLayers().size()-2; i >= 0; i--){

			network.getLayers().get(i).calculateRecurrent();
		}


		// adjust weights for bias
		network.getBias().adjustWeights(learningRate);

		// adjust weights for input layer
		for(Neuron n : network.getInputLayer().getNeurons()){

			n.adjustWeights(learningRate);
		}

		// adjust weights for hidden layer
		for(int i = 0; i < network.getLayers().size()-1; i++){

			for(Neuron n : network.getLayers().get(i).getNeurons()){

				n.adjustWeights(learningRate);
			}
		}

		// adjust value for hidden and output layer
		for(Layer l : network.getLayers()){

			for(Neuron n : l.getNeurons()){
				n.setValue(n.getValue() + learningRate*n.getError());
			}	
		}


		network.getOutputLayer().fillValueSet(network.getOutputValueSet());


		return ValueSet.distance(network.getOutputValueSet(), desired);
	}

}
