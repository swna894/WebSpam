package com.ever.webSpam.sample;

import java.net.MalformedURLException;
import java.net.URL;

public class ContainsTest {

	public static void main(String[] args) {
		String uri = "https://sites.google.com/site/mas16ajicheongug/home/daejeon-seogu-chuljangmasaji";
		if (uri.contains("sites.google.com/site")) { 
			System.err.println("true");
		} else {
			System.err.println("false");
		}

		try {
			URL aURL = new URL("http://java.sun.com:80/docs/books/tutorial"
			        + "/index.html?name=networking#DOWNLOADING");
			System.out.println("protocol = " + aURL.getProtocol());
			System.out.println("authority = " + aURL.getAuthority());
			System.out.println("host = " + aURL.getHost());
			System.out.println("port = " + aURL.getPort());
			System.out.println("path = " + aURL.getPath());
			System.out.println("query = " + aURL.getQuery());
			System.out.println("filename = " + aURL.getFile());
			System.out.println("ref = " + aURL.getRef());
			
			uri = uri.substring(8);
			System.err.println(uri);
			System.err.println(uri.split("/").length);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
