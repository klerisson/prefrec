package br.ufu.facom.lsi.prefrec.representation;

import br.ufu.facom.lsi.prefrec.representation.controller.PrefMatrixScorer;
import br.ufu.facom.lsi.prefrec.representation.model.UserItemScorerList;

public class Main {

	public static void main(String[] args) {

		try {
			
			UserItemScorerList uisList = new UserItemScorerList();
			uisList.loadByUserFold(Integer.parseInt(args[0]));
			
			PrefMatrixScorer pms = new PrefMatrixScorer();
			pms.fill(uisList);
			pms.serialize();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

//	/**
//	 * Deserialize the input file
//	 */
//	public static void readInput() throws Exception {
//
//		File f = new File("../StratifiedMatrixByUser.prefrecstratificator");
//
//		try (InputStream file = new FileInputStream(f);
//				InputStream buffer = new BufferedInputStream(file);
//				ObjectInput input = new ObjectInputStream(buffer);) {
//
//			// deserialize the Map
//			 sm = (StratifiedMatrix) input
//					.readObject();
//
//		} catch (Exception e) {
//			throw e;
//		}
//	}
}
