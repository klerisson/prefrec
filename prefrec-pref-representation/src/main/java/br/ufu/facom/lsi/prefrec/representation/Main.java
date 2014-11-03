package br.ufu.facom.lsi.prefrec.representation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import br.ufu.facom.lsi.prefrec.representation.controller.PrefMatrixScorer;
import br.ufu.facom.lsi.prefrec.representation.model.StratifiedMatrix;
import br.ufu.facom.lsi.prefrec.representation.model.UserItemScorerList;

public class Main {

	private static StratifiedMatrix sm;
	
	public static void main(String[] args) {

		try {
			readInput();
			
			UserItemScorerList uisList = new UserItemScorerList();
			uisList.saveStratiefMatrix(sm);
			uisList.loadAll();
			
			PrefMatrixScorer pms = new PrefMatrixScorer();
			pms.fill(uisList);
			pms.serialize();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Deserialize the input file
	 */
	public static void readInput() throws Exception {

		File f = new File("../StratifiedMatrixByUser.prefrecstratificator");

		try (InputStream file = new FileInputStream(f);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);) {

			// deserialize the Map
			 sm = (StratifiedMatrix) input
					.readObject();

		} catch (Exception e) {
			throw e;
		}
	}
}
