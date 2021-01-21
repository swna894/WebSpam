package com.ever.webSpam.manual;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class Manual {


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	
	private StringProperty doc = new SimpleStringProperty();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(length = 1024)
	@Access(AccessType.PROPERTY)
	public String getDoc() { return doc.get(); }
	public void setDoc(String doc) { this.doc.set(doc); }
	public StringProperty docProperty() { return doc; }
	
	@Override
	public String toString() {
		return "Manual [id=" + id + ", doc=" + doc + "]\n";
	}
}
