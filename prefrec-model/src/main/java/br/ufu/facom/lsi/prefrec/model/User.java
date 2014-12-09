package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Klerisson
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = 3885057352508680607L;

	private Long id;
	private List<Item> items;
	private List<User> friends;

	/**
	 * @param id
	 * @param items
	 */
	public User(Long id, List<Item> items) {
		this(id);
		this.items = items;
		this.friends = new ArrayList<>();
	}

	/**
	 * @param id
	 * @param items
	 * @param friends
	 */
	public User(Long id, List<Item> items, List<User> friends) {
		this(id, items);
		this.friends = friends;
	}

	public User(Long userId) {
		super();
		this.id = userId;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the items
	 */
	public List<Item> getItems() {
		return items;
	}

	public Double[] getItemsRateDoubleArray() {
		Double[] result = new Double[this.items.size()];
		for (int i = 0; i < this.items.size(); i++) {
			result[i] = this.items.get(i).getRate();
		}
		return result;
	}

	/**
	 * @return the friends
	 */
	public List<User> getFriends() {
		return friends;
	}

	/**
	 * @param friends
	 *            the friends to set
	 */
	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
