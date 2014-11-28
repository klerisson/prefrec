package br.ufu.facom.lsi.prefrec.execution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.cluster.ClusterEnum;
import br.ufu.facom.lsi.prefrec.cluster.ClustererFactory;
import br.ufu.facom.lsi.prefrec.cluster.apache.KMeansClusterer.CentroidStrategy;
import br.ufu.facom.lsi.prefrec.cluster.distance.MyEuclideanDistance;
import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansImpl;
import br.ufu.facom.lsi.prefrec.cluster.impl.KMeansImpl.KMeansBuilder;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import br.ufu.facom.lsi.prefrec.representation.RepresenterEnum;
import br.ufu.facom.lsi.prefrec.representation.RepresenterFacotry;

public class Execute {

	public static void run(int partitions) throws Exception {

		for (int i = 0; i < partitions; i++) {
			
			UtilityMatrix utilityMatrix = null;
			Representer representer = RepresenterFacotry.getRepresenter(RepresenterEnum.CROSS_VALIDATION);
			try {
				utilityMatrix = representer.createUtilityMatrix(i);
			} catch (Exception e) {
				throw e;
			}
			
			KMeansBuilder clustererBuilder = (KMeansBuilder) ClustererFactory.getClusterBuilder(ClusterEnum.KMEANS);
			KMeansImpl kmeans = clustererBuilder.clustersNumber(4).measure(new MyEuclideanDistance()).centroidStrategy(CentroidStrategy.AVERAGE).build();
			Map<Long, List<User>> cluster = kmeans.cluster(utilityMatrix);
			//System.out.println(cluster.size());
			for(Long id : cluster.keySet()){
				System.out.println(cluster.get(id).size());
			}
			System.out.println(i);
			System.out.println();
		}
			
	}

	private void writeOutput(Long userId, int userFoldId, int itemFoldId,
			Float[] precisionRecall) {

		try {

			StringBuilder msg = new StringBuilder();
			msg.append(userId).append(",").append(userFoldId).append(",")
					.append(itemFoldId).append(",").append(precisionRecall[0])
					.append(",").append(precisionRecall[1])
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
	
	public static void main(String ... args) {
		try {
			Execute.run(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
