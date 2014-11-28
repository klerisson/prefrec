package br.ufu.facom.lsi.prefrec.cluster;

import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansImpl;

public class ClustererFactory {

	public static Builder<Clusterer> getClusterBuilder(ClusterEnum clusterType) {

		switch (clusterType) {
		case KMEANS:
			return new KMeansImpl.KMeansBuilder();
		default:
			return null;
		}
	}
}
