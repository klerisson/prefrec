package br.ufu.facom.lsi.prefrec.agregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
				Double qtdeusers=0.0;

				// Reading Matrix
				Iterator<Map<Long, Double[][]>> it = clusterToUserMatrixScorer
						.get(id).iterator();

				while (it.hasNext()) {
					qtdeusers++;
					Map<Long, Double[][]> map = it.next();
					for (Double[][] temp : map.values()) {

						for (int k = 0; k < temp.length; k++) {
							for (int h = 0; h < temp[k].length; h++) {
								if (temp[k][h] != 0.0 && temp[k][h] !=null) {
									concensualMatrix[k][h] += temp[k][h];
									counter[k][h]++;
								}
							}
						}

					}
				}

				for (int k = 0; k < concensualMatrix.length; k++) {
					for (int h = 0; h < concensualMatrix[k].length; h++) {
						// more than half users rate the item
						if ((counter[k][h] <= ((qtdeusers) / 2)) || k==h) {
							concensualMatrix[k][h] = 0.5;
						} else {
							BigDecimal consensoR= new BigDecimal((concensualMatrix[k][h]
									/ counter[k][h])).setScale(3, RoundingMode.HALF_EVEN);
							concensualMatrix[k][h] = consensoR.doubleValue();
							
						}
					}
				}
			 
				//for(int l = 0; l< concensualMatrix.length; l++){
					//for(int m = 0; m < concensualMatrix[l].length; m++){
						//BigDecimal bd = new BigDecimal(concensualMatrix[l][m]+concensualMatrix[m][l]).setScale(3, RoundingMode.HALF_EVEN);
						//System.out.println(bd.doubleValue());
						//if(bd.doubleValue()!=1){
						//System.out.print(concensualMatrix[l][m]+concensualMatrix[m][l]);
						//System.out.print(" ");
						//}
					//}
					//System.out.println();
				//}

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
