package br.ufu.facom.lsi.prefrec.representation.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufu.facom.lsi.prefrec.model.Item;
import br.ufu.facom.lsi.prefrec.model.User;
import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.representation.Representer;
import br.ufu.facom.lsi.prefrec.util.AppPropertiesEnum;
import br.ufu.facom.lsi.prefrec.util.GetConnection;
import br.ufu.facom.lsi.prefrec.util.PropertiesUtil;

public class CrossValidationTestersRepresenter extends Representer {

	@Override
	public UtilityMatrix createUtilityMatrix(int userFold, int itemFold)
			throws Exception {

		UtilityMatrix um = new UtilityMatrix();
		List<Long> usersId = new ArrayList<Long>();

		String selectSQL = "select distinct(userid) from "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " where folduserid = " + userFold + " order by userid;";

		try (Connection conn = GetConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(selectSQL);) {

			while (rs.next()) {
				usersId.add(rs.getLong("userid"));
			}
		} catch (Exception e) {
			throw e;
		}

		for (Long userId : usersId) {

			List<Item> itemList = new ArrayList<>();

			String selectSQL2 = "select itemid, rate, folditemid from "
					+ PropertiesUtil
							.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
					+ " where folduserid = " + userFold + " and userid = "
					+ userId + " order by itemid";

			try (Connection conn = GetConnection.getConnection();
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(selectSQL2);) {

				while (rs.next()) {
					Double nota = -1.0;
					try {
						if (rs.getLong("folditemid") != itemFold) {
							nota = rs.getDouble("rate");
						}
					} catch (NumberFormatException nfe) {

					}
					itemList.add(new Item(rs.getLong("itemid"), nota));
				}
			} catch (Exception e) {
				throw e;
			}

			// retrieve friends list and their centrality
			List<User> friends = new ArrayList<>();
			Map<User, Double> centrality = new HashMap<>();
			Map<User, Double> mutualFriends = new HashMap<>();
			Map<User, Double> interaction = new HashMap<>();
			Map<User, Double> similarity = new HashMap<>();
			String friendshipSql = "select f.friendid,c.centrality,m.jaccard,i.interaction,s.similarity from "
					+ PropertiesUtil
							.getAppPropertie(AppPropertiesEnum.FRIENDSHIP_TABLE)
					+ " f, "
					+ PropertiesUtil
							.getAppPropertie(AppPropertiesEnum.CENTRALITY)
					+ " c, "
					+ PropertiesUtil
							.getAppPropertie(AppPropertiesEnum.MUTUALFRIENDS)
							+ " m, "
					+ PropertiesUtil
							.getAppPropertie(AppPropertiesEnum.INTERACTION)
					+ " i,"
					+ PropertiesUtil
							.getAppPropertie(AppPropertiesEnum.SIMILARITY)
					+ " s where f.friendid=c.userid and f.userid=m.userid and f.userid=i.userid and f.userid=s.userid and"
					+" f.friendid=m.friendid and f.friendid=i.friendid and f.friendid=s.friendid and" 
					+" c.userid=m.friendid and c.userid=i.friendid and c.userid=s.friendid and" 
					+" m.userid=i.userid and m.userid=s.userid and m.friendid=i.friendid and m.friendid=s.friendid and" 
					+" i.userid=s.userid and i.friendid=s.friendid and f.userid= " + userId
					+ " order by f.friendid;";

			try (Connection conn = GetConnection.getConnection();
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(friendshipSql);) {

				while (rs.next()) {
					User utemp = new User(rs.getLong("friendid"));
					friends.add(utemp);
					centrality.put(utemp, rs.getDouble("centrality"));
					mutualFriends.put(utemp, rs.getDouble("jaccard"));
					interaction.put(utemp, rs.getDouble("interaction"));
					similarity.put(utemp, rs.getDouble("similarity"));
				}

			} catch (Exception e) {
				throw e;
			}

			um.addUser(new User(userId, itemList, friends, centrality,mutualFriends,interaction,similarity));
		}
		return um;
	}

	@Override
	public UtilityMatrix createUtilityMatrix(int parameter) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public static Map<Integer, Double> fetchValidationFold(int userId,
			int idItemFold) throws Exception {

		String selectSQL = "select itemid, rate from "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " where folditemid = " + idItemFold + " and " + " userid = "
				+ userId + " and rate <> -1 order by itemid;";

		Map<Integer, Double> result = new HashMap<>();
		try (Connection conn = GetConnection.getConnection();
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
