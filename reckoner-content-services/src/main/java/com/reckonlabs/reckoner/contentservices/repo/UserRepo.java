package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.user.User;

public interface UserRepo extends MongoRepository<User, String> {

	@Query(value = "{'id' : ?0}")
	List<User> findById(String id);
	@Query(value = "{'id' : ?0}", fields="{'id' : 1, 'active' : 1, 'firstName' : 1, 'lastName' : 1, 'profilePictureUrl' : 1, 'hideProfile' : 1, 'hideVotes' : 1}")	
	List<User> findByIdSummary(String id);
	
	List<User> findByAuthProviderAndAuthProviderId(String authProvider, String authProviderId);
}
