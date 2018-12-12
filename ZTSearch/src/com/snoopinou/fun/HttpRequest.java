package com.snoopinou.fun;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpRequest {
	
	
	DebugViewer debug = ButtonViewer.debug;
	
//	final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36";
//	final String AGENT = "snoopinou";
	
	String stringURL;
	HttpURLConnection con;
	
	
	
	public HttpRequest(String stringURLp) {
		stringURL = stringURLp;
	}
	
	
	public String doPostRequest(Map<String, String> requestParam) throws IOException {
		connect();
		addBodyParameters(requestParam);
		
		debug.println("Doing a POST request at "+ con.getURL()+" with arguments "+buildString(requestParam), DebugViewer.LIGHT);
		
		String response = getResponse();
		
		debug.println("Response is : "+response, DebugViewer.HEAVY);
		
		return response;
	}
	
	public String doGetRequest(Map<String, String> requestParam) throws IOException {
		addURLParameters(requestParam);
		connect();
		
		debug.println("Doing a GET request at "+ con.getURL(), DebugViewer.LIGHT);
		
		String response = getResponse();
		
		debug.println("Response is : "+response, DebugViewer.HEAVY);
		if(response.contains("errorCode")) {
			String code = "no Code";
			String str = response;
			
			// Only get the errorcode (in json format)
			str = str.substring(str.indexOf("errorCode")+1);
			str = str.substring(str.indexOf('"')+2);
			
			code = str.substring(0, str.length()-2);
			
			debug.println("Error Code is : "+code, DebugViewer.LIGHT);
		}
		
		return response;
	}
	
	
	
	private String getResponse() throws IOException {
		BufferedReader in;
		if(con.getResponseCode() != 200) {
			in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		}else {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		}
		String str = "";
		String line;
		
		while((line = in.readLine()) != null) {
			str += line+"\n";
		}
		
		return str;
	}
	
	
	private void addURLParameters(Map<String,String> param) {
		if(param != null) {
			stringURL += "?"+buildString(param);
		}
	}
	
	
	// Add to the connection body the parameters
	private void addBodyParameters(Map<String, String> param) throws IOException {
		con.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeBytes(buildString(param));
		out.flush();
		out.close();
	}
	
	
	// Create the argument for GET and POST request
	private String buildString(Map<String, String> param) {
		String str = "";
		
		for(Map.Entry<String, String> entry : param.entrySet()) {
			str += entry.getKey();
			str += "=";
			str += entry.getValue();
			str += "&";
			
		}
		str = str.substring(0, str.length()-1);
		
		return str;
	}
	
	private void connect() throws IOException {
		con = (HttpURLConnection) new URL(stringURL).openConnection();
//		con.setRequestProperty("User-Agent", USER_AGENT);
//		con.setRequestProperty("Content-Type", "application/json");
	}
	
}
