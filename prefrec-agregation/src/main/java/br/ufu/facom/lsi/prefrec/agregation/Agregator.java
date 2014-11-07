package br.ufu.facom.lsi.prefrec.agregation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Agregator {

	private Map<Long, List<Map<Long, Double[][]>>> clusterToUserMatrixScorer = new LinkedHashMap<>();
	private Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap = new LinkedHashMap<>();

	public Agregator(
			Map<Long, List<Map<Long, Double[][]>>> clusterToUserMatrixScorer) {
		this.clusterToUserMatrixScorer = clusterToUserMatrixScorer;
	}

	public void execute() {

		try {
			// Read input
			// readInput();

			int length = clusterToUserMatrixScorer.values().iterator().next()
					.get(0).values().iterator().next().length;

			// Reading cluster
			for (Long id : clusterToUserMatrixScorer.keySet()) {
				
				Double[][] concensualMatrix = initMatrix(length);
				Double[][] counter = initMatrix(length);
				
				// Reading Matrix
				Iterator<Map<Long, Double[][]>> it = clusterToUserMatrixScorer
						.get(id).iterator();
				
				while (it.hasNext()) {
					Map<Long, Double[][]> map = it.next();
					for (Double[][] temp : map.values()) {
						
						for (int k = 0; k < temp.length; k++) {
							for (int h = 0; h < temp[k].length; h++) {
								if(temp[k][h] != 0) {
									concensualMatrix[k][h] += temp[k][h];
									counter[k][h]++;
								}
							}
						}
						
					}
				}

				for (int k = 0; k < concensualMatrix.length; k++) {
					for (int h = 0; h < concensualMatrix[k].length; h++) {
						if(counter[k][h] != 0){
						concensualMatrix[k][h] = concensualMatrix[k][h]
								/ counter[k][h];
						}
					}
				}

				concensualMatrixMap.put(concensualMatrix,
						clusterToUserMatrixScorer.get(id.intValue()));
			}
			// writeConcensualMatrizMapOutput();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void writeConcensualMatrizMapOutput() throws Exception {
	//
	// try (FileOutputStream fout = new
	// FileOutputStream("../ConcensualMatrixMap.prefrecmining");
	// ObjectOutputStream oos = new ObjectOutputStream(fout)) {
	//
	// oos.writeObject(concensualMatrixMap);
	// oos.flush();
	// fout.flush();
	//
	// } catch (Exception e) {
	// throw e;
	// }
	// }

	private Double[][] initMatrix(int length) {

		Double[][] result = new Double[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				result[i][j] = 0.0;
			}
		}
		return result;
	}

	/**
	 * @return the clusterToUserMatrixScorer
	 */
	public Map<Long, List<Map<Long, Double[][]>>> getClusterToUserMatrixScorer() {
		return clusterToUserMatrixScorer;
	}

	/**
	 * @return the concensualMatrixMap
	 */
	public Map<Double[][], List<Map<Long, Double[][]>>> getConcensualMatrixMap() {
		return concensualMatrixMap;
	}

	// /**
	// * Deserialize the output file
	// */
	// @SuppressWarnings("unchecked")
	// public static void readInput() throws Exception {
	//
	// File f = new File("../ClusterOutput.prefrecCluster");
	//
	// try (InputStream file = new FileInputStream(f);
	// InputStream buffer = new BufferedInputStream(file);
	// ObjectInput input = new ObjectInputStream(buffer);) {
	//
	// // deserialize the Map
	// clusterToUserMatrixScorer = (Map<Long, List<Map<Long, Double[][]>>>)
	// input
	// .readObject();
	//
	// } catch (Exception e) {
	// throw e;
	// }
	// }
}