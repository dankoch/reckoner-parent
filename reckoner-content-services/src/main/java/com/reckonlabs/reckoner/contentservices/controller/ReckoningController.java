package com.reckonlabs.reckoner.contentservices.controller;

import java.lang.Boolean;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

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

import com.reckonlabs.reckoner.contentservices.service.ReckoningService;
import com.reckonlabs.reckoner.contentservices.service.UserService;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.MessageEnum;
import com.reckonlabs.reckoner.domain.message.PostReckoning;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.reckoning.Answer;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningTypeEnum;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;

/**
 * This controller is responsible for the web services for posting and reading
 * the reckonings themselves (along with associated information)
 */
@Controller
public class ReckoningController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	ReckoningService reckoningService;
	
	@Autowired
	UserService userService;
	
	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningController.class);
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method allows for the posting of a Reckoning to the content.
	 * 
	 * @param postReckoning
	 *            PostReckoning
	 * @return postReckoningResponse
	 *            PostReckoningResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */
	@RequestMapping(value = "/reckoning", method = RequestMethod.POST)
	public @ResponseBody
	ServiceResponse postReckoning(@RequestBody PostReckoning postReckoning)
			throws AuthenticationException, Exception {

		// Validate the input.
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postReckoning.getSessionId(), PermissionEnum.POST_RECKONING)) {
			log.info("User with insufficient privileges attempted to post a reckoning: ");
			log.info("Session ID: " + postReckoning.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = ReckoningValidator.validateReckoningPost(postReckoning.getReckoning());
			
			if (validationMessage != null) {
				log.info("Posted reckoning failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return reckoningService.postReckoning(postReckoning.getReckoning(), postReckoning.getSessionId());
	}
	
	/**
	 * This method allows for updates to existing Reckoning content.
	 * 
	 * @param postReckoning
	 *            PostReckoning
	 * @return postReckoningResponse
	 *            PostReckoningResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */
	@RequestMapping(value = "/reckoning/update", method = RequestMethod.POST)
	public @ResponseBody
	ServiceResponse updateReckoning(@RequestBody PostReckoning updateReckoning,
			@RequestParam(required = false, value = "merge") Boolean merge)
			throws AuthenticationException, Exception {
		
		if (merge == null) { merge = true; }
		
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(updateReckoning.getSessionId(), PermissionEnum.UPDATE_ALL_RECKONINGS)) {
			log.info("User with insufficient privileges attempted to approve a reckoning: ");
			log.info("Session ID: " + updateReckoning.getSessionId() + " Reckoning " + updateReckoning.getReckoning().getId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = ReckoningValidator.validateReckoningUpdate(updateReckoning.getReckoning(), merge);
			
			if (validationMessage != null) {
				log.info("Updated reckoning failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return reckoningService.updateReckoning(updateReckoning.getReckoning(), merge, updateReckoning.getSessionId());
	}
	
	/**
	 * This method allows for accepting an unaccepted reckoning.
	 * 
	 * @param postReckoning
	 *            PostReckoning
	 * @return postReckoningResponse
	 *            PostReckoningResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */
	@RequestMapping(value = "/reckoning/approve/{id}", method = RequestMethod.GET)
	public @ResponseBody
	ServiceResponse approveReckoningById(
			@PathVariable String id,		
			@RequestParam(required = false, value = "session_id") String sessionId) 
				throws AuthenticationException {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.APPROVAL)) {
			log.info("User with insufficient privileges attempted to approve a reckoning: ");
			log.info("Session ID: " + sessionId + " Reckoning " + id);
			throw new AuthenticationException();			
		}
		
		Message validationMessage = ReckoningValidator.validateReckoningId(id);
		
		if (validationMessage != null) {
			log.info("Approval request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}		
		
		return reckoningService.approveReckoning(id, sessionId);
	}
	
	/**
	 * This method allows for rejecting an unaccepted reckoning.
	 * 
	 * @param postReckoning
	 *            PostReckoning
	 * @return postReckoningResponse
	 *            PostReckoningResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */
	@RequestMapping(value = "/reckoning/reject/{id}", method = RequestMethod.GET)
	public @ResponseBody
	ServiceResponse rejectReckoningById(
			@PathVariable String id,		
			@RequestParam(required = false, value = "session_id") String sessionId) 
					throws AuthenticationException {
		
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.APPROVAL)) {
			log.info("User with insufficient privileges attempted to reject a reckoning: ");
			log.info("Session ID: " + sessionId + " Reckoning " + id);
			throw new AuthenticationException();			
		}
		
		Message validationMessage = ReckoningValidator.validateReckoningId(id);
		
		if (validationMessage != null) {
			log.info("Rejection request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}	

		return reckoningService.rejectReckoning(id, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of a specific Reckoning by ID.
	 * 
	 * @param id
	 *           String
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/id/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningById(
			@PathVariable String id,		
			@RequestParam(required = false, value = "session_id") String sessionId,
			@RequestParam(required = false, value = "include_unaccepted") Boolean includeUnaccepted,
			@RequestParam(required = false, value = "page_visit") Boolean pageVisit) 
					throws AuthenticationException {

			if (includeUnaccepted == null) { includeUnaccepted = false; }
			if (pageVisit == null) { pageVisit = false; }

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view a reckoning: ");
			log.info("Session ID: " + sessionId + " Reckoning " + id);
			throw new AuthenticationException();			
		}
		
		return reckoningService.getReckoning(id, includeUnaccepted, pageVisit, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of unapproved Reckonings.
	 * 
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param latestFirst
	 *           Boolean
	 * @param sessionId
	 *           String 
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/approvalqueue", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningsAwaitingApproval(
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "latest_first") Boolean latestFirst,			
			@RequestParam(required = false, value = "session_id") String sessionId) 
					throws AuthenticationException {
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.APPROVAL)) {
			log.info("User with insufficient privileges attempted to view approval queue: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		}
		
		Message validationMessage = ReckoningValidator.validateReckoningQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Approval queue request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getApprovalQueue(page, size, latestFirst, sessionId);
	}
	
	/**
	 * This method allows for the retrieval of reckoning summaries related to a specific user.
	 * 
	 * @param submitter_id
	 *           String
	 * @param sessionId
	 *           String  
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/user/{submitterId}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningSummariesByUser(
			@PathVariable String submitterId,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "session_id") String sessionId) 
				throws AuthenticationException {
		
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_PROFILE)) {
			log.info("User with insufficient privileges attempted to retrieve profile information: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		}		
		
		Message validationMessage = ReckoningValidator.validateReckoningQuery (page, size);
		
		if (validationMessage != null) {
			log.info("Open reckoning by user request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}	
		
		return reckoningService.getReckoningSummariesByUser (submitterId, page, size, sessionId);
	}
	
	/**
	 * This method allows for the general retrieval of open reckoning summaries.
	 * 
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param postedAfter
	 *           Date
	 * @param postedBefore
	 *           Date    
	 * @param closedAfter
	 *           Date
	 * @param closedBefore
	 *           Date 
	 * @param sessionId
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckonings(
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "posted_after") Date postedAfter,
			@RequestParam(required = false, value = "posted_before") Date postedBefore,
			@RequestParam(required = false, value = "closed_after") Date closedAfter,
			@RequestParam(required = false, value = "closed_before") Date closedBefore,
			@RequestParam(required = false, value = "include_tags") String includeTagsString,
			@RequestParam(required = false, value = "exclude_tags") String excludeTagsString,
			@RequestParam(required = false, value = "highlighted") Boolean highlighted,
			@RequestParam(required = false, value = "submitted_by") String submittedBy,			
			@RequestParam(required = false, value = "sort_by") String sortBy,
			@RequestParam(required = false, value = "ascending") Boolean ascending,
			@RequestParam(required = false, value = "randomize") Boolean randomize,
			@RequestParam(required = false, value = "session_id") String sessionId)
				throws AuthenticationException {
		
		List<String> includeTagsList = convertList(includeTagsString);
		List<String> excludeTagsList = convertList(excludeTagsString);
		
		Message validationMessage = ReckoningValidator.validateReckoningQuery(ReckoningTypeEnum.OPEN_AND_CLOSED, 
				postedAfter, postedBefore, closedAfter, closedBefore, includeTagsList, excludeTagsList, sortBy, ascending, page, size, randomize);
	
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view a list of reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();
		}	
	
		if (validationMessage != null) {
			log.info("Reckoning query failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getReckoningSummaries(ReckoningTypeEnum.OPEN_AND_CLOSED, postedAfter, postedBefore,
				closedAfter, closedBefore, includeTagsList, excludeTagsList, highlighted, submittedBy, ApprovalStatusEnum.APPROVED,
				sortBy, ascending, page, size, randomize, sessionId);
	}
	
	/**
	 * This method allows for the general retrieval of open reckoning summaries.
	 * 
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param postedAfter
	 *           Date
	 * @param postedBefore
	 *           Date    
	 * @param closedAfter
	 *           Date
	 * @param closedBefore
	 *           Date 
	 * @param sessionId
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/open", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getOpenReckonings(
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "posted_after") Date postedAfter,
			@RequestParam(required = false, value = "posted_before") Date postedBefore,
			@RequestParam(required = false, value = "closed_after") Date closedAfter,
			@RequestParam(required = false, value = "closed_before") Date closedBefore,
			@RequestParam(required = false, value = "include_tags") String includeTagsString,
			@RequestParam(required = false, value = "exclude_tags") String excludeTagsString,
			@RequestParam(required = false, value = "highlighted") Boolean highlighted,
			@RequestParam(required = false, value = "submitted_by") String submittedBy,		
			@RequestParam(required = false, value = "sort_by") String sortBy,
			@RequestParam(required = false, value = "ascending") Boolean ascending,
			@RequestParam(required = false, value = "randomize") Boolean randomize,			
			@RequestParam(required = false, value = "session_id") String sessionId) 
				throws AuthenticationException {
		
		List<String> includeTagsList = convertList(includeTagsString);
		List<String> excludeTagsList = convertList(excludeTagsString);
		
		Message validationMessage = ReckoningValidator.validateReckoningQuery(ReckoningTypeEnum.OPEN, 
				postedAfter, postedBefore, closedAfter, closedBefore, includeTagsList, excludeTagsList, sortBy, ascending, page, size, randomize);
	
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view a list of reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();
		}	
	
		if (validationMessage != null) {
			log.info("Reckoning query failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getReckoningSummaries(ReckoningTypeEnum.OPEN, postedAfter, postedBefore,
				closedAfter, closedBefore, includeTagsList, excludeTagsList, highlighted, submittedBy, ApprovalStatusEnum.APPROVED,
				sortBy, ascending, page, size, randomize, sessionId);
	}
	
	/**
	 * This method allows for the general retrieval of closed reckoning summaries.
	 * 
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param postedAfter
	 *           Date
	 * @param postedBefore
	 *           Date    
	 * @param closedAfter
	 *           Date
	 * @param closedBefore
	 *           Date 
	 * @param sessionId
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/closed", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getClosedReckonings(
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "posted_after") Date postedAfter,
			@RequestParam(required = false, value = "posted_before") Date postedBefore,
			@RequestParam(required = false, value = "closed_after") Date closedAfter,
			@RequestParam(required = false, value = "closed_before") Date closedBefore,
			@RequestParam(required = false, value = "include_tags") String includeTagsString,
			@RequestParam(required = false, value = "exclude_tags") String excludeTagsString,
			@RequestParam(required = false, value = "highlighted") Boolean highlighted,
			@RequestParam(required = false, value = "submitted_by") String submittedBy,		
			@RequestParam(required = false, value = "sort_by") String sortBy,
			@RequestParam(required = false, value = "ascending") Boolean ascending,
			@RequestParam(required = false, value = "randomize") Boolean randomize,			
			@RequestParam(required = false, value = "session_id") String sessionId) 
				throws AuthenticationException {
		
		List<String> includeTagsList = convertList(includeTagsString);
		List<String> excludeTagsList = convertList(excludeTagsString);
		
		Message validationMessage = ReckoningValidator.validateReckoningQuery(ReckoningTypeEnum.CLOSED, 
				postedAfter, postedBefore, closedAfter, closedBefore, includeTagsList, excludeTagsList, sortBy, ascending, page, size, randomize);
	
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view a list of reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();
		}	
	
		if (validationMessage != null) {
			log.info("Reckoning query failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getReckoningSummaries(ReckoningTypeEnum.CLOSED, postedAfter, postedBefore,
				closedAfter, closedBefore, includeTagsList, excludeTagsList, highlighted, submittedBy, ApprovalStatusEnum.APPROVED,
				sortBy, ascending, page, size, randomize, sessionId);
	}
	
	/**
	 * This method allows for the retrieval or reckonings related to the specified reckoning
	 * 
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param postedAfter
	 *           Date
	 * @param postedBefore
	 *           Date    
	 * @param closedAfter
	 *           Date
	 * @param closedBefore
	 *           Date 
	 * @param sessionId
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/related/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getRelatedReckonings(
			@PathVariable String id,
			@RequestParam(required = false, value = "size") Integer size,		
			@RequestParam(required = false, value = "session_id") String sessionId) 
				throws AuthenticationException {
		
		Message validationMessage = ReckoningValidator.validateReckoningId(id);
		
		// Set default size for requests
		if (size == null) { size = 4; }
	
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view related reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();
		}	
	
		if (validationMessage != null) {
			log.info("Related reckoning query failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getRelatedReckoningSummaries(ReckoningTypeEnum.OPEN_AND_CLOSED, id, size);
	}
	
	/**
	 * This method allows for the retrieval or reckonings related to the specified reckoning
	 * 
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param postedAfter
	 *           Date
	 * @param postedBefore
	 *           Date    
	 * @param closedAfter
	 *           Date
	 * @param closedBefore
	 *           Date 
	 * @param sessionId
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/related/{id}/open", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getRelatedOpenReckonings(
			@PathVariable String id,
			@RequestParam(required = false, value = "size") Integer size,		
			@RequestParam(required = false, value = "session_id") String sessionId) 
				throws AuthenticationException {
		
		Message validationMessage = ReckoningValidator.validateReckoningId(id);
		
		// Set default size for requests
		if (size == null) { size = 4; }
	
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view related reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();
		}	
	
		if (validationMessage != null) {
			log.info("Related reckoning query failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getRelatedReckoningSummaries(ReckoningTypeEnum.OPEN, id, size);
	}
	
	/**
	 * This method allows for the retrieval or reckonings related to the specified reckoning
	 * 
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param postedAfter
	 *           Date
	 * @param postedBefore
	 *           Date    
	 * @param closedAfter
	 *           Date
	 * @param closedBefore
	 *           Date 
	 * @param sessionId
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/related/{id}/closed", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getRelatedClosedReckonings(
			@PathVariable String id,
			@RequestParam(required = false, value = "size") Integer size,		
			@RequestParam(required = false, value = "session_id") String sessionId) 
				throws AuthenticationException {
		
		Message validationMessage = ReckoningValidator.validateReckoningId(id);
		
		// Set default size for requests
		if (size == null) { size = 4; }
	
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view related reckonings: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();
		}	
	
		if (validationMessage != null) {
			log.info("Related reckoning query failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getRelatedReckoningSummaries(ReckoningTypeEnum.CLOSED, id, size);
	}
	
	/**
	 * This method allows for the retrieval of a random Reckoning.
	 * 
	 * @param id
	 *           String
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/random", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getRandomReckoning(	
			@RequestParam(required = false, value = "session_id") String sessionId) 
					throws AuthenticationException {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view a random reckoning: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		}
		
		return reckoningService.getRandomReckoning(ReckoningTypeEnum.OPEN_AND_CLOSED);
	}
	
	/**
	 * This method allows for the retrieval of a random closed Reckoning.
	 * 
	 * @param id
	 *           String
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/random/closed", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getRandomClosedReckoning(	
			@RequestParam(required = false, value = "session_id") String sessionId) 
					throws AuthenticationException {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view a random closed reckoning: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		}
		
		return reckoningService.getRandomReckoning(ReckoningTypeEnum.CLOSED);
	}
	
	/**
	 * This method allows for the retrieval of a random open Reckoning.
	 * 
	 * @param id
	 *           String
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/random/open", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getRandomOpenReckoning(	
			@RequestParam(required = false, value = "session_id") String sessionId) 
					throws AuthenticationException {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to view a random open reckoning: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		}
		
		return reckoningService.getRandomReckoning(ReckoningTypeEnum.OPEN);
	}
	
	private static List<String> convertList(String source) {
		List<String> returnList = null;
		
		if (source != null) {
			try {
				returnList = Arrays.asList((URLDecoder.decode(source, "UTF-8")).split(","));
			} catch (Exception e) {
				log.error("Error when extracting UTF string from URL: ", e);
			}
		}
		
		return returnList;
	}
}
