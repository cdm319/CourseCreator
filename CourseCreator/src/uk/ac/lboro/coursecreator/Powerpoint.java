package uk.ac.lboro.coursecreator;

import java.util.List;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import uk.ac.lboro.coursecreator.model.Course;

public class Powerpoint {
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
	public static String getSlideTitle(XSLFSlide slide) {
		if ("".equals(slide.getTitle())) {
			XSLFShape[] shapes = slide.getShapes();
			double avgFontSize = getSlideAvgFontSize(slide);
		} else {
			return slide.getTitle();
		}
		
		return null;
	}
	public static Course parseCoursePresentation(XMLSlideShow pptx) {
		Course course = new Course();
		XSLFSlide[] slides = pptx.getSlides();
		
		for (int i = 0; i < slides.length; i++) {
			XSLFSlide slide = slides[i];
			if (i == 0) {
				//title slide
				course.setTitle(getSlideTitle(slide));
			} else if (i == 1) {
				//timetable slide
				
			}
			XSLFShape[] shapes = slides[i].getShapes();
		}
		
		return course;
		/*
		XSLFShape[] shapes = slides[1].getShapes();
		for (XSLFShape shape : shapes) {
			String shapeText = "<p>";
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				for (XSLFTextParagraph para: paras) {
					List<XSLFTextRun> lines = para.getTextRuns();
					for (XSLFTextRun line : lines) {
						String text = line.getText();
						if (text.equals(slides[1].getTitle())) {
							shapeText += "<h2>" + text + "</h2>";
						} else {
							shapeText += text;
						}
					}
				}
			}
			shapeText += "</p>";
			System.out.println(shapeText);
		}
		*/
	}
}
