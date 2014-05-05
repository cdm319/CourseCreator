package uk.ac.lboro.coursecreator.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Unit model bean.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
public class Unit {
	//the unique Unit ID number
	private int unitId;
	
	//the title of the Unit
	@Size(min=3, max=80, message="Please enter between 3 and 80 characters.")
	private String title;
	
	//the date on which the Unit will be released
	@NotEmpty(message="Please enter the Unit Release Date.")
	private Date releaseDate;
	
	//whether the Unit is available now or not
	private Boolean nowAvailable;
	
	//list of Lesson objects within the unit
	private List<Lesson> lessons;
	
	//Basic constructor method
	public Unit() {
		this.unitId = 0;
		this.title = "";
		this.releaseDate = new Date(0);
		this.nowAvailable = false;
		this.lessons = new ArrayList<Lesson>();
	}
	
	//Getters and setters
	public int getUnitId() {
		return unitId;
	}
	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public Boolean getNowAvailable() {
		return nowAvailable;
	}
	public void setNowAvailable(Boolean nowAvailable) {
		this.nowAvailable = nowAvailable;
	}
	public List<Lesson> getLessons() {
		return lessons;
	}
	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}
}
