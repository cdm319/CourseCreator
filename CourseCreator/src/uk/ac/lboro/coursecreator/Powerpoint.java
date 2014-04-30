package uk.ac.lboro.coursecreator;

import java.util.Date;
import java.util.List;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import uk.ac.lboro.coursecreator.model.Course;

public class Powerpoint {
	/**
	 * Retrieve's the average font size used on a given slide.
	 * 
	 * @param 	slide
	 * @return 	Average font size on slide.
	 */
	public static double getSlideAvgFontSize(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		int totalTextRuns = 0;
		double totalFontSize = 0.0, avgFontSize = 0.0;
		
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				for (XSLFTextParagraph para : paras) {
					List<XSLFTextRun> spans = para.getTextRuns();
					for (XSLFTextRun span : spans) {
						totalTextRuns = totalTextRuns + 1;
						totalFontSize = totalFontSize + span.getFontSize();
					}
				}
			}
		}
		
		avgFontSize = totalFontSize / (double)totalTextRuns;
		
		return avgFontSize;
	}
	
	/**
	 * Retrieve's the average font size used in a given shape.
	 * 
	 * @param 	shape
	 * @return	Average font size on shape.
	 */
	public static double getShapeAvgFontSize(XSLFShape shape) {
		int totalTextRuns = 0;
		double totalFontSize = 0.0, avgFontSize = 0.0;
		
		List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
		for (XSLFTextParagraph para : paras) {
			List<XSLFTextRun> spans = para.getTextRuns();
			for (XSLFTextRun span : spans) {
				totalTextRuns = totalTextRuns + 1;
				totalFontSize = totalFontSize + span.getFontSize();
			}
		}
		
		avgFontSize = totalFontSize / (double)totalTextRuns;
		
		return avgFontSize;
	}
	
	/**
	 * Retrieve's the title of a given PowerPoint slide.
	 * 
	 * @param 	slide
	 * @return	The title of the slide.
	 */
	public static String getSlideTitle(XSLFSlide slide) {
		if ("".equals(slide.getTitle())) {
			XSLFShape[] shapes = slide.getShapes();
			double avgFontSize = getSlideAvgFontSize(slide);
			
			for (XSLFShape shape : shapes) {
				//if shape is text shape
				if (shape instanceof XSLFTextShape) {
					if (getShapeAvgFontSize(shape) > avgFontSize) {
						//get the first shape on the slide with a higher font size than the average
						List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
						return paras.get(0).getText();
					}
				}
			}
		} else {
			return slide.getTitle();
		}
		
		return null;
	}
	
	public static Date getDate(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		
		
		
		return null;
	}
	
	/**
	 * Method to parse a Course Structure presentation into meaningful data
	 * and return this in a Course model object.
	 * 
	 * @param 	pptx
	 * @return	A Course object contained course structure data.
	 */
	public static Course parseCoursePresentation(XMLSlideShow pptx) {
		Course course = new Course();
		XSLFSlide[] slides = pptx.getSlides();
		
		for (int i = 0; i < slides.length; i++) {
			XSLFSlide slide = slides[i];
			if (i == 0) {
				//title slide, contains course structure details
				
				//get course title
				course.setTitle(getSlideTitle(slide));
				
				//get course blurb
				
				//get course start date
				
				//get course intro video
				
				//get course forum URL
				
				//get institution name
				
				//get institution URL
				
				//get administrator name
				
				//get administrator email address
				
			} else if (i == 1) {
				//potential timetable slide
				
			}
		}
		
		return course;
	}
}
