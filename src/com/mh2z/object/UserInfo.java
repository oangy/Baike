package com.mh2z.object;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {

	private String name;
	private int gender;
	private String imageUrl;

	public UserInfo(String name, int gender, String imageUrl) {
		super();
		this.name = name;

		this.gender = gender;
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeString(name);
		parcel.writeInt(gender);
		parcel.writeString(imageUrl);
	}

}
