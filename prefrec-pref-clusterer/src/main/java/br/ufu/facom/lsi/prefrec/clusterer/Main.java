package br.ufu.facom.lsi.prefrec.clusterer;

import gov.sandia.cognition.learning.algorithm.clustering.AffinityPropagation;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterCentroidDivergenceFunction;
import gov.sandia.cognition.math.DivergenceFunction;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import br.ufu.facom.lsi.prefrec.clusterer.distance.SimilarityDivergence;
import br.ufu.facom.lsi.prefrec.representation.controller.PrefMatrixScorer;

/**
 * @author Klerisson
 *
 */
public class Main {

	private static Map<DoublePoint, Long> userScoreDoublePoint = new LinkedHashMap<>();
	private static int matrixLength;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			System.out.println("Início: " + new Date());
			PrefMatrixScorer pms = new PrefMatrixScorer();
			pms.deserialize();

			// CosineDistance cd = new CosineDistance();
//			EuclideanDistance ed = new EuclideanDistance();
//			DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<DoublePoint>(
//					20, 4, ed);
//
//			List<Cluster<DoublePoint>> clusters = dbscan
//					.cluster(toDoublePointList(pms));
//
//			System.out.println(clusters.size());

//			for (Cluster<DoublePoint> c : clusters) {
//				
//				Iterator<DoublePoint> it = c.getPoints().iterator();
//				while(it.hasNext()) {
//					DoublePoint dp = (DoublePoint) it.next();
//					for (Object o : c.getPoints()) {
//
//						DoublePoint dpTemp = (DoublePoint) o;
//						System.out.println(ed.compute(dp.getPoint(),
//								dpTemp.getPoint()));
//					}
//				}
//
//			}
			
			FuzzyKMeansClusterer<DoublePoint> fkmc = new FuzzyKMeansClusterer<DoublePoint>(3, 3);
			List<CentroidCluster<DoublePoint>> clustersFuzzy = fkmc.cluster(toDoublePointList(pms));
			System.out.println(clustersFuzzy.size());
			

			//==========================
//			AffinityPropagation<DoublePoint> ap = new AffinityPropagation<>();
//			DivergenceFunction<DoublePoint, DoublePoint> df = new SimilarityDivergence();
//			ap.setDivergence(df);
//			ap.learn(toDoublePointList(pms));
//			
//			List<gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster<DoublePoint>> clustersAff = ap.getResult();
//			System.out.println(clustersAff.size());
//			
			
			// Create the output
//			serializeOutputs(clusterVectorToMatrix(clusters));
			System.out.println("Fim: " + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void serializeOutputs(
			Map<Long, List<Map<Long, Double[][]>>> cluster) throws Exception {

		try (FileOutputStream fout = new FileOutputStream(
				"../ClusterOutput.prefrecCluster");
				ObjectOutputStream oos = new ObjectOutputStream(fout)) {

			oos.writeObject(cluster);
			oos.flush();
			fout.flush();

		} catch (Exception e) {
			throw e;
		}
	}

	private static Map<Long, List<Map<Long, Double[][]>>> clusterVectorToMatrix(
			List<Cluster<DoublePoint>> clusters) {

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

	private static List<DoublePoint> toDoublePointList(PrefMatrixScorer pms) {

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

}
