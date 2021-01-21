package com.webspam.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WordSearch {
	
	private String homeDirectory = System.getProperty("user.home");
	private ObservableList<String> spamList = FXCollections.observableArrayList("대출", "게임", "위조", "중절수술", "자격증", "바카라", "황금성", "먹튀", "부스타빗", "온카지노",
			"네임드", "바둑이게임", "쓰리랑게임", "망치게임", "먹튀검증", "적토마게임", "미프진", "비아그라", "시알리스", "조루방지제", "딜도", "자위용품", "우머나이저",
			"출장안마", "룸싸롱", "풀싸롱", "키스방", "오피", "호스트바", "수요비", "방석방", "셔츠룸", "밍키넷", "춘자넷", "소라넷", "소액결재", "현금화",
			"정보이용료", "섹파");

	private HashMap<String, Integer> hashMapSpam = null;
	public String CountWord(String string) {
		
		File spamFile = new File(homeDirectory + File.separator + "Desktop" + File.separator + "spam.txt");
		
		if(spamFile.exists()) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(spamFile),"utf-8"));
				String line = reader.readLine();
				while (line != null) {
					spamList.add(line.trim());
					// read next line
					line = reader.readLine();	
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
		
		hashMapSpam = new HashMap<String, Integer>();
		String[] words = null;
		words = string.split(" "); // Split the word using space
		
		for (String spam : spamList) {
			int count = 0;
			for (String word : words) {
				if (word.contains(spam)) // Search for the given word
				{
					count++; // If Present increase the count by one
				}
			}
			if(count > 0) {
				hashMapSpam.put(spam, count);
			}
		}		
		return hashMapSpam.toString();
	}
}
