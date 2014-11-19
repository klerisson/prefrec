package br.ufu.facom.lsi.prefrec.clusterer.distance;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.util.FastMath;

public class MyEuclideanDistance implements DistanceMeasure{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3148652824586393232L;

	@Override
	public double compute(double[] p1, double[] p2) {
		double sum = 0;
		//double sumP1 = 0;
		//double sumP2 = 0;
		double qtderates = 0;
		for (int i = 0; i < p1.length; i++) {
			// if p1[i]==0 or p2[i]==0 it doesn't use
			if (p1[i] != 0 &&  p2[i] != 0 ) {//teste &&
				sum += FastMath.pow((p1[i] - p2[i]),2);
				//sumP1 += FastMath.pow(p1[i], 2);
				//sumP2 += FastMath.pow(p2[i], 2);
				qtderates++;
			}
		}

		return (FastMath.sqrt(sum)/qtderates);
	}
	

}