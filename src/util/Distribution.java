package util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



/**
 * <pre>
 * Represent a distribution of weights.
 * A random element can be generated with the probability distribution following the normalized weights
 *
 * This example show how to setWeight and remove value, and how to get the non zeros elements.
 *
 * <code> {@code
 * Distribution<A> distribution = new Distribution<A>();
 * distribution.setWeight(a1,1);
 * distribution.setWeight(a2,1);
 *
 * // Have 50% change to return a1, and 50% to return a2
 * A a = distribution.generate();
 *
 * // probability is equal to 0.5
 * double probability = distribution.getProbability(a1);
 * distribution.remove(a1);
 *
 * // Return the collection containing a1
 * Set<A> set = distribution.getNonZeroElements();
 * }</code></pre>
 *
 * @param <A> The space of the distribution
 */

public class Distribution<A> implements Serializable {

    /**
     * The version of the class used for serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * The sum of all the weights
     * Is equal to null when undefined
     * Is recomputed whenever an element is added or deleted, to not accumulate approximation errors
     */
    private Double sumOfWeights;


    public void setWeights(Map<A, Double> weights) {
        this.weights = weights;
    }

    public Map<A, Double> getWeights() {
        return weights;
    }

    /**
     * The map representing the probability distribution (not necessarily normalized)
     * It represents only elements associated to a non-zero weight
     */
    private Map<A, Double> weights;


    /**
     * Create an empty distribution
     */
    public Distribution() {
        weights = new HashMap<>();
        sumOfWeights = null;
    }


    /**
     * Create a distribution with a single element of weight 1.0
     *
     * @param element The element
     */
    public Distribution(A element) {
        weights = new HashMap<>();
        weights.put(element,1.0);
        sumOfWeights = null;
    }

    /**
     * Add a new element to the distribution.
     * If the element was already present, change its weight.
     *
     * @param element An element
     * @param weight The weight of the element
     */
    public void setWeight(A element, double weight) throws IllegalArgumentException{
        if(weight > 0.0) {
            weights.put(element, weight);
            sumOfWeights = null;
        } else if(weight == 0.0) {
            remove(element);
        } else {
            throw new IllegalArgumentException("A weight is always non-negative");
        }
    }

    public void sanityCheck() throws Exception {
        this.computeSumOfWeights();
        if (this.sumOfWeights>1.02 || this.sumOfWeights <0.98){
            System.out.println("sum : "+this.sumOfWeights);
            System.out.println(this.toString());
            System.exit(1);
            throw new Exception("Not a valid distribution !!");

        }
        //System.out.println("sanity check passed");
        //System.out.println("sanity check passed for : "+ this.toString());
    }
    /**
     * Remove an element if it exists.
     *
     * @param element The element
     */
    public void remove(A element) {
        weights.remove(element);
        sumOfWeights = null;
    }


    /**
     * Normalize the distribution so the sum of all weights equals normalizationValue.
     *
     * @param normalizationValue The new sum of all weights
     */
    public void normalize(double normalizationValue) {
        computeSumOfWeights();

        if(normalizationValue == sumOfWeights) {
            return;
        }

        if(sumOfWeights == 0.0) {
            return;
        }

        if(normalizationValue < 0) {
            throw new IllegalArgumentException("The normalization value should be non-negative");
        }

        double factor = normalizationValue/sumOfWeights;

        for(Map.Entry<A,Double> entry: weights.entrySet()) {
            entry.setValue(entry.getValue()*factor);
        }

        sumOfWeights = normalizationValue;
    }


    /**
     * Normalize the distribution so the sum of all values equals 1.0
     * (this ensures that this is a valid probability distribution)
     */
    public void normalize() {
        normalize(1.0);
    }
    
    /**
     * Return the weight of an element
     *
     * @param element The element
     * @return The weight of the element
     */
    /*public double getWeight(A element) {
        Double d = weights.get(element);
        if(d == null) {
            return 0.0;
        }
        return d;
    }*/

