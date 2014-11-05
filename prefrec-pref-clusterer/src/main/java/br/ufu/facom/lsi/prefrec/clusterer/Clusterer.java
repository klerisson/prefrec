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
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import br.ufu.facom.lsi.prefrec.clusterer.distance.CosineDistance;
import br.ufu.facom.lsi.prefrec.clusterer.distance.SimilarityDivergence;
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
			System.out.println("Início: " + new Date());
			// PrefMatrixScorer pms = new PrefMatrixScorer();
			// pms.deserialize();

			// cluster
			switch (args[0]) {
			case "DBSCAN":
				DistanceMeasure dm = null;
				if (args[1].equals("euclidean")) {
					dm = new EuclideanDistance();
				} else {
					dm = new CosineDistance();
				}
				DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(
						Integer.parseInt(args[2]), Integer.parseInt(args[3]),
						dm);

				List<Cluster<DoublePoint>> clusters = dbscan
						.cluster(toDoublePointList(pms));
				this.cluster = clusterVectorToMatrix(clusters);
				break;

			case "FUZZY":
				FuzzyKMeansClusterer<DoublePoint> fkmc = new FuzzyKMeansClusterer<DoublePoint>(
						Integer.parseInt(args[2]), Integer.parseInt(args[3]));

				List<CentroidCluster<DoublePoint>> clustersFuzzy = fkmc
						.cluster(toDoublePointList(pms));

				this.cluster = clusterVectorToMatrix(clustersFuzzy);
				break;

			case "AFFINITY":
				AffinityPropagation<DoublePoint> ap = new AffinityPropagation<>();
				DivergenceFunction<DoublePoint, DoublePoint> df = new SimilarityDivergence();
				ap.setDivergence(df);
				ap.learn(toDoublePointList(pms));

				List<gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster<DoublePoint>>
				clustersAff = ap.getResult();
				
				this.cluster = clusterAffinityVectorToMatrix(clustersAff);
				break;
				
			case "KMEANSPLUSPLUS":
				KMeansPlusPlusClusterer<DoublePoint> kMeanPlusPlus = new KMeansPlusPlusClusterer<>(2);
				List<CentroidCluster<DoublePoint>> clustersMKmeansPlusPlus = kMeanPlusPlus.cluster(toDoublePointList(pms));
				this.cluster =  clusterVectorToMatrix(clustersMKmeansPlusPlus);
				break;
				
			case "MULTIKMEANS":
				KMeansPlusPlusClusterer<DoublePoint> kMean = new KMeansPlusPlusClusterer<>(3);
				MultiKMeansPlusPlusClusterer<DoublePoint> mKMeans = new MultiKMeansPlusPlusClusterer<>(kMean, 5);
				List<CentroidCluster<DoublePoint>> clustersMKmeans = mKMeans.cluster(toDoublePointList(pms));
				this.cluster =  clusterVectorToMatrix(clustersMKmeans);
				break;
			}
	
			System.out.println("Fim: " + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<Long, List<Map<Long, Double[][]>>> clusterAffinityVectorToMatrix(
			List<gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster<DoublePoint>> clusters) {
		
		Map<Long, List<Map<Long, Double[][]>>> matrix = new LinkedHashMap<>();

		for (Long i = 0L; i < clusters.size(); i++) {
			List<Map<Long, Double[][]>> clusterList = new ArrayList<>();

			for (DoublePoint dp : clusters.get(i.intValue()).getMembers()) {

				Map<Long, Double[][]> userMatrix = new LinkedHashMap<>();

				Long id = userScoreDoublePoint.get(dp);
				Double[][] matrixDouble = new Double[matrixLength][matrixLength];

				int index = 0;
				for (int j = 0; j < matrixLength; j++) {
					for (int k = 0; k < matrixLength; k++) {
						matrixDouble[j][k] = dp.getPoint()[index];
						index++;
					}
				}
				userMatrix.put(id, matrixDouble);
				clusterList.add(userMatrix);
			}
			matrix.put(i, clusterList);
		}
		return matrix;
	}

//	private void serializeOutputs(
//			Map<Long, List<Map<Long, Double[][]>>> cluster) throws Exception {
//
//		try (FileOutputStream fout = new FileOutputStream(
//				"../ClusterOutput.prefrecCluster");
//				ObjectOutputStream oos = new ObjectOutputStream(fout)) {
//
//			oos.writeObject(cluster);
//			oos.flush();
//			fout.flush();
//
//		} catch (Exception e) {
//			throw e;
//		}
//	}

	private <T extends Cluster<DoublePoint>> Map<Long, List<Map<Long, Double[][]>>> clusterVectorToMatrix(
			List<T> clusters) {

		Map<Long, List<Map<Long, Double[][]>>> matrix = new LinkedHashMap<>();

		for (Long i = 0L; i < clusters.size(); i++) {
			List<Map<Long, Double[][]>> clusterList = new ArrayList<>();

			for (DoublePoint dp : clusters.get(i.intValue()).getPoints()) {

				Map<Long, Double[][]> userMatrix = new LinkedHashMap<>();

				Long id = userScoreDoublePoint.get(dp);
				Double[][] matrixDouble = new Double[matrixLength][matrixLength];

				int index = 0;
				for (int j = 0; j < matrixLength; j++) {
					for (int k = 0; k < matrixLength; k++) {
						matrixDouble[j][k] = dp.getPoint()[index];
						index++;
					}
				}
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
				for (int j = 0; j < d[i].length; j++) {
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
