package br.ufu.facom.lsi.prefrec.stratification.model;

import java.io.Serializable;
import java.util.Date;

public class UserItemScorer implements Serializable{

	private static final long serialVersionUID = 1223120191745462536L;

	private int nota;

	private Long itemId;

	private Long userId;
	
	private Date date;

	public int getNota() {
		return nota;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
