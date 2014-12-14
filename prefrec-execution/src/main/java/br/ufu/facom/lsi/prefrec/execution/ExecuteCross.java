package br.ufu.facom.lsi.prefrec.execution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import br.ufu.facom.lsi.prefrec.agregation.Agregator;
import br.ufu.facom.lsi.prefrec.agregation.PreferenceMatrix;
import br.ufu.facom.lsi.prefrec.cluster.ClusterEnum;
import br.ufu.facom.lsi.prefrec.cluster.Clusterer;
import br.ufu.facom.lsi.prefrec.cluster.ClustererFactory;
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansClusterer.CentroidStrategy;
import br.ufu.facom.lsi.prefrec.cluster.distance.MyEuclideanDistance;
import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansImpl.KMeansBuilder;
import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import br.ufu.facom.lsi.prefrec.representation.RepresenterEnum;
import br.ufu.facom.lsi.prefrec.representation.RepresenterFacotry;
import br.ufu.facom.lsi.prefrec.representation.impl.CrossValidationTestersRepresenter;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.XPrefRecSocialAverage;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.XPrefRecSocialThreshold;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.impl.CentralityStrenghtTie;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.impl.FriendshipStrenghtTie;

public class ExecuteCross {

	public static void run(int partitions) throws Exception {

		for (int i = 0; i < partitions; i++) {

			UtilityMatrix utilityMatrix = null;
			Representer representer = RepresenterFacotry
					.getRepresenter(RepresenterEnum.CROSS_VALIDATION);
			utilityMatrix = representer.createUtilityMatrix(i);

			KMeansBuilder clustererBuilder = (KMeansBuilder) ClustererFactory
					.getClusterBuilder(ClusterEnum.KMEANS);
			Clusterer clusterer = clustererBuilder.clustersNumber(2)
					.measure(new MyEuclideanDistance())
					.centroidStrategy(CentroidStrategy.MAJORITY).build();

			// KMeansPlusPlusBuilder clustererBuilder = (KMeansPlusPlusBuilder)
			// ClustererFactory
			// .getClusterBuilder(ClusterEnum.KMEANS_PLUS_PLUS);
			// Clusterer clusterer = clustererBuilder.clustersNumber(4)
			// .measure(new MyEuclideanDistance()).build();

			Map<Long, List<User>> cluster = clusterer.cluster(utilityMatrix);
			// System.out.println(cluster.size());
			for (Long id : cluster.keySet()) {
				System.out.println(cluster.get(id).size());

			}
			System.out.println();
			System.out.println(i);
			System.out.println();

			Agregator agregator = new Agregator(cluster, utilityMatrix);
			agregator.execute();

			Miner miner = new Miner();
			try {

				TreeMap<Integer, Integer> itemMap = new TreeMap<>();
				int cont = 0;
				for (Long id : utilityMatrix.getUniqueItemIds()) {
					// create map index to itemid
					itemMap.put(cont, id.intValue());
					cont++;
				}

				miner.buildModels(new ArrayList<Double[][]>(agregator
						.getConcensualMatrixMap().keySet()), itemMap);
 
			} catch (Exception e1) {
				throw e1;
			}

			//XPrefRec xprefrec = new XPrefRec(
			 //agregator.getConcensualMatrixMap(), miner);

			//XPrefRec xprefrec = new XPrefRecSocialAverage(
				//	agregator.getConcensualMatrixMap(), miner,
					//new CentralityStrenghtTie());
			
			XPrefRec xprefrec = new XPrefRecSocialThreshold(
					agregator.getConcensualMatrixMap(), miner,
					new CentralityStrenghtTie(),.001);

			for (int j = 0; j < partitions; j++) {

				CrossValidationTestersRepresenter testerRepresenter = (CrossValidationTestersRepresenter) RepresenterFacotry
						.getRepresenter(RepresenterEnum.CROSS_VALIDATION_TESTER);

				UtilityMatrix testerUtilityMatrix = testerRepresenter
						.createUtilityMatrix(i, j);
				Map<Long, Double[][]> testersPrefMatrixMap = PreferenceMatrix
						.build(testerUtilityMatrix);

				for (Entry<Long, Double[][]> entry : testersPrefMatrixMap
						.entrySet()) {

					Map<Integer, Double> itemIdToRate;
					try {
						itemIdToRate = CrossValidationTestersRepresenter
								.fetchValidationFold(entry.getKey().intValue(),
										j);
						if (itemIdToRate != null && itemIdToRate.size() > 1) {
							try {
								Float[] precisionRecall = xprefrec
										.run(entry.getKey(), entry.getValue(),
												itemIdToRate, null,
												testerUtilityMatrix);

								// Find consensual matrix by centroid
								// Float[] precisionRecall =
								// xprefrec.run(entry.getKey()
								// , entry.getValue(),
								// itemIdToRate, clusterer.getClusterCenters(),
								// testerUtilityMatrix);

								if (precisionRecall != null) {
									writeOutput(entry.getKey(), i, j,
											precisionRecall);
								}

							} catch (Exception e) {
								writeOutput(entry.getKey(), i, j, new Float[] {
										-1f, -1f });
							}
						}
					} catch (Exception e) {
						throw e;
					}
				}
			}
		}
	}

	private static void writeOutput(Long userId, int userFoldId,
			int itemFoldId, Float[] precisionRecall) {

		try {

			StringBuilder msg = new StringBuilder();
			msg.append(userId).append(";").append(userFoldId).append(";")
					.append(itemFoldId).append(";").append(precisionRecall[0])
					.append(";").append(precisionRecall[1])
					.append(System.lineSeparator());

			if (!Files.exists(Paths.get("./output.cvs"))) {
				Files.createFile(Paths.get("./output.cvs"));
			}
			Files.write(Paths.get("./output.cvs"), msg.toString().getBytes(),
					StandardOpenOption.APPEND);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
