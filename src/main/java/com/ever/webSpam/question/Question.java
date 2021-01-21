package com.ever.webSpam.question;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class Question implements Comparable<Question> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private final StringProperty uri = new SimpleStringProperty();
	private final StringProperty memo = new SimpleStringProperty();
	private final StringProperty date = new SimpleStringProperty();

	@Transient
	private Integer no;

	private BooleanProperty selected = new SimpleBooleanProperty(false);

	private final StringProperty qc = new SimpleStringProperty();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	public String getQc() {
		return qc.get();
	}

	public void setQc(String qc) {
		this.qc.set(qc);
	}

	public StringProperty qcProperty() {
		return qc;
	}

	@Column(length = 1024)
	@Access(AccessType.PROPERTY)
	public String getUri() {
		return uri.get();
	}

	public void setUri(String uri) {
		this.uri.set(uri);
	}

	public StringProperty uriProperty() {
		return uri;
	}

	@Access(AccessType.PROPERTY)
	public String getDate() {
		return date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public StringProperty dateProperty() {
		return date;
	}

	@Access(AccessType.PROPERTY)
	public String getMemo() {
		return memo.get();
	}

	public void setMemo(String memo) {
		this.memo.set(memo);
	}

	public StringProperty memoProperty() {
		return memo;
	}

	@Access(AccessType.PROPERTY)
	public Boolean getSelected() {
		return selected.get();
	}

	public void setSelected(Boolean selected) {
		this.selected.set(selected);
	}

	public BooleanProperty selectedProperty() {
		return selected;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", uri=" + uri + ", memo=" + memo + ", date=" + date + ", no=" + no
				+ ", selected=" + selected + ", qc=" + qc + "]\n";
	}

	@Override
	public int compareTo(Question m) {
		int compareResult = this.getDate().compareTo(m.getDate());
		if (compareResult < 0) {
			return 1;
		} else if (compareResult > 0) {
			return -1;
		}
		return 0;
	}

}
