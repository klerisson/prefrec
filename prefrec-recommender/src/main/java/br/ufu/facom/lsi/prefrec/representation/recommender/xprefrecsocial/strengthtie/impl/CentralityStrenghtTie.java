/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.impl;

import java.util.Map;

import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie;

/**
 * @author Klerisson
 *
 */
public class CentralityStrenghtTie implements StrenghtTie {

	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie#strenghtTieCalc(br.ufu.facom.lsi.prefrec.model.User)
	 */
	@Override
	public Map<User, Double> strenghtTieCalc(User user) {
		return user.getCentralityMap();
	}

}
