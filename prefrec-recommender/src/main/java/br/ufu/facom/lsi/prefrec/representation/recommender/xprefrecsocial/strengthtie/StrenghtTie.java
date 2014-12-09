/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation.recommender.xprefrecsocial.strengthtie;

import java.util.Map;

import br.ufu.facom.lsi.prefrec.model.User;

/**
 * @author Klerisson
 *
 */
public interface StrenghtTie {
	/**
	 * 
	 * @param user
	 * @return a map of friends to its strenght tie value.
	 */
	Map<User, Double> strenghtTieCalc(User user);
	
}
