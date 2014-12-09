/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial;

import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie;

/**
 * @author Klerisson
 *
 */
public class XPrefRecSocialThreshold extends XPrefRecSocial{

	public XPrefRecSocialThreshold(
			Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap,
			Miner miner, StrenghtTie strenghtTieStrategy) {
		super(concensualMatrixMap, miner, strenghtTieStrategy);
	}

	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec#run(java.lang.Long, java.lang.Double[][], java.util.Map, java.util.Map, br.ufu.facom.lsi.prefrec.model.UtilityMatrix)
	 */
	@Override
	public Float[] run(Long userId, Double[][] itemItem,
			Map<Integer, Double> itemIdToRate,
			Map<Long, Double[]> clusterCenters,
			UtilityMatrix testerUtilityMatrix) throws Exception {
		// TODO Auto-generated method stub
		return super.run(userId, itemItem, itemIdToRate, clusterCenters,
				testerUtilityMatrix);
	}

	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec#findSimilarConcensualMatrix(java.lang.Double[][])
	 */
	@Override
	protected Double[][] findSimilarConcensualMatrix(Double[][] itemItem) {
		// TODO Auto-generated method stub
		return super.findSimilarConcensualMatrix(itemItem);
	}

	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec#findSimilarConcensualMatrix(java.lang.Long, java.util.Map, br.ufu.facom.lsi.prefrec.model.UtilityMatrix)
	 */
	@Override
	protected Double[][] findSimilarConcensualMatrix(Long userId,
			Map<Long, Double[]> clusterCenters,
			UtilityMatrix testerUtilityMatrix) {
		// TODO Auto-generated method stub
		return super.findSimilarConcensualMatrix(userId, clusterCenters,
				testerUtilityMatrix);
	}

	

}
