package com.mhacks.bankrupt;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Company implements Parcelable {
	private String name;
	private String descr;
	public String ticker;
	private String Sector, industry;
	private String subindustry;

	public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
		public Company createFromParcel(Parcel in) {
			return new Company(in);
		}

		public Company[] newArray(int size) {
			return new Company[size];
		}
	};

	public Company(String n, String d, String dt) {
		name = n;
		descr = d;
		subindustry = dt;
	}

	public Company(String n, String d, String dt, String a, String b, String c) {
		name = n;
		descr = d;
		subindustry = dt;
		ticker = a;
		industry = b;
		Sector = c;
	}

	public Company(Parcel in) {
		this(in.readString(), in.readString(), in.readString(),
				in.readString(), in.readString(), in.readString());
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return descr;
	}

	public String getDate() {
		return subindustry;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name);
		out.writeString(descr);
		out.writeString(subindustry);
		out.writeString(ticker);
		out.writeString(Sector);
		out.writeString(industry);
	}

	// public String toString() {
	// return getName() + ", " + getDescription() + ", " + getDate().toString();
	// }
}
