package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.document.mongodb.repository.Query;
import org.springframework.data.document.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.user.User;

public interface UserRepo extends MongoRepository<User, String> {

	List<User> findById(String id);
	List<User> findByAuthProviderAndAuthProviderId(String authProvider, String authProviderId);
}
