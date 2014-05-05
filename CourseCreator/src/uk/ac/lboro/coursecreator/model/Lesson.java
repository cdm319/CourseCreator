package uk.ac.lboro.coursecreator.model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Lesson model bean.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
public class Lesson {
	//the unique lesson ID number
	private int lessonId;
	
	//the title of the lesson
	@Size(min=3, max=80, message="Please enter between 3 and 80 characters.")
	private String lessonTitle;
	
	//HTML code containing lesson notes
	@NotEmpty(message="Please enter some notes for the lesson.")
	private String lessonNotes;
	
	//the YouTube ID of the lesson's video
	@NotEmpty(message="Please enter the YouTube URL for the lesson's video.")
	private String lessonVideoId;
	
	//HTML code containing a list of lesson objectives, can be empty
	private String lessonObjectives;
	
	//Basic constructor method
	public Lesson() {
		this.lessonId = 0;
		this.lessonTitle = "";
		this.lessonNotes = "";
		this.lessonVideoId = "";
		this.lessonObjectives = "";
	}
	
	/**
	 * Utility method to return a shorter version of the lesson notes for
	 * displaying on the front end.
	 * 
	 * @return	truncated lesson notes
	 */
	public String getShortLessonNotes() {
		String notes = lessonNotes;
		return notes.substring(0, 116) + "...";
	}
	
	/**
	 * Utility method to return a shorter version of the lesson objectives
	 * for displaying on the front end.
	 * 
	 * @return	truncated lesson objectives
	 */
	public String getShortLessonObjectives() {
		String objs = lessonObjectives;
		return objs.substring(0, 116) + "...";
	}
	
	//Getters and setters
	public int getLessonId() {
		return lessonId;
	}
	public void setLessonId(int lessonId) {
		this.lessonId = lessonId;
	}
	public String getLessonTitle() {
		return lessonTitle;
	}
	public void setLessonTitle(String lessonTitle) {
		this.lessonTitle = lessonTitle;
	}
	public String getLessonNotes() {
		return lessonNotes;
	}
	public void setLessonNotes(String lessonNotes) {
		this.lessonNotes = lessonNotes;
	}
	public String getLessonVideoId() {
		return lessonVideoId;
	}
	public void setLessonVideoId(String lessonVideoId) {
		this.lessonVideoId = lessonVideoId;
	}
	public String getLessonObjectives() {
		return lessonObjectives;
	}
	public void setLessonObjectives(String lessonObjectives) {
		this.lessonObjectives = lessonObjectives;
	}
}
