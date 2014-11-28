package br.ufu.facom.lsi.prefrec.representation;

import br.ufu.facom.lsi.prefrec.representation.impl.CrossValidationRepresenter;

public final class RepresenterFacotry {

	public static Representer getRepresenter(RepresenterEnum representerType) {
		
		switch (representerType) {
		case CROSS_VALIDATION:
			return new CrossValidationRepresenter();

		default:
			return null;
		}
		
	}

}
