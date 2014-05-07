package uk.ac.lboro.coursecreator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import uk.ac.lboro.coursecreator.model.Course;
import uk.ac.lboro.coursecreator.model.Lesson;
import uk.ac.lboro.coursecreator.model.Unit;

/**
 * Utility class that parses various data from a Powerpoint presentation using the 
 * Apache POI libraries.
 * 
 * @author Chris Matthews <C.Matthews2-09@student.lboro.ac.uk>
 */
public class Powerpoint {
	//constant regex patterns
	private static final Pattern URL_REGEX = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?(https://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#!&\\n\\-=?\\+\\%/\\.\\w]+)?");
	private static final Pattern NAMED_EMAIL_REGEX = Pattern.compile("(?:\"?([^\"]*)\"?\\s)?(?:<?([A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6})>?)", Pattern.CASE_INSENSITIVE);
	private static final Pattern DATE_REGEX = Pattern.compile("(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](20)\\d\\d");
	private static final Pattern YOUTUBE_REGEX = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=))((\\w|-){11})(?:\\S+)?$");
	private static final Pattern UNI_NAME_REGEX = Pattern.compile("(([A-Z][a-zA-Z]*\\s*)+)?(University|College|School|Institution)(\\s)?(of|Of)?(\\s)?(([A-Z][a-zA-Z]*\\s*)+)?");
	
	/**
	 * Retrieve's the average font size used on a given slide.
	 * 
	 * @param 	slide
	 * @return 	Average font size on slide
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
	 * @return	Average font size on shape
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
	 * @return	The title of the slide
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
	
	/**
	 * Searches a given slide for a name/email address pair, such as:
	 * Joe Bloggs <j.bloggs@email.com>
	 * 
	 * @param 	slide
	 * @return	A list of results
	 */
	public static List<String> getNamedEmailAddress(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		List<String> results = new ArrayList<String>();
		
		//loop through every shape on the slide
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph on the slide
				for (XSLFTextParagraph para : paras) {
					String text = para.getText();
					Matcher emailMatcher = NAMED_EMAIL_REGEX.matcher(text);
					
					//check if a potential match has been found in the text
					if (emailMatcher.find()) {
						String foundName = emailMatcher.group(1);
						results.add(foundName);
						
						String foundEmail = emailMatcher.group(2);
						results.add(foundEmail);
						
						return results;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches a given slide for a non-YouTube URL.
	 * 
	 * @param 	slide
	 * @return	A non-YouTube URL
	 */
	public static String getNonYouTubeURL(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		
		//loop through every shape on the slide
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph on the slide
				for (XSLFTextParagraph para : paras) {
					String text = para.getText();
					
					Matcher urlMatcher = URL_REGEX.matcher(text);
					Matcher ytMatcher = YOUTUBE_REGEX.matcher(text);
					
					//check if a URL has been found that doesn't point to YouTube
					if (urlMatcher.find() && !ytMatcher.find()) {
						return urlMatcher.group();
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches a given slide for a non-YouTube URL, which also contains at least one of 
	 * a list of given strings.
	 * 
	 * @param 	slide
	 * @param 	includedStrings
	 * @return	A string containing a URL matching at least one of the includedStrings
	 */
	public static String getNonYouTubeURL(XSLFSlide slide, List<String> includedStrings) {
		XSLFShape[] shapes = slide.getShapes();
		
		//loop through every shape on the slide
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph on the slide
				for (XSLFTextParagraph para : paras) {
					String text = para.getText();
					
					Matcher urlMatcher = URL_REGEX.matcher(text);
					Matcher ytMatcher = YOUTUBE_REGEX.matcher(text);
					
					//check if a URL has been found that doesn't point to YouTube and contains the additionalText
					if (urlMatcher.find() && !ytMatcher.find()) {
						String found = urlMatcher.group();
						
						for (String included : includedStrings) {
							if (found.contains(included)) {
								return found;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches a given slide for a valid YouTube Video URL.
	 * 
	 * @param 	slide
	 * @return	a valid YouTube Video ID
	 */
	public static String getYouTubeVideoID(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		
		//loop through every shape on the slide
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph on the slide
				for (XSLFTextParagraph para : paras) {
					String text = para.getText();
					Matcher ytMatcher = YOUTUBE_REGEX.matcher(text);
					
					//check if a potential match has been found in the text
					if (ytMatcher.find()) {						
						return ytMatcher.group(1);
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches a given slide for the name of an Academical Institution, for
	 * example "Loughborough University", "University of Cambridge", 
	 * "Loughborough College", etc.
	 * 
	 * @param 	slide
	 * @return	The name of an Academical Institution
	 */
	public static String getInstitutionName(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		
		//loop through every shape on the slide
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph on the slide
				for (XSLFTextParagraph para : paras) {
					String text = para.getText();
					Matcher nameMatcher = UNI_NAME_REGEX.matcher(text);
					
					if (nameMatcher.find()) {
						return nameMatcher.group();
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches a given slide for a Text Run containing a specific string
	 * 
	 * @param 	slide
	 * @param 	searchTexts
	 * @return	The TextRun containing one of the given strings
	 */
	public static String getSpecificText(XSLFSlide slide, List<String> searchTexts) {
		XSLFShape[] shapes = slide.getShapes();
		
		//loop through every shape on the slide
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph on the slide
				for (XSLFTextParagraph para : paras) {
					List<XSLFTextRun> spans = para.getTextRuns();
					
					//loop through every "span" in each paragraph
					for (XSLFTextRun span : spans) {
						String text = span.getText();
						
						for (String search : searchTexts) {
							if (text.contains(search)) {
								return text;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Searches a given slide for a UK formatted date in the 21st Century, such as 25/12/2014
	 * 
	 * @param 	slide
	 * @return	A date object containing the found date
	 */
	public static Date getDate(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		
		//loop through every shape on the slide
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph in the shape
				for (XSLFTextParagraph para : paras) {
					String text = para.getText();
					Matcher dateMatcher = DATE_REGEX.matcher(text);
					
					//check if a potential date has been found in the text
					if (dateMatcher.find()) {
						String foundDate = dateMatcher.group();
						Date date = null;
						
						//look only for UK formatted dates at this time
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
						dateFormat.setLenient(false);
						
						try {
							//attempt to parse the date
							date = dateFormat.parse(foundDate);
						} catch (ParseException e) {
							//do nothing, let it keep searching for another potential date
						}
						
						if (null != date) {
							//if a date has been found, set the time and return it
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.set(Calendar.HOUR_OF_DAY, 12);
							date = cal.getTime();
							return date;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	public static String getLongestParagraph(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		String longestParagraph = "";
		
		//loop through every shape
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				
				//loop through every paragraph, if it is longer than longestParagraph, replace
				for (XSLFTextParagraph para : paras) {
					String text = para.getText();
					
					if (text.length() > longestParagraph.length()) {
						longestParagraph = text;
					}
				}
			}
		}
		
		return longestParagraph;
	}
	
	private static String convertSlideToHTML(XSLFSlide slide) {
		XSLFShape[] shapes = slide.getShapes();
		String html = "";
		
		//loop through every shape
		for (XSLFShape shape : shapes) {
			if (shape instanceof XSLFTextShape) {
				List<XSLFTextParagraph> paras = ((XSLFTextShape) shape).getTextParagraphs();
				String shapeText = "";
				
				for (XSLFTextParagraph para : paras) {
					//ignore the title paragraph
					if (!para.getText().equals(getSlideTitle(slide))) {
						if (para.isBullet()) {
							//bullet pointed paragraph
							
							//if this is the first bullet, add a list tag
							if (!shapeText.endsWith("</li>")) {
								shapeText = shapeText.concat("<ul>");
							}
							shapeText = shapeText.concat("<li>");
							
							List<XSLFTextRun> spans = para.getTextRuns();
							for (XSLFTextRun span : spans) {
								String text = span.getText();
								
								//check for hyperlink
								//TODO YouTube links appear to span multiple TextRuns, ignore for the moment
								Matcher urlMatch = URL_REGEX.matcher(text);
								if (urlMatch.matches()) { text = "<a href='" + urlMatch.group() + "'>" + urlMatch.group() + "</a>"; }
								
								//check for formatting
								if (span.isBold()) { text = "<b>" + text + "</b>"; }
								if (span.isItalic()) { text = "<i>" + text + "</i>"; }
								if (span.isUnderline()) { text = "<u>" + text + "</u>"; }
								if (span.isStrikethrough()) { text = "<s>" + text + "</s>"; }
								if (span.isSubscript()) { text = "<sub>" + text + "</sub>"; }
								if (span.isSuperscript()) { text = "<sup>" + text + "</sup>"; }
								
								shapeText = shapeText.concat(text);
							}
							
							shapeText = shapeText.concat("</li>");
						} else {
							//normal paragraph
							
							//if last paragraph was a bullet point, close the list tag
							if (shapeText.endsWith("</li>")) {
								shapeText = shapeText.concat("</ul>");
							}
							
							shapeText = shapeText.concat("<p>");
							
							List<XSLFTextRun> spans = para.getTextRuns();
							for (XSLFTextRun span : spans) {
								String text = span.getText();
								
								//check for hyperlink
								Matcher urlMatch = URL_REGEX.matcher(text);
								if (urlMatch.matches()) { text = "<a href='" + text + "'>" + text + "</a>"; }
								
								//check for formatting
								if (span.isBold()) { text = "<b>" + text + "</b>"; }
								if (span.isItalic()) { text = "<i>" + text + "</i>"; }
								if (span.isUnderline()) { text = "<u>" + text + "</u>"; }
								if (span.isStrikethrough()) { text = "<s>" + text + "</s>"; }
								if (span.isSubscript()) { text = "<sub>" + text + "</sub>"; }
								if (span.isSuperscript()) { text = "<sup>" + text + "</sup>"; }
								
								shapeText = shapeText.concat(text);
							}
							
							shapeText = shapeText.concat("</p>");
						}
					}
				}
				
				//ensure lists are closed correctly
				if (shapeText.endsWith("</li>")) {
					shapeText = shapeText.concat("</ul>");
				}
				
				html = html.concat(shapeText);
			}
		}
		
		return html;
	}
	
	/**
	 * Method to parse a Course Structure presentation into meaningful data
	 * and return this in a Course model object.
	 * 
	 * @param 	pptx
	 * @return	A Course object containing course structure data.
	 */
	public static Course parseCoursePresentation(XMLSlideShow pptx) {
		Course course = new Course();
		XSLFSlide[] slides = pptx.getSlides();
		
		//loop through each slide
		for (int i = 0; i < slides.length; i++) {
			XSLFSlide slide = slides[i];
			
			if (i == 0) {
				//title slide, contains course structure details
				
				//get course title
				course.setTitle(getSlideTitle(slide));
				
				//get course blurb
				course.setBlurb(getLongestParagraph(slide));
				
				//get course start date
				course.setStartDate(getDate(slide));
				
				//get course intro video
				course.setIntroVideoId(getYouTubeVideoID(slide));
				
				//get course forum URL
				List<String> forumKeywords = new ArrayList<String>();
				forumKeywords.add("forum");
				forumKeywords.add("group");
				String forum = getNonYouTubeURL(slide, forumKeywords);
				if (null == forum || "".equals(forum)) {
					forum = getNonYouTubeURL(slide);
				}
				course.setForumURL(forum);
				
				//get institution name
				course.setInstitutionName(getInstitutionName(slide));
				
				//get institution URL
				List<String> urlKeywords = new ArrayList<String>();
				urlKeywords.add(".edu");
				urlKeywords.add(".ac");
				urlKeywords.add(".sch");
				urlKeywords.add("college");
				urlKeywords.add("university");
				urlKeywords.add("school");
				urlKeywords.add("education");
				String instURL = getNonYouTubeURL(slide, urlKeywords);
				if (null == instURL || "".equals(instURL)) {
					instURL = getNonYouTubeURL(slide);
				}
				course.setInstitutionURL(instURL);
				
				//get administrator details
				List<String> adminDetails = getNamedEmailAddress(slide);
				if (adminDetails != null && adminDetails.size() > 0) {
					course.setAdministratorName(adminDetails.get(0));	//administrator name
					course.setAdministratorEmail(adminDetails.get(1));	//administrator email address
				}
			}
		}
		
		return course;
	}

	/**
	 * Method to parse a Unit/Lecture presentation into meaningful data
	 * and return this in a Unit model object.
	 * 
	 * @param 	pptx
	 * @return	A Unit object containing Unit and Lessons data
	 */
	public static Unit parseUnitPresentation(XMLSlideShow pptx, int unitId) {
		Unit unit = new Unit();
		XSLFSlide[] slides = pptx.getSlides();
		
		Lesson lastLesson = new Lesson();
		String unitObjectives = "";
		
		//set the new Unit's ID to the next available Unit ID number
		unit.setUnitId(unitId);
		
		//loop through every slide in the presentation
		for (int i=0; i < slides.length; i++) {
			XSLFSlide slide = slides[i];
			if (i == 0) {
				//this is the title slide, containing Unit data
				
				//set the Unit title to the title of the first slide
				unit.setTitle(getSlideTitle(slide));
				
				//set the Release Date to the date on the first slide
				Date date = getDate(slide);
				unit.setReleaseDate(date);
				
				//set the Now Available flag based on the Release Date
				Date today = new Date();
				
				if (date != null) {
					if (date.after(today)) {
						unit.setNowAvailable(false);
					} else {
						unit.setNowAvailable(true);
					}
				} else {
					unit.setNowAvailable(false);
				}
			} else {
				//get the slide title
				String title = getSlideTitle(slide);
				
				if (isObjectives(title)) {
					//Unit objectives slide
					unitObjectives = convertSlideToHTML(slide);
				} else {
					//these are potential Lesson slides, containing Lesson data
					
					if (!title.equals("") && !lastLesson.getLessonTitle().equals("") && (lastLesson.getLessonTitle().equals(title) || title.contains(lastLesson.getLessonTitle()))) {
						//continue with last Lesson object
						String additionalNotes = convertSlideToHTML(slide);
						String originalNotes = lastLesson.getLessonNotes();
						
						lastLesson.setLessonNotes(originalNotes + additionalNotes);
					} else {
						//create new Lesson object
						Lesson lesson = new Lesson();
						
						//set the Lesson ID
						lesson.setLessonId(lastLesson.getLessonId() + 1);
						
						//set the Lesson Title
						lesson.setLessonTitle(title);
						
						//set the Lesson Video ID, if none is found, use the previous Lesson's video
						String videoId = getYouTubeVideoID(slide);
						if (null != videoId) {
							lesson.setLessonVideoId(videoId);
						} else {
							lesson.setLessonVideoId(lastLesson.getLessonVideoId());
						}
						
						//set the Lesson Notes
						lesson.setLessonNotes(convertSlideToHTML(slide));
						
						//set the Lesson Objectives to the overall Unit objectives
						lesson.setLessonObjectives(unitObjectives);
						
						//add the Lesson object to the Unit object
						unit.getLessons().add(lesson);
						lastLesson = lesson;
					}
				}
			}
		}
		
		return unit;
	}

	/**
	 * Checks whether a given string contains words relating to objectives.
	 * 
	 * @param 	title
	 * @return	Whether the string contains objective related words
	 */
	private static boolean isObjectives(String title) {
		List<String> words = new ArrayList<String>();
		words.add("Objectives");
		words.add("Learning Outcomes");
		
		for (String word : words) {
			if (title.toLowerCase().contains(word.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
}
