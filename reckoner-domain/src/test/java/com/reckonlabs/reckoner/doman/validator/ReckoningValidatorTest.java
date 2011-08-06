package com.reckonlabs.reckoner.doman.validator;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.reckoning.Answer;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;

public class ReckoningValidatorTest {

	@Test
	public void testValidateReckoningPost() {
		Message valResult;
		valResult = ReckoningValidator.validateReckoningPost(buildReckoning(null, "test", 2));
		Assert.assertTrue(valResult.getCode().equalsIgnoreCase("R100"));
		
		valResult = ReckoningValidator.validateReckoningPost(buildReckoning("Answer", "test", 0));
		Assert.assertTrue(valResult.getCode().equalsIgnoreCase("R101"));
		
		valResult = ReckoningValidator.validateReckoningPost(buildReckoning("Answer", "test", 1));
		System.out.println("XXX" + valResult.getCode());
		Assert.assertTrue(valResult.getCode().equalsIgnoreCase("R102"));	
		
		valResult = ReckoningValidator.validateReckoningPost(buildReckoning("Answer", null, 2));
		Assert.assertTrue(valResult.getCode().equalsIgnoreCase("R103"));
		
		valResult = ReckoningValidator.validateReckoningPost(buildReckoning("Answer", "test", 2));
		Assert.assertTrue(valResult == null);
	}
	
	private Reckoning buildReckoning(String question, String submitterId, int numAnswers) {
		Reckoning returnVal = new Reckoning();
		
		returnVal.setQuestion(question);		
		returnVal.setSubmitterId(submitterId);
		returnVal.setApproverId("approver");
		
		for (int i = 0; i < numAnswers; i++) {
			Answer answer = new Answer();
			answer.setIndex(i);
			answer.setText("Answer " + Integer.toString(i));
			answer.setSubtitle("Subtitle " + Integer.toString(i));
			
			if (returnVal.getAnswers() == null) {
				returnVal.setAnswers(new LinkedList<Answer>());
			}
			returnVal.getAnswers().add(answer);
		}
		
		return returnVal;
	}

}
