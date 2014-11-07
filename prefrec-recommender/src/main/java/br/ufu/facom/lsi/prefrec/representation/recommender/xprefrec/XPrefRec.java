package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import prefdb.model.PrefDatabase;
import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import control.Validation;

public class XPrefRec {

	private Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap;
	private EuclideanDistance euclideanDistance; 
	
	/**
	 * @param concensualMatrixMap
	 */
	public XPrefRec(
			Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap) {
		super();
		this.concensualMatrixMap = concensualMatrixMap;
		this.euclideanDistance = new EuclideanDistance();
	}

	public Float[] run(Long userId, Double[][] itemItem, Map<Integer, Double> itemIdToRate,
			Miner miner) throws Exception {
		
		Double[][] choosenConcensualMatrix = findSimilarConcensualMatrix(itemItem);
		//Test execution 
		Validation v = miner.getValidation(choosenConcensualMatrix);
		try {
			PrefDatabase pdb = miner.toPrefDatabase(itemIdToRate);
			if(pdb != null) {
				v.runOverModel(3, pdb);
				return new Float[]{v.getAvPrecision(), v.getAvRecall()};
			}
		} catch (Exception e) {
			throw e;
		}
		return null;	
	}

	private Double[][] findSimilarConcensualMatrix(Double[][] itemItem) {

		double[] itemItemVector = matrixToVector(itemItem);
		Double[][] result = null;
		double distance = Double.MAX_VALUE;
		for(Double[][] concensualTemp : this.concensualMatrixMap.keySet()){
			
			double[] concensualVector = matrixToVector(concensualTemp);
			double currentDistance = this.euclideanDistance.compute(itemItemVector, concensualVector);
			if(currentDistance <= distance){
				result = concensualTemp;
				distance = currentDistance;
			}
		}
		
		return result;
	}
	
	private double[] matrixToVector(Double[][] matrix){
		
		List<Double> result = new ArrayList<>();
		
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix.length; j++){
				result.add(matrix[i][j]);
			}
		}
		return ArrayUtils.toPrimitive(result.toArray(new Double[result.size()]));
	}
	
}