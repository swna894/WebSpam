package com.ever.webSpam.cressQc;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


public class SpamCheck {
	
	@JsonIgnoreProperties(ignoreUnknown = true, value = {"false"})
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
    
	public Boolean getHam() { return ham.get(); }
	public void setHam(Boolean ham) { this.ham.set(ham); }
	public BooleanProperty hamProperty() {	return ham; }
	
	public Boolean getHamLow() { return hamLow.get(); }
	public void setHamLow(Boolean hamLow) { this.hamLow.set(hamLow); }
	public BooleanProperty hamLowProperty() {	return hamLow; }
	
	public Boolean getSpamAd() { return spamAd.get(); }
	public void setSpamAd(Boolean spamAd) { this.spamAd.set(spamAd); }
	public BooleanProperty spamAdProperty() {	return spamAd; }
	
	public Boolean getSpamText() { return spamText.get(); }
	public void setSpamText(Boolean spamText) { this.spamText.set(spamText); }
	public BooleanProperty spamTextProperty() {	return spamText; }
	
	public Boolean getSpamRedir() { return spamRedir.get(); }
	public void setSpamRedir(Boolean spamRedir) { this.spamRedir.set(spamRedir); }
	public BooleanProperty spamRedirProperty() {	return spamRedir; }
	
	public Boolean getSpamMalware() { return spamMalware.get(); }
	public void setSpamMalware(Boolean spamMalware) { this.spamMalware.set(spamMalware); }
	public BooleanProperty spamMalwareProperty() {	return spamMalware; }
	
	public Boolean getSpamCopy() { return spamCopy.get(); }
	public void setSpamCopy(Boolean spamCopy) { this.spamCopy.set(spamCopy); }
	public BooleanProperty spamCopyProperty() {	return spamCopy; }
	

	public Boolean getSpamPorn() { return spamPorn.get(); }
	public void setSpamPorn(Boolean spamPorn) { this.spamPorn.set(spamPorn); }
	public BooleanProperty spamPornProperty() {	return spamPorn; }
	
	public Boolean getSpamPornWeak() { return spamPornWeak.get(); }
	public void setSpamPornWeak(Boolean spamPornWeak) { this.spamPornWeak.set(spamPornWeak); }
	public BooleanProperty spamPornWeakProperty() {	return spamPornWeak; }

	public Boolean getSpamDecep() { return spamDecep.get(); }
	public void setSpamDecep(Boolean spamDecep) { this.spamDecep.set(spamDecep); }
	public BooleanProperty spamDecepProperty() {	return spamDecep; }
	
	public Boolean getSpamManip() { return spamManip.get(); }
	public void setSpamManip(Boolean spamManip) { this.spamManip.set(spamManip); }
	public BooleanProperty spamManipProperty() {	return spamManip; }
	
	public Boolean getSpamIllegal() { return spamIllegal.get(); }
	public void setSpamIllegal(Boolean spamIllegal) { this.spamIllegal.set(spamIllegal); }
	public BooleanProperty spamIllegalProperty() {	return spamIllegal; }
	
	public Boolean getLookCont() { return lookCont.get(); }
	public void setLookCont(Boolean lookCont) { this.lookCont.set(lookCont); }
	public BooleanProperty lookContProperty() {	return lookCont; }
	
	public Boolean getLookList() { return lookList.get(); }
	public void setLookList(Boolean lookList) { this.lookList.set(lookList); }
	public BooleanProperty lookListProperty() {	return lookList; }
	
	public Boolean getLookCh() { return lookCh.get(); }
	public void setLookCh(Boolean lookCh) { this.lookCh.set(lookCh); }
	public BooleanProperty lookChProperty() {	return lookCh; }
		
	public Boolean getLookMain() { return lookMain.get(); }
	public void setLookMain(Boolean lookMain) { this.lookMain.set(lookMain); }
	public BooleanProperty lookMainProperty() {	return lookMain; }
	
	@Override
	public String toString() {
		return "SpamCheck [lookMain=" + lookMain + ", lookCh=" + lookCh + ", lookList=" + lookList + ", lookCont="
				+ lookCont + ", ham=" + ham + ", hamLow=" + hamLow + ", spamAd=" + spamAd + ", spamText=" + spamText
				+ ", spamRedir=" + spamRedir + ", spamMalware=" + spamMalware + ", spamCopy=" + spamCopy + ", spamPorn="
				+ spamPorn + ", spamDecep=" + spamDecep + ", spamManip=" + spamManip + ", spamIllegal=" + spamIllegal
				+ "]\n";
	}
	

}
