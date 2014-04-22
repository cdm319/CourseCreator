package uk.ac.lboro.coursecreator;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

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
	private Course courseStructure;
	
	/**
	 * Constructor creates new Course model object.
	 */
	public CourseAction() {
		if(courseStructure == null) {
			courseStructure = new Course();
		}
	}
	
	/**
	 * Validates the Administrator Name field on the front end.
	 * 
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateAdminName(FacesContext context, UIComponent component, Object convertedValue) {
		String adminName = convertedValue.toString();
		
		if (adminName.matches(".*[0-9].*")) {
			//not a valid name
			throw new ValidatorException(new FacesMessage("Please enter a valid name."));
		}
		if (adminName.length() > 80) {
			//too long
			throw new ValidatorException(new FacesMessage("This name is too long."));
		}
		if (adminName.length() < 3) {
			//too short
			throw new ValidatorException(new FacesMessage("This name is too short."));
		}
	}
	
	/**
	 * Validates the Administrator Email field on the front end.
	 * 
	 * @param context
	 * @param component
	 * @param convertedValue
	 */
	public void validateAdminEmail(FacesContext context, UIComponent component, Object convertedValue) {
		String adminEmail = convertedValue.toString();
		
		Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		Matcher emailMatch = emailRegex.matcher(adminEmail);
		
		if (!emailMatch.find()) {
			//email not valid
			throw new ValidatorException(new FacesMessage("Please enter a valid email address."));
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
}