    public double getWeight(A element){
        //System.out.println("trying to find : "+element.toString());
        for (A a : this.getNonZeroElements()){
            if (a.equals(element)){
                return this.weights.get(a);
            }
        }
        //System.out.println("not found : "+ element.toString());
        return 0.0;
    }

    /**
     * Return the probability of an element
     *
     * @param element The element
     * @return The probability of this element
     */
    public double getProbability(A element) {
        computeSumOfWeights();
        Double d = weights.get(element);
        if(d == null) {
            return 0.0;
        }
        return d/sumOfWeights;
    }


    /**
     * Get all elements that have a weight different of zero
     *
     * @return The set of elements
     */
    public Set<A> getNonZeroElements() {
        //WARNING : TO BE CHANGED !!!!!!!!
        /*
        for (A a : weights.keySet()){
            if (this.weights.get(a) == 0){
                this.weights.remove(a);
            }
        }
        */
        return weights.keySet();
    }


    /**
     * Computes the sum of weights of the distribution
     */
    private void computeSumOfWeights() {
        //We don't recompute it if it is already computed
        if(sumOfWeights != null) {
            return;
        }
        sumOfWeights = 0.0;
        for(Map.Entry<A,Double> entry : weights.entrySet()) {
            sumOfWeights += entry.getValue();
        }
    }

    public double getSumOfWeights() {
	    computeSumOfWeights();
	    return sumOfWeights;
    }

    /**
     * format the distribution using a precision of 0.0001.
     *
     * @return The string describing the object
     */
    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("###.###################");
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(A elem : getNonZeroElements()) {
            sb.append(elem.toString()).append("=");
            //sb.append(formatter.format(getProbability(elem)));
            sb.append(this.getWeight(elem));
            sb.append(", \n");
        }
        sb.append("}");
        return sb.toString();
    }

    public void addWeight(A a, double probability) {
        //System.out.println("adding weight : " + probability+ "to the pair : "+a.toString());
        this.weights.put(a,probability);
        //System.out.println(" probability : "+probability + "should equal : "+this.getWeight(a));
    }

    public int size() {
        return this.weights.size();
    }

    @Override
    public int hashCode() {
        //System.out.println("Distribution::testing hashCode");
        return this.weights.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        //System.out.println("Distribution::testing equals function");
        Distribution<A> distribPrime = new Distribution<>();
        try{
            distribPrime = (Distribution<A>) obj;
        }
        catch(Exception e){
            System.out.println("Distribution::Cannot cast obj : " + obj.toString() + " into Distribution<??>");
            return false;
        }
        return this.weights.equals(distribPrime.weights);
    }
    /*
    public boolean ToleranceEquality(double a, double b){
        if (a - 0.0001<b || b<a+0.0001){
            return true;
        }
        return false;
    }
    @Override
    public boolean equals(Object obj) {
        System.out.println("Distribution::testing equals function");
        Distribution<A> distribPrime = new Distribution<>();
        try{
            distribPrime = (Distribution<A>) obj;
        }
        catch(Exception e){
            System.out.println("Distribution::Cannot cast obj : " + obj.toString() + " into Distribution<??>");
            return false;
        }
        int indice = 0;
        for (A key : this.weights.keySet()){
            if (!(distribPrime.weights.containsKey(key)) || !(ToleranceEquality(this.weights.get(key),((Distribution<A>) obj).weights.get(key)))){
                //System.out.println("key : " + key.toString());
                //System.out.print("proba for this : " + this.weights.get(key) + " proba for obj : " + distribPrime.weights.get(key));
                return false;
            }
        }
        for (A key : distribPrime.weights.keySet()){
            if (!(this.weights.containsKey(key)) || !(ToleranceEquality(this.weights.get(key),((Distribution<A>) obj).weights.get(key)))){
                //System.out.println("key : " + key.toString());
                //System.out.print("proba for this : " + this.weights.get(key) + " proba for obj : " + distribPrime.weights.get(key));
                return false;
            }
        }
        return true;
    }
     */
}
