package br.ufu.facom.lsi.prefrec.representation.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import br.ufu.facom.lsi.prefrec.representation.util.AppPropertiesEnum;
import br.ufu.facom.lsi.prefrec.representation.util.GetConnection;
import br.ufu.facom.lsi.prefrec.representation.util.PropertiesUtil;

public class UserItemScorerList extends ArrayList<UserItemScorer> {

	private static final long serialVersionUID = 2983537998993872885L;

	private final SortedSet<Long> uniqueItens;
	private final SortedSet<Long> uniqueUsers;
	private final LinkedList<Long> usersDelimiter;

	private Long biggerItemId;

	public UserItemScorerList() {
		super(new ArrayList<UserItemScorer>());
		this.uniqueItens = new TreeSet<Long>();
		this.uniqueUsers = new TreeSet<Long>();
		this.usersDelimiter = new LinkedList<Long>();
		this.biggerItemId = -1L;
	}

	public void loadAll() throws Exception {

		String selectSQL = "SELECT idusuario, idfilme, nota, "
				+ "dataavaliacao FROM "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.AVALICAO_TABLE)
				+ " order by idusuario, idfilme";

		// String selectSQL = "SELECT idusuario, idfilme, nota, "
		// + "dataavaliacao FROM teste_a order by (idusuario, idfilme)";

