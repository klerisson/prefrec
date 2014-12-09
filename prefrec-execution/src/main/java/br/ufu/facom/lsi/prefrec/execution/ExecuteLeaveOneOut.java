package br.ufu.facom.lsi.prefrec.execution;

import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import br.ufu.facom.lsi.prefrec.representation.RepresenterEnum;
import br.ufu.facom.lsi.prefrec.representation.RepresenterFacotry;

public class ExecuteLeaveOneOut {

	public static void run(int usersQt) throws Exception {
		
		for (int i = 0; i < usersQt; i++) {
				
			UtilityMatrix utilityMatrix = null;
			Representer representer = RepresenterFacotry
					.getRepresenter(RepresenterEnum.LEAVE_ONE_OUT);
			utilityMatrix = representer.createUtilityMatrix(i);
			
		}
	}
}
