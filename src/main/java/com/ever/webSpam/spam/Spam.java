package com.ever.webSpam.spam;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.ever.webSpam.cressQc.SpamCheck;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class Spam {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private StringProperty label = new SimpleStringProperty();
	private final StringProperty uri = new SimpleStringProperty();
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty scope = new SimpleStringProperty();
	private final StringProperty email = new SimpleStringProperty();
	private final StringProperty comment = new SimpleStringProperty();
	private final StringProperty workday = new SimpleStringProperty();
	
	private final StringProperty defer = new SimpleStringProperty(); // 검수보류
	private final StringProperty notCheck = new SimpleStringProperty();
	
	private String requestTime;
	private String timeOfInspection;
	
	private Boolean BooleanNotCheck = new Boolean(true);
	private Boolean BooleanDefer = new Boolean(true);
	private Boolean BooleanBlock = new Boolean(true);
	
	private final BooleanProperty lookMain = new SimpleBooleanProperty();
	private final BooleanProperty lookCh = new SimpleBooleanProperty();
	private final BooleanProperty lookList = new SimpleBooleanProperty();   
	private final BooleanProperty lookCont = new SimpleBooleanProperty();   
	private final BooleanProperty ham = new SimpleBooleanProperty();
	private final BooleanProperty hamLow = new SimpleBooleanProperty();
	private final BooleanProperty spamAd = new SimpleBooleanProperty();
	private final BooleanProperty spamText = new SimpleBooleanProperty();
	private final BooleanProperty spamRedir = new SimpleBooleanProperty();
	private final BooleanProperty spamMalware = new SimpleBooleanProperty();
	private final BooleanProperty spamCopy = new SimpleBooleanProperty();
	private final BooleanProperty spamPorn = new SimpleBooleanProperty();
	private final BooleanProperty spamPornWeak = new SimpleBooleanProperty();
	private final BooleanProperty spamDecep = new SimpleBooleanProperty();
	private final BooleanProperty spamManip = new SimpleBooleanProperty();
	private final BooleanProperty spamIllegal = new SimpleBooleanProperty();
	private final BooleanProperty spamWhite = new SimpleBooleanProperty();
	
	@Transient
	private Integer no;
	
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	
	@Transient
	private SpamCheck spamCheck;
	
	private final BooleanProperty selected = new SimpleBooleanProperty(false);
    
	private final StringProperty qc = new SimpleStringProperty();
	
	public SpamCheck getSpamCheck() {
		return spamCheck;
	}
	public void setSpamCheck(SpamCheck spamCheck) {
		this.spamCheck = spamCheck;
		this.lookMainProperty().bindBidirectional(spamCheck.lookMainProperty());
		this.lookChProperty().bindBidirectional(spamCheck.lookChProperty());
		this.lookListProperty().bindBidirectional(spamCheck.lookListProperty());
		this.lookContProperty().bindBidirectional(spamCheck.lookContProperty());
		this.hamProperty().bindBidirectional(spamCheck.hamProperty());
		this.hamLowProperty().bindBidirectional(spamCheck.hamLowProperty());

		this.spamAdProperty().bindBidirectional(spamCheck.spamAdProperty());
		this.spamTextProperty().bindBidirectional(spamCheck.spamTextProperty());
		this.spamRedirProperty().bindBidirectional(spamCheck.spamRedirProperty());
		this.spamMalwareProperty().bindBidirectional(spamCheck.spamMalwareProperty());
		this.spamCopyProperty().bindBidirectional(spamCheck.spamCopyProperty());
		this.spamPornProperty().bindBidirectional(spamCheck.spamPornProperty());
		this.spamPornWeakProperty().bindBidirectional(spamCheck.spamPornWeakProperty());
		this.spamDecepProperty().bindBidirectional(spamCheck.spamDecepProperty());
		this.spamManipProperty().bindBidirectional(spamCheck.spamManipProperty());
		this.spamIllegalProperty().bindBidirectional(spamCheck.spamIllegalProperty());
	}
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamWhite() { return spamWhite.get(); }
	public void setSpamWhite(Boolean spamWhite) { this.spamWhite.set(spamWhite); }
	public BooleanProperty spamWhiteProperty() { return spamWhite; }
	
	@Access(AccessType.PROPERTY)
	public String getLabel() { return label.get(); }
	public void setLabel(String label) { this.label.set(label); }
	public StringProperty labelProperty() { return label; }
	
	@Access(AccessType.PROPERTY)
	public String getQc() { return qc.get(); }
	public void setQc(String qc) { this.qc.set(qc); }
	public StringProperty qcProperty() { return qc; }
	
	
	@Column(length = 1024)
	@Access(AccessType.PROPERTY)
	public String getUri() { return uri.get(); }
	public void setUri(String uri) { this.uri.set(uri); }
	public StringProperty uriProperty() { return uri; }
 
	@Column(length = 1024)
	@Access(AccessType.PROPERTY)
	public String getComment() { return comment.get(); }
	public void setComment(String comment) { this.comment.set(comment); }
	public StringProperty commentProperty() { return comment; }
	
	@Access(AccessType.PROPERTY)
	public String getEmail() { return email.get(); }
	public void setEmail(String email) { this.email.set(email); }
	public StringProperty emailProperty() { return email; }
	
	@Access(AccessType.PROPERTY)
	public String getScope() { return scope.get(); }
	public void setScope(String scope) { this.scope.set(scope); }
	public StringProperty scopeProperty() { return scope; }

	@Access(AccessType.PROPERTY)
	public String getDefer() { return defer.get(); }
	public void setDefer(String defer) { this.defer.set(defer); }
	public StringProperty deferProperty() {	return defer; }
	
	@Access(AccessType.PROPERTY)
	public String getNotCheck() { return notCheck.get(); }
	public void setNotCheck(String notCheck) { this.notCheck.set(notCheck); }
	public StringProperty notCheckProperty() {	return notCheck; }
	
	@Access(AccessType.PROPERTY)
	public String getWorkday() { return workday.get(); }
	public void setWorkday(String workday) { this.workday.set(workday); }
	public StringProperty workdayProperty() { return workday; }
	
	@Access(AccessType.PROPERTY)
	public String getName() { return name.get(); }
	public void setName(String name) { this.name.set(name); }
	public StringProperty nameProperty() { return name; }

	@Access(AccessType.PROPERTY)
	public Boolean getHam() { return ham.get(); }
	public void setHam(Boolean ham) { this.ham.set(ham); }
	public BooleanProperty hamProperty() {	return ham; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getHamLow() { return hamLow.get(); }
	public void setHamLow(Boolean hamLow) { this.hamLow.set(hamLow); }
	public BooleanProperty hamLowProperty() {	return hamLow; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamAd() { return spamAd.get(); }
	public void setSpamAd(Boolean spamAd) { this.spamAd.set(spamAd); }
	public BooleanProperty spamAdProperty() {	return spamAd; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamText() { return spamText.get(); }
	public void setSpamText(Boolean spamText) { this.spamText.set(spamText); }
	public BooleanProperty spamTextProperty() {	return spamText; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamRedir() { return spamRedir.get(); }
	public void setSpamRedir(Boolean spamRedir) { this.spamRedir.set(spamRedir); }
	public BooleanProperty spamRedirProperty() {	return spamRedir; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamMalware() { return spamMalware.get(); }
	public void setSpamMalware(Boolean spamMalware) { this.spamMalware.set(spamMalware); }
	public BooleanProperty spamMalwareProperty() {	return spamMalware; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamCopy() { return spamCopy.get(); }
	public void setSpamCopy(Boolean spamCopy) { this.spamCopy.set(spamCopy); }
	public BooleanProperty spamCopyProperty() {	return spamCopy; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamPorn() { return spamPorn.get(); }
	public void setSpamPorn(Boolean spamPorn) { this.spamPorn.set(spamPorn); }
	public BooleanProperty spamPornProperty() {	return spamPorn; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamPornWeak() { return spamPornWeak.get(); }
	public void setSpamPornWeak(Boolean spamPornWeak) { this.spamPornWeak.set(spamPornWeak); }
	public BooleanProperty spamPornWeakProperty() {	return spamPornWeak; }

	@Access(AccessType.PROPERTY)
	public Boolean getSpamDecep() { return spamDecep.get(); }
	public void setSpamDecep(Boolean spamDecep) { this.spamDecep.set(spamDecep); }
	public BooleanProperty spamDecepProperty() {	return spamDecep; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamManip() { return spamManip.get(); }
	public void setSpamManip(Boolean spamManip) { this.spamManip.set(spamManip); }
	public BooleanProperty spamManipProperty() {	return spamManip; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamIllegal() { return spamIllegal.get(); }
	public void setSpamIllegal(Boolean spamIllegal) { this.spamIllegal.set(spamIllegal); }
	public BooleanProperty spamIllegalProperty() {	return spamIllegal; }

	@Access(AccessType.PROPERTY)
	public Boolean getSelected() { return selected.get(); }
	public void setSelected(Boolean selected) { this.selected.set(selected); }
	public BooleanProperty selectedProperty() {	return selected; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getLookCont() { return lookCont.get(); }
	public void setLookCont(Boolean lookCont) { this.lookCont.set(lookCont); }
	public BooleanProperty lookContProperty() {	return lookCont; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getLookList() { return lookList.get(); }
	public void setLookList(Boolean lookList) { this.lookList.set(lookList); }
	public BooleanProperty lookListProperty() {	return lookList; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getLookCh() { return lookCh.get(); }
	public void setLookCh(Boolean lookCh) { this.lookCh.set(lookCh); }
	public BooleanProperty lookChProperty() {	return lookCh; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getLookMain() { return lookMain.get(); }
	public void setLookMain(Boolean lookMain) { this.lookMain.set(lookMain); }
	public BooleanProperty lookMainProperty() {	return lookMain; }

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public String getTimeOfInspection() {
		return timeOfInspection;
	}
	public void setTimeOfInspection(String timeOfInspection) {
		this.timeOfInspection = timeOfInspection;
	}
	public Boolean getBooleanNotCheck() {
		return BooleanNotCheck;
	}
	public void setBooleanNotCheck(Boolean booleanNotCheck) {
		BooleanNotCheck = booleanNotCheck;
	}
	public Boolean getBooleanDefer() {
		return BooleanDefer;
	}
	public void setBooleanDefer(Boolean booleanDefer) {
		BooleanDefer = booleanDefer;
	}
	public Boolean getBooleanBlock() {
		return BooleanBlock;
	}
	public void setBooleanBlock(Boolean booleanBlock) {
		BooleanBlock = booleanBlock;
	}
	public void setLabel(StringProperty label) {
		this.label = label;
	}
	@Override
	public String toString() {
		return "Spam " + no + " [uri=" + uri + ", name=" + name + ", scope=" + scope + ", email=" + email + ", comment=" + comment
				+ ", workday=" + workday + ", notCheck=" + notCheck + ", lookMain=" + lookMain + ", lookCh=" + lookCh
				+ ", lookList=" + lookList + ", lookCont=" + lookCont + ", ham=" + ham + ", hamLow=" + hamLow
				+ ", spamAd=" + spamAd + ", spamText=" + spamText + ", spamRedir=" + spamRedir + ", spamMalware="
				+ spamMalware + ", spamCopy=" + spamCopy + ", spamPorn=" + spamPorn + ", spamDecep=" + spamDecep
				+ ", spamManip=" + spamManip + ", spamIllegal=" + spamIllegal + ", selected=" + selected + ", qc=" + qc
				+ "]\n";
	}
}
