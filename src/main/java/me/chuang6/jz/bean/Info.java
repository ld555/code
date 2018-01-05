package me.chuang6.jz.bean;

import java.io.Serializable;
import java.util.Date;

public class Info implements Serializable {
	private Integer id;

	private Integer periods;

	private String number;

	private Date addtime;

	public Info(int periods, String number) {
		this.periods = periods;
		this.number = number;
	}

	public Info() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number == null ? null : number.trim();
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	@Override
	public String toString() {
		return "期号:" + periods + " 号码:" + number + " 获取成功";
	}
}