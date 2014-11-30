/**
 * 
 */
package br.ufu.facom.lsi.prefrec.cluster.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.random.JDKRandomGenerator;

import br.ufu.facom.lsi.prefrec.cluster.Builder;
import br.ufu.facom.lsi.prefrec.cluster.Clusterer;
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansClusterer;
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansClusterer.CentroidStrategy;
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansPlusPlusClusterer.EmptyClusterStrategy;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;

/**
 * @author Klerisson
 *
 */
public class KMeansImpl extends Clusterer{

	private KMeansClusterer<DoublePoint> kMeans;
	private Map<Long, Double[]> clusterCenters;

	private KMeansImpl(KMeansBuilder builder) {
		this.kMeans = new KMeansClusterer<DoublePoint>(builder.clustersNumber,
				-1, builder.measure, new JDKRandomGenerator(),
				EmptyClusterStrategy.LARGEST_VARIANCE, builder.centroidStrategy);
	}

	@Override
	public Map<Long, List<User>> cluster(UtilityMatrix utilityMatrix) throws Exception {
		
		Map<DoublePoint, User> doublePointMap = this.toDoublePointMap(utilityMatrix);
		List<DoublePoint> doublePointList = new ArrayList<>();
		for(DoublePoint dp : doublePointMap.keySet()){
			doublePointList.add(dp);
		}

		List<CentroidCluster<DoublePoint>> clustersKmeans = kMeans.cluster(doublePointList);
		fetchClustersCenter(clustersKmeans);
		return clusterVectorToClusterUser(clustersKmeans, doublePointMap);
	}

	/**
	 * @param clustersKmeans
	 */
	private void fetchClustersCenter(
			List<CentroidCluster<DoublePoint>> clustersKmeans) {
		this.clusterCenters = new HashMap<>();
		Iterator<CentroidCluster<DoublePoint>> it = clustersKmeans.iterator();
		int clusterIdx = 0;
		while (it.hasNext()) {
			final Long clusterIdxLong = new Long(clusterIdx);
			Double[] points = ArrayUtils.toObject(it.next().getCenter().getPoint());
			this.clusterCenters.put(clusterIdxLong, points);
			clusterIdx++;
		}
	}
	
	/**
	 * @return the clusterCenters
	 */
	public Map<Long, Double[]> getClusterCenters() {
		return clusterCenters;
	}

	public static class KMeansBuilder implements Builder<Clusterer> {

		private int clustersNumber;
		private DistanceMeasure measure;
		private CentroidStrategy centroidStrategy;

		public KMeansBuilder clustersNumber(int clustersNumber) {
			this.clustersNumber = clustersNumber;
			return this;
		}

		public KMeansBuilder measure(DistanceMeasure measure) {
			this.measure = measure;
			return this;
		}

		public KMeansBuilder centroidStrategy(CentroidStrategy centroidStrategy) {
			this.centroidStrategy = centroidStrategy;
			return this;
		}

		public KMeansImpl build() {
			return new KMeansImpl(this);
		}

	}
}
