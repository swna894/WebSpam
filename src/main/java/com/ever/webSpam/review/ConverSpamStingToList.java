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
		int LOOK_CH = 10;
		int LOOK_LIST = 11;
		int LOOK_CONT = 12;
		int HAM = 13;
		int HAM_LOW = 14;
		int HAM_FISH = 15;
		int SPAM_AD = 16;
		int SPAM_TEXT = 17;
		int SPAM_REDIR = 18;
		int SPAM_MALWARE = 19;
		int SPAM_COPY = 20;
		int SPAM_PORN = 21;
		int SPAM_PORNWEAK = 22;
		int SPAM_DECEP = 23;
		int SPAM_MAINIP = 24;
		int SPAM_ILLEGAL = 25;
		
		
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
			if (array[LOOK_CH].equals("true")) {
				spam.setLookCh(true);
			}
			if (array[LOOK_LIST].equals("true")) {
				spam.setLookList(true);
			}
			if (array[LOOK_CONT].equals("true")) {
				spam.setLookCont(true);
			}
			if (array[HAM].equals("true")) {
				spam.setHam(true);
			}
			if (array[HAM_LOW].equals("true")) {
				spam.setHamLow(true);
			}
			if (array[HAM_FISH].equals("true")) {
				spam.setHamFish(true);
			}
			
			if (array[SPAM_AD].equals("true")) {
				spam.setSpamAd(true);
			}
			if (array[SPAM_TEXT].equals("true")) {
				spam.setSpamText(true);
			}
			if (array[SPAM_REDIR].equals("true")) {
				spam.setSpamRedir(true);
			}
			if (array[SPAM_MALWARE].equals("true")) {
				spam.setSpamMalware(true);
			}
			if (array[SPAM_COPY].equals("true")) {
				spam.setSpamCopy(true);
			}
			if (array[SPAM_PORN].equals("true")) {
				spam.setSpamPorn(true);
			}
			if (array[SPAM_PORNWEAK].equals("true")) {
				spam.setSpamPornWeak(true);
			}		
			if (array[SPAM_DECEP].equals("true")) {
				spam.setSpamDecep(true);
			}
			if (array[SPAM_MAINIP].equals("true")) {
				spam.setSpamManip(true);
			}
			if (array[SPAM_ILLEGAL].equals("true")) {
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
