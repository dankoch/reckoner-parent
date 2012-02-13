package com.reckonlabs.reckoner.contentservices.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.content.Content;

public interface ContentRepo extends MongoRepository<Content, String> {

	@Query(value = "{'id' : ?0}")
	List<Content> findById(String id);
	
	@Query(value = "{'id' : ?0, 'approved' : ?1}")
	List<Content> findByIdAndApproved(String id, boolean approved);
	
	@Query(value = "{'comments.commentId' : ?0}", fields="{'flags' : 0, 'favorites' : 0}")
	List<Content> getContentCommentById (String commentId);
	
	@Query(value = "{'media.mediaId' : ?0}", fields="{'comments' : 0, 'flags' : 0, 'favorites' : 0}")
	List<Content> getContentMediaById (String commentId);
}
