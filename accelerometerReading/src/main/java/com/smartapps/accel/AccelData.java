package com.smartapps.accel;

import java.io.Serializable;

public class AccelData implements Serializable {
	private long timestamp;
	private double x;
	private double y;
	private double z;
	private double longtermz;
	
	
	
	public AccelData(long timestamp, double x, double y, double z, double longtermz) {
		this.timestamp = timestamp;
		this.x = x;
		this.y = y;
		this.z = z;
		this.longtermz = longtermz;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	public double getLongtermZ() {
		return longtermz;
	}
	public void setLongtermz(double longtermz) {
		this.longtermz = longtermz;
	}

	public String toString()
	{
		return "t="+timestamp+", x="+x+", y="+y+", z="+z+", longtermz="+longtermz;
	}


}
