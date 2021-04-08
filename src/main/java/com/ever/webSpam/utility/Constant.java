package com.ever.webSpam.utility;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface Constant {

	ObservableList<String> observableListName = FXCollections.observableArrayList(" ", "나상운", "송호열", "엄정란", "연제관", "이미원",
			"이성규", "임수진", "정영미", "정영선", "최동우", "최붕희", "허창건");
	ObservableList<String> observableListSpam = FXCollections.observableArrayList(" ", "비정상 광고 컨테트", "비정상 텍스트", 
			"스펨 사이트로 리디렉션", "악성 소프트웨어", "저작권위반", "음란물", "기만적 컨텐트", "웹순위 조작 활동", "불법 사이트", "정상 컨텐트", 
			"정상 컨텐트 저품질");
	
	ObservableList<String> observableListSite = FXCollections.observableArrayList(" ",
			"서비스 메인 페이지", "채널 메인 페이지", "컨텐츠 리스트 페이지", "컨텐트 페이지", "사이트미분류", "검수불가", "보류");

	ObservableList<String> observableListWhite = FXCollections.observableArrayList(" ", "All", "Target", "NoTarget");
	String user = "swna8934@gmail.com";
	String password = "new135!#%";
	//String host = "http://cafe1404.cafe24.com/";
    String host = "http://localhost:8080/";
	Charset charset = Charset.forName("UTF-8");
	
	//String JSON_FILE = System.getProperty("user.home") + File.separator + "manual.json";
	//String QC_FILE = "d:/웹스팸/";
	String QC_FILE = Paths.get(".").toAbsolutePath().normalize().toString();
	String EXCEL_FILE = "d:/웹스팸/manual.xlsx";
	String JSON_FILE = "d:/웹스팸/manual.json";
	String CATEGORY_FILE = "d:/웹스팸/category.json";
	String SPAM_FILE = "d:/웹스팸/spam.json";
	String HOME_DIR = System.getProperty("user.home");
	//public static String FILE = "d:/웹스팸/manual.xlsx";
	Boolean isDBMS = false;
	Boolean isFile = new File(QC_FILE + "/manual.xlsx").exists();
	public enum HttpMethod {
		GET, POST, PUT, DELETE
	}




}
