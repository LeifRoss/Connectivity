package connectivity.neuron;

public interface Activator {

	/**
	 * Calculates the activator function
	 * @param sum
	 * @return
	 */
	public float calculateActivator(float sum);

	/**
	 * Calculates the inverse activator function
	 * @param sum
	 * @return
	 */
	public float calculateInverse(float sum);

}
