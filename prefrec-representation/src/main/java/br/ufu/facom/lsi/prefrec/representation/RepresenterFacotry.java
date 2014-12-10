package br.ufu.facom.lsi.prefrec.representation;

import br.ufu.facom.lsi.prefrec.representation.impl.CrossValidationRepresenter;
import br.ufu.facom.lsi.prefrec.representation.impl.CrossValidationTestersRepresenter;
import br.ufu.facom.lsi.prefrec.representation.impl.LeaveOneOutRepresenter;
import br.ufu.facom.lsi.prefrec.representation.impl.LeaveOneOutTesterRepresenter;

public final class RepresenterFacotry {

	public static Representer getRepresenter(RepresenterEnum representerType) {
		
		switch (representerType) {
		case CROSS_VALIDATION:
			return new CrossValidationRepresenter();
		case CROSS_VALIDATION_TESTER:
			return new CrossValidationTestersRepresenter();
		case LEAVE_ONE_OUT:
			return new LeaveOneOutRepresenter();
		case LEAVE_ONE_OUT_TESTER:
			return new LeaveOneOutTesterRepresenter();
		default:
			return null;
		}
		
	}

}
