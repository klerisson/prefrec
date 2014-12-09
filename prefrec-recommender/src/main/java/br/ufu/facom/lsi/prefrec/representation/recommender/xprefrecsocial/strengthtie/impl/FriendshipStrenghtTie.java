/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie;

/**
 * @author Klerisson
 *
 */
public class FriendshipStrenghtTie implements StrenghtTie {

	/* (non-Javadoc)
	 * @see br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie.StrenghtTie#strenghtTieCalc(br.ufu.facom.lsi.prefrec.model.User)
	 */
	@Override
	public Map<User, Double> strenghtTieCalc(User user) {
		Map<User, Double> resultMap = new HashMap<>();
		for(User friend : user.getFriends()){
			resultMap.put(friend, 1D);						
		}
		return resultMap;
	}

}
