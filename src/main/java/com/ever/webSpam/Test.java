package com.ever.webSpam;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

	static List<String> channelOne = Arrays.asList("tumblr.com", "tistory.com", "blogspot.com", "tiktok.com",
			"postype.com", "egloos.com", "wordpress.com");
	static List<String> channelTwo = Arrays.asList("blog.naver.com", "soundcloud.com", "blog.daum.net", "facebook.com",
			".tiktok.com", "twitter.com", "blog.sina.com.cn", "pinterest.com", "instagram.com");
	static List<String> channelThree = Arrays.asList("youtube.com/channel/", "youtube.com/user/");
	static List<String> channelAll = new ArrayList<String>();
 static List<String> server = Arrays.asList("sites.google.com/view/");
	
	public static void main(String[] args) {
		channelAll.addAll(channelOne);
		channelAll.addAll(channelTwo);
		channelAll.addAll(channelThree);
		System.err.println("all = " + channelAll);

		URL url = null;
		String urlString = "https://www.instagram.com/muffdanae_/";
		try {
			if (!urlString.startsWith("http")) {
				urlString = "http://" + urlString;
			}
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String host = url.getHost();
		String path = url.getFile();

		System.out.println("host =" + url.getHost()); // 호스트 이름 출력
		System.out.println("filename =" + url.getFile());
		System.out.println("path =" + url.getPath());

		if (path.equals("") || path.equals("/")) {
			boolean isContain = channelAll.stream().anyMatch(u -> host.contains(u));
			if (isContain) {
				System.out.println("0. 채널");
			} else {
				System.out.println("서비스");
			}
		} else {
			// 1. 채널 확인
			if(urlString.endsWith("/")) {
				urlString = urlString.substring(0, urlString.length()-1);
			}
			if (isChannel(urlString)) {
				System.out.println("1. 채널");
			} else if(isList(path)){
				System.out.println("2. 리스트");
			} 
		
			// 3. 컨텐드 확인

		}
		
//		boolean isContain = server.stream().anyMatch(u -> "https://www.sites.google.com/view/730q6me8vm".contains(u));
//		if (isContain) {
//			long count = urlString.chars().filter(ch -> ch == '/').count();
//			if (count < 5) {
//				System.err.println("서버");
//			} else {
//				System.err.println("리스트");
//			}
//			
//		}
		
	}

	private static boolean isList(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	// 채널 확인
	private static Boolean isChannel(String url) {

		boolean isContain = channelTwo.stream().anyMatch(u -> url.contains(u));

		if (isContain) {
			long count = url.chars().filter(ch -> ch == '/').count();
			if (count < 4) {
				return true;
			}
		}

		isContain = channelThree.stream().anyMatch(u -> url.contains(u));

		if (isContain) {
			long count = url.chars().filter(ch -> ch == '/').count();
			if (count < 5) {
				return true;
			}
		}
		return false;
	}

}
