package br.ufu.facom.lsi.prefrec.representation;

import br.ufu.facom.lsi.prefrec.representation.controller.PrefMatrixScorer;
import br.ufu.facom.lsi.prefrec.representation.model.UserItemScorerList;

public class Representer {
	
	private UserItemScorerList uisList;
	private PrefMatrixScorer pms;
	
//	public static void main(String[] args) {
//
//		try {
//
//			UserItemScorerList uisList = new UserItemScorerList();
//			uisList.loadByUserFold(Integer.parseInt(args[0]));
//
//			PrefMatrixScorer pms = new PrefMatrixScorer();
//			pms.fill(uisList);
//			pms.serialize();
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//		}
//
//	}

	public void run(int fold) {

		try {

			uisList = new UserItemScorerList();
			uisList.loadByUserFold(fold);

			pms = new PrefMatrixScorer();
			pms.fill2(uisList);
			//pms.serialize();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	public void buildForModelChoice(int idUserFold, int idItemFold) {
		
		try {
			uisList = new UserItemScorerList();
			uisList.loadModelUsers(idUserFold, idItemFold);

			pms = new PrefMatrixScorer();
			pms.fill(uisList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the uisList
	 */
	public UserItemScorerList getUisList() {
		return uisList;
	}

	/**
	 * @return the pms
	 */
	public PrefMatrixScorer getPrefMatrixScorer() {
		return pms;
	}	

	// /**
	// * Deserialize the input file
	// */
	// public static void readInput() throws Exception {
	//
	// File f = new File("../StratifiedMatrixByUser.prefrecstratificator");
	//
	// try (InputStream file = new FileInputStream(f);
	// InputStream buffer = new BufferedInputStream(file);
	// ObjectInput input = new ObjectInputStream(buffer);) {
	//
	// // deserialize the Map
	// sm = (StratifiedMatrix) input
	// .readObject();
	//
	// } catch (Exception e) {
	// throw e;
	// }
	// }
}
