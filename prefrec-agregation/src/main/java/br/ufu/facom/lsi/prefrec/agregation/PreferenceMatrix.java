package br.ufu.facom.lsi.prefrec.agregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.model.Item;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;

public class PreferenceMatrix {

	/**
	 * 
	 * @return Map user as key and a matrix ItenItem
	 */
	public static Map<Long, Double[][]> build(UtilityMatrix utilityMatrix) throws Exception {
		
		Map<Long, Double[][]> matrixMap = new LinkedHashMap<Long, Double[][]>();
		List<Long> uniqueItens = utilityMatrix.getUniqueItemIds();
		int matrixSize = uniqueItens.size();

		for (User u : utilityMatrix.getUsers()) {

			List<Item> userItemList = utilityMatrix.getUserItemList(u);
			Double[][] rm = new Double[matrixSize][matrixSize];

			int rowId = 0;
			for (int i=0;i<userItemList.size();i++) {
				int columnId = 0;
				for (int j=0;j<userItemList.size();j++) {

					Double[] ratings = new Double[] {
							userItemList.get(i).getRate(),
							userItemList.get(j).getRate() };

					if (i == j) {
						rm[rowId][columnId] = 0.5;
					} else if (ratings[0] == -1 || ratings[1] == -1) {
						rm[rowId][columnId] = -1.0;
					} else if (ratings != null) {
						double score = (ratings[0] / ratings[1])
								/ ((ratings[0] / ratings[1]) + 1);
						//BigDecimal scoreR = new BigDecimal(score).setScale(3,
							//	RoundingMode.HALF_EVEN);
						rm[rowId][columnId] = score;
					}
					columnId++;
				}
				rowId++;
			}
			matrixMap.put(u.getId(), rm);
		}
		return matrixMap;
	}

}
