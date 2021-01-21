package com.ever.webSpam.restful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import com.ever.webSpam.utility.Constant;


public class HttpDeleteClient implements Constant {

	public static final String USER_AGENT = "Mozilla/5.0";

	public int sendRquestWithAuthHeader(String url) throws IOException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(url);
			connection.setRequestProperty("Authorization", createBasicAuthHeaderValue());
			return connection.getResponseCode();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public int sendRquestWithAuthenticator(String url) throws IOException {
		setAuthenticator();

		HttpURLConnection connection = null;
		try {
			connection = createConnection(url);
			return connection.getResponseCode();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public HttpURLConnection createConnection(String urlString) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(String.format(urlString));
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", createBasicAuthHeaderValue());
			connection.setRequestMethod("DELETE");
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true); // this is to enable writing
			connection.setDoInput(true); // this is to enable reading
			// connection.setConnectTimeout(10000); // 컨텍션타임아웃 10초
			// connection.setReadTimeout(10000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 컨텐츠조회 타임아웃 5총
		return connection;
	}

	public StringBuffer getHttpResult(String url, String json) {
		StringBuffer response = null;
		HttpURLConnection con = createConnection(url);
		try {				
			     /** POSTing **/
	            OutputStream os = con.getOutputStream();
	            os.write(json.getBytes("UTF-8"));
	            os.flush();

	            os.close();
	            con.connect();
	            
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String inputLine;
				response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine + "\n");
				}
				return response;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}
		return null;
	}

	public String createBasicAuthHeaderValue() {
		String auth = user + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		return authHeaderValue;
	}

	public void setAuthenticator() {
		Authenticator.setDefault(new BasicAuthenticator());
	}

	private final class BasicAuthenticator extends Authenticator {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, password.toCharArray());
		}
	}
}
