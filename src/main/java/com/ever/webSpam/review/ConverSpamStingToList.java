package com.ever.webSpam.review;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ever.webSpam.spam.Spam;
import com.ever.webSpam.spam.RestSpamRepository;


@Service
public class ConverSpamStingToList {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

	@Autowired
	RestSpamRepository restSpamRepository;

	@Autowired
	ReviewController crossQcController;
	
	public List<Spam> post(List<String> downloadList) {
		List<Spam> spamList = getListSpamBean(downloadList);
		//List<Spam> newSpamList = restSpamRepository.saveAll(spamList);
		//if(newSpamList != null) {
		//	return newSpamList;
		//}
		return spamList;

	}

	public List<Spam> getListSpamBean(final List<String> stringList) {

		int URI = 0;
		int SCOPE = 1;
		int NAME = 7;
		int NA = 3;
		int DEFER = 4;
		int RESPONSETIME = 6; 
		int LOOKMAIN = 9;
		
		final List<Spam> spamList = new ArrayList<Spam>();
		
		for (final String line : stringList) {
			Spam spam = new Spam();
			String[] array = line.split("\t");

			if (array[URI].equals("uri") || array[URI].equals("key"))
				continue;

			spam.setUri(array[URI]);
			spam.setScope(array[SCOPE]);
			spam.setName(array[NAME]);
			spam.setRequestTime(array[2]);
			spam.setTimeOfInspection(array[6]);
			spam.setEmail(array[8]);
			spam.setBooleanNotCheck(Boolean.valueOf(array[NA]));
			spam.setNotCheck(array[NA].equals("true") ? "검수불가" : "");
			spam.setBooleanDefer(Boolean.valueOf(array[DEFER]));
			spam.setDefer(array[DEFER].equals("true") ? "보류" : "");
			spam.setBooleanBlock(Boolean.valueOf(array[5]));
			
			//if (array[LABEL].contains("lookMain")) {
			if (array[LOOKMAIN].equals("true")) {
				spam.setLookMain(true);
			}
			if (array[10].equals("true")) {
				spam.setLookCh(true);
			}
			if (array[11].equals("true")) {
				spam.setLookList(true);
			}
			if (array[12].equals("true")) {
				spam.setLookCont(true);
			}
			if (array[13].equals("true")) {
				spam.setHam(true);
			}
			if (array[14].equals("true")) {
				spam.setHamLow(true);
			}
			if (array[15].equals("true")) {
				spam.setSpamAd(true);
			}
			if (array[16].equals("true")) {
				spam.setSpamText(true);
			}
			if (array[17].equals("true")) {
				spam.setSpamRedir(true);
			}
			if (array[18].equals("true")) {
				spam.setSpamMalware(true);
			}
			if (array[19].equals("true")) {
				spam.setSpamCopy(true);
			}
			if (array[20].equals("true")) {
				spam.setSpamPorn(true);
			}
			if (array[21].equals("true")) {
				spam.setSpamPornWeak(true);
			}		
			if (array[22].equals("true")) {
				spam.setSpamDecep(true);
			}
			if (array[23].equals("true")) {
				spam.setSpamManip(true);
			}
			if (array[24].equals("true")) {
				spam.setSpamIllegal(true);
			}
//			try {
//				if (!array[LABEL].equals("null")) {
//					final JSONParser jsonParser = new JSONParser();
//					final JSONObject jsonObj = (JSONObject) jsonParser.parse(array[LABEL]);
//					final Boolean ham = (Boolean) jsonObj.get("ham");
//					final Boolean hamLow = (Boolean) jsonObj.get("hamLow");
//					if (ham != null && ham) {
//						spam.setHam(true);
//					}
//					if (hamLow != null && hamLow) {
//						spam.setHamLow(true);
//					}
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
	
			spam.setSelected(false);
			spam.setWorkday(LocalDate.parse(array[RESPONSETIME], formatter).toString());

			spamList.add(spam);
		}
		return spamList;
	}
}
