package com.ever.webSpam.spam;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ever.webSpam.cressQc.SpamCheck;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class unknowledge {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private final StringProperty uri = new SimpleStringProperty();

    
	public void setSpamCheck(SpamCheck spamCheck) {
	}
	
	@Column(length = 1024)
	@Access(AccessType.PROPERTY)
	public String getUri() { return uri.get(); }
	public void setUri(String uri) { this.uri.set(uri); }
	public StringProperty uriProperty() { return uri; }
 

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

}
