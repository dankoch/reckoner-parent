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

import com.reckonlabs.reckoner.contentservices.service.UserService;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostOAuthUser;
import com.reckonlabs.reckoner.domain.message.PostPermission;
import com.reckonlabs.reckoner.domain.message.PostUser;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.validator.UserValidator;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.user.User;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;

/**
 * This controller is responsible for the web services related to authenticating,
 * creating, and modifying user accounts.
 */
@Controller
public class UserController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	UserService userService;

	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(UserController.class);
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method completes the authentication process for a user who has authenticated via an OAUTH provider.
	 * The OAUTH credentials are accepted, which will be used to pull the user information, determine whether they
	 * already exist, and react accordingly (including the establishing of a user session).
	 * 
	 * @param postOAuthUser
	 *            PostOAuthUser
	 * @return authenticationResponse
	 *            AuthenticationResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/authentication/oauth", method = RequestMethod.POST)
	public @ResponseBody
	UserServiceResponse authenticateOAuth(@RequestBody PostOAuthUser postOAuthUser)
			throws AuthenticationException, Exception {

		// Validate the input.
		if (!StringUtils.hasLength(postOAuthUser.getUserToken())) {
			log.info("Null OAUTH user token provided to authenticate user.");
			throw new AuthenticationException();
		} else {
			Message validationMessage = UserValidator.validateOAuthUserPost(postOAuthUser);
			
			if (validationMessage != null) {
				log.info("Posted authentication failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				UserServiceResponse response = new UserServiceResponse();
				response.setMessage(validationMessage);
				response.setSuccess(false);
				
				return response;
			}
		}
		
		return userService.authenticateOAuthUser(postOAuthUser.getUserToken(), 
				ProviderEnum.valueOf(postOAuthUser.getProvider()), postOAuthUser.getExpires(),
				postOAuthUser.getRefreshToken());
	}
	
	/**
	 * This method allows for logging out the user associated with the specified user session id.
	 * 
	 * @param sessionId
	 *            String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/logout", method = RequestMethod.GET)
	public @ResponseBody
	UserServiceResponse logoutUser(		
			@RequestParam(required = true, value = "session_id") String sessionId) {	
		
		return userService.logoutUser(sessionId);
	}
	
	/**
	 * This method retrieves the user account specific to user associated with the access session id.
	 * 
	 * @param sessionId
	 *            String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/me", method = RequestMethod.GET)
	public @ResponseBody
	UserServiceResponse getUserInformation(		
			@RequestParam(required = true, value = "session_id") String sessionId) {	
		
		return userService.getUserBySessionId(sessionId);
	}
		
	/**
	 * This method retrieves the information associated with the specified user.
	 * 
	 * @param userId
	 *            String
	 * @param sessionId
	 *            String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/id/{userId}", method = RequestMethod.GET)
	public @ResponseBody
	UserServiceResponse getUserInformation(
			@PathVariable String userId,
			@RequestParam(required = true, value = "session_id") String sessionId) 
					throws AuthenticationException {
		
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_PROFILE)) {
			log.info("User with insufficient privileges attempted to get a user's info from their sessionID: ");
			log.info("Session ID: " + sessionId + " User ID: " + userId);
			throw new AuthenticationException();			
		}
		
		return userService.getUserByUserId(userId);
	}
	
	
	/**
	 * Updates the group permissions associated with a user, including whether the
	 * user is active.
	 * 
	 * @param postPermissions
	 *            PostPermission
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/permissions", method = RequestMethod.POST)
	public @ResponseBody
	UserServiceResponse setUserPermissions(@RequestBody PostPermission postPermission)
			throws AuthenticationException, Exception {	
		
		// Validate the input and necessary permissions.
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postPermission.getSessionId(), PermissionEnum.UPDATE_PERMS)) {
			log.info("User with insufficient privileges attempted to change permmissions: ");
			log.info("Session ID: " + postPermission.getSessionId() + " User ID: " + postPermission.getUserId());
			throw new AuthenticationException();			
		}
		else {
			Message validationMessage = UserValidator.validatePermissionPost(postPermission);
			
			if (validationMessage != null) {
				log.info("Posted authentication failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				UserServiceResponse response = new UserServiceResponse();
				response.setMessage(validationMessage);
				response.setSuccess(false);
				
				return response;
			}
		}
		
		return userService.updateUserPermissions(postPermission.getAction(), 
				postPermission.getGroups(), postPermission.getActive(), postPermission.getUserId());
	}
	
	/**
	 * This method updates the the user-maintained pieces of a user account (i.e. the pieces
	 * not directly bound to an OAUTH provider)
	 * 
	 * @param sessionId
	 *            String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/update", method = RequestMethod.POST)
	public @ResponseBody
	UserServiceResponse updateUserInformation(@RequestBody PostUser postUser) 
			throws AuthenticationException, Exception {	
		
		// Validate the input and necessary permissions.
		if (serviceProps.isEnableServiceAuthentication()) {
			UserServiceResponse user = userService.getUserBySessionId(postUser.getSessionId());
			if ((user.getUser() == null) || !(user.getUser().getId().equals(postUser.getUser().getId()))) {
				if (!userService.hasPermission(postUser.getSessionId(), PermissionEnum.UPDATE_PROFILE_INFO)) {
					log.info("User with insufficient privileges attempted to change permmissions: ");
					log.info("Session ID: " + postUser.getSessionId() + " User ID: " + postUser.getUser().getId());
					throw new AuthenticationException();
				}	
			}
		}
		else {
			Message validationMessage = UserValidator.validateUserUpdate(postUser);
			
			if (validationMessage != null) {
				log.info("Posted authentication failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				UserServiceResponse response = new UserServiceResponse();
				response.setMessage(validationMessage);
				response.setSuccess(false);
				
				return response;
			}
		}
		
		return userService.updateUserInformation(postUser.getUser());
	}
}
