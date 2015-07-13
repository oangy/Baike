package com.mh2z.object;

public class ListItem {
	private int id;
	private String name;
	private int flag; // flag:0表示分类，1表示词条
	private String picurl;

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ListItem(int id, String name, int flag,String url) {
		super();
		this.id = id;
		this.name = name;
		this.flag = flag;
		this.picurl=url;
	}

	public ListItem(int id, String name, int flag) {
		super();
		this.id = id;
		this.name = name;
		this.flag = flag;
	}

}
