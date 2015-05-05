package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import prefdb.model.PrefDatabase;
import br.ufu.facom.lsi.prefrec.cluster.distance.CosineDistanceNormalized;
import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.model.Item;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import control.Validation;

public class XPrefRec {

	private DistanceMeasure distanceMeasure;
	protected Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap;
	protected Miner miner;
	private List<Long> totalOriginalItems;

	/**
	 * @param concensualMatrixMap
	 */
	public XPrefRec(
			Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap,
			Miner miner) {
		super();
		this.miner = miner;
		this.concensualMatrixMap = concensualMatrixMap;
		this.distanceMeasure = new CosineDistanceNormalized();
	}

	public Float[] run(Long userId, Double[][] itemItem,
			Map<Integer, Double> itemIdToRate,
			Map<Long, Double[]> clusterCenters,
			UtilityMatrix testerUtilityMatrix, List<Long> totalOriginalItems) throws Exception {
		
		this.totalOriginalItems = totalOriginalItems;
		return this.run(userId, itemItem, itemIdToRate, clusterCenters, testerUtilityMatrix);
	}
	
	public Float[] run(Long userId, Double[][] itemItem,
			Map<Integer, Double> itemIdToRate,
			Map<Long, Double[]> clusterCenters,
			UtilityMatrix testerUtilityMatrix) throws Exception {

		Double[][] choosenConcensualMatrix = null;
		if (clusterCenters == null) {
			choosenConcensualMatrix = findSimilarConcensualMatrix(itemItem);
		} else {
			choosenConcensualMatrix = findSimilarConcensualMatrix(userId,
					clusterCenters, testerUtilityMatrix);
		}
		// Test execution
		Validation v = miner.getValidation(choosenConcensualMatrix);
		try {
			PrefDatabase pdb = miner.toPrefDatabase(itemIdToRate);
			if (pdb != null) {
				v.runOverModel(1, pdb);
				return new Float[] { v.getAvPrecision(), v.getAvRecall(),v.getRulesNotFoundGeneralRate() };
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	protected Double[][] findSimilarConcensualMatrix(Double[][] itemItem) {

		double[] itemItemVector = matrixToVector(itemItem);
		Double[][] result = null;
		double distance = Double.MAX_VALUE;
		for (Double[][] concensualTemp : this.concensualMatrixMap.keySet()) {

			double[] concensualVector = matrixToVector(concensualTemp);
			double currentDistance = this.distanceMeasure.compute(
					itemItemVector, concensualVector);
			//System.out.println(currentDistance);
			if (currentDistance <= distance) {
				result = concensualTemp;
				distance = currentDistance;
			}
		}
		return result;
	}

	public Double[][] findSimilarConcensualMatrix(Long userId, Map<Long, Double[]> clusterCenters,
			UtilityMatrix testerUtilityMatrix) {

		User user = testerUtilityMatrix.getUserItemList(userId);
		Double[][] result = null;
		double distance = Double.MAX_VALUE;
		for(int i = 0; i < clusterCenters.size(); i++){
			
			double[] userPoints = userItemsToArray(user.getItems());
			double[] centroid = ArrayUtils
					.toPrimitive(clusterCenters.get(new Long(i)));
			double currentDistance = this.distanceMeasure.compute(
					userPoints, centroid);
			if (currentDistance <= distance) {
				result = this.getConcensualFromIndex(i);
				distance = currentDistance;
			}
		}
		return result;
	}

	protected Double[][] getConcensualFromIndex(int index){
		List<Double[][]> keys = new ArrayList<>(this.concensualMatrixMap.keySet());
		return (Double[][]) keys.get(index);
	}
	
	
	protected double[] userItemsToArray(List<Item> items) {
		double[] userPoints = new double[this.totalOriginalItems.size()];
		for (int i = 0; i < this.totalOriginalItems.size(); i++) {
			Item temp = new Item(this.totalOriginalItems.get(i), 0.0);
			if(items.contains(temp)){
				userPoints[i] = items.get(items.indexOf(temp)).getRate();	
			} else {
				userPoints[i] = -1.0;
			}
		}
		return userPoints;
	}

	protected double[] matrixToVector(Double[][] matrix) {

		List<Double> result = new ArrayList<>();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = i + 1; j < matrix.length; j++) {// teste era j=i+1
				result.add(matrix[i][j]);
			}
		}
		return ArrayUtils
				.toPrimitive(result.toArray(new Double[result.size()]));
	}

}
