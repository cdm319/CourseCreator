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

/**
 * Managed bean that handles custom validation across the web application.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
@ManagedBean
@NoneScoped
public class Validation {
	//constant regex patterns for validation
	private static final Pattern URL_REGEX = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?(https://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#!&\\n\\-=?\\+\\%/\\.\\w]+)?");
	private static final Pattern YOUTUBE_REGEX = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=))((\\w|-){11})(?:\\S+)?$");
	
	
	/**
	 * Validates any alphabetical only field on the front end.
	 * 
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateTextOnly(FacesContext context, UIComponent component, Object convertedValue) {
		String text = convertedValue.toString();
		
		if (text.matches(".*[0-9].*")) {
			//string contains numbers
			throw new ValidatorException(new FacesMessage("Please do not enter numbers."));
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
		
		//field is optional, so only validate if it isn't blank
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
	 * Validates and PowerPoint file upload field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validatePowerPointFile(FacesContext context, UIComponent component, Object convertedValue) {
		Part tempFile = (Part) convertedValue;
		List<String> fileTypes = new ArrayList<String>();
		fileTypes.add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
		
		if (tempFile.getSize() < 2) {
			//smaller than 2 bytes
			throw new ValidatorException(new FacesMessage("Please upload a valid .pptx file."));
		}
		if (tempFile.getSize() > 10485760) {
			//larger than 10mb
			throw new ValidatorException(new FacesMessage("Please upload a smaller presentation."));
		}
		if (!fileTypes.contains(tempFile.getContentType())) {
			//not a valid pptx file
			throw new ValidatorException(new FacesMessage("Presentation must be a PowerPoint 2007 or newer (.pptx) file."));
		}
	}
	
	/**
	 * Validates any date field on the front end in UK date format.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateDate(FacesContext context, UIComponent component, Object convertedValue) {
		String stringDate = convertedValue.toString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
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
			validateYouTubeURL(context, component, convertedValue);
		}
	}
	
	/**
	 * Validates any YouTube video URL field on the front end.
	 *
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateYouTubeURL(FacesContext context, UIComponent component, Object convertedValue) {
		String videoURL = convertedValue.toString();
		
		Matcher ytMatch = YOUTUBE_REGEX.matcher(videoURL);
		
		if (!ytMatch.find()) {
			//YouTube URL not valid
			throw new ValidatorException(new FacesMessage("Please enter a valid YouTube URL."));
		}
	}
	
	/**
	 * Validates any URL field on the front end.  Custom method used as the Hibernate Validator's
	 * \@URL annotation is too strict (i.e. requires a schema such as http://).
	 * 
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateURL(FacesContext context, UIComponent component, Object convertedValue) {
		String url = convertedValue.toString();
		
		Matcher urlMatch = URL_REGEX.matcher(url);
		
		if (!urlMatch.find()) {
			//URL not valid
			throw new ValidatorException(new FacesMessage("Please enter a valid URL."));
		}
	}
	
	/**
	 * Validates any optional URL field on the front end.  Custom method used as the Hibernate Validator's
	 * \@URL annotation is too strict (i.e. requires a schema such as http://).
	 * 
	 * @param context			The FacesContext object.
	 * @param component			The UI Component related to the Validator.
	 * @param convertedValue	The value currently set on the front end.
	 */
	public void validateOptionalURL(FacesContext context, UIComponent component, Object convertedValue) {
		String url = convertedValue.toString();
		
		//field is optional, so only validate if it's not blank
		if (null != url && !"".equals(url)) {
			Matcher urlMatch = URL_REGEX.matcher(url);
			
			if (!urlMatch.find()) {
				//URL not valid
				throw new ValidatorException(new FacesMessage("Please enter a valid URL."));
			}
		}
	}
}
