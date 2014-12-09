/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial;

import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.mining.cprefminermulti.Miner;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrec.XPrefRec;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie;

/**
 * @author Klerisson
 *
 */
public abstract class XPrefRecSocial extends XPrefRec {

	protected StrenghtTie strenghtTieStrategy;
		
	public XPrefRecSocial(
			Map<Double[][], List<Map<Long, Double[][]>>> concensualMatrixMap,
			Miner miner, StrenghtTie strenghtTieStrategy) {
		super(concensualMatrixMap, miner);
		this.strenghtTieStrategy = strenghtTieStrategy;
	}
}
