package uk.ac.lboro.coursecreator.model;

/**
 * Lesson model bean.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
public class Lesson {
	private int lessonId;
	private String lessonTitle;
	private String lessonNotes;
	private String lessonVideoId;
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
