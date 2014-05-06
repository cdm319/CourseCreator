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
	private Part tempUnitPptx;
	
	//temporary unit details
	private int selectedUnit;
	private Unit tempUnit = new Unit();
	private Unit currentUnit = new Unit();
	private String tempUnitDate;
	
	//temporary lesson details
	private int selectedLesson;
	private Lesson tempLesson = new Lesson();
	private String tempLessonVid;
	
	/**
	 * Constructor creates new Course model object if necessary.
	 */
	public CourseAction() {
		if(course == null) {
			course = new Course();
		}
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
	 * Handles the creation of a new Unit object through the Create Unit screen and adds
	 * this to the Course's list of units.
	 * 
	 * @return	Navigation rule to Unit Details page of newly created Unit object
	 */
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
	
	/**
	 * Method to handle the form data from the Import Unit page and upload
	 * the corresponding .pptx file for processing.
	 * 
	 * @return The navigation rule to the Unit Details page.
	 */
	public String importUnitPptx() {
		//ensure that a file has been uploaded
		if(tempUnitPptx != null && tempUnitPptx.getSize() != 0) {
			try {
				//create a temporary file
				File pptxFile = File.createTempFile("unit", ".pptx");
				tempUnitPptx.write(pptxFile.getAbsolutePath());
				
				//import PowerPoint presentation
				XMLSlideShow pptx = new XMLSlideShow(new FileInputStream(pptxFile));
				
				//parse PowerPoint presentation into a Unit object
				Unit importedUnit = Powerpoint.parseUnitPresentation(pptx, nextAvailableUnitId());
				
				//add the new Unit to the Course model and set the currentUnit object
				course.getUnits().add(importedUnit);
				currentUnit = importedUnit;
				
				//delete the temporary file
				pptxFile.delete();
			} catch (IOException e) {
				throw new ValidatorException(new FacesMessage("Invalid PowerPoint file detected."));
			}
		} else {
			throw new ValidatorException(new FacesMessage("Invalid PowerPoint file detected."));
		}
		
		//navigate to Unit Details page of newly imported Unit
		return "unitDetails";
	}
	
	/**
	 * Handles the saving of data from the Edit Unit page, once the user has made some changes
	 * to the Unit object.  Differs from the createUnit() method as it directly edits an existing
	 * Unit object saved in the Course's list of Units.
	 * 
	 * @return	Navigation rule to the Unit Details page
	 */
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
	
	/**
	 * Sets the selected unit details into memory before navigating to that Unit's details.
	 * 
	 * @return	Navigation rule to the Unit Details page
	 */
	public String showSelectedUnit() {
		List<Unit> units = course.getUnits();
		
		for (Unit unit : units) {
			if (unit.getUnitId() == selectedUnit) {
				currentUnit = unit;
			}
		}
		
		return "unitDetails";
	}
	
	/**
	 * Sets up temporary variables for the Edit Unit screen, before navigating to said screen.
	 * 
	 * @return	Navigation rule to the Edit Unit page
	 */
	public String editCurrentUnit() {
		tempUnit = currentUnit;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		tempUnitDate = df.format(tempUnit.getReleaseDate());
		
		return "unitEdit";
	}
	
	/**
	 * Resets the temporary variables from the Edit Unit screen upon the user clicking the 
	 * cancel button.
	 * 
	 * @return	Navigation rule to the Unit Details page
	 */
	public String cancelEditUnit() {
		tempUnit = new Unit();
		tempUnitDate = null;
		
		return "unitDetails";
	}
	
	/**
	 * Deletes a Unit object from the Course's list of Units after the user clicks on
	 * the delete button on the Course Details page.
	 * 
	 * @return	Navigation rule to the Course Details page
	 */
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
	
	/**
	 * Finds the next unused Unit ID number based on existing units.
	 * 
	 * @return	The next available Unit ID
	 */
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
	
	/**
	 * Handles the creation of a new Lesson object within the currentUnit object through the
	 * Create Lesson page and adds this to the currentUnit object's list of lessons.
	 * 
	 * @return	Navigation rule to Unit Details page of currentUnit
	 */
	public String createLesson() {
		//set the new Lesson ID number
		tempLesson.setLessonId(nextAvailableLessonId());
		
		//convert tempLessonVid to YouTube Video ID only
		Matcher ytMatch = YOUTUBE_REGEX.matcher(tempLessonVid);
		
		if (ytMatch.matches() && (ytMatch.group(1).length() == 11)) {
			String youtubeId = ytMatch.group(1);
			tempLesson.setLessonVideoId(youtubeId);
		}
		
		//save new lesson into the currentUnit object and update the Course model
		List<Lesson> currentLessons = currentUnit.getLessons();
		currentLessons.add(tempLesson);
		currentUnit.setLessons(currentLessons);
		
		//replace the old currentUnit in the Course model with the new currentUnit
		for (int i=0; i < course.getUnits().size(); i++) {
			if (course.getUnits().get(i).getUnitId() == currentUnit.getUnitId()) {
				//this is the Unit object to replace
				course.getUnits().set(i, currentUnit);
			}
		}
		
		tempLesson = new Lesson();
		tempLessonVid = null;
		
		return "unitDetails";
	}
	
	/**
	 * Handles the saving of data from the Edit Lesson page. Differs from the createLesson()
	 * method as it doesn't assign a new Lesson ID number and it directly edits the existing
	 * Lesson object in the currentUnit.
	 * 
	 * @return	Navigation rule to the Unit Details page
	 */
	public String saveLessonChanges() {
		//convert tempLessonVid to YouTube Video ID only
		Matcher ytMatch = YOUTUBE_REGEX.matcher(tempLessonVid);
		
		if (ytMatch.matches() && (ytMatch.group(1).length() == 11)) {
			String youtubeId = ytMatch.group(1);
			tempLesson.setLessonVideoId(youtubeId);
		}
		
		//get current lesson object, directly update it
		for (int i=0; i < currentUnit.getLessons().size(); i++) {
			if (currentUnit.getLessons().get(i).getLessonId() == tempLesson.getLessonId()) {
				//this is the Lesson object to edit
				currentUnit.getLessons().get(i).setLessonTitle(tempLesson.getLessonTitle());
				currentUnit.getLessons().get(i).setLessonVideoId(tempLesson.getLessonVideoId());
				currentUnit.getLessons().get(i).setLessonNotes(tempLesson.getLessonNotes());
				currentUnit.getLessons().get(i).setLessonObjectives(tempLesson.getLessonObjectives());
			}
		}
		
		//replace the old currentUnit in the Course model with the new currentUnit
		for (int i=0; i < course.getUnits().size(); i++) {
			if (course.getUnits().get(i).getUnitId() == currentUnit.getUnitId()) {
				//this is the Unit object to replace
				course.getUnits().set(i, currentUnit);
			}
		}
		
		//reset the temporary variables
		tempLesson = new Lesson();
		tempLessonVid = null;
		
		return "unitDetails";
	}
	
	/**
	 * Sets the temporary Lesson object using the user selected Lesson ID, then navigates to the
	 * Edit Lesson page.
	 * 
	 * @return	Navigation rule to the Edit Lesson page
	 */
	public String editSelectedLesson() {
		List<Lesson> lessons = currentUnit.getLessons();
		
		for (Lesson lesson : lessons) {
			if (lesson.getLessonId() == selectedLesson) {
				tempLesson = lesson;
			}
		}
		
		tempLessonVid = "http://www.youtube.com/watch?v=" + tempLesson.getLessonVideoId();
		
		return "lessonEdit";
	}
	
	/**
	 * Resets the temporary variables from the Edit Lesson screen upon the user clicking the 
	 * cancel button.
	 * 
	 * @return	Navigation rule to the Unit Details page
	 */
	public String cancelEditLesson() {
		tempLesson = new Lesson();
		tempLessonVid = null;
		
		return "unitDetails";
	}
	
	/**
	 * Deletes a Lesson object from the currentUnit's list of lessons after the user clicks
	 * on the delete button on the Unit Details page.
	 * 
	 * @return	Navigation rule to the Unit Details page
	 */
	public String deleteSelectedLesson() {
		List<Lesson> lessons = currentUnit.getLessons();
		
		//search for the selected lesson object
		for (int i=0; i < lessons.size(); i++) {
			if (lessons.get(i).getLessonId() == selectedLesson) {
				//delete the selected lesson object
				lessons.remove(i);
			}
		}
		
		//reset the remaining lesson objects into a sequential list
		for (Lesson lesson : lessons) {
			int currentId = lesson.getLessonId();
			if (currentId > selectedLesson) {
				lesson.setLessonId(currentId-1);
			}
		}
		
		//replace the old currentUnit in the Course model with the new currentUnit
		for (int i=0; i < course.getUnits().size(); i++) {
			if (course.getUnits().get(i).getUnitId() == currentUnit.getUnitId()) {
				//this is the Unit object to replace
				course.getUnits().set(i, currentUnit);
			}
		}
		
		//reset the selected lesson ID to 0, then refresh the Unit Details page
		selectedLesson = 0;
		return "unitDetails";
	}
	
	/**
	 * Finds the next unused Lesson ID number based on existing lessons in the currentUnit object.
	 * 
	 * @return	The next available Lesson ID
	 */
	private int nextAvailableLessonId() {
		int nextId;
		
		if (currentUnit.getLessons().size() > 0) {
			int maxExistingId = 0;
			
			//loop through every Lesson object, to find the highest Lesson ID
			for (Lesson lesson : currentUnit.getLessons()) {
				if (lesson.getLessonId() > maxExistingId) {
					maxExistingId = lesson.getLessonId();
				}
			}
			
			nextId = maxExistingId + 1;
		} else {
			nextId = 1;
		}
		
		return nextId;
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

	public String getTempLessonVid() {
		return tempLessonVid;
	}

	public void setTempLessonVid(String tempLessonVid) {
		this.tempLessonVid = tempLessonVid;
	}

	public Lesson getTempLesson() {
		return tempLesson;
	}

	public void setTempLesson(Lesson tempLesson) {
		this.tempLesson = tempLesson;
	}

	public Part getTempUnitPptx() {
		return tempUnitPptx;
	}

	public void setTempUnitPptx(Part tempUnitPptx) {
		this.tempUnitPptx = tempUnitPptx;
	}
}
