package com.webspam.java;

public class TargetQC {

	private String url;
	private String scope;
	private String name;
	private String donotQc;
	private String spam;
	private String quality;
	private String purpose;
	private String subSpam;
	private String subUrl;
	private String Qc;
	private String comment;
	private String commit;
	
	public TargetQC() {
		// TODO Auto-generated constructor stub
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDonotQc() {
		return donotQc;
	}

	public void setDonotQc(String donotQc) {
		this.donotQc = donotQc;
	}

	public String getSpam() {
		return spam;
	}

	public void setSpam(String spam) {
		this.spam = spam;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getSubSpam() {
		return subSpam;
	}

	public void setSubSpam(String subSpam) {
		this.subSpam = subSpam;
	}

	public String getSubUrl() {
		return subUrl;
	}

	public void setSubUrl(String subUrl) {
		this.subUrl = subUrl;
	}

	public String getQc() {
		return Qc;
	}

	public void setQc(String qc) {
		Qc = qc;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}

	@Override
	public String toString() {
		return "QcTarget [url=" + url + ", scope=" + scope + ", name=" + name + ", donotQc=" + donotQc + ", spam="
				+ spam + ", quality=" + quality + ", purpose=" + purpose + ", subSpam=" + subSpam + ", subUrl=" + subUrl
				+ ", Qc=" + Qc + ", comment=" + comment + "]\n";
	}



}
