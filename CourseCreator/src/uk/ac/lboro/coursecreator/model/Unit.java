package uk.ac.lboro.coursecreator.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit model bean.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
public class Unit {
	private int unitId;
	private String title;
	private Date releaseDate;
	private String nowAvailable;
	private List<Lesson> lessons;
	
	//Basic constructor method
	public Unit() {
		this.unitId = 0;
		this.title = "";
		this.releaseDate = new Date(0);
		this.nowAvailable = "N";
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
	public String getNowAvailable() {
		return nowAvailable;
	}
	public void setNowAvailable(String nowAvailable) {
		this.nowAvailable = nowAvailable;
	}
	public List<Lesson> getLessons() {
		return lessons;
	}
	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}
}
