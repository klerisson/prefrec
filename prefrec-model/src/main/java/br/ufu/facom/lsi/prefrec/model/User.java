package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Klerisson
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = 3885057352508680607L;

	private Long id;
	private List<Item> items;
	private List<User> friends;
	private Map<User, Double> centralityMap;

	private Map<User, Double> mutualFriendsMap;
	private Map<User, Double>interactionMap;
	
	public User(Long userId) {
		super();
		this.id = userId;
	}

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

	
	
	/**
	 * @param id
	 * @param items
	 * @param friends
	 * @param centralityMap
	 */
	public User(Long id, List<Item> items, List<User> friends,
			Map<User, Double> centralityMap,Map<User, Double> mutualFriendsMap,Map<User, 
			Double> interactionMap ) {
		this(id, items, friends);
		this.centralityMap = centralityMap;
		this.mutualFriendsMap = mutualFriendsMap;
		this.interactionMap = interactionMap;
	}
	
	public User(Long id, List<Item> items, List<User> friends,
			Map<User, Double> centralityMap,Map<User, Double> mutualFriendsMap) {
		this(id, items, friends);
		this.centralityMap = centralityMap;
		this.mutualFriendsMap = mutualFriendsMap;
	}
	public User(Long id, List<Item> items, List<User> friends,
			Map<User, Double> centralityMap) {
		this(id, items, friends);
		this.centralityMap = centralityMap;
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
	 * @return the centralityMap
	 */
	public Map<User, Double> getCentralityMap() {
		return centralityMap;
	}
	
	public Map<User, Double> getMutualFriendsMap() {
		// TODO Auto-generated method stub
		return mutualFriendsMap;
	}
	public Map<User, Double> getInteractionMap() {
		// TODO Auto-generated method stub
		return interactionMap;
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
