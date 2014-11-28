package br.ufu.facom.lsi.prefrec.cluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;

public abstract class Clusterer {

	public abstract Map<Long, List<User>> cluster(
			UtilityMatrix utilityMatrix) throws Exception;

	protected Map<DoublePoint, User> toDoublePointMap(
			UtilityMatrix utilityMatrix) {

		LinkedHashMap<DoublePoint, User> doublePointToUser = new LinkedHashMap<>();
		List<DoublePoint> result = new ArrayList<>();

		for (User user : utilityMatrix.getUsers()) {
			// Create double point and add to the result list
			Double[] ds = user.getItemsRateDoubleArray();
			DoublePoint dp = new DoublePoint(ArrayUtils.toPrimitive(ds));
			result.add(dp);
			
			// Create a map to link user score with the doublepoint instead of
			// the matrix score
			doublePointToUser.put(dp, user);
		}
		return doublePointToUser;
	}

	protected <T extends Cluster<DoublePoint>> Map<Long, List<User>> clusterVectorToClusterUser(
			List<T> clusters, Map<DoublePoint, User> doublePointMap) {

		Map<Long, List<User>> result = new LinkedHashMap<>();
		Iterator<T> it = clusters.iterator();
		int clusterIdx = 0;
		while (it.hasNext()) {
			final Long clusterIdxLong = new Long(clusterIdx);
			List<User> usersInCluster = new ArrayList<>();
			for (DoublePoint dp : it.next().getPoints()) {
				User u = doublePointMap.get(dp);
				usersInCluster.add(u);
			}
			result.put(clusterIdxLong, usersInCluster);
			clusterIdx++;
		}
		return result;
	}

}
