package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Klerisson
 *
 */
public class User implements Serializable{

	private static final long serialVersionUID = 3885057352508680607L;
	
	private Long id;
	private List<Item> items;
	
	/**
	 * @param id
	 * @param items
	 */
	public User(Long id, List<Item> items) {
		super();
		this.id = id;
		this.items = items;
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
	
	public Double[] getItemsRateDoubleArray(){
		Double[] result = new Double[this.items.size()];
		for(int i = 0; i < this.items.size(); i++) {
			result[i] = this.items.get(i).getRate();
		}
		return result;
	}
	
}
