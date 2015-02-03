package br.ufu.facom.lsi.prefrec.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;

public abstract class Clusterer {

	protected Map<Long, List<User>> clusterMap;

	protected DistanceMeasure distance;

	public abstract Map<Long, List<User>> cluster(UtilityMatrix utilityMatrix)
			throws Exception;

	/**
	 * @return the clusterCenters
	 */
	public abstract Map<Long, Double[]> getClusterCenters();

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

	public double silhouetteCalc() throws Exception {
		if (this.clusterMap == null) {
			throw new Exception("Clusters does not exit yet!");
		}
		//Cluster id to its coefficient
		Map<Long,Double> silhouetteClustersAvg = new HashMap<>();
		for (Long clusterId : this.clusterMap.keySet()) {
			LinkedList<User> users = new LinkedList<User>(
					clusterMap.get(clusterId));

			usersSilhoutteCalc(users);
			silhouetteClustersAvg.put(clusterId, calcSilhouetteClustersAvg(users));
		}
		
//		double a = 0.0;
//		for(Entry<Long, Double> entry : silhouetteClustersAvg.entrySet()){
//			a += entry.getValue();
//		}
//		a = a/silhouetteClustersAvg.keySet().size();
		
		
		silhouetteClustersAvg = new HashMap<>();
		for (Long clusterId : this.clusterMap.keySet()) {
			LinkedList<User> users = new LinkedList<User>(
					clusterMap.get(clusterId));

			usersSilhoutteCalc(users, getOutsiders(clusterId));
			silhouetteClustersAvg.put(clusterId, calcSilhouetteClustersAvg(users));
		}
		
		for(Entry<Long, List<User>> entry: clusterMap.entrySet()){
			for(User u : entry.getValue()){
				u.calcSilhouette();
			}
		}
		double allSilhouette = 0.0;
		int total = 0;
		for(Entry<Long, List<User>> entry: clusterMap.entrySet()){
			for(User u : entry.getValue()){
				allSilhouette += u.getSilhouette();
				total++;
			}
		}
		
		return allSilhouette / total;
	}

	private List<User> getOutsiders(Long clusterId) {
		List<User> result = new ArrayList<User>();
		for(Entry<Long, List<User>> entry : this.clusterMap.entrySet()){
			if(entry.getKey().equals(clusterId)){
				continue;
			}
			result.addAll(entry.getValue());
		}
		return result;
	}

	private void usersSilhoutteCalc(LinkedList<User> users, List<User> others) {
		
		Iterator<User> it = users.iterator();
		while (it.hasNext()) {
			User current = it.next();
			double dist = 0.0;
			//double minDist = 100.0;
			for (User other : others) {
				if(other.equals(current)){
					continue;
				}
				
				dist += this.distance.compute(ArrayUtils.toPrimitive(current
						.getItemsRateDoubleArray()), ArrayUtils
						.toPrimitive(other.getItemsRateDoubleArray()));
			}
			//System.out.println(dist);
			current.setSilhouetteExtraClusters(dist/others.size());
			//current.setSilhouetteExtraClusters(minDist);
		}
	}

	private Double calcSilhouetteClustersAvg(LinkedList<User> users) {
		Double result = 0.0;
		for(User u : users){
			result += u.getSilhoutteIntraCluster();
		}
		return result/users.size();
	}

	private void usersSilhoutteCalc(LinkedList<User> users) {
		//double dist = 0.0;
		Iterator<User> it = users.iterator();
		while (it.hasNext()) {
			double dist = 0.0;
			User current = it.next();
			for (User other : users) {
				if(other.equals(current)){
					continue;
				}
				dist += this.distance.compute(ArrayUtils.toPrimitive(current
						.getItemsRateDoubleArray()), ArrayUtils
						.toPrimitive(other.getItemsRateDoubleArray()));
			}
			if(users.size() >1){
				current.setSilhouetteIntraCluster(dist/(users.size() - 1));
			}
			else 
				current.setSilhouetteIntraCluster(0);
		}
	}
}
