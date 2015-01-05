package br.ufu.facom.lsi.prefrec.cluster.distance;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.util.FastMath;

public class CosineDistanceNormalized implements DistanceMeasure {

	private static final long serialVersionUID = 8188347590074168067L;

	@Override
	public double compute(double[] p1, double[] p2) {

		double sum = 0;
		double sumP1 = 0;
		double sumP2 = 0;
		double qtdeRates = 0;	
		double qtdeRatesP1 = 0;	
		double qtdeRatesP2 = 0;	
		double avgP1 = 0;
		double avgP2 = 0;
		for (int i = 0; i < p1.length; i++) {
			if (p1[i] != -1 ) {
				 avgP1 += p1[i];
				 qtdeRatesP1++;
			}
			if(p2[i] != -1){
				 avgP2 += p2[i];
				 qtdeRatesP2++;
			}
				
		}
		avgP1=avgP1/qtdeRatesP1;
		avgP2=avgP2/qtdeRatesP2;
		for (int i = 0; i < p1.length; i++) {
			// if p1[i]==0 or p2[i]==0 it doesn't use
			if (p1[i] != -1 && p2[i] != -1) {
				sum += ((avgP1-p1[i]) *(avgP2- p2[i]));
				 qtdeRates++;
			}
			if (p1[i]!=-1){
				sumP1 += FastMath.pow((avgP1-p1[i]), 2);
			}
			if (p2[i]!=-1){
				sumP2 += FastMath.pow((avgP2-p2[i]), 2);
			}
			}
		

		return qtdeRates == 0 ? Double.MAX_VALUE :(1-(sum / (FastMath
				.sqrt(sumP1) * FastMath.sqrt(sumP2))));
	}

}
