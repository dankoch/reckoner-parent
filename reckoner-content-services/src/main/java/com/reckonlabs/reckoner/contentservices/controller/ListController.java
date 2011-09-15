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

import com.reckonlabs.reckoner.contentservices.service.ListService;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.DataServiceList;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.message.UserServiceResponse;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.validator.ReckoningValidator;
import com.reckonlabs.reckoner.domain.validator.UserValidator;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.user.ProviderEnum;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;

/**
 * This controller is responsible for the web services related to authenticating,
 * creating, and modifying user accounts.
 */
@Controller
public class ListController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	ListService listService;
	
	private static final Logger log = LoggerFactory
			.getLogger(ListController.class);
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method retrieves the groups to which a user can belong.
	 */
	@RequestMapping(value = "/list/user/groups", method = RequestMethod.GET)
	public @ResponseBody
	DataServiceList getGroups() {	
		return listService.getValidGroups();
	}
	
	/**
	 * This method retrieves the permissions a user can have.
	 */
	@RequestMapping(value = "/list/user/permissions", method = RequestMethod.GET)
	public @ResponseBody
	DataServiceList getPermissions() {	
		return listService.getValidPermissions();
	}
	
	/**
	 * This method retrieves the permissions a user can have.
	 */
	@RequestMapping(value = "/list/user/providers", method = RequestMethod.GET)
	public @ResponseBody
	DataServiceList getProviders() {	
		return listService.getValidProviders();
	}
}
