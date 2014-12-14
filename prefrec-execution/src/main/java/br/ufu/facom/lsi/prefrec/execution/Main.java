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
			ExecuteLeaveOneOut.run(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
