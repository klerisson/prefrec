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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
