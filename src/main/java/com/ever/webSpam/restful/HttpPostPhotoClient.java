package com.ever.webSpam.restful;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import com.ever.webSpam.utility.Constant;



public class HttpPostPhotoClient implements Constant {

	public static final String USER_AGENT = "Mozilla/5.0";
	public String boundary = Long.toHexString(System.currentTimeMillis());

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
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
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

	public StringBuffer getHttpResult(String url, String pathPhoto) {
		StringBuffer response = null;
		HttpURLConnection con = createConnection(url);
		try {

			PrintWriter writer = null;
			try {
				writer = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));

			    File dir = new File(pathPhoto);
			    for (File file : dir.listFiles()) {
			        if (file.isDirectory()) {
			            continue;
			        }
					writer.println("--" + boundary);
					writer.println("Content-Disposition: form-data; name=\"" + "files" + "\"; filename=\""
							+ file.getName() + "\"");
					writer.println("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()));
					writer.println("Content-Transfer-Encoding: binary");
					writer.println();
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
						for (String line; (line = reader.readLine()) != null;) {
							writer.println(line);
						}
					} finally {
						if (reader != null) {
							reader.close();
						}
					}
				}
				writer.println("--" + boundary + "--");
			} finally {
				if (writer != null)
					writer.close();
			}
			
			
			// Connection is lazily executed whenever you request any status.
			int responseCode = ((HttpURLConnection) con).getResponseCode();

			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String inputLine;
				response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine + "\n");
				}
				return response;
			} else {
				return null;
			}
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
