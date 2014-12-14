/**
 * 
 */
package br.ufu.facom.lsi.prefrec.representation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.ufu.facom.lsi.prefrec.model.UtilityMatrix;
import br.ufu.facom.lsi.prefrec.util.AppPropertiesEnum;
import br.ufu.facom.lsi.prefrec.util.GetConnection;
import br.ufu.facom.lsi.prefrec.util.PropertiesUtil;

/**
 * @author Klerisson
 *
 */
public abstract class Representer {

	public abstract UtilityMatrix createUtilityMatrix(int parameter)
			throws Exception;

	public abstract UtilityMatrix createUtilityMatrix(int parameter1,
			int parameter2) throws Exception;

	public List<Long> getAllUserIds() throws Exception {

		List<Long> usersId = new ArrayList<>();
		String selectSQL = "select distinct(userid) from "
				+ PropertiesUtil
						.getAppPropertie(AppPropertiesEnum.DATA_TABLE_STRATIFIED)
				+ " order by userid;";

		try (Connection conn = GetConnection.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(selectSQL);) {
			
			while (rs.next()) {
				usersId.add(rs.getLong("userid"));
			}
			return usersId;
		} catch (Exception e) {
			throw e;
		}
	};
}
