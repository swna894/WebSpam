package com.ever.webSpam.restful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.ever.webSpam.utility.Constant;


public class HttpGetClient implements Constant {

	public static final String USER_AGENT = "Mozilla/5.0";

	public int sendRquestWithAuthHeader(String url) throws IOException {
		HttpURLConnection connection = null;
		try {
			connection = createConnection(url, null);
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
			connection = createConnection(url, null);
			return connection.getResponseCode();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	
	public HttpURLConnection createConnection(String urlString , Map<String, Object> parameter ) {
		HttpURLConnection connection = null;
		String uri = ceateUrl(urlString, parameter);
		try {
			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", createBasicAuthHeaderValue());
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
			//connection.setConnectTimeout(10000); // 컨텍션타임아웃 10초
			//connection.setReadTimeout(5000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 컨텐츠조회 타임아웃 5총
		return connection;
	}

	
	private String ceateUrl(String url, Map<String, Object> parameter) {
		String param = null;
		// 파라미터가 있을 경우, 파라미터키=파라미터값&파라미터키=파라미터값&파라미터키=파라미터값 의 형태로 만든다.
		if (parameter != null) {
			StringBuffer sb = new StringBuffer();
			for (String key : parameter.keySet()) {
				if (sb.length() > 0) {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(parameter.get(key));
			}
			param = sb.toString().replace(" ", "%20");
		} else {
			param = "";
		}
		// Http method가 GET 방식의 경우 파라미터를 url 주소 뒤에 붙인다.

		if (url.contains("?")) {
			url += "&" + param;
		} else {
			url += "?" + param;
		}
		return url;
	}

	public StringBuffer getHttpResult(String url, Map<String, Object> parmater) {
		StringBuffer response = null;
		HttpURLConnection con = createConnection(url, parmater);
		try {
			int responseCode = con.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				// System.out.println("\nSending 'GET' request to URL : " + url);
				// System.out.println("Response Code : " + responseCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String inputLine;
				response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine + "\n");
				}
				in.close();
				return response;
			} else {
				System.err.println("Response Code : " + url + " ====> +" + responseCode);
				return null;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
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
