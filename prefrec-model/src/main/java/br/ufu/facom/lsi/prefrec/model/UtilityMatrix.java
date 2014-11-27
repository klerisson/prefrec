package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Klerisson
 *
 */
public class UtilityMatrix implements Serializable {

	private static final long serialVersionUID = -4891906983420220873L;

	private List<User> users;

	public UtilityMatrix() {
		super();
		this.users = new ArrayList<>();
	}
	
	public boolean addUser(User user) {
		return this.users.add(user);
	}
	
}
