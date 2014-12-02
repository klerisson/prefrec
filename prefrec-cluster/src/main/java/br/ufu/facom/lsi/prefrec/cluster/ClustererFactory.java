package br.ufu.facom.lsi.prefrec.cluster;

import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansImpl;
import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansPlusPlusImpl;

public class ClustererFactory {

	public static Builder<Clusterer> getClusterBuilder(ClusterEnum clusterType) {

		switch (clusterType) {
		case KMEANS:
			return new KMeansImpl.KMeansBuilder();
		case KMEANS_PLUS_PLUS:
			return new KMeansPlusPlusImpl.KMeansPlusPlusBuilder();
		default:
			return null;
		}
	}
}
