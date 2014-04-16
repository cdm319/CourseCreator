package uk.ac.lboro.coursecreator.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Course model bean.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
public class Course {
	private String title;
	private String blurb;
	private String introVideoId;
	private Date startDate;
	private String forumURL;
	
	private String institutionName;
	private String institutionURL;
	private String institutionLogo;
	
	private String administratorName;
	private String administratorEmail;
	
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
