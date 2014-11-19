package br.ufu.facom.lsi.prefrec.representation.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.representation.model.UserItemScorer;
import br.ufu.facom.lsi.prefrec.representation.model.UserItemScorerList;

public class PrefMatrixScorer {

	private Map<Long, Double[][]> matrixMap;
	private int count;

	public PrefMatrixScorer() {
		super();
		this.matrixMap = new LinkedHashMap<Long, Double[][]>();
	}

	/**
	 * 
	 * @return Map user as key and a matrix ItenItem
	 */
	public Map<Long, Double[][]> getMatrixMap() {
		return matrixMap;
	}

	public void fill2(UserItemScorerList uisList) throws IOException {

		int matrixSize = uisList.getUniqueItens().size();
		List<Long> uniqueItens = new ArrayList<>(uisList.getUniqueItens());

		while (uisList.hasNext()) {
			List<UserItemScorer> subListByUser = uisList.nextSubList();
			Double[][] rm = new Double[matrixSize][matrixSize];
			//BufferedWriter bw = new BufferedWriter(new FileWriter("matrix_"
				//	+ subListByUser.get(0).getUserId()));

			int rowId = 0;
			//Double[][] rawRm = new Double[matrixSize][matrixSize];
			for (Long i : uniqueItens) {
				int columnId = 0;
				for (Long j : uniqueItens) {

					Double[] ratings = UserItemScorerList.getItemScoreById(
							subListByUser, i, j);

					//double ratio = ratings[1] != 0 ? (ratings[0] / ratings[1]): 0;
					//rawRm[rowId][columnId] = columnId < rowId ? ratio : -1;
					//bw.write(String.valueOf(rawRm[rowId][columnId]) + ";");

					if (i == j) {
						rm[rowId][columnId] = 0.5;
					} else if (ratings[0] == 0 || ratings[1] == 0) {// ratings[0]
																	// == 0 ||
																	// ratings[1]
																	// == 0
						rm[rowId][columnId] = 0.0;
					} else if (ratings != null) {

						double score = (ratings[0] / ratings[1])
								/ ((ratings[0] / ratings[1]) + 1);
						BigDecimal scoreR = new BigDecimal(score).setScale(3,
								RoundingMode.HALF_EVEN);

						rm[rowId][columnId] = scoreR.doubleValue();

					}
					//bw.write(String.valueOf(rm[rowId][columnId]) + ";");
					columnId++;
				}
				//bw.write("\n");
				rowId++;
			}	
			//bw.flush();
			//bw.close();
			this.matrixMap.put(subListByUser.get(0).getUserId(), rm);
		}
		
	}

	public void fill(UserItemScorerList uisList) {

		int matrixSize = uisList.getBiggerItemId().intValue();
		while (uisList.hasNext()) {
			List<UserItemScorer> subListByUser = uisList.nextSubList();

			Double[][] rm = new Double[matrixSize][matrixSize];
			for (int i = 0; i < matrixSize; i++) {
				for (int j = 0; j < matrixSize; j++) {

					Double[] ratings = UserItemScorerList.getItemScoreById(
							subListByUser, i + 1, j + 1);

					if (i == j) {
						rm[i][j] = 0.5;
					} else if (ratings[0] == 0 || ratings[1] == 0) {
						rm[i][j] = 0.0;
					} else if (ratings != null) {
						double score = (ratings[0] / ratings[1])
								/ ((ratings[0] / ratings[1]) + 1);
						rm[i][j] = score;
					}
				}
			}
			this.matrixMap.put(subListByUser.get(0).getUserId(), rm);
			// System.out.println("Size: " + this.matrixMap.size());
			// if(this.matrixMap.size() == 50){
			// try {
			// this.serialize();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
		}
	}

	public void serialize() throws Exception {

		try (FileOutputStream fout = new FileOutputStream("../output"
				+ this.count + ".prefrec");
				ObjectOutputStream oos = new ObjectOutputStream(fout)) {

			oos.writeObject(this.matrixMap);
			oos.flush();
			fout.flush();
			this.matrixMap.clear();
			this.count++;

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Deserialize the output file
	 */
	public void deserialize() throws Exception {

		this.matrixMap = new LinkedHashMap<Long, Double[][]>();
		File dir = new File("../");

		// list the files using our FileFilter
		File[] files = dir.listFiles(new PrefrecFileFilter());
		for (File f : files) {

			try (InputStream file = new FileInputStream(f);
					InputStream buffer = new BufferedInputStream(file);
					ObjectInput input = new ObjectInputStream(buffer);) {

				// deserialize the Map
				@SuppressWarnings("unchecked")
				Map<Long, Double[][]> recovered = (LinkedHashMap<Long, Double[][]>) input
						.readObject();
				this.matrixMap.putAll(recovered);

			} catch (Exception e) {
				throw e;
			}
		}
	}
}

/**
 * A class that implements the Java FileFilter interface.
 */
class PrefrecFileFilter implements FileFilter {

	// Array if other extensions need to be added
	private final String[] okFileExtensions = new String[] { "prefrec" };

	public boolean accept(File file) {
		for (String extension : okFileExtensions) {
			if (file.getName().toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}
}
