package com.ever.webSpam.category;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class SpamCategory {


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private final StringProperty uri = new SimpleStringProperty();

	private final BooleanProperty lookMain = new SimpleBooleanProperty();
	private final BooleanProperty lookCh = new SimpleBooleanProperty();
	private final BooleanProperty lookList = new SimpleBooleanProperty();   
	private final BooleanProperty lookCont = new SimpleBooleanProperty();
	private final BooleanProperty ham = new SimpleBooleanProperty();
	private final BooleanProperty hamLow = new SimpleBooleanProperty();
	private final BooleanProperty spamAd = new SimpleBooleanProperty();
	private final BooleanProperty spamText = new SimpleBooleanProperty();
	private final BooleanProperty spamCopy = new SimpleBooleanProperty();
	private final BooleanProperty spamIllegal = new SimpleBooleanProperty();

	@Access(AccessType.PROPERTY)
	public Boolean getLookCont() { return lookCont.get(); }
	public void setLookCont(Boolean lookCont) { this.lookCont.set(lookCont); }
	public BooleanProperty lookContProperty() {	return lookCont; }
	
	@Access(AccessType.PROPERTY)
	public Boolean getSpamIllegal() { return spamIllegal.get(); }
	public void setSpamIllegal(Boolean spamIllegal) { this.spamIllegal.set(spamIllegal); }
	public BooleanProperty spamIllegalProperty() {	return spamIllegal; }
	
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
	
	@Access(AccessType.PROPERTY)
	public String getUri() { return uri.get(); }
	public void setUri(String uri) { this.uri.set(uri); }
	public StringProperty uriProperty() { return uri; }
	
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
	public Boolean getSpamCopy() { return spamCopy.get(); }
	public void setSpamCopy(Boolean spamCopy) { this.spamCopy.set(spamCopy); }
	public BooleanProperty spamCopyProperty() {	return spamCopy; }
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "SpamCategory [uri=" + uri + ", lookMain=" + lookMain + ", lookCh=" + lookCh + ", lookList=" + lookList
				+ ", lookCont=" + lookCont + ", hamLow=" + hamLow + ", spamAd=" + spamAd + ", spamText=" + spamText
				+ "]";
	}

	

}
