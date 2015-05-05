package br.ufu.facom.lsi.prefrec.execution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.ufu.facom.lsi.prefrec.agregation.Agregator;
import br.ufu.facom.lsi.prefrec.agregation.PreferenceMatrix;
import br.ufu.facom.lsi.prefrec.cluster.ClusterEnum;
import br.ufu.facom.lsi.prefrec.cluster.Clusterer;
import br.ufu.facom.lsi.prefrec.cluster.ClustererFactory;
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansClusterer.CentroidStrategy;
import br.ufu.facom.lsi.prefrec.cluster.distance.CosineDistanceNormalized;
import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansImpl.KMeansBuilder;
import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import br.ufu.facom.lsi.prefrec.representation.RepresenterEnum;
import br.ufu.facom.lsi.prefrec.representation.RepresenterFacotry;
import br.ufu.facom.lsi.prefrec.representation.impl.LeaveOneOutTesterRepresenter;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.XPrefRecSocialThreshold;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.impl.InteractionStrenghtTie;
import br.ufu.facom.lsi.prefrec.util.AppPropertiesEnum;
import br.ufu.facom.lsi.prefrec.util.PropertiesUtil;

public class ExecuteLeaveOneOutEnsemble {

	public static void run(List<Long> usersId) throws Exception {
		// int h=1;
		for (int h = 2; h <=5; h++) {
			for (Long currentUserId : usersId) {

				Representer representer = RepresenterFacotry
						.getRepresenter(RepresenterEnum.LEAVE_ONE_OUT);
				UtilityMatrix utilityMatrix = representer
						.createUtilityMatrix(currentUserId.intValue());

				KMeansBuilder clustererBuilder = (KMeansBuilder) ClustererFactory
						.getClusterBuilder(ClusterEnum.KMEANS);
				Clusterer clusterer = clustererBuilder.clustersNumber(h)
						.measure(new CosineDistanceNormalized())
						.centroidStrategy(CentroidStrategy.AVERAGE).build();

				// KMeansPlusPlusBuilder clustererBuilder =
				// (KMeansPlusPlusBuilder)
				// ClustererFactory
				// .getClusterBuilder(ClusterEnum.KMEANS_PLUS_PLUS);
				// Clusterer clusterer = clustererBuilder.clustersNumber(3)
				// .measure(new MyEuclideanDistance()).build();

				Map<Long, List<User>> cluster = clusterer
						.cluster(utilityMatrix);
				// System.out.println(cluster.size());
				for (Long id : cluster.keySet()) {
					System.out.println(cluster.get(id).size());

				}
				double silhouete = clusterer.silhouetteCalc();
				System.out.println(silhouete);
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

				XPrefRec xprefrec = new XPrefRec(
						agregator.getConcensualMatrixMap(), miner);

				XPrefRec xprefrecSocial = new XPrefRecSocialThreshold(
						agregator.getConcensualMatrixMap(), miner,
						new InteractionStrenghtTie(), 0.001);

				// XPrefRec xprefrecSocial = new XPrefRecSocialAverage(
				// agregator.getConcensualMatrixMap(), miner,
				// new FriendshipStrenghtTie());

				LeaveOneOutTesterRepresenter testerRepresenter = (LeaveOneOutTesterRepresenter) RepresenterFacotry
						.getRepresenter(RepresenterEnum.LEAVE_ONE_OUT_TESTER);

				UtilityMatrix testerUtilityMatrix = testerRepresenter
						.createUtilityMatrix(
								currentUserId.intValue(),
								Integer.parseInt(PropertiesUtil
										.getAppPropertie(AppPropertiesEnum.RATED_SIZE)));

				int ratedSize = Integer.parseInt(PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.RATED_SIZE));
				Map<Long, Double[][]> testersPrefMatrixMap = PreferenceMatrix
						.build(testerUtilityMatrix);

				try {
					Long userId = testersPrefMatrixMap.keySet().iterator()
							.next();
					User user = testerUtilityMatrix.getUserItemList(userId);

					Map<Integer, Double> itemsIdToRate = null;
					boolean isSocial = false;
					if (!user.hasFriends()
							|| (xprefrecSocial.findSimilarConcensualMatrix(
									userId, clusterer.getClusterCenters(),
									testerUtilityMatrix)) == null) {
						// nao social
						itemsIdToRate = testerRepresenter.getValidationItems(
								userId, testerUtilityMatrix.getUniqueItemIds(),
								false);

					} else {
						// Social
						itemsIdToRate = testerRepresenter.getValidationItems(
								userId, testerUtilityMatrix.getUniqueItemIds(),
								true);
						isSocial = true;
					}

					if (itemsIdToRate != null && itemsIdToRate.size() > 1) {
						try {

							// Float[] precisionRecall = xprefrec.run(userId,
							// testersPrefMatrixMap.get(userId),
							// itemsIdToRate, null, testerUtilityMatrix);

							// Find consensual matrix by centroid

							Float[] precisionRecall;
							if (isSocial) {
								precisionRecall = xprefrecSocial.run(userId,
										testersPrefMatrixMap.get(userId),
										itemsIdToRate,
										clusterer.getClusterCenters(),
										testerUtilityMatrix);
							} else {
								precisionRecall = xprefrec.run(userId,
										testersPrefMatrixMap.get(userId),
										itemsIdToRate,
										clusterer.getClusterCenters(),
										testerUtilityMatrix);
							}
							if (precisionRecall != null) {
								writeOutput(userId, precisionRecall, h,
										ratedSize);
							}

						} catch (Exception e) {
							// writeOutput(userId, new Float[] { -1f, -1f
							// },h,ratedSize);
						}
					}

				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	private static void writeOutput(Long userId, Float[] precisionRecall,
			int fileId, int ratedS) {

		try {
			StringBuilder msg = new StringBuilder();
			msg.append(userId).append(";").append(precisionRecall[0])
					.append(";").append(precisionRecall[1])
					.append(System.lineSeparator());

			if (!Files.exists(Paths.get("./output-" + fileId + "-" + ratedS
					+ ".cvs"))) {
				Files.createFile(Paths.get("./output-" + fileId + "-" + ratedS
						+ ".cvs"));
			}
			Files.write(
					Paths.get("./output-" + fileId + "-" + ratedS + ".cvs"),
					msg.toString().getBytes(), StandardOpenOption.APPEND);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

