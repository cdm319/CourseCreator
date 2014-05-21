package uk.ac.lboro.coursecreator;

import static org.junit.Assert.*;

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
	
	
}
