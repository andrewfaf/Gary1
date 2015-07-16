package com.smartapps.accel;

import java.io.Serializable;

public class AccelData implements Serializable {
	private long timestamp;
	private double x;
	private double y;
	private double z;
	private double longtermz;
	
	
	
	public AccelData(long timestamp, double z, double longtermz) {
		this.timestamp = timestamp;
		this.z = z;
		this.longtermz = longtermz;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public double getZ() {
		return z;
	}
	public double getLongtermZ() {
		return longtermz;
	}

	public String toString()
	{
		return "t="+timestamp+", z="+z+", longtermz="+longtermz;
	}


}
