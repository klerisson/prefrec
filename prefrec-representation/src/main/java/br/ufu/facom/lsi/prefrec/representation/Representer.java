/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation;

import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;

/**
 * @author Klerisson
 *
 */
public interface Representer {

	UtilityMatrix createUtilityMatrix(int parameter) throws Exception;
	UtilityMatrix createUtilityMatrix(int userFold, int itemFold) throws Exception;
}
