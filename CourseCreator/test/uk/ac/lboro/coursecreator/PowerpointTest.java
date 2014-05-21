package uk.ac.lboro.coursecreator;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PowerpointTest {
	
	private XMLSlideShow ppt;

	@Before
	public void setUp() {
		//setup ppt
		ppt = new XMLSlideShow();
		
		//get title and content slide layout
		XSLFSlideMaster master = ppt.getSlideMasters()[0];
		XSLFSlideLayout layout = master.getLayout(SlideLayout.TITLE_AND_CONTENT);
		
		//create slide with Title and Content
		XSLFSlide slide = ppt.createSlide(layout);
		
		//set dummy title
		XSLFTextShape titleShape = slide.getPlaceholder(0);
		titleShape.setText("Test Slide");
		
		//set dummy content
		XSLFTextShape contentShape = slide.getPlaceholder(1);
		contentShape.clearText();
		
		XSLFTextRun span1 = contentShape.addNewTextParagraph().addNewTextRun();
		span1.setFontSize(40);
		span1.setText("Chris Matthews <chrismatthews@outlook.com>");
		
		XSLFTextRun span2 = contentShape.addNewTextParagraph().addNewTextRun();
		span2.setFontSize(30);
		span2.setText("Loughborough University - http://www.lboro.ac.uk");
		
		XSLFTextRun span3 = contentShape.addNewTextParagraph().addNewTextRun();
		span3.setFontSize(20);
		span3.setText("01/04/2014");
		
		XSLFTextRun span4 = contentShape.addNewTextParagraph().addNewTextRun();
		span4.setFontSize(10);
		span4.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque volutpat velit quis turpis "
				+ "convallis luctus. Vestibulum rutrum ullamcorper lorem, vel.");
		
		XSLFTextRun span5 = contentShape.addNewTextParagraph().addNewTextRun();
		span5.setFontSize(20);
		span5.setText("http://www.youtube.com/watch?v=j-jKUDNm9EM");
	}
	
	@After
	public void tearDown() {
		ppt = null;
	}
	
	@Test
	public void testShapeAvgFontSizeTitle() {
		XSLFShape shape = ppt.getSlides()[0].getShapes()[0];
		double size = Powerpoint.getShapeAvgFontSize(shape);
		assertEquals(44, size, 0.1);
	}
	
	@Test
	public void testShapeAvgFontSizeContent() {
		XSLFShape shape = ppt.getSlides()[0].getShapes()[1];
		double size = Powerpoint.getShapeAvgFontSize(shape);
		assertEquals(24, size, 0.1);
	}
	
	@Test
	public void testSlideAvgFontSize() {
		XSLFSlide slide = ppt.getSlides()[0];
		double size = Powerpoint.getSlideAvgFontSize(slide);
		assertEquals(27, size, 0.5);
	}
	
	@Test
	public void testSlideTitle() {
		XSLFSlide slide = ppt.getSlides()[0];
		assertEquals("Test Slide", Powerpoint.getSlideTitle(slide));
	}
	
	@Test
	public void testNamedEmailAddress() {
		XSLFSlide slide = ppt.getSlides()[0];
		List<String> expected = new ArrayList<String>();
		expected.add("Chris Matthews");
		expected.add("chrismatthews@outlook.com");
		assertEquals(expected, Powerpoint.getNamedEmailAddress(slide));
	}
	
	@Test
	public void testNonYouTubeURL() {
		XSLFSlide slide = ppt.getSlides()[0];
		List<String> ac = new ArrayList<String>();
		ac.add(".ac.uk");
		assertEquals("http://www.lboro.ac.uk", Powerpoint.getNonYouTubeURL(slide, ac));
	}
	
	@Test
	public void testYouTubeVideoID() {
		XSLFSlide slide = ppt.getSlides()[0];
		assertEquals("j-jKUDNm9EM", Powerpoint.getYouTubeVideoID(slide));
	}
	
	@Test
	public void testInstitutionName() {
		XSLFSlide slide = ppt.getSlides()[0];
		String actual = Powerpoint.getInstitutionName(slide).trim();
		assertEquals("Loughborough University", actual);
	}

	@Test
	public void testDate() {
		XSLFSlide slide = ppt.getSlides()[0];
		
		//setup expected date object
		Calendar cal = new GregorianCalendar();
		cal.set(2014, 03, 01, 12, 0, 0);
		Date expectedDate = cal.getTime();
		
		//setup date format as comparing dates too precise (milliseconds)
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
		df.setLenient(false);
		
		String expected = df.format(expectedDate);
		String actual = df.format(Powerpoint.getDate(slide));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLongestParagraph() {
		XSLFSlide slide = ppt.getSlides()[0];
		String expected = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque volutpat velit quis "
				+ "turpis convallis luctus. Vestibulum rutrum ullamcorper lorem, vel.";
		
		assertEquals(expected, Powerpoint.getLongestParagraph(slide));
	}
}
