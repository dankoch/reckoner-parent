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

import com.reckonlabs.reckoner.contentservices.service.NotesService;
import com.reckonlabs.reckoner.contentservices.service.UserService;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostFavorite;
import com.reckonlabs.reckoner.domain.message.PostFlag;
import com.reckonlabs.reckoner.domain.message.CommentServiceList;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;
import com.reckonlabs.reckoner.domain.validator.CommentValidator;
import com.reckonlabs.reckoner.domain.validator.NotesValidator;

/**
 * This controller is responsible for the web services for posting and reading
 * notes attached to content (including favorites and flags).
 */
@Controller
public class NotesController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	NotesService notesService;
	
	@Autowired
	UserService userService;
	
	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(NotesController.class);
	
	/**
	 * This method handles {@link OfferUnavailableException}.
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method allows for the posting of a favorite to a reckoning.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/favorite/{id}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postReckoningFavorite(@PathVariable String id,
			@RequestBody PostFavorite postFavorite)
			throws AuthenticationException, Exception {
		
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postFavorite.getSessionId(), PermissionEnum.FAVORITE)) {
			log.info("User with insufficient privileges attempted to favorite a reckoning: ");
			log.info("Session ID: " + postFavorite.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = NotesValidator.validateFavoritePost(postFavorite.getFavorite(), id);
			
			if (validationMessage != null) {
				log.info("Posted reckoning favorite failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return notesService.postReckoningFavorite(postFavorite.getFavorite(), id, postFavorite.getSessionId());
	}
	
	/**
	 * This method allows for the posting of a flag to a reckoning.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/flag/{id}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postReckoningFlag(@PathVariable String id,
			@RequestBody PostFlag postFlag)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postFlag.getSessionId(), PermissionEnum.FLAG)) {
			log.info("User with insufficient privileges attempted to flag a reckoning: ");
			log.info("Session ID: " + postFlag.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = NotesValidator.validateFlagPost(postFlag.getFlag(), id);
			
			if (validationMessage != null) {
				log.info("Posted reckoning flag failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return notesService.postReckoningFlag(postFlag.getFlag(), id, postFlag.getSessionId());
	}
	
	/**
	 * This method allows for the posting of a favorite to a reckoning comment.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/comment/favorite/{id}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postReckoningCommentFavorite(@PathVariable String id,
			@RequestBody PostFavorite postFavorite)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postFavorite.getSessionId(), PermissionEnum.FAVORITE)) {
			log.info("User with insufficient privileges attempted to favorite a reckoning comment: ");
			log.info("Session ID: " + postFavorite.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = NotesValidator.validateFavoritePost(postFavorite.getFavorite(), id);
			
			if (validationMessage != null) {
				log.info("Posted reckoning comment favorite failed validation: " + validationMessage.getCode() 
						+ ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return notesService.postReckoningCommentFavorite(postFavorite.getFavorite(), id, postFavorite.getSessionId());
	}

	
	/**
	 * This method allows for the posting of a flag to a reckoning comment.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/comment/flag/{id}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postReckoningCommentFlag(@PathVariable String id,
			@RequestBody PostFlag postFlag)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postFlag.getSessionId(), PermissionEnum.FLAG)) {
			log.info("User with insufficient privileges attempted to flag a reckoning comment: ");
			log.info("Session ID: " + postFlag.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = NotesValidator.validateFlagPost(postFlag.getFlag(), id);
			
			if (validationMessage != null) {
				log.info("Posted reckoning comment flag failed validation: " + validationMessage.getCode() 
						+ ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return notesService.postReckoningCommentFlag(postFlag.getFlag(), id, postFlag.getSessionId());
	}

	/**
	 * This method allows for the retrieval of all favorited reckonings according to the parameters specified.
	 * 
	 * @param userId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/favorite", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getFavoritedReckonings(
			@RequestParam(required = false, value = "flagged_after") Date flaggedAfter,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.APPROVAL)) {
			log.info("User with insufficient privileges attempted to retrieve flagged reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = NotesValidator.validateUserFavoriteQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Flagged reckoning request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return notesService.getFavoritedReckonings(flaggedAfter, page, size, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of all flagged reckonings according to the parameters specified.
	 * 
	 * @param userId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/flag", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getFlaggedReckonings(
			@RequestParam(required = false, value = "flagged_after") Date flaggedAfter,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.APPROVAL)) {
			log.info("User with insufficient privileges attempted to retrieve flagged reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = NotesValidator.validateUserFavoriteQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Flagged reckoning request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return notesService.getFlaggedReckonings(flaggedAfter, page, size, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of all favorited reckoning comments according to the parameters specified.
	 * 
	 * @param userId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/comment/favorite", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getFavoritedReckoningComments(
			@RequestParam(required = false, value = "favorited_after") Date flaggedAfter,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.APPROVAL)) {
			log.info("User with insufficient privileges attempted to retrieve flagged reckoning comments: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = NotesValidator.validateUserFavoriteQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Flagged reckoning comment request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return notesService.getFavoritedReckoningComments(flaggedAfter, page, size, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of all flagged reckoning comments according to the parameters specified.
	 * 
	 * @param userId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/comment/flag", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getFlaggedReckoningComments(
			@RequestParam(required = false, value = "flagged_after") Date flaggedAfter,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.APPROVAL)) {
			log.info("User with insufficient privileges attempted to retrieve flagged reckoning comments: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = NotesValidator.validateUserFavoriteQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Flagged reckoning request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return notesService.getFlaggedReckoningComments(flaggedAfter, page, size, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of all reckonings favorited by a specified user.
	 * Paging is by the number of reckonings on which the user has commented.
	 * 
	 * @param userId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/favorite/user/{userId}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getFavoritedReckoningsByUserId(@PathVariable String userId,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_PROFILE)) {
			log.info("User with insufficient privileges attempted to retrieve a user's favorite reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = NotesValidator.validateUserFavoriteQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Favorited reckonings by user request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return notesService.getFavoritedReckoningsByUser(userId, page, size, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of all reckonings favorited by a specified user.
	 * Paging is by the number of reckonings on which the user has commented.
	 * 
	 * @param userId
	 *           String
	 * @return commentServiceList
	 *            CommentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/notes/reckoning/comment/favorite/user/{userId}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getFavoritedReckoningCommentsByUserId(@PathVariable String userId,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_PROFILE)) {
			log.info("User with insufficient privileges attempted to retrieve a user's favorite reckoning comments: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		Message validationMessage = NotesValidator.validateUserFavoriteQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Favorited reckonings by user request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return notesService.getFavoritedCommentsByUser(userId, page, size, sessionId);
	}
}
