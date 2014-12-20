/**
 * 
 */
package br.ufu.facom.lsi.prefrec.execution;

import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;

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
			
			Representer r = new Representer() {
				
				@Override
				public UtilityMatrix createUtilityMatrix(int parameter1, int parameter2)
						throws Exception {
					return null;
				}
				
				@Override
				public UtilityMatrix createUtilityMatrix(int parameter) throws Exception {
					return null;
				}
			};
			
			ExecuteLeaveOneOut.run(r.getAllUserIds());
			//ExecuteCross.run(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
