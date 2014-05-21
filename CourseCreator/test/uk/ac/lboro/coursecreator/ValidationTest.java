package uk.ac.lboro.coursecreator;

import javax.faces.validator.ValidatorException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ValidationTest {
	private FacesContextMock ctx = new FacesContextMock();
	private Validation validate = new Validation();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testValidateTextOnlyInvalid() {		
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please do not enter numbers.");
		validate.validateTextOnly(ctx, null, "Str1ng w1th numb3r5.");
	}
	
	@Test
	public void testValidateTextOnlyValid() {
		validate.validateTextOnly(ctx, null, "String without numbers.");
	}
	
	@Test
	public void testValidateDateValid() {
		validate.validateDate(ctx, null, "24/04/2014");
	}
	
	@Test
	public void testValidateDateInvalidOutOfRange() {
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please enter a valid date (dd/mm/yyyy).");
		validate.validateDate(ctx, null, "40/01/2014");
	}
	
	@Test
	public void testValidateDateInvalidAmerican() {
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please enter a valid date (dd/mm/yyyy).");
		validate.validateDate(ctx, null, "04/24/2014");
	}
	
	@Test
	public void testValidateDateInvalidYMD() {
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please enter a valid date (dd/mm/yyyy).");
		validate.validateDate(ctx, null, "2014-04-24");
	}
	
	@Test
	public void testValidateYouTubeURLValidShortForm() {
		validate.validateYouTubeURL(ctx, null, "http://youtu.be/j-jKUDNm9EM");
	}
	
	@Test
	public void testValidateYouTubeURLValidLongForm() {
		validate.validateYouTubeURL(ctx, null, "http://www.youtube.com/watch?v=j-jKUDNm9EM");
	}
	
	@Test
	public void testValidateYouTubeURLValidWithParameters() {
		validate.validateYouTubeURL(ctx, null, "http://youtu.be/j-jKUDNm9EM?t=4m55s");
	}
	
	@Test
	public void testValidateYouTubeURLInvalidURLNotYouTube() {
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please enter a valid YouTube URL.");
		validate.validateYouTubeURL(ctx, null, "http://www.google.com");
	}
	
	@Test
	public void testValidateYouTubeURLInvalidNotURL() {
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please enter a valid YouTube URL.");
		validate.validateYouTubeURL(ctx, null, "YouTube");
	}
	
	@Test
	public void testValidateURLValidFull() {
		validate.validateURL(ctx, null, "http://www.google.com");
	}
	
	@Test
	public void testValidateURLValidNoSchema() {
		validate.validateURL(ctx, null, "www.google.com");
	}
	
	@Test
	public void testValidateURLValidNoSchemaOrWWW() {
		validate.validateURL(ctx, null, "google.com");
	}
	
	@Test
	public void testValidateURLValidSubdomain() {
		validate.validateURL(ctx, null, "http://maps.google.com");
	}
	
	@Test
	public void testValidateURLValidSubdomainNoSchema() {
		validate.validateURL(ctx, null, "maps.google.com");
	}
	
	@Test
	public void testValidateURLValidTrailingSlash() {
		validate.validateURL(ctx, null, "http://www.google.com/");
	}
	
	@Test
	public void testValidateURLValidIP() {
		validate.validateURL(ctx, null, "192.168.0.1");
	}
	
	@Test
	public void testValidateURLValidWithPort() {
		validate.validateURL(ctx, null, "http://www.google.com:80");
	}
	
	@Test
	public void testValidateURLValidSubdirectory() {
		validate.validateURL(ctx, null, "http://www.google.com/maps");
	}
	
	@Test
	public void testValidateURLValidFile() {
		validate.validateURL(ctx, null, "http://www.google.com/index.html");
	}
	
	@Test
	public void testValidateURLInvalidNotURL() {
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please enter a valid URL.");
		validate.validateURL(ctx, null, "Google com");
	}
	
	@Test
	public void testValidateOptionalURLValidFull() {
		validate.validateOptionalURL(ctx, null, "http://www.google.com");
	}
	
	@Test
	public void testValidateOptionalURLValidEmpty() {
		validate.validateOptionalURL(ctx, null, "");
	}
	
	@Test
	public void testValidateOptionalURLInvalidWhitespace() {
		exception.expect(ValidatorException.class);
		exception.expectMessage("Please enter a valid URL.");
		validate.validateOptionalURL(ctx, null, " ");
	}
}
