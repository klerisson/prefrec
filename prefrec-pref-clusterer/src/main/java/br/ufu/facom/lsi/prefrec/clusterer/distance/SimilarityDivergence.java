/**
 * 
 */
package br.ufu.facom.lsi.prefrec.clusterer.distance;

import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.CloneableSerializable;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

/**
 * @author Klerisson
 *
 */
public class SimilarityDivergence implements
		DivergenceFunction<DoublePoint, DoublePoint> {

	private static final long serialVersionUID = -7554167632490054124L;
	private EuclideanDistance euclideanDistance;
	
	public SimilarityDivergence() {
		super();
		this.euclideanDistance = new EuclideanDistance();
	}

	@Override
	public CloneableSerializable clone() {
		//TODO
		return null;
	}

	@Override
	public double evaluate(DoublePoint first, DoublePoint second) {
		return -this.euclideanDistance.compute(first.getPoint(), second.getPoint());
	}

}
