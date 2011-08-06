package com.reckonlabs.reckoner.contentservices.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.ExceptionHandler;
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

/**
 * This controller is responsible for the web services for posting and reading
 * the reckonings themselves (along with associated information)
 */
@Controller
public class ReckoningController {
	
	@Resource
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
	 * This method allows for the retrieval of Reckoning content.
	 * 
	 * @param postReckoning
	 *            PostReckoning
	 * @return postReckoningResponse
	 *            PostReckoningResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */	
	@RequestMapping(value = "/reckoning", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoning(
			@RequestParam(required = true, value = "page") Long page,
			@RequestParam(required = true, value = "size") Long size,
			@RequestParam(required = false, value = "approved") boolean approved,
			@RequestParam(required = false, value = "rejected") boolean rejected,
			@RequestParam(required = false, value = "id") String id,
			@RequestParam(required = false, value = "submitter_id") String submitterId,
			@RequestParam(required = false, value = "posted_after_date") Date postedAfter,
			@RequestParam(required = false, value = "posted_before_date") Date postedBefore,
			@RequestParam(required = false, value = "closed_after_date") Date closedAfter,
			@RequestParam(required = false, value = "closed_before_date") Date closedBefore,
			@RequestParam(required = false, value = "tag") String tag,		
			@RequestParam(required = false, value = "sort_by") String sortBy,	
			@RequestParam(required = false, value = "filter") String filter,
			@RequestParam(required = true, value = "user_token") String userToken) {
		
		return reckoningService.queryReckoning(page, size, approved, rejected, id, submitterId, postedAfter, 
				postedBefore, closedAfter, closedBefore, tag, sortBy, filter, userToken);
	}
}
