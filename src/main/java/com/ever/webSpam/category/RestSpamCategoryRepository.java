package com.ever.webSpam.category;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.ever.webSpam.restful.HttpGetClient;
import com.ever.webSpam.utility.Constant;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestSpamCategoryRepository implements Constant {

	private final String url = host + "web/category";
	private HttpGetClient httpGetClient;
//	private HttpPostClient httpPostClient;
//	private HttpPutClient httpPutClient;
//	private HttpDeleteClient httpDeleteClient;
	private ObjectMapper mapper;

	@PostConstruct
	public void init() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);

	}

//	public List<Spam> findByUri(Map<String, Object> paramater) {
//		httpGetClient = new HttpGetClient();
//		StringBuffer response = httpGetClient.getHttpResult(url + "/uri", paramater);
//		if (response != null && response.length() > 0) {
//			try {
//				return Arrays.asList(mapper.readValue(response.toString(), Spam[].class));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

	public List<SpamCategory> findAllByOrderByUriAsc() {
		httpGetClient = new HttpGetClient();
		StringBuffer response = httpGetClient.getHttpResult(url, null);
		if (response != null && response.length() > 0) {
			try {
				return Arrays.asList(mapper.readValue(response.toString(), SpamCategory[].class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}



}
