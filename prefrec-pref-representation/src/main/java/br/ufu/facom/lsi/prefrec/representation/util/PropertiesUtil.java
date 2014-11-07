package br.ufu.facom.lsi.prefrec.representation.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static final String APP_PROPERTIES = "app.properties";
	
	public static String getAppPropertie(AppPropertiesEnum code) throws Exception{

		try (InputStream in = GetConnection.class.getClassLoader()
				.getResourceAsStream(PropertiesUtil.APP_PROPERTIES)) {

			Properties props = new Properties();
			props.load(in);

			return (String) props.get(code.getValue());
			
		} catch (Exception e) {
			throw e;
		}
	}
	
}
