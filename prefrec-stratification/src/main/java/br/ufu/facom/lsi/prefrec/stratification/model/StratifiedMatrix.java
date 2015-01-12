package br.ufu.facom.lsi.prefrec.stratification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StratifiedMatrix implements Serializable {

	private static final long serialVersionUID = 1342407738338892343L;

	private Long[] usersId;
	private Long[] itemsId;
	private Double[][] ratings;

	private TreeMap<Integer, List<Double[]>> partitions;
	private TreeMap<Integer, List<Double[]>> partitionsFromItem;

	private int columnIdx;
	private int rowIdx;

	public StratifiedMatrix(int folds, int rowLenght, int columnLenght) {
		this.partitions = new TreeMap<Integer, List<Double[]>>();
		this.ratings = new Double[rowLenght][columnLenght];
		this.itemsId = new Long[columnLenght];
		this.usersId = new Long[rowLenght];
	}

	public void addColumn(Double[] col, int stratum, int itemId, Long[] users) {
		this.usersId = users;
		this.itemsId[columnIdx] = Long.valueOf(itemId);
		for (int i = 0; i < col.length; i++) {
			this.ratings[i][this.columnIdx] = col[i];
		}
		this.columnIdx++;
		List<Double[]> temp = this.partitions.get(stratum);
		if (temp != null) {
			temp.add(col);
		} else {
			temp = new ArrayList<Double[]>();
			temp.add(col);
			this.partitions.put(stratum, temp);
		}
	}

	public void addRow(Double[] row, int stratum, int userId, Long[] itens) {
		this.itemsId = itens;
		this.usersId[rowIdx] = Long.valueOf(userId);
		for (int i = 0; i < row.length; i++) {
			this.ratings[this.rowIdx][i] = row[i];
		}
		this.rowIdx++;
		List<Double[]> temp = this.partitions.get(stratum);
		if (temp != null) {
			temp.add(row);
		} else {
			temp = new ArrayList<Double[]>();
			temp.add(row);
			this.partitions.put(stratum, temp);
		}
	}

	public void addRestOfRows(Map<Integer, Double[]> restOfRows) {
		for (Map.Entry<Integer, Double[]> entry : restOfRows.entrySet()) {
			this.addRow(entry.getValue(), this.partitions.lastKey(),
					entry.getKey(), this.itemsId);
		}
	}

	public void addRestOfColumns(Map<Integer, Double[]> restOfColumns) {
		for (Map.Entry<Integer, Double[]> entry : restOfColumns.entrySet()) {
			this.addColumn(entry.getValue(), this.partitions.lastKey(),
					entry.getKey(), this.usersId);
		}
	}

	/**
	 * @return the ratings
	 */
	public Double[][] getRatings() {
		return ratings;
	}

	/**
	 * @return the partitions
	 */
	public TreeMap<Integer, List<Double[]>> getPartitions() {
		return partitions;
	}

	/**
	 * @return the usersId
	 */
	public Long[] getUsersId() {
		return usersId;
	}

	/**
	 * @return the itemsId
	 */
	public Long[] getItemsId() {
		return itemsId;
	}

	/**
	 * @return the partitionsFromItem
	 */
	public TreeMap<Integer, List<Double[]>> getPartitionsFromItem() {
		return partitionsFromItem;
	}

	/**
	 * @param partitionsFromItem
	 *            the partitionsFromItem to set
	 */
	public void setPartitionsFromItem(
			TreeMap<Integer, List<Double[]>> partitionsFromItem) {
		this.partitionsFromItem = partitionsFromItem;
	}

}
