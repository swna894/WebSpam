package com.ever.webSpam.spam;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.ever.webSpam.restful.HttpDeleteClient;
import com.ever.webSpam.restful.HttpGetClient;
import com.ever.webSpam.restful.HttpPostClient;
import com.ever.webSpam.utility.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties
@Service	
public class RestSpamRepository implements Constant {

	private final String url = host + "web/spams";
	private HttpGetClient httpGetClient;
	private HttpPostClient httpPostClient;
//	private HttpPutClient httpPutClient;
	private HttpDeleteClient httpDeleteClient;
	private ObjectMapper mapper;


	@PostConstruct
	public void init() {
		mapper = new ObjectMapper();
//	    mapper = JsonMapper.builder()
//        .addHandler(new DeserializationProblemHandler() {
//            @Override
//            public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
//                if (targetType == Boolean.class) {
//                    return Boolean.TRUE.toString().equalsIgnoreCase(valueToConvert);
//                }
//                return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
//            }
//        })
//        .build();  
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		
	}

	public List<Spam> findByUri(Map<String, Object> paramater) {
	httpGetClient = new HttpGetClient();
	StringBuffer response = httpGetClient.getHttpResult(url + "/uri", paramater);
	if (response != null && response.length() > 0) {
		try {
			return Arrays.asList(mapper.readValue(response.toString(), Spam[].class));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	return null;
}

	public List<Spam> findAllByOrderByWorkdayDesc() {
		httpGetClient = new HttpGetClient();
		StringBuffer response = httpGetClient.getHttpResult(url, null);
//		System.err.println(response);

		if (response != null && response.length() > 0) {
			try {
				return Arrays.asList(mapper.readValue(response.toString(), Spam[].class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<Spam> saveAll(List<Spam> spamList) {
		httpPostClient = new HttpPostClient();
		String json = "";
		try {
			json = mapper.writeValueAsString(spamList);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		StringBuffer response = httpPostClient.getHttpResult(url, json);
		if (response != null) {
			try {
				return Arrays.asList(mapper.readValue(response.toString(), Spam[].class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public List<Spam> deleteBySelectedAndWorkday(boolean b, String date) {
		httpDeleteClient = new HttpDeleteClient();
		String jsonList = "";
		try {
			jsonList = mapper.writeValueAsString(null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		StringBuffer response = httpDeleteClient.getHttpResult(url + "/" + date, jsonList);
		if (response != null) {
			try {
				return Arrays.asList(mapper.readValue(response.toString(), Spam[].class));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	public List<Spam> deleteBySpam(Spam spam) {
		httpDeleteClient = new HttpDeleteClient();
		String jsonList = "";
		try {
			jsonList = mapper.writeValueAsString(spam);
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuffer response = httpDeleteClient.getHttpResult(url, jsonList);
		if (response != null) {
			try {
				return Arrays.asList(mapper.readValue(response.toString(), Spam[].class));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	public List<Spam> findBySelectedOrderByWorkdayDesc(boolean b) {
		// TODO Auto-generated method stub
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

//	public List<Account> put(List<Account> accountList) {
//		httpPutClient = new HttpPutClient();
//		String jsonList = "";
//		try {
//			jsonList = mapper.writeValueAsString(accountList);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		
//		StringBuffer response = httpPutClient.getHttpResult(url, jsonList);
//		if (response != null) {
//			try {
//				return Arrays.asList(mapper.readValue(response.toString(), Account[].class));
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
