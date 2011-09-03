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
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostOAuthUser;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;
import com.reckonlabs.reckoner.domain.validator.UserValidator;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
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
			log.warn("Null user token received for postReckoning.");
			throw new AuthenticationException();
		} else {
			Message validationMessage = UserValidator.validateOAuthUserPost(postOAuthUser);
			
			if (validationMessage != null) {
				log.warn("Posted authentication failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				UserServiceResponse response = new UserServiceResponse();
				response.setMessage(validationMessage);
				response.setSuccess(false);
				
				return response;
			}
		}
		
		return userService.authenticateOAuthUser(postOAuthUser.getUserToken(), 
				ProviderEnum.valueOf(postOAuthUser.getProvider()), postOAuthUser.getExpires());
	}
	
	/**
	 * This method allows for logging out the user associated with the specified user token.
	 * 
	 * @param userToken
	 *            String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/logout", method = RequestMethod.GET)
	public @ResponseBody
	ServiceResponse logoutUser(		
			@RequestParam(required = true, value = "user_token") String userToken) {	
		
		return userService.logoutUser(userToken);
	}
	
	/**
	 * This method retrieves the user account specific to user associated with the access token.
	 * 
	 * @param userToken
	 *            String
	 * @return serviceResponse
	 *            ServiceResponse
	 * @throws Exception
	 *            exception
	 */
	@RequestMapping(value = "/user/me", method = RequestMethod.GET)
	public @ResponseBody
	ServiceResponse getUserInformation(		
			@RequestParam(required = true, value = "user_token") String userToken) {	
		
		return userService.getUserByToken(userToken);
	}
	
}
