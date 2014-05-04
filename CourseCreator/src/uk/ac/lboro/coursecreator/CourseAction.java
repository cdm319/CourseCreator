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
	private Course courseStructure;
	
	//temporary course details
	private String tempDate;
	private String tempIntroVid;
	
	//temporary file upload variables
	private Part tempLogoImage;
	private Part tempCoursePptx;
	
	/**
	 * Constructor creates new Course model object if necessary.
	 */
	public CourseAction() {
		if(courseStructure == null) {
			courseStructure = new Course();
		}
	}
	
	/**
	 * Method that adds sample data to the Create Course page via an AJAX call.
	 * 
	 * @return null
	 */
	public String useTestData() {
		courseStructure.setTitle("Advanced Human-Computer Interaction");
		courseStructure.setBlurb("The aim of the module is to introduce to students advanced concepts, definitions "
				+ "and techniques in Human-Computer Interaction and to introduce appropriate evaluation techniques."
				+ " For Loughborough University students, this module involves team work, which is peer assessed.");
		this.setTempIntroVid("http://www.youtube.com/watch?v=Jwj2l3yl27E");
		this.setTempDate("05/10/2014");
		courseStructure.setForumURL("http://www.lboro.ac.uk/compsci/advancedhci/forum");
		
		courseStructure.setInstitutionName("Loughborough University");
		courseStructure.setInstitutionURL("http://www.lboro.ac.uk/");
		courseStructure.setInstitutionLogo("iVBORw0KGgoAAAANSUhEUgAAAQ4AAABLCAYAAACBU2nOAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpGM0NFQThGOUFCOTkxMUUzQTlCQUM3QkQ2OEVCNEZBQyIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpGM0NFQThGQUFCOTkxMUUzQTlCQUM3QkQ2OEVCNEZBQyI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkYzQ0VBOEY3QUI5OTExRTNBOUJBQzdCRDY4RUI0RkFDIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkYzQ0VBOEY4QUI5OTExRTNBOUJBQzdCRDY4RUI0RkFDIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+bNPdJgAAKzdJREFUeNrsXQlg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");
		
		courseStructure.setAdministratorName("Mashhuda Glencross");
		courseStructure.setAdministratorEmail("M.Glencross@lboro.ac.uk");
		
		return null;
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
				
				courseStructure.setTitle(importedCourse.getTitle());
				courseStructure.setBlurb(importedCourse.getBlurb());
				courseStructure.setStartDate(importedCourse.getStartDate());
				courseStructure.setIntroVideoId(importedCourse.getIntroVideoId());
				courseStructure.setForumURL(importedCourse.getForumURL());
				courseStructure.setInstitutionName(importedCourse.getInstitutionName());
				courseStructure.setInstitutionURL(importedCourse.getInstitutionURL());
				courseStructure.setInstitutionLogo(importedCourse.getInstitutionLogo());
				courseStructure.setAdministratorName(importedCourse.getAdministratorName());
				courseStructure.setAdministratorEmail(importedCourse.getAdministratorEmail());
				
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				tempDate = df.format(courseStructure.getStartDate());
				
				tempIntroVid = "http://www.youtube.com/watch?v=" + courseStructure.getIntroVideoId();
				
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

	public Part getTempCoursePptx() {
		return tempCoursePptx;
	}

	public void setTempCoursePptx(Part tempCoursePptx) {
		this.tempCoursePptx = tempCoursePptx;
	}
}
