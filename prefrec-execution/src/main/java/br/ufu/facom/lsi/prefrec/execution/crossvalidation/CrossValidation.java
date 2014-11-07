/**
 * 
 */
package br.ufu.facom.lsi.prefrec.execution.crossvalidation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import br.ufu.facom.lsi.prefrec.agregation.Agregator;
import br.ufu.facom.lsi.prefrec.clusterer.Clusterer;
import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec;

/**
 * @author Klerisson
 *
 *         -- treinamento, p/ cada estrato de usuario i fa�a (geracao do modelo)
 *         select userid,itemid, rate from datatable where folduserid!=i;
 * 
 *         -- teste, para cada estrato de item j fa�a select userid,itemid, rate
 *         from datatable where folduserid=i and folditemid!=j;
 * 
 *         select userid,itemid, rate from datatable where folduserid=i and
 *         folditemid=j; -- guardar itens a serem recomendados
 * 
 *         -- para cada usuario k fa�a (recomendacao) -- gere a matriz de
 *         preferencias e encontre o grupo mais semelhante (compara��o entre as
 *         matrizes), fa�a a recomenda��o dos itens do estrato de itens j --
 *         passar para o cprefminer a matriz e lista de itens
 * 
 *         -- pegar resultado e validar, calculando precisao, recall e acur�cia,
 *         gravar estatistticas, verificar com o guilherme se o cpref j� possui
 *         essa parte
 */
public class CrossValidation {

	private int partitions;

	public CrossValidation(int partitions) {
		this.partitions = partitions;
	}

	public void execute() {

		for (int i = 0; i < partitions; i++) {

			// br.ufu.facom.lsi.prefrec.representation.Main.main(new
			// String[]{Integer.toString(i)});
			Representer representer = new Representer();
			representer.run(i);

			/*
			 * args[0] cluster typ: "DBSCAN", "FUZZY", "AFFINITY"
			 * 
			 * DBSCAN: args[1] distante type: "euclidean", "cosine"; args[2]
			 * stands for maximum radius of the neighborhood to be considered ;
			 * args[3] minimum number of points needed for a cluster
			 * 
			 * FUZZY: args[1] the number of clusters to split the data into
			 * args[2] the fuzziness factor, must be > 1.0
			 */
			// br.ufu.facom.lsi.prefrec.clusterer.Main.main(new
			// String[]{"DBSCAN", "euclidean", "20", "4"});
			Clusterer clusterer = new Clusterer();
			// clusterer.execute(representer.getPrefMatrixScorer(), new String[]
			// {
			// "DBSCAN", "cosine", "1", "4" });
			 clusterer.execute(representer.getPrefMatrixScorer(),
			 new String[] { "KMEANSPLUSPLUS" });
			//clusterer.execute(representer.getPrefMatrixScorer(),
					//new String[] { "AFFINITY" });

			Agregator agregator = new Agregator(clusterer.getCluster());
			agregator.execute();

			// br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Main.main(new
			// String[]{Integer.toString(i)});
			// Miner miner = new Miner(agregator.getConcensualMatrixMap());
			Miner miner = new Miner();
			try {
				miner.buildModels(new ArrayList<Double[][]>(agregator
						.getConcensualMatrixMap().keySet()));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			XPrefRec xprefrec = new XPrefRec(agregator.getConcensualMatrixMap());
			for (int j = 0; j < partitions; j++) {

				Representer representerModelChoice = new Representer();
				representerModelChoice.buildForModelChoice(i, j);

				Set<Long> testUsers = representerModelChoice.getUisList()
						.getUniqueUsers();
				for (Long userId : testUsers) {

					Map<Integer, Double> itemIdToRate;
					try {

						itemIdToRate = representerModelChoice.getUisList()
								.fetchValidationFold(userId.intValue(), j);
						if (itemIdToRate != null && itemIdToRate.size() > 1) {
							Float[] precisionRecall = xprefrec.run(userId,
									representerModelChoice
											.getPrefMatrixScorer()
											.getMatrixMap().get(userId),
									itemIdToRate, miner);
							if(precisionRecall != null){
								writeOutput(userId, i, j, precisionRecall);
							}
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void writeOutput(Long userId, int userFoldId, int itemFoldId,
			Float[] precisionRecall) {

		try {

			StringBuilder msg = new StringBuilder();
			msg.append(userId).append(",").append(userFoldId).append(",")
					.append(itemFoldId).append(",").append(precisionRecall[0])
					.append(",").append(precisionRecall[1]).append(System.lineSeparator());

			if(!Files.exists(Paths.get("./output.cvs"))){
				Files.createFile(Paths.get("./output.cvs"));
			}
			Files.write(Paths.get("./output.cvs"), msg.toString().getBytes(), StandardOpenOption.APPEND);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	// /**
	// * Read the Stratified Matrices, by item and by user, inputs generated by
	// * prefrec Stratification module
	// */
	// public void readInput() throws Exception {
	//
	// try (InputStream file = new FileInputStream(new File(
	// "../StratifiedMatrixByUser.prefrecstratificator"));
	// InputStream buffer = new BufferedInputStream(file);
	// ObjectInput input = new ObjectInputStream(buffer);) {
	//
	// // deserialize the Map
	// this.sm = (StratifiedMatrix) input.readObject();
	//
	// } catch (Exception e) {
	// throw e;
	// }
	//
	// }

}
