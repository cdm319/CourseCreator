package uk.ac.lboro.coursecreator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.Part;

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
				courseStructure.setInstitutionLogo(base64logo);
				
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
			courseStructure.setStartDate(date);
		} catch (ParseException e) {
			return null;
		}
		
		//convert tempIntroVid to YouTube Video ID only, store in model
		if (tempIntroVid != null && !"".equals(tempIntroVid)) {
			Matcher ytMatch = YOUTUBE_REGEX.matcher(tempIntroVid);
			
			if (ytMatch.matches() && (ytMatch.group(1).length() == 11)) {
				String youtubeId = ytMatch.group(1);
				courseStructure.setIntroVideoId(youtubeId);
			}
		}
		
		//setup fake units and lessons to test Course Details page.
			List<Unit> units = new ArrayList<Unit>();
			
			for (int i=0; i < 3; i++) {
				Unit unit = new Unit();
				unit.setUnitId(i);
				unit.setTitle("Unit " + String.valueOf(i));
				unit.setReleaseDate(new Date());
				
				List<Lesson> lessons = new ArrayList<Lesson>();
				
				if(unit.getUnitId() != 1) {
					for (int j=0; j < 5; j++) {
						Lesson lesson = new Lesson();
						
						lesson.setLessonId(j);
						lesson.setLessonTitle("Lesson " + String.valueOf(j));
						lesson.setLessonNotes("Curabitur sagittis sed quam vitae sagittis. Nulla ut mollis elit, et "
								+ "faucibus dolor. Interdum et malesuada fames ac ante ipsum primis in faucibus. "
								+ "Pellentesque mattis vel tellus et convallis. Phasellus laoreet, erat et euismod "
								+ "imperdiet, dui tortor elementum arcu, sit amet tempus diam nibh in dui.");
						lesson.setLessonObjectives("Curabitur sagittis sed quam vitae sagittis. Nulla ut mollis elit, et "
								+ "faucibus dolor. Interdum et malesuada fames ac ante ipsum primis in faucibus. "
								+ "Pellentesque mattis vel tellus et convallis. Phasellus laoreet, erat et euismod "
								+ "imperdiet, dui tortor elementum arcu, sit amet tempus diam nibh in dui.");
						lesson.setLessonVideoId("j-jKUDNm9EM");
						
						lessons.add(lesson);
					}
				}
				
				unit.setLessons(lessons);
				
				units.add(unit);
			}
			
			courseStructure.setUnits(units);
		
		//move on to the Course Details page
		return "courseDetails";
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
