/**
 * 
 */
package br.ufu.facom.lsi.prefrec.clusterer.distance;

import org.apache.commons.math3.ml.clustering.DoublePoint;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * @author cricia
 * 
 */
public class MySimilarityDivergence implements
		DivergenceFunction<DoublePoint, DoublePoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8307391135308603188L;

	private MyEuclideanDistance myEuclideanDistance;

	public MySimilarityDivergence() {
		super();
		this.myEuclideanDistance = new MyEuclideanDistance();
	}

	@Override
	public double evaluate(DoublePoint first, DoublePoint second) {
		return -this.myEuclideanDistance.compute(first.getPoint(),
				second.getPoint());
	}

	@Override
	public CloneableSerializable clone() {
		// TODO
		return null;
	}
}
