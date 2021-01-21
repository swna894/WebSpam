package com.ever.webSpam.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.ever.webSpam.category.SpamCategory;
import com.ever.webSpam.manual.Manual;
import com.ever.webSpam.spam.Spam;
import com.ever.webSpam.utility.Constant;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonUtil implements Constant{

	private ObjectMapper mapper;
	
	public JsonUtil() {
		super();
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	public String saveJsonToCategoryFile(List<SpamCategory> categoryList) {

		try {
			String jsonList = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(categoryList);
			Files.write(Paths.get(CATEGORY_FILE), jsonList.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public List<SpamCategory> convertJsonToCategoryList(String filePath) {

		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s));
			return Arrays.asList(mapper.readValue(contentBuilder.toString(), SpamCategory[].class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public Object saveJsonToSpamFile(List<Spam> spamList) {
		try {
			String jsonList = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(spamList);
			Files.write(Paths.get(SPAM_FILE), jsonList.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
//	public List<Spam> convertJsonToSpamList(String jsonPath) {
//		StringBuilder contentBuilder = new StringBuilder();
//		try (Stream<String> stream = Files.lines(Paths.get(jsonPath), StandardCharsets.UTF_8)) {
//			stream.forEach(s -> contentBuilder.append(s));
//			return Arrays.asList(mapper.readValue(contentBuilder.toString(), Spam[].class));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public List<Manual> convertJsonToManualList(String jsonPath) {
		StringBuilder contentBuilder = new StringBuilder();
		
		try (Stream<String> stream = Files.lines(Paths.get(jsonPath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s));
			return Arrays.asList(mapper.readValue(contentBuilder.toString(), Manual[].class));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public String saveJsonToManualFile(List<Manual> manualList) {
		String jsonList = "";
		try {
			jsonList = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(manualList);
			Files.write(Paths.get(JSON_FILE), jsonList.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonList;
	}
}
