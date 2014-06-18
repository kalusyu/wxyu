package com.sg.mtfont.xml;

import java.io.Serializable;

public class Config implements Serializable{
	
	private boolean isFree;

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}
	
	
}

class ConfigTag{
	public static final String IS_FREE = "isFree";
}
