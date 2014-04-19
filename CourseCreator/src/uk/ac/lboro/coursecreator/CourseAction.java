package uk.ac.lboro.coursecreator;

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
public class CourseAction {
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
	 * 
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateAdministratorName(FacesContext context, UIComponent component, Object convertedValue) {
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