		try (Connection conn = GetConnection.getSimpleConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectSQL);) {

			long previousUserId = -1;

			while (rs.next()) {

				Long userId = rs.getLong("idusuario");
				if (previousUserId != userId) {
					this.usersDelimiter.add(Long.valueOf(this.size()));
					previousUserId = userId;
				}

				Long itemId = rs.getLong("idfilme");
				if (this.biggerItemId < itemId) {
					this.biggerItemId = itemId;
				}

				UserItemScorer uis = new UserItemScorer();
				uis.setItemId(itemId);
				uis.setUserId(userId);
				uis.setDate(rs.getTimestamp("dataavaliacao"));

				try {
					uis.setNota(Integer.valueOf(rs.getString("nota")));
				} catch (NumberFormatException nfe) {
					uis.setNota(0);
				}
				this.addToUniqueUsers(uis.getUserId());
				this.addToUniqueItens(uis.getItemId());
				this.add(uis);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public void loadByUserFold(int fold) throws Exception {

		String selectSQL = "select userid, itemid, rate from "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " where folduserid != " + fold;

		try (Connection conn = GetConnection.getSimpleConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectSQL);) {

			long previousUserId = -1;
			while (rs.next()) {

				Long userId = rs.getLong("userid");
				if (previousUserId != userId) {
					this.usersDelimiter.add(Long.valueOf(this.size()));
					previousUserId = userId;
				}

				Long itemId = rs.getLong("itemid");
				if (this.biggerItemId < itemId) {
					this.biggerItemId = itemId;
				}

				UserItemScorer uis = new UserItemScorer();
				uis.setItemId(itemId);
				uis.setUserId(userId);
				// uis.setDate(rs.getTimestamp("dataavaliacao"));

				try {
					uis.setNota(Integer.valueOf(rs.getString("rate")));
				} catch (NumberFormatException nfe) {
					uis.setNota(0);
				}
				this.addToUniqueUsers(uis.getUserId());
				this.addToUniqueItens(uis.getItemId());
				this.add(uis);
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public void saveStratiefMatrix(StratifiedMatrix stratifiedMatrix)
			throws Exception {

		// userid,itemid,rate,folduserid,folditemid
		String sql = "insert into "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " values (?,?,?,?,?)";

		try (Connection conn = GetConnection.getSimpleConnection();
				PreparedStatement preparedStatement = conn
						.prepareStatement(sql)) {

			TreeMap<Integer, List<Integer[]>> userFolders = stratifiedMatrix
					.getPartitions();
			TreeMap<Integer, List<Integer[]>> itemFolders = stratifiedMatrix
					.getPartitionsFromItem();
			Long[] usersId = stratifiedMatrix.getUsersId();
			Long[] itemsId = stratifiedMatrix.getItemsId();
			Integer[][] ratings = stratifiedMatrix.getRatings();

			Iterator<Integer> userFolderIterator = userFolders
					.navigableKeySet().iterator();
			int currentUserFold = userFolderIterator.next();
			int counterUserInserted = 0;

			Iterator<Integer> itemFolderIterator = itemFolders
					.navigableKeySet().iterator();
			int currentItemFold = itemFolderIterator.next();
			int counterItemInserted = 0;

			for (int i = 0; i < usersId.length; i++) {

				int userOnFold = userFolders.get(currentUserFold).size();
				if (counterUserInserted == userOnFold) {
					currentUserFold = userFolderIterator.next();
					counterUserInserted = 0;
				}

				for (int j = 0; j < itemsId.length; j++) {

					int itemOnFold = itemFolders.get(currentItemFold).size();
					preparedStatement.setLong(1, usersId[i]); // userid
					preparedStatement.setLong(2, itemsId[j]); // itemid
					preparedStatement.setLong(3, ratings[i][j]); // rate
					preparedStatement.setLong(4, currentUserFold); // folduserid
					preparedStatement.setLong(5, currentItemFold); // folditemid
					preparedStatement.executeUpdate();

					counterItemInserted++;
					if (counterItemInserted == itemOnFold) {
						if (!itemFolderIterator.hasNext()) {
							itemFolderIterator = itemFolders.navigableKeySet()
									.iterator();
						}
						currentItemFold = itemFolderIterator.next();
						counterItemInserted = 0;
					}
				}
				counterUserInserted++;
			}

		} catch (Exception e) {
			throw e;
		}
	}

	public Set<Long> getUniqueUsers() {
		return uniqueUsers;
	}

	private void addToUniqueUsers(Long userId) {
		this.uniqueUsers.add(userId);
	}

	public Set<Long> getUniqueItens() {
		return uniqueItens;
	}

	private void addToUniqueItens(Long itemId) {
		this.uniqueItens.add(itemId);
	}

	public long uniquesSize() {
		return this.uniqueItens.size();
	}

	public List<UserItemScorer> nextSubList() {

		Long from = this.usersDelimiter.pollFirst();
		Long to = this.usersDelimiter.peekFirst();

		if (from == null) {
			return null;
		} else if (to == null) {
			return this.subList(from.intValue(), this.size());
		}

		return this.subList(from.intValue(), (to.intValue() + 1));
	}

	public boolean hasNext() {
		return ((this.usersDelimiter.peekFirst() == null) ? false : true);
	}

	/**
	 * 
	 * @param Map
	 *            <Integer, Integer> of item id as key and its score given by
	 *            the user.
	 * @return
	 */
	public Map<Integer, Integer> getItemScoreByUserId(Long userId) {

		Map<Integer, Integer> itemScore = new LinkedHashMap<>();
		for (UserItemScorer uis : this) {
			if (uis.getUserId().equals(userId)) {
				itemScore.put(uis.getItemId().intValue(), uis.getNota());
			}
		}
		return itemScore;
	}

	public static Double[] getItemScoreById(List<UserItemScorer> list, long id,
			long id2) {
		Double[] result = { 0.0, 0.0 };
		boolean[] found = { false, false };

		for (UserItemScorer object : list) {
			if (object.getItemId().equals(id) && !found[0]) {
				result[0] = (double) object.getNota();
				found[0] = true;
			}

			if (object.getItemId().equals(id2) && !found[1]) {
				result[1] = (double) object.getNota();
				found[1] = true;
			}

			if (found[0] && found[1]) {
				return result;
			}
		}

		if (found[0] && found[1]) {
			return result;
		} else {
			return null;
		}
	}

	public Long getBiggerItemId() {
		return biggerItemId;
	}

	public void loadModelUsers(int idUserFold, int idItemFold) throws Exception {

		String selectSQL = "select userid, itemid, rate, folditemid from "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " where folduserid = " + idUserFold;// + " and folditemid != "
														// + idItemFold;

		try (Connection conn = GetConnection.getSimpleConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectSQL);) {

			long previousUserId = -1;
			while (rs.next()) {

				Long userId = rs.getLong("userid");
				if (previousUserId != userId) {
					this.usersDelimiter.add(Long.valueOf(this.size()));
					previousUserId = userId;
				}

				Long itemId = rs.getLong("itemid");
				if (this.biggerItemId < itemId) {
					this.biggerItemId = itemId;
				}

				UserItemScorer uis = new UserItemScorer();
				uis.setItemId(itemId);
				uis.setUserId(userId);
				// uis.setDate(rs.getTimestamp("dataavaliacao"));

				try {

					if (rs.getLong("folditemid") == idItemFold) {
						uis.setNota(0);
					} else {
						uis.setNota(rs.getInt("rate"));
					}

				} catch (NumberFormatException nfe) {
					uis.setNota(0);
				}
				this.addToUniqueUsers(uis.getUserId());
				this.addToUniqueItens(uis.getItemId());
				this.add(uis);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @return the usersDelimiter
	 */
	public LinkedList<Long> getUsersDelimiter() {
		return usersDelimiter;
	}

	public List<Integer> fetchAllDistinctItems() throws Exception {

		String selectSQL = "select distinct(itemid) from "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " order by itemid";
		List<Integer> result = new ArrayList<>();
		try (Connection conn = GetConnection.getSimpleConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectSQL);) {

			while (rs.next()) {
				Long userId = rs.getLong("userid");
				result.add(userId.intValue());
			}

			return result;
		} catch (Exception e) {
			throw e;
		}
	}

	public Map<Integer, Double> fetchValidationFold(int userId, int idItemFold)
			throws Exception {

		String selectSQL = "select itemid, rate from "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " where folditemid = " + idItemFold + " and " + " userid = "
				+ userId + " and rate != 0";

		Map<Integer, Double> result = new HashMap<>();
		try (Connection conn = GetConnection.getSimpleConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectSQL);) {

			while (rs.next()) {
				int itemId = rs.getInt("itemid");
				int rate = rs.getInt("rate");

				result.put(itemId, new Double(rate));
			}
			return result;

		} catch (Exception e) {
			throw e;
		}
	}

}
