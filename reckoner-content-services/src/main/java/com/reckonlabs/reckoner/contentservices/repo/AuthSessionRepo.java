package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.reckonlabs.reckoner.domain.user.AuthSession;

public interface AuthSessionRepo extends MongoRepository<AuthSession, String> {

	List<AuthSession> findById(String id);
	List<AuthSession> findByUserToken(String userToken);
	List<AuthSession> findByReckonerUserId(String reckonerUserId);
}
