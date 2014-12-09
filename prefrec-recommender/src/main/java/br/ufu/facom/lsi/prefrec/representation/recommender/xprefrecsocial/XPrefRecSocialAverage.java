/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial;

import java.util.List;
import java.util.Map;
import java.util.Set;

import prefdb.model.PrefDatabase;
import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie;
import control.Validation;

/**
 * @author Klerisson
 *
 */
public class XPrefRecSocialAverage extends XPrefRecSocial {

	public XPrefRecSocialAverage(
			Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap,
			Miner miner, StrenghtTie strenghtTieStrategy) {
		super(concensualMatrixMap, miner, strenghtTieStrategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec
	 * #run(java.lang.Long, java.lang.Double[][], java.util.Map, java.util.Map,
	 * br.ufu.facom.lsi.prefrec.model.UtilityMatrix)
	 */
	@Override
	public Float[] run(Long userId, Double[][] itemItem,
			Map<Integer, Double> itemIdToRate,
			Map<Long, Double[]> clusterCenters,
			UtilityMatrix testerUtilityMatrix) throws Exception {

		Double[][] choosenConcensualMatrix = this.findSimilarConcensualMatrix(
				userId, clusterCenters, testerUtilityMatrix);

		// Test execution
		Validation v = miner.getValidation(choosenConcensualMatrix);
		try {
			PrefDatabase pdb = miner.toPrefDatabase(itemIdToRate);
			if (pdb != null) {
				v.runOverModel(3, pdb);
				return new Float[] { v.getAvPrecision(), v.getAvRecall() };
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec
	 * #findSimilarConcensualMatrix(java.lang.Long, java.util.Map,
	 * br.ufu.facom.lsi.prefrec.model.UtilityMatrix)
	 */
	@Override
	protected Double[][] findSimilarConcensualMatrix(Long userId,
			Map<Long, Double[]> clusterCenters,
			UtilityMatrix testerUtilityMatrix) {

		User user = testerUtilityMatrix.getUserItemList(userId);
		Map<User, Double> strenghtTieMap = this.strenghtTieStrategy
				.strenghtTieCalc(user);

		//Concensual matrix key
		Double[][] similarMatrix = null;
		double previousAverage = Double.MIN_VALUE;
		for (Double[][] key : this.concensualMatrixMap.keySet()) {
			List<Map<Long, Double[][]>> clusters = this.concensualMatrixMap
					.get(key);
			Double[] sumAndQtbyCluster = new Double[]{0d,0d};
			for (Map<Long, Double[][]> usersInCluster : clusters) {
				Set<Long> usersId = usersInCluster.keySet();
				for (Long userInClusterId : usersId) {
					User uTemp = new User(userInClusterId);
					if(user.getFriends().contains(uTemp)){
						sumAndQtbyCluster[0] += strenghtTieMap.get(uTemp);
						sumAndQtbyCluster[1]++ ;
					}
				}
			}
			
			double average = sumAndQtbyCluster[0] / sumAndQtbyCluster[1];
			if(average > previousAverage){
				previousAverage = average;
				similarMatrix = key;
			}
		}
		return similarMatrix;
	}
}
