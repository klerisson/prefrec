package br.ufu.facom.lsi.prefrec.cluster.distance;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.util.FastMath;

public class MyPearsonCorrelationSimilarity implements DistanceMeasure {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3148652824586393232L;

	@Override
	public double compute(double[] p1, double[] p2) {
		double sumNum = 0;
		double sumDen1 = 0;
		double sumDen2 = 0;
		double avgP1 = 0;
		double avgP2 = 0;
		double qtdeRates = 0;
		for (int i = 0; i < p1.length; i++) {
			if (p1[i] != -1 && p2[i] != -1) {

				 avgP1 += p1[i];
				 avgP2 += p2[i];
				qtdeRates++;
			}
		}
		if (qtdeRates == 0) {
			return (Double.MAX_VALUE);
		} else {
			avgP1=avgP1/qtdeRates;
			avgP2=avgP2/qtdeRates;
			for (int i = 0; i < p1.length; i++) {
				if (p1[i] != -1 && p2[i] != -1) {
					 sumNum += (p1[i] - avgP1)*(p2[i] - avgP2);
					 sumDen1 += FastMath.pow((p1[i] - avgP1), 2);
					 sumDen2 += FastMath.pow((p2[i] - avgP2), 2);
				}
			}
			return (1- (sumNum/(FastMath.sqrt(sumDen1)*FastMath.sqrt(sumDen2))));
		}
	}

}
