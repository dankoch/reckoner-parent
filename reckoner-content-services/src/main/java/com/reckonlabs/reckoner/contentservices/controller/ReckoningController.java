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

import com.reckonlabs.reckoner.contentservices.service.ReckoningService;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostReckoning;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;
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
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningController.class);
	
	/**
	 * This method handles {@link OfferUnavailableException}.
	 */
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

		if (!StringUtils.hasLength(postReckoning.getUserToken())) {
			log.warn("Null user token received for postReckoning.");
			throw new AuthenticationException();
		} else {
			Message validationMessage = ReckoningValidator.validateReckoningPost(postReckoning.getReckoning());
			
			if (validationMessage != null) {
				log.warn("Posted reckoning failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return reckoningService.postReckoning(postReckoning.getReckoning(), postReckoning.getUserToken());
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
	@RequestMapping(value = "/reckoning/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningById(
			@PathVariable String id,		
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getReckoning(id, userToken);
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
	 * @param userToken
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
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		Message validationMessage = ReckoningValidator.validateReckoningQuery (page, size);
		
		if (validationMessage != null) {
			log.warn("Approval queue request failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ReckoningServiceList(null, validationMessage, false);
		}
		
		return reckoningService.getApprovalQueue(page, size, latestFirst, userToken);
	}
	
	/**
	 * This method allows for the retrieval of reckonings related to a specific user.
	 * 
	 * @param submitter_id
	 *           String
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer
	 * @param userToken
	 *           String  
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/user/{submitterId}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningsByUser(
			@PathVariable String submitterId,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getReckoningsByUser (submitterId, page, size, userToken);
	}
	
	/**
	 * This method allows for the retrieval of reckoning summaries related to a specific user.
	 * 
	 * @param submitter_id
	 *           String
	 * @param userToken
	 *           String  
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/user/{submitterId}/summary", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningSummariesByUser(
			@PathVariable String submitterId,
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getReckoningSummariesByUser (submitterId, userToken);
	}
	
	/**
	 * This method allows for the retrieval of open reckonings related to a specific user.
	 * 
	 * @param submitter_id
	 *           String
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer     
	 * @param userToken
	 *           String        
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/user/{submitterId}/open", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getOpenReckoningsByUser(
			@PathVariable String submitterId,	
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getOpenReckoningsByUser (submitterId, page, size, userToken);
	}
	
	/**
	 * This method allows for the retrieval of closed reckonings related to a specific user.
	 * 
	 * @param submitter_id
	 *           String
	 * @param page
	 *           Integer
	 * @param size
	 *           Integer     
	 * @param userToken
	 *           String         
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/user/{submitterId}/closed", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getClosedReckoningsByUser(
			@PathVariable String submitterId,	
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getClosedReckoningsByUser (submitterId, page, size, userToken);
	}
	
	/**
	 * This method allows for the retrieval of reckonings related to a specific user awaiting approval.
	 * 
	 * @param submitter_id
	 *           String       
	 * @param userToken
	 *           String        
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/user/{submitterId}/approvalqueue", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getApprovalQueueReckoningsByUser(
			@PathVariable String submitterId,	
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getApprovalQueueByUser(submitterId, userToken);
	}

	/**
	 * This method allows for the retrieval of the most recent open highlighted reckoning.
	 * 
	 * @param userToken
	 *           String          
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/highlighted", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getHighlightedReckonings(
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getHighlightedReckonings(null, userToken);
	}
	
	/**
	 * This method allows for the retrieval of the most recent open highlighted reckoning.
	 * 
	 * @param userToken
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/highlighted/open", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getHighlightedOpenReckoning(
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getHighlightedReckonings(true, userToken);
	}
	
	/**
	 * This method allows for the retrieval of the most recent closed highlighted reckoning.
	 * 
	 * @param userToken
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/highlighted/closed", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getHighlightedClosedReckoning(
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getHighlightedReckonings(false, userToken);
	}
	
	/**
	 * This method allows for the general retrieval of reckoning summaries.
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
	 * @param userToken
	 *           String   
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getOpenReckonings(
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "posted_after") Date postedAfter,
			@RequestParam(required = false, value = "posted_before") Date postedBefore,
			@RequestParam(required = false, value = "closed_after") Date closedAfter,
			@RequestParam(required = false, value = "closed_before") Date closedBefore,
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getReckoningSummaries(page, size, postedAfter, postedBefore,
				closedAfter, closedBefore, userToken);
	}
	
	/**
	 * This method allows for the retrieval of reckonings related to a specific tag.
	 * 
	 * @param tag
	 *           String
	 * @param userToken
	 *           String  
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning/tag/{tag}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningsByTag(
			@PathVariable String tag,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.getReckoningsByTag(tag, page, size, userToken);
	}
}
