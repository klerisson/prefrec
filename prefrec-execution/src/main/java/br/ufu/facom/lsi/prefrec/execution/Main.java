/**
 * 
 */
package br.ufu.facom.lsi.prefrec.execution;

import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import control.Start;

/**
 * @author Klerisson
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Start.RANGING_VOTING_VERSION=1;
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
			//ExecuteLeaveOneOutEnsemble.run(r.getSpecificUserIds(12.8));
			//ExecuteLeaveOneOut.run(r.getSpecificUserIdsbyRates((1.3*124.44)));
			//ExecuteLeaveOneOutEnsemble.run(r.getAllUserIds());
			//ExecuteLeaveOneOut.run(r.getAllUserIds());//select all users as testers
			ExecuteLeaveOneOut.run(r.getSpecificUserIds(1.2*8.6));//select a specific group as testers
			//ExecuteCross.run(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
