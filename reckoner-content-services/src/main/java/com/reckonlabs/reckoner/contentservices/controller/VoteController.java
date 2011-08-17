package com.reckonlabs.reckoner.contentservices.controller;

import java.lang.Boolean;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.reckonlabs.reckoner.contentservices.service.VoteService;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostVote;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;
import com.reckonlabs.reckoner.domain.validator.VoteValidator;

/**
 * This controller is responsible for the web services for posting and reading
 * comments associated with Reckonings (as well as other site content).
 */
@Controller
public class VoteController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	VoteService voteService;
	
	private static final Logger log = LoggerFactory
			.getLogger(VoteController.class);
	
	/**
	 * This method handles {@link OfferUnavailableException}.
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method allows for the posting of a vote to the answer index of a specified reckoning.
	 * 
	 * @param id
	 *           String
	 * @param answer
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/vote/reckoning/{id}/answer/{answer}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postReckoningComment(@PathVariable String id,
			@PathVariable Integer answer,
			@RequestBody PostVote postVote)
			throws AuthenticationException, Exception {

		if (!StringUtils.hasLength(postVote.getUserToken())) {
			log.warn("Null user token received for postVoting.");
			throw new AuthenticationException();
		} else {
			Message validationMessage = VoteValidator.validateVotePost(postVote.getVote(), id, answer);
			
			if (validationMessage != null) {
				log.warn("Posted vote failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		postVote.getVote().setAnswerIndex(answer);
		return voteService.postReckoningVote(postVote.getVote(), id, answer);
	}
	
	/**
	 * This method allows for retrieving a user's vote on a particular reckoning.
	 * 
	 * @param id
	 *           String
	 * @param reckoningId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/vote/user/{id}/reckoning/{reckoningId}", method = RequestMethod.GET)	
	public @ResponseBody
	ServiceResponse getUserReckoningVote(@PathVariable String id,
			@PathVariable String reckoningId,
			@RequestParam(required = true, value = "user_token") String userToken)
			throws AuthenticationException, Exception {

		Message validationMessage = ReckoningValidator.validateReckoningId(reckoningId);
		
		if (validationMessage != null) {
			log.warn("Rejection request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}	

		return voteService.getUserReckoningVote(id, reckoningId);
	}
	
	/**
	 * This method allows for retrieving a user's voting record across reckonings.
	 * 
	 * @param id
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/vote/user/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ServiceResponse getUserVotingRecord(@PathVariable String id,
			@RequestParam(required = true, value = "user_token") String userToken)
			throws AuthenticationException, Exception {

		return voteService.getUserVotedReckonings(id);
	}

}
