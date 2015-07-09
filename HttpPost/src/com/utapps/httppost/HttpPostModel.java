package com.utapps.httppost;

import java.io.Serializable;

public class HttpPostModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mEmail;

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		mEmail = email;
	}
	
}
