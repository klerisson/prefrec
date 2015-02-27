package br.ufu.facom.lsi.prefrec.util;

public enum AppPropertiesEnum {

	AVALICAO_TABLE("avaliacao_filme"), DATA_TABLE_STRATIFIED("datatable"), FRIENDSHIP_TABLE(
			"friendshiptable"), CENTRALITY("friendscentrality"), RATED_SIZE("ratedsize"), 
			MUTUALFRIENDS("mutualfriends"), DATATABLE_RAND("datatable_rand"), INTERACTION("friendsinteraction"), 
			SIMILARITY("similarity_score"), DATA_TABLE_SPECIFIC("datatable_specific");

	private String value;

	private AppPropertiesEnum(String s) {
		value = s;
	}

	public String getValue() {
		return value;
	}

}