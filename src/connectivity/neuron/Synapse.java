package connectivity.neuron;

public class Synapse {

	private Neuron parent;
	private Neuron child;
	
	private float weight;
	
	
	public Synapse(Neuron parent, Neuron child){
		clear();
		
		this.parent = parent;
		this.child = child;
	}
	
	
	public void clear(){
		
		parent = null;
		child = null;
		weight = 0;					
	}
	
	
	public float getWeight(){
		return weight;
	}
	
	public void setWeight(float w){
		this.weight = w;
	}
	
	
	public Neuron getParent(){
		return parent;
	}
	
	public Neuron getChild(){
		return child;
	}
	
	
}
