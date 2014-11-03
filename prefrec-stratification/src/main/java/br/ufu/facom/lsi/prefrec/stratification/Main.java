/**
 * 
 */
package br.ufu.facom.lsi.prefrec.stratification;

import br.ufu.facom.lsi.prefrec.representation.model.UserItemScorerList;
import br.ufu.facom.lsi.prefrec.stratification.controller.Stratificator;


/**
 * @author Klerisson
 *
 */
public class Main {

	public static void main(String[] args) {
		
		try {

			UserItemScorerList uisList = new UserItemScorerList();
			uisList.loadAll();
			
			Stratificator stratificator = new Stratificator(uisList, 5);
			stratificator.stratifyByItem();
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
