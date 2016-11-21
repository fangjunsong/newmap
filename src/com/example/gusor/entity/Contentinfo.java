package com.example.gusor.entity;

public class Contentinfo {
private int image;
private String name;
private String type;
private String address;
private String money;
private String meter;
public Contentinfo(int image, String name, String type, String address,
		String money, String meter) {
	super();
	this.image = image;
	this.name = name;
	this.type = type;
	this.address = address;
	this.money = money;
	this.meter = meter;
}
public int getImage() {
	return image;
}
public void setImage(int image) {
	this.image = image;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public String getMoney() {
	return money;
}
public void setMoney(String money) {
	this.money = money;
}
public String getMeter() {
	return meter;
}
public void setMeter(String meter) {
	this.meter = meter;
}
public Contentinfo() {
	// TODO 自动生成的构造函数存根
}
}
