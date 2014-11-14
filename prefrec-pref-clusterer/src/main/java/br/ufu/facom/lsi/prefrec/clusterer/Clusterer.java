package br.ufu.facom.lsi.prefrec.clusterer;

import gov.sandia.cognition.learning.algorithm.clustering.AffinityPropagation;
import gov.sandia.cognition.math.DivergenceFunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import br.ufu.facom.lsi.prefrec.clusterer.distance.CosineDistance;
import br.ufu.facom.lsi.prefrec.clusterer.distance.MyEuclideanDistance;
import br.ufu.facom.lsi.prefrec.clusterer.distance.MySimilarityDivergence;
import br.ufu.facom.lsi.prefrec.representation.controller.PrefMatrixScorer;

/**
 * @author Klerisson
 * 
 */
public class Clusterer {

	private Map<DoublePoint, Long> userScoreDoublePoint = new LinkedHashMap<>();
	private Map<Long, List<Map<Long, Double[][]>>> cluster;
	private int matrixLength;

	/**
	 * @param args
	 */
	public void execute(PrefMatrixScorer pms, String[] args) {

		try {
			System.out.println("Inicio: " + new Date());
			// PrefMatrixScorer pms = new PrefMatrixScorer();
			// pms.deserialize();

			// cluster
			switch (args[0]) {
			case "DBSCAN":
				DistanceMeasure dm = null;
				if (args[1].equals("euclidean")) {
					dm = new MyEuclideanDistance();
				} else {
					dm = new CosineDistance();
				}
				DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(
						Double.parseDouble(args[2]), Integer.parseInt(args[3]),
						dm);

				List<Cluster<DoublePoint>> clusters = dbscan
						.cluster(toDoublePointList(pms));
				System.out.println(clusters.size());
				this.cluster = clusterVectorToMatrix(clusters, pms);
				break;

			case "FUZZY":
				FuzzyKMeansClusterer<DoublePoint> fkmc = new FuzzyKMeansClusterer<DoublePoint>(
						Integer.parseInt(args[1]), Double.parseDouble(args[2]));

				List<CentroidCluster<DoublePoint>> clustersFuzzy = fkmc
						.cluster(toDoublePointList(pms));

				this.cluster = clusterVectorToMatrix(clustersFuzzy, pms);
				break;

			case "AFFINITY":
				AffinityPropagation<DoublePoint> ap = new AffinityPropagation<>();
				DivergenceFunction<DoublePoint, DoublePoint> df = new MySimilarityDivergence();
				ap.setDivergence(df);
				ap.learn(toDoublePointList(pms));

				List<gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster<DoublePoint>> clustersAff = ap
						.getResult();
				System.out.println(clustersAff.size());

				this.cluster = clusterAffinityVectorToMatrix(clustersAff, pms);
				break;

			case "KMEANSPLUSPLUS":
				MyEuclideanDistance distance = new MyEuclideanDistance();
				KMeansPlusPlusClusterer<DoublePoint> kMeanPlusPlus = new KMeansPlusPlusClusterer<>(
						7, -1, distance);
				List<CentroidCluster<DoublePoint>> clustersMKmeansPlusPlus = kMeanPlusPlus
						.cluster(toDoublePointList(pms));
				this.cluster = clusterVectorToMatrix(clustersMKmeansPlusPlus,
						pms);
				break;

			case "MULTIKMEANS":
				KMeansPlusPlusClusterer<DoublePoint> kMean = new KMeansPlusPlusClusterer<>(
						3);
				MultiKMeansPlusPlusClusterer<DoublePoint> mKMeans = new MultiKMeansPlusPlusClusterer<>(
						kMean, 200);
				List<CentroidCluster<DoublePoint>> clustersMKmeans = mKMeans
						.cluster(toDoublePointList(pms));
				this.cluster = clusterVectorToMatrix(clustersMKmeans, pms);
				break;

			default:

				Map<Long, List<Map<Long, Double[][]>>> matrix = new LinkedHashMap<>();

				List<Map<Long, Double[][]>> clusterList = new ArrayList<>();
				for (Long id : pms.getMatrixMap().keySet()) {
					
					Map<Long, Double[][]> userMatrix = new LinkedHashMap<>();
					Double[][] matrixDouble = pms.getMatrixMap().get(id);
					userMatrix.put(id, matrixDouble);
					clusterList.add(userMatrix);
					
				}
				matrix.put(0L, clusterList);
				this.cluster = matrix;
				break;
			}
			System.out.println("Fim: " + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<Long, List<Map<Long, Double[][]>>> clusterAffinityVectorToMatrix(
			List<gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster<DoublePoint>> clusters,
			PrefMatrixScorer pms) {

		Map<Long, List<Map<Long, Double[][]>>> matrix = new LinkedHashMap<>();

		for (Long i = 0L; i < clusters.size(); i++) {
			List<Map<Long, Double[][]>> clusterList = new ArrayList<>();

			for (DoublePoint dp : clusters.get(i.intValue()).getMembers()) {

				Map<Long, Double[][]> userMatrix = new LinkedHashMap<>();

				Long id = userScoreDoublePoint.get(dp);
				Double[][] matrixDouble = pms.getMatrixMap().get(id);

				// for(int k = 0; k < matrixDouble.length; k++){
				// for(int h = 0; h < matrixDouble[k].length; h++){

				// System.out.print(matrixDouble[k][h]);
				// System.out.print(" ");

				// }
				// System.out.println();
				// }

				userMatrix.put(id, matrixDouble);
				clusterList.add(userMatrix);
			}
			matrix.put(i, clusterList);
		}
		return matrix;
	}

	// private void serializeOutputs(
	// Map<Long, List<Map<Long, Double[][]>>> cluster) throws Exception {
	//
	// try (FileOutputStream fout = new FileOutputStream(
	// "../ClusterOutput.prefrecCluster");
	// ObjectOutputStream oos = new ObjectOutputStream(fout)) {
	//
	// oos.writeObject(cluster);
	// oos.flush();
	// fout.flush();
	//
	// } catch (Exception e) {
	// throw e;
	// }
	// }

	private <T extends Cluster<DoublePoint>> Map<Long, List<Map<Long, Double[][]>>> clusterVectorToMatrix(
			List<T> clusters, PrefMatrixScorer pms) {

		Map<Long, List<Map<Long, Double[][]>>> matrix = new LinkedHashMap<>();

		for (Long i = 0L; i < clusters.size(); i++) {
			List<Map<Long, Double[][]>> clusterList = new ArrayList<>();

			for (DoublePoint dp : clusters.get(i.intValue()).getPoints()) {

				Map<Long, Double[][]> userMatrix = new LinkedHashMap<>();

				Long id = userScoreDoublePoint.get(dp);
				Double[][] matrixDouble = pms.getMatrixMap().get(id);

				userMatrix.put(id, matrixDouble);
				clusterList.add(userMatrix);
			}
			matrix.put(i, clusterList);
		}

		return matrix;

	}

	private List<DoublePoint> toDoublePointList(PrefMatrixScorer pms) {

		List<DoublePoint> result = new ArrayList<>();

		for (Long key : pms.getMatrixMap().keySet()) {
			Double[][] d = pms.getMatrixMap().get(key);
			List<Double> pointList = new ArrayList<>();
			matrixLength = d.length;
			for (int i = 0; i < matrixLength; i++) {
				for (int j = i + 1; j < d[i].length; j++) {
					if (d[i][j] == null) {
						d[i][j] = 0.0;
						pointList.add(d[i][j]);
					} else {
						pointList.add(d[i][j]);
					}
				}
			}
			// Create double point and add to the result list
			Double[] ds = pointList.toArray(new Double[pointList.size()]);
			DoublePoint dp = new DoublePoint(ArrayUtils.toPrimitive(ds));
			result.add(dp);
			// Create a map to link user score with the doublepoint instead of
			// the matrix score
			userScoreDoublePoint.put(dp, key);
		}
		return result;
	}

	/**
	 * @return the cluster
	 */
	public Map<Long, List<Map<Long, Double[][]>>> getCluster() {
		return cluster;
	}

}
