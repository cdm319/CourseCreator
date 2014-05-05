package uk.ac.lboro.coursecreator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.tomcat.util.codec.binary.Base64;

import uk.ac.lboro.coursecreator.model.Course;
import uk.ac.lboro.coursecreator.model.Lesson;
import uk.ac.lboro.coursecreator.model.Unit;

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
	
	//regex constants
	private static final Pattern YOUTUBE_REGEX = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=))((\\w|-){11})(?:\\S+)?$");
	
	//the Course model bean
	private Course course;
	
	//temporary course details
	private String tempDate;
	private String tempIntroVid;
	
	//temporary file upload variables
	private Part tempLogoImage;
	private Part tempCoursePptx;
	
	//temporary variables for selected unit/lesson IDs
	private int selectedUnit;
	private int selectedLesson;
	
	//temporary unit details
	private Unit tempUnit = new Unit();
	private Unit currentUnit = new Unit();
	private String tempUnitDate;
	
	/**
	 * Constructor creates new Course model object if necessary.
	 */
	public CourseAction() {
		if(course == null) {
			course = new Course();
		}
	}
	
	/**
	 * Method to handle the form data from the Import Course page and upload
	 * the corresponding .pptx file for processing.
	 * 
	 * @return The navigation rule to the Edit Course page.
	 */
	public String importCoursePptx() {
		if(tempCoursePptx != null && tempCoursePptx.getSize() != 0) {
			try {
				File pptxFile = File.createTempFile("course", ".pptx");
				tempCoursePptx.write(pptxFile.getAbsolutePath());
				
				XMLSlideShow pptx = new XMLSlideShow(new FileInputStream(pptxFile));
				Course importedCourse = Powerpoint.parseCoursePresentation(pptx);
				
				course.setTitle(importedCourse.getTitle());
				course.setBlurb(importedCourse.getBlurb());
				course.setStartDate(importedCourse.getStartDate());
				course.setIntroVideoId(importedCourse.getIntroVideoId());
				course.setForumURL(importedCourse.getForumURL());
				course.setInstitutionName(importedCourse.getInstitutionName());
				course.setInstitutionURL(importedCourse.getInstitutionURL());
				course.setInstitutionLogo(importedCourse.getInstitutionLogo());
				course.setAdministratorName(importedCourse.getAdministratorName());
				course.setAdministratorEmail(importedCourse.getAdministratorEmail());
				
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				tempDate = df.format(course.getStartDate());
				
				tempIntroVid = "http://www.youtube.com/watch?v=" + course.getIntroVideoId();
				
				pptxFile.delete();
			} catch (IOException e) {
				throw new ValidatorException(new FacesMessage("Invalid PowerPoint file detected."));
			}
		} else {
			throw new ValidatorException(new FacesMessage("Invalid PowerPoint file detected."));
		}
		
		return "courseEdit";
	}
	
	/**
	 * Method to handle the form data from Create/Edit Course pages and move
	 * to the Course Details page.
	 * 
	 * @return	The navigation rule to the Course Details page
	 */
	public String createCourse() {
		//upload logo image, convert to base64, store in model
		if(tempLogoImage != null && tempLogoImage.getSize() != 0) {
			try {
				InputStream input = tempLogoImage.getInputStream();
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				byte[] buffer = new byte[1048576];
				int bytesRead;
				
				while ((bytesRead = input.read(buffer, 0, buffer.length)) != -1) {
					data.write(buffer, 0, bytesRead);
				}
				
				data.flush();
				
				String base64logo = Base64.encodeBase64String(data.toByteArray());
				course.setInstitutionLogo(base64logo);
				
				tempLogoImage = null;
			} catch (IOException e) {
				return null;
			}
		}
		
		//convert tempDate to Date format, store in model
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
		try {
			Date date = dateFormat.parse(tempDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 12);
			date = cal.getTime();
			course.setStartDate(date);
		} catch (ParseException e) {
			return null;
		}
		
		//convert tempIntroVid to YouTube Video ID only, store in model
		if (tempIntroVid != null && !"".equals(tempIntroVid)) {
			Matcher ytMatch = YOUTUBE_REGEX.matcher(tempIntroVid);
			
			if (ytMatch.matches() && (ytMatch.group(1).length() == 11)) {
				String youtubeId = ytMatch.group(1);
				course.setIntroVideoId(youtubeId);
			}
		}
		
		//move on to the Course Details page
		return "courseDetails";
	}
	
	private int nextAvailableUnitId() {
		int nextId;
		
		if (course.getUnits().size() > 0) {
			int maxExistingId = 0;
			
			//loop through every Unit object, to find the highest Unit ID
			for (Unit unit : course.getUnits()) {
				if (unit.getUnitId() > maxExistingId) {
					maxExistingId = unit.getUnitId();
				}
			}
			
			nextId = maxExistingId + 1;
		} else {
			nextId = 1;
		}
		
		return nextId;
	}
	
	public String createUnit() {
		//set the new Unit ID number
		tempUnit.setUnitId(nextAvailableUnitId());
		
		//convert tempUnitDate to Date format, set the new Unit Release Date and Now Available variables
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
		try {
			Date date = dateFormat.parse(tempUnitDate);
			Date today = new Date();
			
			//set whether the Unit should be locked or unlocked
			if (date.after(today)) {
				tempUnit.setNowAvailable(false);
			} else {
				tempUnit.setNowAvailable(true);
			}
			
			//set the time to 12:00PM, fixes an output bug where the date would appear to be one day early
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 12);
			date = cal.getTime();
			
			//set the Release Date onto the temporary Unit model
			tempUnit.setReleaseDate(date);
		} catch (ParseException e) {
			return null;
		}
		
		//add the temporary Unit model to the Course model
		List<Unit> currentUnits = course.getUnits();
		currentUnits.add(tempUnit);
		course.setUnits(currentUnits);
		
		//set the currently opened Unit and reset the temporary Unit model
		currentUnit = tempUnit;
		tempUnit = new Unit();
		tempUnitDate = null;
		
		return "unitDetails";
	}
	
	public String editCurrentUnit() {
		tempUnit = currentUnit;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		tempUnitDate = df.format(tempUnit.getReleaseDate());
		
		return "unitEdit";
	}
	
	public String saveUnitChanges() {
		//convert tempUnitDate to Date format, set the new Unit Release Date and Now Available variables
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
		try {
			Date date = dateFormat.parse(tempUnitDate);
			Date today = new Date();
			
			//set whether the Unit should be locked or unlocked
			if (date.after(today)) {
				tempUnit.setNowAvailable(false);
			} else {
				tempUnit.setNowAvailable(true);
			}
			
			//set the time to 12:00PM, fixes an output bug where the date would appear to be one day early
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, 12);
			date = cal.getTime();
			
			//set the Release Date onto the temporary Unit model
			tempUnit.setReleaseDate(date);
		} catch (ParseException e) {
			return null;
		}
		
		for (int i=0; i < course.getUnits().size(); i++) {
			if (course.getUnits().get(i).getUnitId() == currentUnit.getUnitId()) {
				//this is the Unit object to edit
				course.getUnits().get(i).setTitle(tempUnit.getTitle());
				course.getUnits().get(i).setReleaseDate(tempUnit.getReleaseDate());
				course.getUnits().get(i).setNowAvailable(tempUnit.getNowAvailable());
			}
		}
		
		//set the currently opened Unit and reset the temporary Unit model
		currentUnit = tempUnit;
		tempUnit = new Unit();
		tempUnitDate = null;
		
		return "unitDetails";
	}
	
	public String showSelectedUnit() {
		List<Unit> units = course.getUnits();
		
		for (Unit unit : units) {
			if (unit.getUnitId() == selectedUnit) {
				currentUnit = unit;
			}
		}
		
		return "unitDetails";
	}
	
	public String deleteSelectedUnit() {
		List<Unit> units = course.getUnits();
		
		//search for the selected unit object
		for (int i=0; i < units.size(); i++) {
			if (units.get(i).getUnitId() == selectedUnit) {
				//delete the selected unit object
				units.remove(i);
			}
		}
		
		//reset the remaining Unit IDs into a sequential list
		for (Unit unit : units) {
			int currentId = unit.getUnitId();
			if (currentId > selectedUnit) {
				unit.setUnitId(currentId-1);
			}
		}
		
		//reset the selectedUnit ID to 0, then refresh the Course Details page
		selectedUnit = 0;
		return "courseDetails";
	}
	
	//Getters and setters
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
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

	public Part getTempCoursePptx() {
		return tempCoursePptx;
	}

	public void setTempCoursePptx(Part tempCoursePptx) {
		this.tempCoursePptx = tempCoursePptx;
	}

	public int getSelectedUnit() {
		return selectedUnit;
	}

	public void setSelectedUnit(int selectedUnit) {
		this.selectedUnit = selectedUnit;
	}

	public int getSelectedLesson() {
		return selectedLesson;
	}

	public void setSelectedLesson(int selectedLesson) {
		this.selectedLesson = selectedLesson;
	}

	public Unit getTempUnit() {
		return tempUnit;
	}

	public void setTempUnit(Unit tempUnit) {
		this.tempUnit = tempUnit;
	}

	public String getTempUnitDate() {
		return tempUnitDate;
	}

	public void setTempUnitDate(String tempUnitDate) {
		this.tempUnitDate = tempUnitDate;
	}

	public Unit getCurrentUnit() {
		return currentUnit;
	}

	public void setCurrentUnit(Unit currentUnit) {
		this.currentUnit = currentUnit;
	}
}
