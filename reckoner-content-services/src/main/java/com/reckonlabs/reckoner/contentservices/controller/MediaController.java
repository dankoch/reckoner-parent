package com.reckonlabs.reckoner.contentservices.controller;

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

import com.reckonlabs.reckoner.contentservices.service.MediaService;
import com.reckonlabs.reckoner.contentservices.service.UserService;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostMedia;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;
import com.reckonlabs.reckoner.domain.validator.ContentValidator;
import com.reckonlabs.reckoner.domain.validator.MediaValidator;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;

/**
 * This controller is responsible for the web services for posting and reading
 * comments associated with Reckonings (as well as other site content).
 */
@Controller
public class MediaController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	MediaService mediaService;
	
	@Autowired
	UserService userService;
	
	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(MediaController.class);
	
	/**
	 * This method handles {@link OfferUnavailableException}.
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method allows for the attaching of new media to a given reckoning.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/media/reckoning/{id}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postReckoningMedia(@PathVariable String id,
			@RequestBody PostMedia postMedia)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postMedia.getSessionId(), PermissionEnum.UPDATE_ALL_RECKONINGS)) {
			log.info("User with insufficient privileges attempted add media to a Reckoning: ");
			log.info("Session ID: " + postMedia.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = MediaValidator.validateMediaPost(postMedia.getMedia(), id);
			
			if (validationMessage != null) {
				log.info("Posted media failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return mediaService.postReckoningMedia(postMedia.getMedia(), id);
	}
	
	/**
	 * This method allows for the retrieval of a specific Reckoning media item by ID.
	 * 
	 * @param id
	 *           String
	 * @return reckoningServiceList
	 *            ReckoningServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/media/reckoning/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ReckoningServiceList getReckoningMediaByMediaId(@PathVariable String id,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_RECKONING)) {
			log.info("User with insufficient privileges attempted to retrieve reckoning media by id: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		return mediaService.getReckoningMediaById(id);
	}
	
	/**
	 * This method allows for the deletion of media attached to a Reckoning.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/media/reckoning/delete/{mediaId}", method = RequestMethod.GET)	
	public @ResponseBody
	ServiceResponse deleteReckoningMedia(@PathVariable String mediaId,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.UPDATE_ALL_RECKONINGS)) {
			log.info("User with insufficient privileges attempted to delete Reckoning media: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		return mediaService.deleteReckoningMedia(mediaId);
	}
	
	/**
	 * This method allows for the attaching of new media to a given content item.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/media/content/{id}", method = RequestMethod.POST)	
	public @ResponseBody
	ServiceResponse postContentMedia(@PathVariable String id,
			@RequestBody PostMedia postMedia)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postMedia.getSessionId(), PermissionEnum.UPDATE_ALL_CONTENT)) {
			log.info("User with insufficient privileges attempted add media to a Content: ");
			log.info("Session ID: " + postMedia.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = MediaValidator.validateMediaPost(postMedia.getMedia(), id);
			
			if (validationMessage != null) {
				log.info("Posted media failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return mediaService.postContentMedia(postMedia.getMedia(), id);
	}
	
	/**
	 * This method allows for the retrieval of a specific Content media item by ID.
	 * 
	 * @param id
	 *           String
	 * @return contentServiceList
	 *            ContentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/media/content/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ContentServiceList getContentMediaByMediaId(@PathVariable String id,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_CONTENT)) {
			log.info("User with insufficient privileges attempted to retrieve content media by id: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		return mediaService.getContentMediaById(id);
	}
	
	/**
	 * This method allows for the deletion of media attached to a Content.
	 * 
	 * @param id
	 *           String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/media/content/delete/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ServiceResponse getUserCommentsById(@PathVariable String mediaId,
			@RequestParam(required = false, value = "session_id") String sessionId)
			throws AuthenticationException, Exception {

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.UPDATE_ALL_CONTENT)) {
			log.info("User with insufficient privileges attempted to delete Content media: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();			
		} 
		
		return mediaService.deleteContentMedia(mediaId);
	}
}
