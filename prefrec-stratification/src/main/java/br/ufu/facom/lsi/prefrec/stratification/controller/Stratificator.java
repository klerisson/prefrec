package br.ufu.facom.lsi.prefrec.stratification.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.representation.model.StratifiedMatrix;
import br.ufu.facom.lsi.prefrec.representation.model.UserItemScorerList;

/**
 * @author Klerisson
 *
 */
public class Stratificator {

	private int folds;

	private Long[] itens;
	private double[] itensMean;
	private double[] rangeItemCounter;
	private double[] itensPercentile;
	private double[] itensOnRange;

	private Long[] users;
	private double[] usersMean;
	private double[] rangeUserCounter;
	private double[] usersPercentile;
	private double[] usersOnRange;

	private Integer rangeElementQt;
	private Integer[][] ratings;
	private StratifiedMatrix stratifiedMatrix;
	private StratifiedMatrix stratifiedMatrixByUser;

	private List<Integer> usedItens;

	public Stratificator(UserItemScorerList uisList, int folds)
			throws Exception {

		this.folds = folds;
		this.users = uisList.getUniqueUsers().toArray(
				new Long[uisList.getUniqueUsers().size()]);
		
		this.usersMean = new double[this.users.length];
		this.rangeUserCounter = new double[4];
		this.usersPercentile = new double[4];
		this.usersOnRange = new double[4];
		this.itens = uisList.getUniqueItens().toArray(
				new Long[uisList.getUniqueItens().size()]);
		this.itensMean = new double[this.itens.length];
		this.rangeItemCounter = new double[4];
		this.itensPercentile = new double[4];
		this.itensOnRange = new double[4];
		this.ratings = new Integer[this.users.length][this.itens.length];
		this.usedItens = new ArrayList<Integer>();

		// Initialize ratings matrix
		for (int i = 0; i < this.users.length; i++) {
			for (int j = 0; j < this.itens.length; j++) {
				this.ratings[i][j] = 0;
			}
		}

		for (int i = 0; i < this.users.length; i++) {
			Map<Integer, Integer> userScores = uisList
					.getItemScoreByUserId(this.users[i]);
			for (Integer idx : userScores.keySet()) {
				int itemIndex = this.findItemIndex(idx);
				this.ratings[i][itemIndex] = userScores.get(idx);
			}
		}

	}

	public void stratifyByItem() throws Exception {

		this.stratifiedMatrix = new StratifiedMatrix(folds, this.users.length,
				this.itens.length);
		this.rangeElementQt = (int) Math.ceil(this.itens.length
				/ Double.valueOf(this.folds));

		for (int i = 0; i < this.itens.length; i++) {
			double sum = 0.0;
			double qtItem = 0.0;
			for (int j = 0; j < this.users.length; j++) {
				if (!this.ratings[j][i].equals(0)) {
					sum += this.ratings[j][i];
					qtItem++;
				}
			}

			this.itensMean[i] = sum / qtItem;
			this.classifyItemMean(this.itensMean[i]);
		}

		for (int i = 0; i < this.itensPercentile.length; i++) {
			this.itensPercentile[i] = this.rangeItemCounter[i]
					/ this.itens.length;
		}

		// loop through item
		for (int i = 0; i < this.folds; i++) {

			for (int j = 0; j < this.rangeItemCounter.length; j++) {

				this.itensOnRange[j] = Math.round(this.itensPercentile[j]
						* this.rangeElementQt);
				this.fillStratifiedMatrix(i, j, this.itensOnRange[j]);
			}
		}
		
		if(this.usedItens.size() < this.itens.length){
			Map<Integer, Integer[]> restOfColumns = new HashMap<Integer, Integer[]>();
			for(int i = 0; i < this.itens.length; i ++){
				if(!this.usedItens.contains(i)){
					restOfColumns.put(this.itens[i].intValue(), getColumnFrom(i, this.ratings));
					this.usedItens.add(i);
				} 
			}
			this.stratifiedMatrix.addRestOfColumns(restOfColumns);
		}
		//++++++++++++++
		
		Integer[][] ratingsTemp = this.stratifiedMatrix.getRatings();
		for(int k = 0; k < ratingsTemp.length;k++){
			for(int h = 0; h < ratingsTemp[k].length; h++){
				System.out.print(ratingsTemp[k][h]);
				System.out.print(" ");
			}
			System.out.println("|");
		}
		
		//+++++++++
		this.usedItens.clear();
		this.stratifyByUser();
		this.stratifiedMatrixByUser.setPartitionsFromItem(this.stratifiedMatrix.getPartitions());
		//Write output
		//this.writeStratifiedMatrixOutput(this.stratifiedMatrixByUser, "../StratifiedMatrixByUser.prefrecstratificator");
		//save on database
		new UserItemScorerList().saveStratiefMatrix(this.stratifiedMatrixByUser);
	}

	private Integer[] getColumnFrom(int column, Integer[][] ratings) {
		Integer[] result = new Integer[this.users.length];
		for(int i = 0; i < this.users.length; i++){
			result[i] = ratings[i][column];
		}
		return result;
	}

	private void fillStratifiedMatrix(int stratum, int range,
			double itensOnRange) {

		switch (range) {
		case 0:
			fillWithAverage(itensOnRange, 2, stratum);
			break;

		case 1:
			fillWithAverage(itensOnRange, 3, stratum);
			break;

		case 2:
			fillWithAverage(itensOnRange, 4, stratum);
			break;

		case 3:
			fillWithAverage(itensOnRange, 5, stratum);
			break;
		}
	}

