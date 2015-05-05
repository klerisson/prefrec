package br.ufu.facom.lsi.prefrec.execution;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import br.ufu.facom.lsi.prefrec.cluster.distance.MyEuclideanDistance;
import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansImpl.KMeansBuilder;
import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import br.ufu.facom.lsi.prefrec.representation.RepresenterEnum;
import br.ufu.facom.lsi.prefrec.representation.RepresenterFacotry;
import br.ufu.facom.lsi.prefrec.representation.impl.LeaveOneOutTesterRepresenter;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.XPrefRecSocial;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.XPrefRecSocialThreshold;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.impl.FriendshipStrenghtTie;
import br.ufu.facom.lsi.prefrec.util.AppPropertiesEnum;
import br.ufu.facom.lsi.prefrec.util.PropertiesUtil;

public class ExecuteLeaveOneOut {

	public static void run(List<Long> usersId) throws Exception {
		// int h=1;
		for (int h = 6; h <= 8; h++) {
			for ( int q = 0; q < usersId.size(); q++) {
				Long currentUserId = usersId.get(q);
				
				Representer representer = RepresenterFacotry
						.getRepresenter(RepresenterEnum.LEAVE_ONE_OUT);
				UtilityMatrix utilityMatrix = representer
						.createUtilityMatrix(currentUserId.intValue());

				KMeansBuilder clustererBuilder = (KMeansBuilder) ClustererFactory
						.getClusterBuilder(ClusterEnum.KMEANS);
				Clusterer clusterer = clustererBuilder.clustersNumber(h)
						.measure(new MyEuclideanDistance())
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
				// double silhouete = clusterer.silhouetteCalc();
				// System.out.println(silhouete);
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

					// ////// Write SocialStream input files
					//serializeCluster(cluster, clusterer.getClusterCenters(),
						//	itemMap, h, currentUserId);
					// //////////////////////////

					miner.buildModels(new ArrayList<Double[][]>(agregator
							.getConcensualMatrixMap().keySet()), itemMap);

				} catch (Exception e1) {
					throw e1;
				}

				// XPrefRec xprefrec = new XPrefRec(
				// agregator.getConcensualMatrixMap(), miner);
				XPrefRec xprefrec = new XPrefRecSocialThreshold(
						agregator.getConcensualMatrixMap(), miner,
						new FriendshipStrenghtTie(), 0.001);

				// XPrefRec xprefrec = new XPrefRecSocialAverage(
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
					Map<Integer, Double> itemsIdToRate = testerRepresenter
							.getValidationItems(userId, testerUtilityMatrix
									.getUniqueItemIds(),
									(xprefrec instanceof XPrefRecSocial) ? true
											: false);

					if (itemsIdToRate != null && itemsIdToRate.size() > 1) {
						try {

							// Float[] precisionRecall = xprefrec.run(userId,
							// testersPrefMatrixMap.get(userId),
							// itemsIdToRate, null, testerUtilityMatrix);

							// Find consensual matrix by centroid
							Float[] precisionRecall = xprefrec.run(userId,
									testersPrefMatrixMap.get(userId),
									itemsIdToRate,
									clusterer.getClusterCenters(),
									testerUtilityMatrix,
									utilityMatrix.getUniqueItemIds());

							if (precisionRecall != null) {
								writeOutput(userId, precisionRecall, h,
										ratedSize);
							}

						} catch (Exception e) {
							e.printStackTrace();
							writeOutput(userId, new Float[] { -1f, -1f, -1f },
									h, ratedSize);
						}
					}

				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	private static void serializeCluster(Map<Long, List<User>> cluster,
			Map<Long, Double[]> centroid, TreeMap<Integer, Integer> itemMap,
			int id, Long userId) {

		try {

			FileOutputStream fout = new FileOutputStream(
					"/Users/cricia/Documents/workspace-sts-3.4.0.RELEASE/SocialStream/input/"
							+ id + "_" + userId + ".cluster");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(cluster);
			oos.close();

			fout = new FileOutputStream(
					"/Users/cricia/Documents/workspace-sts-3.4.0.RELEASE/SocialStream/input/"
							+ id + "_" + userId + ".centroid");
			oos = new ObjectOutputStream(fout);
			oos.writeObject(centroid);
			oos.close();

			fout = new FileOutputStream(
					"/Users/cricia/Documents/workspace-sts-3.4.0.RELEASE/SocialStream/input/"
							+ id + "_" + userId + ".itemMap");
			oos = new ObjectOutputStream(fout);
			oos.writeObject(itemMap);
			oos.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void writeOutput(Long userId, Float[] precisionRecall,
			int fileId, int ratedS) {

		try {
			StringBuilder msg = new StringBuilder();
			msg.append(userId).append(";").append(precisionRecall[0])
					.append(";").append(precisionRecall[1]).append(";")
					.append(precisionRecall[2]).append(System.lineSeparator());

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
