/**
 * 
 */
package br.ufu.facom.lsi.prefrec.execution;

/**
 * @author Klerisson
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ExecuteCross.run(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
