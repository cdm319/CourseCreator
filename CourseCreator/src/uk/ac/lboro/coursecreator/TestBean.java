package uk.ac.lboro.coursecreator;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class TestBean {
	private String name;
	private String emailAddress;
	
	public TestBean() {
		this.name = "Chris";
		this.emailAddress = "chrismatthews@outlook.com";
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
