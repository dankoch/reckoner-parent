package com.reckonlabs.reckoner.contentservices.controller;

import java.lang.Boolean;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

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
import com.reckonlabs.reckoner.contentservices.service.UserService;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostComment;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;
import com.reckonlabs.reckoner.domain.validator.CommentValidator;

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
	
	@Autowired
	UserService userService;
	
	@Resource
	ServiceProps serviceProps;
	
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
	 * This method allows for the posting of a comment associated with a given reckoning.
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

		if (serviceProps.isEnableServiceAuthentication() && 
				serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postComment.getSessionId(), PermissionEnum.COMMENT)) {
			log.info("User with insufficient privileges attempted to post a comment: ");
			log.info("Session ID: " + postComment.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = CommentValidator.validateCommentPost(postComment.getComment(), id);
			
			if (validationMessage != null) {
				log.info("Posted comment failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return commentService.postReckoningComment(postComment.getComment(), postComment.getSessionId(), id);
	}
	
	/**
	 * This method allows for the retrieval of a comment with a specific ID.
	 * 
	 * @param id
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/comments/id/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getCommentById(@PathVariable String id,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to retrieve a comment: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = CommentValidator.validateCommentQuery(id);
		
		if (validationMessage != null) {
			log.info("Retrieve comment request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return commentService.getComment(id, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of all comments made by a specified user (including associated reckoning summaries).
	 * Paging is by the number of reckonings on which the user has commented.
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
	ReckoningServiceList getUserCommentsById(@PathVariable String userId,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_PROFILE)) {
			log.info("User with insufficient privileges attempted to retrieve a user's comment list: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = CommentValidator.validateUserCommentQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Comments by user request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return commentService.getCommentsByUser(userId, page, size, sessionId);
	}
	
}
