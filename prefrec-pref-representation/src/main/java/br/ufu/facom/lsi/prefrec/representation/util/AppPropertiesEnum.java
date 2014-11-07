package br.ufu.facom.lsi.prefrec.representation.util;

public enum AppPropertiesEnum {

	AVALICAO_TABLE("avaliacao_filme"), DATA_TABLE_STRATIFIED("datatable");
	 
	private String value;
 
	private AppPropertiesEnum(String s) {
		value = s;
	}
 
	public String getValue() {
		return value;
	}
	
}
