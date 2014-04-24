package uk.ac.lboro.coursecreator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import uk.ac.lboro.coursecreator.model.Course;

/**
 * Session scoped bean that acts as the backing bean for the Course
 * Structure creation and management pages. 
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
@ManagedBean
@SessionScoped
public class CourseAction implements Serializable {
	//default serial UID
	private static final long serialVersionUID = 1L;
	
	//the Course model bean
	private Course courseStructure;
	
	//temporary course details
	private Part tempLogoImage;
	private String tempDate;
	private String tempIntroVid;
	
	
	/**
	 * Constructor creates new Course model object.
	 */
	public CourseAction() {
		if(courseStructure == null) {
			courseStructure = new Course();
		}
	}
	
	/**
	 * Throwaway test method for form testing.
	 * 
	 * @return null
	 */
	public String testForm() {
		return "index";
	}

	
	//Getters and setters
	public Course getCourseStructure() {
		return courseStructure;
	}

	public void setCourseStructure(Course courseStructure) {
		this.courseStructure = courseStructure;
	}

	public Part getTempLogoImage() {
		return tempLogoImage;
	}

	public void setTempLogoImage(Part logoImage) {
		this.tempLogoImage = logoImage;
	}

	public String getTempDate() {
		return tempDate;
	}

	public void setTempDate(String tempDate) {
		this.tempDate = tempDate;
	}

	public String getTempIntroVid() {
		return tempIntroVid;
	}

	public void setTempIntroVid(String tempIntroVid) {
		this.tempIntroVid = tempIntroVid;
	}
}
