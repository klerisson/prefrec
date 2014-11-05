package br.ufu.facom.lsi.prefrec.mining.cprefminermulti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import prefdb.model.Bituple;
import prefdb.model.PrefDatabase;
import prefdb.model.PrefValue;
import preprocessing.cv.persistence.BitupleOutput;
import control.Log;
import control.Start;
import control.Validation;
import data.model.Database;
import data.model.FullTuple;
import data.model.Key;

/**
 * @author Guilherme
 *
 */
public class Miner {

	private String[] attributes = { "director.cpm", "genre.cpm",
			"language.cpm", "star.cpm", "year.cpm", "user.cpm" };

	private Map<Key, FullTuple> features;
	private Map<Double[][], Validation> validationMap;

	public Miner() {
		super();
		Log.LOG_DIR = "..\\miningoutput\\log\\";
		features = loadFeatures();
		this.validationMap = new LinkedHashMap<>();
	}

	public void buildModels(ArrayList<Double[][]> concensualMatrixList) throws Exception {

		try {
			for (Double[][] concensualMatrix : concensualMatrixList) {

				Validation validation = toValidation(concensualMatrix, features);
				// constrói o modelo (tabelas de probabilidade)
				validation.buildModel();
				this.validationMap.put(concensualMatrix, validation);
			}
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * @param args
	 */
	// public void execute(Double[][] concensualMatrix,
	// Map<Integer, Double> itemIdToRate) {
	//
	// try {
	//
	// // executa o teste sobre o modelo criado no passo anterior
	// validation.runOverModel(3, toPrefDatabase(features, itemIdToRate));
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public Map<Key, FullTuple> loadFeatures() {

		ArrayList<String> attribsList = new ArrayList<>();
		attribsList.addAll(Arrays.asList(attributes));

		Integer[] maxMult = { 1, 3, 1, 4, 1, 0 };

		Database d = new Database("../miningoutput/", attribsList, "user.cpm",
				maxMult, ',');
		Map<Key, FullTuple> tuples = d.getMapFullTuples();
		return tuples;
	}

	/**
	 * 
	 * Converte um mapa com id do item e atributos associados a uma matriz item
	 * x item em um objeto Validation compatível com o CPrefMiner Multi.
	 * 
	 * @param matrix
	 *            matriz item x item de um usuário/cluster
	 * @param features
	 *            mapa com os atributos/características de um filme
	 * @return Um objeto Validation do CPrefMiner Multi, de tal modo que é
	 *         possível executar o minerador sobre as avaliações da matriz
	 *         considerando as características dos itens.
	 * @throws Exception
	 *             -
	 */
	public Validation toValidation(Double[][] matrix,
			Map<Key, FullTuple> features) throws Exception {
		ArrayList<Bituple> bituples = new ArrayList<>();

		Double d = 0.5;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] > d) {
					Bituple b = new Bituple(features.get(new Key(i + 1)),
							features.get(new Key(j + 1)), new PrefValue(1));
					bituples.add(b);
				}
			}
		}

		Map<Integer, String> relsNameMap = new HashMap<>();
		for (int i = 0; i < (attributes.length - 1); i++) {
			relsNameMap.put(i, "" + i);
		}

		ArrayList<PrefDatabase> prefDatabases = new ArrayList<>();
		prefDatabases.add(new PrefDatabase(bituples));
		BitupleOutput bo = new BitupleOutput();
		bo.setListPDB(prefDatabases);

		Start.prepareRandomSeed();

		return new Validation(bo);
	}

	public PrefDatabase toPrefDatabase(Map<Integer, Double> ids) {
		ArrayList<Bituple> bituples = new ArrayList<>();
		for (Map.Entry<Integer, Double> i : ids.entrySet()) {
			for (Map.Entry<Integer, Double> j : ids.entrySet()) {
				if (i != j && i.getValue() > j.getValue()) { // não pode ser o
																// mesmo item e
																// o item da
																// esquerda deve
																// ser preferido
																// (nota maior)
																// que o item da
																// direita
					Bituple b = new Bituple(
							features.get(new Key(i.getKey() + 1)),
							features.get(new Key(j.getKey() + 1)),
							new PrefValue(1));
					bituples.add(b);
				}
			}
		}
		return new PrefDatabase(bituples);
	}

	public Validation getValidation(Double[][] choosenConcensualMatrix) {
		return this.validationMap.get(choosenConcensualMatrix);
	}
	

	// @SuppressWarnings("unchecked")
	// public static void readInput() throws Exception {
	//
	// File f = new File("../ConcensualMatrixMap.prefrecmining");
	//
	// try (InputStream file = new FileInputStream(f);
	// InputStream buffer = new BufferedInputStream(file);
	// ObjectInput input = new ObjectInputStream(buffer);) {
	//
	// // deserialize the Map
	// concensualMatrixMap = (Map<Double[][], List<Map<Long, Double[][]>>>)
	// input
	// .readObject();
	//
	// } catch (Exception e) {
	// throw e;
	// }
	// }

}
