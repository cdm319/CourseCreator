package uk.ac.lboro.coursecreator.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Course model bean.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
public class Course {
	//Course Title, can't be blank, must be between 3 and 80 characters
	@NotEmpty(message="Please enter the Course Title.")
	@Size(min=3, max=80, message="Please enter between 3 and 80 characters.")
	private String title;
	
	//Course Blurb, can't be blank, must be between 20 and 300 characters
	@NotEmpty(message="Please enter the Course Blurb.")
	@Size(min=20, max=300, message="Please enter between 20 and 300 characters.")
	private String blurb;
	
	//Course Introductory YouTube Video ID, optional
	private String introVideoId;
	
	//Course Start Date, can't be blank
	@NotEmpty(message="Please enter the Course Start Date.")
	private Date startDate;
	
	//Course Forum URL, optional, validated through custom method
	private String forumURL;
	
	//Institution name, can't be blank, must be between 3 and 80 characters
	@NotEmpty(message="Please enter the Institution Name.")
	@Size(min=3, max=80, message="Please enter between 3 and 80 characters.")
	private String institutionName;
	
	//Institution URL, can't be blank, URL validated through custom method
	@NotEmpty(message="Please enter the Institution's web address.")
	private String institutionURL;
	
	//Institution Logo, optional, Base64 encoded image
	private String institutionLogo;
	
	//Administrator's Name, can't be blank, must be between 3 and 80 characters
	@NotEmpty(message="Please enter the Administrator's name.")
	@Size(min=3, max=80, message="Please enter between 3 and 80 characters.")
	private String administratorName;
	
	//Administrator's Email Address, can't be blank, must be a valid email address
	@NotEmpty(message="Please enter the Administrator's email address.")
	@Email(message="Please enter a valid email address.")
	private String administratorEmail;
	
	//List of units within course
	private List<Unit> units;

	//Basic constructor method
	public Course() {
		this.title = "";
		this.blurb = "";
		this.introVideoId = "";
		this.startDate = new Date(0);
		this.forumURL = "";
		
		this.institutionName = "";
		this.institutionURL = "";
		this.institutionLogo = "";
		
		this.administratorName = "";
		this.administratorEmail = "";
		
		this.units = new ArrayList<Unit>();
	}

	//Getters and setters
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBlurb() {
		return blurb;
	}

	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}

	public String getIntroVideoId() {
		return introVideoId;
	}

	public void setIntroVideoId(String introVideoId) {
		this.introVideoId = introVideoId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getForumURL() {
		return forumURL;
	}

	public void setForumURL(String forumURL) {
		this.forumURL = forumURL;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getInstitutionURL() {
		return institutionURL;
	}

	public void setInstitutionURL(String institutionURL) {
		this.institutionURL = institutionURL;
	}

	public String getInstitutionLogo() {
		return institutionLogo;
	}

	public void setInstitutionLogo(String institutionLogo) {
		this.institutionLogo = institutionLogo;
	}

	public String getAdministratorName() {
		return administratorName;
	}

	public void setAdministratorName(String administratorName) {
		this.administratorName = administratorName;
	}

	public String getAdministratorEmail() {
		return administratorEmail;
	}

	public void setAdministratorEmail(String administratorEmail) {
		this.administratorEmail = administratorEmail;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}
}
