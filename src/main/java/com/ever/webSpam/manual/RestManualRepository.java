package com.ever.webSpam.manual;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.ever.webSpam.restful.HttpDeleteClient;
import com.ever.webSpam.restful.HttpGetClient;
import com.ever.webSpam.restful.HttpPutClient;
import com.ever.webSpam.utility.Constant;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service	
public class RestManualRepository implements Constant {

	private final String url = host + "web/manuals";
	private HttpGetClient httpGetClient;
//	private HttpPostClient httpPostClient;
	private HttpPutClient httpPutClient;
 	private HttpDeleteClient httpDeleteClient;
	private ObjectMapper mapper;


	@PostConstruct
	public void init() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		
	}

	public List<Manual> findByAll(Map<String, Object> paramater) {
	httpGetClient = new HttpGetClient();
	StringBuffer response = httpGetClient.getHttpResult(url, paramater);
	if (response != null && response.length() > 0) {
		try {
			return Arrays.asList(mapper.readValue(response.toString(), Manual[].class));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return null;
}

	
	public Manual put(Manual manual) {
	httpPutClient = new HttpPutClient();
	String jsonList = "";
	try {
		jsonList = mapper.writeValueAsString(manual);
	} catch (IOException e) {
		e.printStackTrace();
	}

	
	StringBuffer response = httpPutClient.getHttpResult(url, jsonList);
	if (response != null) {
		try {
			return mapper.readValue(response.toString(), Manual.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return null;
}

	
	public List<Manual> delete(Manual manual) {
	httpDeleteClient = new HttpDeleteClient();
	String jsonList = "";
	try {
		jsonList = mapper.writeValueAsString(manual);
	} catch (IOException e) {
		e.printStackTrace();
	}

	
	StringBuffer response = httpDeleteClient.getHttpResult(url, jsonList);
	if (response != null) {
		try {
			return Arrays.asList(mapper.readValue(response.toString(), Manual[].class));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return null;
}
	
//	public List<Account> getAccountList() {
//		httpGetClient = new HttpGetClient();
//		StringBuffer response = httpGetClient.getHttpResult(url, null);
//		if (response != null) {
//			try {
//				return Arrays.asList(mapper.readValue(response.toString(), Account[].class));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}


	
//	public Account save(Account account) {
//		httpPostClient = new HttpPostClient();
//		String json = "";
//		try {
//			json = mapper.writeValueAsString(account);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		
//		StringBuffer response = httpPostClient.getHttpResult(url, json);
//		if (response != null) {
//			try {
//				return mapper.readValue(response.toString(), Account.class);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}


	
//	public List<Account> delete(List<Account> accountList) {
//		httpDeleteClient = new HttpDeleteClient();
//		String jsonList = "";
//		try {
//			jsonList = mapper.writeValueAsString(accountList);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		
//		StringBuffer response = httpDeleteClient.getHttpResult(url, jsonList);
//		if (response != null) {
//			try {
//				return Arrays.asList(mapper.readValue(response.toString(), Account[].class));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

//	public Account findByShop(Map<String, Object> paramater) {
//		return findAccount(url+ "/s", paramater);
//	}
	
//	public Account findByEmail(Map<String, Object> paramater) {
//		return findAccount(url+ "/e", paramater);
//	}
	

}
