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

			
			long startTime = System.currentTimeMillis();
	        
			UserItemScorerList uisList = new UserItemScorerList();
			uisList.loadAll();
			
			Stratificator stratificator = new Stratificator(uisList, 5);
			stratificator.stratifyByItem();
			
			long endTime = System.currentTimeMillis();
		      System.out.println("Runtime: "+((double)(endTime-startTime)/1000) + " seconds");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
