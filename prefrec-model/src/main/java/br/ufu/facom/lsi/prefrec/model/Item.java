/**
 * 
 */
package br.ufu.facom.lsi.prefrec.model;

import java.io.Serializable;

/**
 * @author Klerisson
 *
 */
public class Item implements Serializable {

	private static final long serialVersionUID = 2614346518688065606L;

	private Long id;
	private Double rate;
	
	/**
	 * @param id
	 * @param rate
	 */
	public Item(Long id, Double rate) {
		super();
		this.id = id;
		this.rate = rate;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the rate
	 */
	public Double getRate() {
		return rate;
	}
	
}
