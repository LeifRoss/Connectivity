package connectivity.neuron;

import java.util.LinkedList;

public class RecursiveSynapseTrainer implements Trainer {


	private NeuralNetwork net;
	private float rate;
	
	public RecursiveSynapseTrainer(NeuralNetwork net){
		this.net = net;

	}






	@Override
	public float train(ValueSet in, ValueSet desired, float learningRate) {
		this.rate = learningRate;		
		
		net.run(in);
		
		
		Layer output = net.getOutputLayer();
		
		
		for(int i = 0; i < output.getNeurons().size(); i++){
			
			Neuron n = output.getNeurons().get(i);
			float d = desired.getValue(i);
			
			check(n,d,0);		
		}
		

		
		
		return 0;
	}

	private void check(Neuron n, float desired, float pass){
		
		if(!n.hasParents()){
			return;
		}
		
		Activator activator = n.getActivator();
		
		float correct = activator.calculateInverse(desired);
		float current = activator.calculateInverse(n.getValue());
		System.out.println(correct);
		
		float error = (correct - current)*rate;
		n.setError(error);
		
		LinkedList<Synapse> parents = n.getInputSynapses();
		
		
		Synapse selected = parents.getFirst();
		float minDW = 99999f;
		
		for(Synapse s : parents){
			
			Neuron parent = s.getParent();						
				
			float dw = (correct / parent.getValue()) - s.getWeight();
			
			if(dw <= minDW){				
				selected = s;
				minDW = dw;
			}

		}
		
		selected.setWeight(selected.getWeight() + minDW*rate);
		
		
		//float deltaWeight = 0f;
		
		
		
		
		
	}
	
	
}
