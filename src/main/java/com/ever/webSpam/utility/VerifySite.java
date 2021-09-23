package com.ever.webSpam.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

@Component
public class VerifySite {

	private String prefix = "site:";
	private String home = System.getProperty("user.home");
	private String explore = "C:\\Program Files\\Internet Explorer\\iexplore.exe ";
	private String chrome = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
	private String hiddenPrefix = "http://spamdepot.navercorp.com/html/view_doc_text.html?search=";
	//private String hiddenPrefix = "http://cclient006.logm.nfra.io:8021/html/view_doc_text.html?search=";
	
	public VerifySite() {
		// TODO Auto-generated constructor stub
	}

	public void setClipbord(String uri) {
		if (uri.startsWith(prefix)) {
			uri = uri.substring(5);
		}
		// StringSelection stringSelection = new StringSelection(uri);
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(uri);

		// if (stringSelection != null && clipboard != null)
		Platform.runLater(() -> {
			clipboard.setContent(clipboardContent);
		});

	}
	
	public String getClipboard() {
		// StringSelection stringSelection = new StringSelection(uri);
		String url = null;
		Clipboard clipboard = Clipboard.getSystemClipboard();
		
		if(clipboard.hasUrl()) {
			url = clipboard.getUrl();
		} else if(clipboard.hasString()) {
			url = clipboard.getString();
		}

		return url;
	}
	
//	private void startBrowser(String uri, String browser) {
//
//		try {
//			String[] b = { browser, uri };
//			if (process != null && browser.contains("iexplore")) {
//				process.destroyForcibly();
//			}
//			process = Runtime.getRuntime().exec(b);
//
//		} catch (Exception exc) {
//			exc.printStackTrace();
//		}
//
//	}

	public void startBrowser(String uri, String browser) {

		try {
			String[] b = { browser, uri };
			Runtime.getRuntime().exec(b);

		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private Process process = null;

	public void eventSearchResult(String uri) {
		String prefixUrl = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=";
		//prefixUrl = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=";
		prefixUrl = "https://search.naver.com/search.naver?display=10&f=&filetype=0&page=2&research_url=&sm=tab_pge&start=1&where=web&query=";
		if (!uri.startsWith(prefix)) {
			uri = prefix + uri;
		}
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(uri);

		Platform.runLater(() -> {
			clipboard.setContent(clipboardContent);
		});

		uri = prefixUrl + uri;
		try {
			String[] b = { chrome, uri };
			process = Runtime.getRuntime().exec(b);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public void eventSearchResultAutoReview(String uri) {
		String prefixUrl = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=";
		//prefixUrl = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=";
		if (!uri.startsWith(prefix)) {
			uri = prefix + uri;
		}

		uri = prefixUrl + uri;
		try {
			String[] b = { chrome, uri };
			process = Runtime.getRuntime().exec(b);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void eventInspectReult(String uri) {
		String prefixUrl = "http://spamdepot.navercorp.com/html/inspect_result.html?offset=0&limit=20&search=";
		//String prefixUrl = "http://cclient006.logm.nfra.io:8021/html/inspect_result.html?offset=0&limit=20&search=";
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(uri);

		Platform.runLater(() -> {
			clipboard.setContent(clipboardContent);
		});

		uri = prefixUrl + uri;
		try {
			String[] b = { chrome, uri };
			process = Runtime.getRuntime().exec(b);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void eventInspectReultAutoReview(String uri) {
		String prefixUrl = "http://spamdepot.navercorp.com/html/inspect_result.html?offset=0&limit=20&search=";
		//String prefixUrl = "http://cclient006.logm.nfra.io:8021/html/inspect_result.html?offset=0&limit=20&search=";

		uri = prefixUrl + uri;
		try {
			String[] b = { chrome, uri };
			process = Runtime.getRuntime().exec(b);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void hiddenText(String uri) {		
		if (uri.startsWith(prefix)) {
			uri = uri.substring(prefix.length());
		}
		
		try {
			String enStr = URLEncoder.encode(uri, "UTF-8");
			startBrowser(hiddenPrefix + enStr, getChrome());
			//setClipbord(uri);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getExplore() {
		return explore;
	}

	public void setExplore(String explore) {
		this.explore = explore;
	}

	public String getChrome() {
		return chrome;
	}

	public void setChrome(String chrome) {
		this.chrome = chrome;
	}
}
