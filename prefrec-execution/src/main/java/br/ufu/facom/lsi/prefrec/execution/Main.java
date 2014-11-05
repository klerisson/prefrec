/**
 * 
 */
package br.ufu.facom.lsi.prefrec.execution;

import br.ufu.facom.lsi.prefrec.execution.crossvalidation.CrossValidation;

/**
 * @author Klerisson
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CrossValidation cv = new CrossValidation(5);
		cv.execute();
		
	}

}
