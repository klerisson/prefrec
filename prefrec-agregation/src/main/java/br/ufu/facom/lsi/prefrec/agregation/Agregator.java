package br.ufu.facom.lsi.prefrec.agregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;

public class Agregator {

	private Map<Long, List<User>> cluster = new LinkedHashMap<>();
	private Map<Long, Double[][]> preferenceMatrixMap;
	private Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap = new LinkedHashMap<>();

	public Agregator(Map<Long, List<User>> cluster, UtilityMatrix utilityMatrix) throws Exception {
		this.cluster = cluster;
		this.preferenceMatrixMap = PreferenceMatrix.build(utilityMatrix);
	}

	public void execute() {

		try {
			int length = cluster.values().iterator().next()
					.get(0).getItems().size();

			for (Long clusterId : this.cluster.keySet()) {

				Double[][] concensualMatrix = initMatrix(length);
				Double[][] counter = initMatrix(length);
				Double qtdeusers = 0.0;
				List<Map<Long, Double[][]>> userToPreferenceMatrix = new ArrayList<>();
				
				// Reading Matrix
				for (User u : cluster.get(clusterId)) {
					qtdeusers++;
					Double[][] preferences = this.preferenceMatrixMap.get(u.getId());
					for (int k = 0; k < preferences.length; k++) {
						for (int h = 0; h < preferences[k].length; h++) {
							if (preferences[k][h] != -1.0 && preferences[k][h] != null) {
								concensualMatrix[k][h] += preferences[k][h];
								counter[k][h]++;
							}
						}
					}
					
					for (int k = 0; k < concensualMatrix.length; k++) {
						for (int h = 0; h < concensualMatrix[k].length; h++) {
							// more than half users rate the item
							if ((counter[k][h] <= ((qtdeusers) / 2)) || k == h) {
								concensualMatrix[k][h] = 0.5;
							} else {
								BigDecimal consensoR = new BigDecimal(
										(concensualMatrix[k][h] / counter[k][h]))
										.setScale(3, RoundingMode.HALF_EVEN);
								concensualMatrix[k][h] = consensoR.doubleValue();

							}
						}
					}
					HashMap<Long, Double[][]> mapUserToPrefMatrix = new HashMap<>();
					mapUserToPrefMatrix.put(u.getId(), this.preferenceMatrixMap.get(u.getId()));
					userToPreferenceMatrix.add(mapUserToPrefMatrix);
				}
				concensualMatrixMap.put(concensualMatrix, 
						userToPreferenceMatrix);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private Double[][] initMatrix(int length) {

		Double[][] result = new Double[length][length];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				result[i][j] = 0.0;
			}
		}
		return result;
	}

//	/**
//	 * @return the clusterToUserMatrixScorer
//	 */
//	public Map<Long, List<User>> getClusterToUserMatrixScorer() {
//		return cluster;
//	}

	/**
	 * @return the concensualMatrixMap
	 */
	public Map<Double[][], List<Map<Long, Double[][]>>> getConcensualMatrixMap() {
		return concensualMatrixMap;
	}
}
