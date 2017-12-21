package me.chuang6.jz.bean;

public class HotNumber {
	private String number;
	private Integer count;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public HotNumber(String number, Integer count) {
		super();
		this.number = number;
		this.count = count;
	}
	
	
	
	
	
}
