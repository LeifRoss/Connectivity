package connectivity.neuron;

import java.util.LinkedList;

public class Neuron {


	private LinkedList<Synapse> parents;
	private LinkedList<Synapse> children;

	private float error;
	private float value;

	private Activator activator;


	public Neuron(){
		clear();

		try {
			setActivator(ActivatorFactory.retrieve(ActivatorFactory.SIGMOID_ACTIVATOR));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public Neuron(Activator in){
		clear();
		setActivator(in);
	}


	public void fire(){

		if(parents.isEmpty()){
			return;
		}

		float sum = 0.0f;

		for(Synapse s : parents){
			sum += s.getParent().getValue()*s.getWeight();
		}

		this.setValue(activator.calculateActivator(sum));
		
	}


	public void clear(){

		value = error = 0f;
		parents = new LinkedList<Synapse>();
		children = new LinkedList<Synapse>();
		activator = null;

	}

	public void adjustWeights(float learningRate) {
		
		for(Synapse s : getOutputSynapses()){		

			s.setWeight(s.getWeight() + learningRate*s.getChild().getError()*getValue());
		}

	}

	

	public LinkedList<Synapse> getInputSynapses(){
		return parents;
	}


	public LinkedList<Synapse> getOutputSynapses(){
		return children;
	}	


	public void connectToChild(Neuron children){

		Synapse synapse = new Synapse(this,children);

		this.children.add(synapse);
		children.parents.add(synapse);
	}


	public void setActivator(Activator in){
		this.activator = in;
	}


	public float getError(){
		return error;
	}


	public void setError(float e){
		this.error = e;
	}


	public float getValue(){
		return value;
	}


	public void setValue(float v){
		this.value = v;
	}

	public boolean hasChildren(){
		return !children.isEmpty();
	}
	
	public boolean hasParents(){
		return !parents.isEmpty();
	}
	
	public Activator getActivator(){
		return activator;
	}
	
	@Override
	public String toString(){

		return "v: "+getValue()+", e: "+getError()+", a: "+activator.toString();
	}



}
