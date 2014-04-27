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
}
