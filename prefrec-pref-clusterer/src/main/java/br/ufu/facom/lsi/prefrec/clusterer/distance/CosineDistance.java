package br.ufu.facom.lsi.prefrec.clusterer.distance;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.util.FastMath;

public class CosineDistance implements DistanceMeasure {

	private static final long serialVersionUID = 8188347590074168067L;

	@Override
	public double compute(double[] p1, double[] p2) {

		double sum = 0;
		double sumP1 = 0;
		double sumP2 = 0;
		double qtderates = 0;
		for (int i = 0; i < p1.length; i++) {
			// if p1[i]==0 or p2[i]==0 it doesn't use
			if (p1[i] != 0 && p2[i] != 0) {
				sum += (p1[i] * p2[i]);
				sumP1 += FastMath.pow(p1[i], 2);
				sumP2 += FastMath.pow(p2[i], 2);
				qtderates++;
			}
		}

		return ((sum / (FastMath.sqrt(sumP1) * FastMath.sqrt(sumP2)))/qtderates);
	}

}
