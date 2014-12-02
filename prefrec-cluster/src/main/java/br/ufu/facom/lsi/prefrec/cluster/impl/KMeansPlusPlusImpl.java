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
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansPlusPlusClusterer;
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansPlusPlusClusterer.EmptyClusterStrategy;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;

/**
 * @author cricia
 *
 */
public class KMeansPlusPlusImpl extends Clusterer {

	private KMeansPlusPlusClusterer<DoublePoint> kMeansPlusPlus;
	private List<CentroidCluster<DoublePoint>> clusters;

	private KMeansPlusPlusImpl(KMeansPlusPlusBuilder builder) {
		this.kMeansPlusPlus = new KMeansPlusPlusClusterer<DoublePoint>(builder.clustersNumber,
				-1, builder.measure, new JDKRandomGenerator(),
				EmptyClusterStrategy.LARGEST_VARIANCE);
	}
	
	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.cluster.Clusterer#cluster(br.ufu.facom.lsi.prefrec.model.UtilityMatrix)
	 */
	@Override
	public Map<Long, List<User>> cluster(UtilityMatrix utilityMatrix)
			throws Exception {
		
		Map<DoublePoint, User> doublePointMap = this.toDoublePointMap(utilityMatrix);
		List<DoublePoint> doublePointList = new ArrayList<>();
		for(DoublePoint dp : doublePointMap.keySet()){
			doublePointList.add(dp);
		}
		List<CentroidCluster<DoublePoint>> clustersKmeans = kMeansPlusPlus.cluster(doublePointList);
		return clusterVectorToClusterUser(clustersKmeans, doublePointMap);
	}

	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.cluster.Clusterer#getClusterCenters()
	 */
	@Override
	public Map<Long, Double[]> getClusterCenters() {
		Map<Long, Double[]> clusterCenters = new HashMap<>();
		Iterator<CentroidCluster<DoublePoint>> it = clusters.iterator();
		int clusterIdx = 0;
		while (it.hasNext()) {
			final Long clusterIdxLong = new Long(clusterIdx);
			Double[] points = ArrayUtils.toObject(it.next().getCenter().getPoint());
			clusterCenters.put(clusterIdxLong, points);
			clusterIdx++;
		}
		return clusterCenters;
	}
	
	public static class KMeansPlusPlusBuilder implements Builder<Clusterer> {

		private int clustersNumber;
		private DistanceMeasure measure;

		public KMeansPlusPlusBuilder clustersNumber(int clustersNumber) {
			this.clustersNumber = clustersNumber;
			return this;
		}

		public KMeansPlusPlusBuilder measure(DistanceMeasure measure) {
			this.measure = measure;
			return this;
		}

		public KMeansPlusPlusImpl build() {
			return new KMeansPlusPlusImpl(this);
		}
	}
}
