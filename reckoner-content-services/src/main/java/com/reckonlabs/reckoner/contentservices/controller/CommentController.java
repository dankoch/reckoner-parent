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

import com.reckonlabs.reckoner.contentservices.service.CommentService;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostComment;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;
import com.reckonlabs.reckoner.domain.validator.CommentValidator;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;

/**
 * This controller is responsible for the web services for posting and reading
 * comments associated with Reckonings (as well as other site content).
 */
@Controller
public class CommentController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	CommentService commentService;
	
	private static final Logger log = LoggerFactory
			.getLogger(CommentController.class);
	
	/**
	 * This method handles {@link OfferUnavailableException}.
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method allows for the retrieval of all comments attached to a given piece of content.
	 * 
	 * @param id
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/comments/reckoning/{id}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postReckoningComment(@PathVariable String id,
			@RequestBody PostComment postComment)
			throws AuthenticationException, Exception {

		if (!StringUtils.hasLength(postComment.getUserToken())) {
			log.warn("Null user token received for postComment.");
			throw new AuthenticationException();
		} else {
			Message validationMessage = CommentValidator.validateCommentPost(postComment.getComment(), id);
			
			if (validationMessage != null) {
				log.warn("Posted reckoning failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return commentService.postReckoningComment(postComment.getComment(), postComment.getUserToken(), id);
	}
	
	/**
	 * This method allows for the retrieval of all comments made by a specified user.
	 * 
	 * @param userId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/comments/user/{userId}", method = RequestMethod.GET)	
	public @ResponseBody
	CommentServiceList getUserCommentsById(@PathVariable String userId,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = true, value = "user_token") String userToken)
			throws AuthenticationException, Exception {

		Message validationMessage = CommentValidator.validateCommentQuery (page, size);
		
		if (validationMessage != null) {
			log.warn("Comments by user request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new CommentServiceList(null, validationMessage, false);
		}
		
		return commentService.getCommentsByUser (userId, page, size, userToken);
	}
	
	/**
	 * This method allows for the retrieval of all reckonings commented on by a given user (summarized).
	 * 
	 * @param userId
	 *           String
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/comments/user/{userId}/reckonings", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getCommentedReckoningsById(@PathVariable String userId,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = true, value = "user_token") String userToken)
			throws AuthenticationException, Exception {

		Message validationMessage = CommentValidator.validateCommentQuery (page, size);
		
		if (validationMessage != null) {
			log.warn("Commented reckonings by user request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return commentService.getCommentedReckoningsByUser (userId, page, size, userToken);
	}

}
