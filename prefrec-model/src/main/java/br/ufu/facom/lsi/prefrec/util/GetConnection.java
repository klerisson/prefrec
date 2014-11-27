package br.ufu.facom.lsi.prefrec.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class GetConnection {

	public static Connection getConnection() throws Exception {

		String DB_CONN_STRING = null;
		String DRIVER_CLASS_NAME = null;
		String USER_NAME = null;
		String PASSWORD = null;

		try (InputStream in = GetConnection.class.getClassLoader()
				.getResourceAsStream(PropertiesUtil.APP_PROPERTIES)) {

			Properties props = new Properties();
			props.load(in);

			DB_CONN_STRING = (String) props.get("connstring");
			DRIVER_CLASS_NAME = (String) props.get("driverClassName");
			USER_NAME = (String) props.get("userName");
			PASSWORD = (String) props.get("password");

		} catch (Exception e) {
			throw new Exception("Cannot read application properties:"
					+ e.getMessage());
		}

		Connection result = null;
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();

			result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME,
					PASSWORD);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

}