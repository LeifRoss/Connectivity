package connectivity.neuron;

import java.util.ArrayList;

public class Layer {


	/**
	 * Sets the rest of the values in the layer to zero
	 */
	public static final int ON_FEWER_SET_ZERO = 0;
	/**
	 * Ignores the rest of layer
	 */
	public static final int ON_FEWER_IGNORE = 1;
	/**
	 * Throws a exception if too few values are passed in
	 */
	public static final int ON_FEWER_ERROR = 2;


	private ArrayList<Neuron> neurons;
	private Neuron bias;

	public Layer(Neuron bias){		
		clear();	
		this.bias = bias;
	}

	public Layer(){		
		clear();		
		this.bias = null;
	}


	public void clear(){

		neurons = new ArrayList<Neuron>();
		bias = null;
	}



	public void fire(){

		for(Neuron n : neurons){
			n.fire();
		}		
	}


	public void add(Neuron n){

		if(bias!=null){
			bias.connectToChild(n);			
		}

		neurons.add(n);		
	}

	public ArrayList<Neuron> getNeurons(){
		return neurons;
	}
	
	
	public void connectToChildLayer(Layer childLayer){

		for(Neuron parent : neurons){
			for(Neuron child : childLayer.neurons){

				parent.connectToChild(child);
			}			
		}		
	}



	public void calculateError(ValueSet desired){
		
		
		int minIndex = Math.min(desired.size(), neurons.size());
		
		for(int i = 0; i < minIndex; i++){
			
			Neuron n = neurons.get(i);			
			n.setError(n.getValue()*(1.0f - n.getValue()) * (desired.getValue(i) - n.getValue()));
			
		}

	}


	public void calculateRecurrent(){


		for(Neuron n : neurons){

			float recurrentMul = 1.0f;

			for(Synapse s : n.getOutputSynapses()){
				recurrentMul = recurrentMul*s.getWeight()*s.getChild().getError();
			}

			n.setError(n.getValue()*(1.0f - n.getValue())*recurrentMul);
		}


	}


	/**
	 * Sets the layer neuron values
	 * @param values
	 * @param mode
	 */
	public void setValues(ValueSet values, int mode){

		switch(mode){
		case ON_FEWER_SET_ZERO:
			setValueSetZero(values);
			break;
		case ON_FEWER_IGNORE:
			setValueIgnore(values);
			break;	
		case ON_FEWER_ERROR:
			try {
				setValueError(values);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private void setValueSetZero(ValueSet values){

		for(int i = 0; i < neurons.size(); i++){

			if(i < values.size()){
				neurons.get(i).setValue(values.getValue(i));
			}else{
				neurons.get(i).setValue(0f);
			}
		}
	}

	private void setValueIgnore(ValueSet values){

		int index = Math.min(neurons.size(), values.size());

		for(int i = 0; i < index; i++){
			neurons.get(i).setValue(values.getValue(i));
		}

	}

	private void setValueError(ValueSet values) throws Exception{

		if(values.size() < neurons.size()){
			throw new Exception("Too few values passed in");
		}

		for(int i = 0; i < neurons.size(); i++){
			neurons.get(i).setValue(values.getValue(i));
		}


	}

	
	public void fillValueSet(ValueSet values){
		
		values.setSize(neurons.size());
		
		for(int i = 0; i < neurons.size(); i++){
			values.setValue(i,neurons.get(i).getValue());
		}
		
	}
	
	
	@Override
	public String toString(){
		
		StringBuilder str = new StringBuilder();
		
		for(Neuron n : neurons){
			str.append("["+n+"]");
		}
		
		return str.toString();
	}

	
}
