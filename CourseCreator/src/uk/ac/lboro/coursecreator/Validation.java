package uk.ac.lboro.coursecreator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.NoneScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

@ManagedBean
@NoneScoped
public class Validation {
	//constant regex patterns for validation
	private static final Pattern URL_REGEX = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");
	private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private static final Pattern YOUTUBE_REGEX = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=))((\\w|-){11})(?:\\S+)?$");
	
	
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
			throw new ValidatorException(new FacesMessage("Please enter a shorter name."));
		}
		if (adminName.length() < 3) {
			//too short
			throw new ValidatorException(new FacesMessage("Please enter a longer name."));
		}
	}
	
	/**
	 * Validates the Administrator Email field on the front end.
	 * 
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateAdminEmail(FacesContext context, UIComponent component, Object convertedValue) {
		String adminEmail = convertedValue.toString();
		
		Matcher emailMatch = EMAIL_REGEX.matcher(adminEmail);
		
		if (!emailMatch.find()) {
			//email not valid
			throw new ValidatorException(new FacesMessage("Please enter a valid email address."));
		}
	}
	
	/**
	 * Validates the Institution Name field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateInstitutionName(FacesContext context, UIComponent component, Object convertedValue) {
		String institutionName = convertedValue.toString();
		
		if (institutionName.length() < 3) {
			//too short
			throw new ValidatorException(new FacesMessage("Please enter a longer name."));
		}
		if (institutionName.length() > 80) {
			//too long
			throw new ValidatorException(new FacesMessage("Please enter a shorter name."));
		}
	}
	
	/**
	 * Validates the Institution URL field on the front end.
	 * 
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateInstitutionURL(FacesContext context, UIComponent component, Object convertedValue) {
		String institutionURL = convertedValue.toString();
		
		Matcher urlMatch = URL_REGEX.matcher(institutionURL);
		
		if (!urlMatch.find()) {
			//URL not valid
			throw new ValidatorException(new FacesMessage("Please enter a valid URL."));
		}
	}
	
	/**
	 * Validates the Institution Logo field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateInstitutionLogo(FacesContext context, UIComponent component, Object convertedValue) {
		Part tempLogo = (Part) convertedValue;
		List<String> fileTypes = new ArrayList<String>();
		fileTypes.add("image/gif");
		fileTypes.add("image/jpeg");
		fileTypes.add("image/png");
		fileTypes.add("image/x-png");
		
		if (tempLogo != null) {
			if (tempLogo.getSize() < 2) {
				//smaller than 2 bytes
				throw new ValidatorException(new FacesMessage("Please upload a valid image file."));
			}
			if (tempLogo.getSize() > 1048576) {
				//larger than 1mb
				throw new ValidatorException(new FacesMessage("Please upload a smaller image."));
			}
			if (!fileTypes.contains(tempLogo.getContentType())) {
				//not a valid image file
				throw new ValidatorException(new FacesMessage("Logo must be a .gif, .jpg or .png image file."));
			}
		}
	}
	
	/**
	 * Validates the Course Title field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateCourseTitle(FacesContext context, UIComponent component, Object convertedValue) {
		String title = convertedValue.toString();
		
		if (title.length() < 3) {
			//too short
			throw new ValidatorException(new FacesMessage("Please enter a longer title."));
		}
		if (title.length() > 80) {
			//too long
			throw new ValidatorException(new FacesMessage("Please enter a shorter title."));
		}
	}
	
	/**
	 * Validates the Course Blurb field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateCourseBlurb(FacesContext context, UIComponent component, Object convertedValue) {
		String blurb = convertedValue.toString();
		
		if (blurb.length() < 20) {
			//too short
			throw new ValidatorException(new FacesMessage("Please enter a longer blurb."));
		}
		if (blurb.length() > 300) {
			//too long
			throw new ValidatorException(new FacesMessage("Please enter a shorter blurb."));
		}
	}
	
	/**
	 * Validates the Course Start Date field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateCourseStartDate(FacesContext context, UIComponent component, Object convertedValue) {
		String stringDate = convertedValue.toString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		if (stringDate == null || "".equals(stringDate)) {
			//required field
			throw new ValidatorException(new FacesMessage("Please enter the Course Start Date."));
		}
		
		try {
			dateFormat.parse(stringDate);
		} catch (Exception e) {
			//invalid date (not in format dd/mm/yyyy)
			throw new ValidatorException(new FacesMessage("Please enter a valid date (dd/mm/yyyy)."));
		}
	}
	
	/**
	 * Validates the Course Introductory Video field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateCourseIntroVideo(FacesContext context, UIComponent component, Object convertedValue) {
		String videoURL = convertedValue.toString();
		
		//field is optional, so only validate if it isn't blank
		if (videoURL != null && !"".equals(videoURL)) {
			Matcher ytMatch = YOUTUBE_REGEX.matcher(videoURL);
			
			if (!ytMatch.find()) {
				//YouTube URL not valid
				throw new ValidatorException(new FacesMessage("Please enter a valid YouTube URL."));
			}
		}
	}
	
	/**
	 * Validates the Course Forum URL field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateCourseForumURL(FacesContext context, UIComponent component, Object convertedValue) {
		String forumURL = convertedValue.toString();
		
		//field is optional, so only validate if it isn't blank
		if(forumURL != null && !"".equals(forumURL)) {
			Matcher urlMatch = URL_REGEX.matcher(forumURL);
			
			if (!urlMatch.find()) {
				//URL not valid
				throw new ValidatorException(new FacesMessage("Please enter a valid URL."));
			}
		}
	}
}
