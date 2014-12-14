/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie;

/**
 * @author Klerisson
 *
 */
public class XPrefRecSocialThreshold extends XPrefRecSocialAverage{

	private double threshold;
	
	public XPrefRecSocialThreshold(
			Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap,
			Miner miner, StrenghtTie strenghtTieStrategy, double threshold) {
		super(concensualMatrixMap, miner, strenghtTieStrategy);
		this.threshold = threshold;
	}

	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec#findSimilarConcensualMatrix(java.lang.Long, java.util.Map, br.ufu.facom.lsi.prefrec.model.UtilityMatrix)
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
		int previous = Integer.MIN_VALUE;
		for (Double[][] key : this.concensualMatrixMap.keySet()) {
			List<Map<Long, Double[][]>> clusters = this.concensualMatrixMap
					.get(key);
			int strenghtSum = 0;
			for (Map<Long, Double[][]> usersInCluster : clusters) {
				Set<Long> usersId = usersInCluster.keySet();
				for (Long userInClusterId : usersId) {
					User uTemp = new User(userInClusterId);
					if(user.getFriends().contains(uTemp) && strenghtTieMap.get(uTemp) >= this.threshold){
						strenghtSum++;
					}
				}
			}
						
			if(strenghtSum > previous){//corrigir
				previous = strenghtSum;
				similarMatrix = key;
			}
		}
		return similarMatrix;
		
	}

}
