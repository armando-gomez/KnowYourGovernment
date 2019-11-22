package com.armandogomez.knowyourgovernment;

public class GovernmentOfficial {
	private String name;
	private String party;
	private String office;
	private String address;
	private String phone;
	private String website;
	private String email;
	private String photoUrl;
	private String googlePlus;
	private String facebook;
	private String twitter;
	private String youtube;

	GovernmentOfficial() {
	}

	public void setSocial(String type, String id) {
		if(type.equals("GooglePlus")) {
			setGooglePlus(id);
		} else if(type.equals("Facebook")) {
			setFacebook(id);
		} else if(type.equals("Twitter")) {
			setTwitter(id);
		} else if(type.equals("YouTube")) {
			setYoutube(id);
		} else {
			return;
		}
	}

	public String getName() {
		return this.name;
	}

	public String getParty() {
		return this.party;
	}

	public String getOffice() {
		return this.office;
	}

	public void setName(String s) {
		this.name = s;
	}

	public void setParty(String s) {
		this.party = s;
	}

	public void setOffice(String s) {
		this.office = s;
	}

	public void setAddress(String s) {
		this.address = s;
	}

	public void setPhone(String s) {
		this.address = s;
	}

	public void setWebsite(String s) {
		this.address = s;
	}

	public void setEmail(String s) {
		this.email = s;
	}

	public void setPhotoUrl(String s) {
		this.photoUrl = s;
	}

	public void setGooglePlus(String s) {
		this.googlePlus = s;
	}

	public void setFacebook(String s) {
		this.facebook = s;
	}

	public void setTwitter(String s) {
		this.twitter = s;
	}

	public void setYoutube(String s) {
		this.youtube = s;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	public String getWebsite() {
		return website;
	}

	public String getEmail() {
		return email;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public String getGooglePlus() {
		return googlePlus;
	}

	public String getFacebook() {
		return facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public String getYoutube() {
		return youtube;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) {
			return true;
		}

		if(!(o instanceof GovernmentOfficial)) {
			return false;
		}

		GovernmentOfficial g = (GovernmentOfficial) o;
		return this.getName().equals(g.getName()) && this.getOffice().equals(g.getOffice());
	}
}