	private void fillWithAverage(double itensOnRange, int newRange, int stratum) {

		int itensOnRangeCounter = 0;
		for (int i = 0; (i < this.ratings[1].length)
				&& (itensOnRangeCounter < itensOnRange); i++) {
			if ((!this.usedItens.contains(i))
					&& (this.itensMean[i] >= (newRange - 1))
					&& (this.itensMean[i] < newRange)) {
				Integer[] col = new Integer[this.ratings.length];
				for (int j = 0; j < this.ratings.length; j++) {
					col[j] = this.ratings[j][i];
				}
				this.stratifiedMatrix.addColumn(col, stratum, this.itens[i].intValue(), this.users);
				itensOnRangeCounter++;
				this.usedItens.add(i);
			}
		}
	}

	private void classifyItemMean(double d) {
		if (d > 4) {
			this.rangeItemCounter[3]++;
		} else if (d > 3) {
			this.rangeItemCounter[2]++;
		} else if (d > 2) {
			this.rangeItemCounter[1]++;
		} else {
			this.rangeItemCounter[0]++;
		}
	}

	private int findItemIndex(Integer idx) throws Exception {
		for (int i = 0; i < this.itens.length; i++) {
			if (this.itens[i].equals(Long.valueOf(idx))) {
				return i;
			}
		}
		throw new Exception("Index out of bounds!");
	}

	private void stratifyByUser() {

		this.stratifiedMatrixByUser = new StratifiedMatrix(folds,
				this.users.length, this.itens.length);
		this.rangeElementQt = (int) Math.ceil(this.users.length
				/ Double.valueOf(this.folds));

		for (int i = 0; i < this.users.length; i++) {
			double sum = 0.0;
			double qtUsers = 0.0;
			for (int j = 0; j < this.itens.length; j++) {
				if (!this.stratifiedMatrix.getRatings()[i][j].equals(0)) {
					sum += this.stratifiedMatrix.getRatings()[i][j];
					qtUsers++;
				}
			}

			this.usersMean[i] = sum / qtUsers;
			this.classifyUserMean(this.usersMean[i]);
		}

		for (int i = 0; i < this.usersPercentile.length; i++) {
			this.usersPercentile[i] = this.rangeUserCounter[i]
					/ this.users.length;
		}
		// loop through users
		for (int i = 0; i < this.folds; i++) {

			for (int j = 0; j < this.rangeUserCounter.length; j++) {

				this.usersOnRange[j] = Math.round(this.usersPercentile[j]
						* this.rangeElementQt);
				
				this.fillStratifiedMatrixByUser(i, j, this.usersOnRange[j]);
			}
		}
		
		if(this.usedItens.size() < this.users.length){
			Map<Integer, Integer[]> restOfRows = new HashMap<Integer, Integer[]>();
			for(int i = 0; i < this.users.length; i ++){
				if(!this.usedItens.contains(i)){
					restOfRows.put(this.users[i].intValue(), this.stratifiedMatrix.getRatings()[i]);
					this.usedItens.add(i);
				} 
			}
			this.stratifiedMatrixByUser.addRestOfRows(restOfRows);
		}
		this.usedItens.clear();

	}

	private void fillStratifiedMatrixByUser(int stratum, int range,
			double usersOnRange) {
		switch (range) {
		case 0:
			fillWithAverageByUser(usersOnRange, 2, stratum);
			break;

		case 1:
			fillWithAverageByUser(usersOnRange, 3, stratum);
			break;

		case 2:
			fillWithAverageByUser(usersOnRange, 4, stratum);
			break;

		case 3:
			fillWithAverageByUser(usersOnRange, 5, stratum);
			break;
		}

	}

	private void fillWithAverageByUser(double usersOnRange, int newRange, int stratum) {

		int usersOnRangeCounter = 0;
		for (int i = 0; (i < this.users.length)
				&& (usersOnRangeCounter < usersOnRange); i++) {
			
			if ((!this.usedItens.contains(i))
					&& (this.usersMean[i] >= (newRange - 1))
					&& (this.usersMean[i] < newRange)) {
			
				Integer[] row = new Integer[this.itens.length];
				for (int j = 0; j < this.itens.length; j++) {
					row[j] = this.stratifiedMatrix.getRatings()[i][j];
				}
				this.stratifiedMatrixByUser.addRow(row, stratum, this.users[i].intValue(), this.stratifiedMatrix.getItemsId());
				usersOnRangeCounter++;
				this.usedItens.add(i);
			}
		}
	}

	private void classifyUserMean(double mean) {
		if (mean > 4) {
			this.rangeUserCounter[3]++;
		} else if (mean > 3) {
			this.rangeUserCounter[2]++;
		} else if (mean > 2) {
			this.rangeUserCounter[1]++;
		} else {
			this.rangeUserCounter[0]++;
		}
	}
	
//	private void writeStratifiedMatrixOutput(StratifiedMatrix sm, String fileName) throws Exception {
//
//		try (FileOutputStream fout = new FileOutputStream(fileName);
//				ObjectOutputStream oos = new ObjectOutputStream(fout)) {
//
//			oos.writeObject(sm);
//			oos.flush();
//			fout.flush();
//			
//		} catch (Exception e) {
//			throw e;
//		}
//	}

	public StratifiedMatrix getStratifiedMatrix() {
		return stratifiedMatrix;
	}

}
