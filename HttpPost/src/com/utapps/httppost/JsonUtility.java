package com.utapps.httppost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JsonUtility {
	//Convert Stream data into String
	public static String streamToString(InputStream stream)
			throws UnsupportedEncodingException, IOException {
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader reader = null;
		reader = new BufferedReader(new  InputStreamReader(stream, "UTF-8")) ;
		String line = null;
		while((line = reader.readLine()) != null){
			stringBuffer.append(line);
		}
		return stringBuffer.toString();
	}
	
}
