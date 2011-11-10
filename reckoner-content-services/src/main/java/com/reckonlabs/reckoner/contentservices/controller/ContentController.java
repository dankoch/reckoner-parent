package com.reckonlabs.reckoner.contentservices.controller;

import java.lang.Boolean;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

import com.reckonlabs.reckoner.contentservices.service.ContentService;
import com.reckonlabs.reckoner.contentservices.service.UserService;
import com.reckonlabs.reckoner.contentservices.utility.ServiceProps;
import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;
import com.reckonlabs.reckoner.domain.message.ContentServiceList;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.PostContent;
import com.reckonlabs.reckoner.domain.message.ServiceResponse;
import com.reckonlabs.reckoner.domain.security.AuthenticationException;
import com.reckonlabs.reckoner.domain.validator.ContentValidator;
import com.reckonlabs.reckoner.domain.user.PermissionEnum;
import com.reckonlabs.reckoner.domain.utility.DateFormatAdapter;

/**
 * This controller is responsible for the web services for posting and reading
 * the reckonings themselves (along with associated information)
 */
@Controller
public class ContentController {
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatAdapter.DATE_PATTERN);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
	@Autowired
	ContentService contentService;
	
	@Autowired
	UserService userService;
	
	@Resource
	ServiceProps serviceProps;
	
	private static final Logger log = LoggerFactory
			.getLogger(ContentController.class);
	
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public void handleAuthenticationException() {
	}
	
	/**
	 * This method allows for the posting of content (i.e. blog posts, podcasts, video posts)
	 * 
	 * @param postContent
	 *            PostContent
	 * @return postContentResponse
	 *            PostContentResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */
	@RequestMapping(value = "/content", method = RequestMethod.POST)
	public @ResponseBody
	ServiceResponse postContent(@RequestBody PostContent postContent)
			throws AuthenticationException, Exception {

		// Validate the input.
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(postContent.getSessionId(), PermissionEnum.POST_CONTENT)) {
			log.info("User with insufficient privileges attempted to post content: ");
			log.info("Session ID: " + postContent.getSessionId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = ContentValidator.validateContentPost(postContent.getContent());
			
			if (validationMessage != null) {
				log.info("Posted content failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return contentService.postContent(postContent.getContent());
	}
	
	/**
	 * This method allows for updates to an existing content.
	 * 
	 * @param postContent
	 *            PostContent
	 * @return postContentResponse
	 *            PostContentResponse
	 * @throws AuthenticationException, Exception
	 *            exception
	 */
	@RequestMapping(value = "/content/update", method = RequestMethod.POST)
	public @ResponseBody
	ServiceResponse updateContent(@RequestBody PostContent updateContent,
			@RequestParam(required = false, value = "merge") Boolean merge)
			throws AuthenticationException, Exception {
		
		if (merge == null) { merge = true; }
		
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(updateContent.getSessionId(), PermissionEnum.UPDATE_ALL_CONTENT)) {
			log.info("User with insufficient privileges attempted to update content: ");
			log.info("Session ID: " + updateContent.getSessionId() + " Content " + updateContent.getContent().getId());
			throw new AuthenticationException();			
		} else {
			Message validationMessage = ContentValidator.validateContentUpdate(updateContent.getContent(), merge);
			
			if (validationMessage != null) {
				log.info("Updated content failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
				return new ServiceResponse(validationMessage, false);
			}
		}

		return contentService.updateContent(updateContent.getContent(), merge);
	}

	/**
	 * This method allows for the retrieval of a specific Content item by ID.
	 * 
	 * @param id
	 *           String
	 * @return contentServiceList
	 *            ContentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/content/id/{id}", method = RequestMethod.GET)	
	public @ResponseBody
	ContentServiceList getContentById(
			@PathVariable String id,		
			@RequestParam(required = false, value = "session_id") String sessionId,
			@RequestParam(required = false, value = "include_unaccepted") Boolean includeUnaccepted,
			@RequestParam(required = false, value = "page_visit") Boolean pageVisit) 
					throws AuthenticationException {

			if (includeUnaccepted == null) { includeUnaccepted = false; }
			if (pageVisit == null) { pageVisit = false; }

		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_CONTENT)) {
			log.info("User with insufficient privileges attempted to get content: ");
			log.info("Session ID: " + sessionId + " Content " + id);
			throw new AuthenticationException();			
		}
		
		return contentService.getContent(id, includeUnaccepted, pageVisit);
	}

	/**
	 * This method allows for the general retrieval of content summaries.
	 * 
	 * @return contentServiceList
	 *            ContentServiceList
	 * @throws Exception
	 *            exception
	 */	
	@RequestMapping(value = "/content", method = RequestMethod.GET)	
	public @ResponseBody
	ContentServiceList getContents(
			@RequestParam(required = false, value = "type") ContentTypeEnum contentType,
			@RequestParam(required = false, value = "page") Integer page,
			@RequestParam(required = false, value = "size") Integer size,
			@RequestParam(required = false, value = "posted_after") Date postedAfter,
			@RequestParam(required = false, value = "posted_before") Date postedBefore,
			@RequestParam(required = false, value = "include_tags") String includeTagsString,
			@RequestParam(required = false, value = "submitted_by") String submittedBy,			
			@RequestParam(required = false, value = "sort_by") String sortBy,
			@RequestParam(required = false, value = "ascending") Boolean ascending,
			@RequestParam(required = false, value = "randomize") Boolean randomize,
			@RequestParam(required = false, value = "session_id") String sessionId,
			@RequestParam(required = false, value = "count_only") Boolean countOnly)
				throws AuthenticationException {
		
		if (countOnly == null) { countOnly = false; }
		
		List<String> includeTags = convertList(includeTagsString);
		
		Message validationMessage = ContentValidator.validateContentQuery(contentType, 
				postedAfter, postedBefore, includeTags, sortBy, ascending, page, size, randomize);
	
		if (serviceProps.isEnableServiceAuthentication() && 
				!userService.hasPermission(sessionId, PermissionEnum.VIEW_CONTENT)) {
			log.info("User with insufficient privileges attempted to view a list of content: ");
			log.info("Session ID: " + sessionId);
			throw new AuthenticationException();
		}	
	
		if (validationMessage != null) {
			log.info("Content query failed validation: " + validationMessage.getCode() + ": " + validationMessage.getMessageText());
			return new ContentServiceList(null, validationMessage, false);
		}
		
		if (countOnly) {
			return contentService.getContentCount(contentType, postedAfter, postedBefore, includeTags, 
					submittedBy, ApprovalStatusEnum.APPROVED);			
		}
		
		return contentService.getContentSummaries(contentType, postedAfter, postedBefore, includeTags, 
				submittedBy, ApprovalStatusEnum.APPROVED, sortBy, ascending, page, size, randomize);
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
