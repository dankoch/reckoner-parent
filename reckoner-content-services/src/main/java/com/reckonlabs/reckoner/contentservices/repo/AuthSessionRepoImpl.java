package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.BasicQuery;

import com.reckonlabs.reckoner.contentservices.factory.MongoDbQueryFactory;
import com.reckonlabs.reckoner.domain.user.AuthSession;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

public class AuthSessionRepoImpl implements AuthSessionRepoCustom {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public static final String AUTH_SESSION_COLLECTION = "authSession";
	
	private static final Logger log = LoggerFactory
			.getLogger(AuthSessionRepoImpl.class);

	@Override
	public void insertNewAuthSession(AuthSession authSession) {
		mongoTemplate.insert(authSession, AUTH_SESSION_COLLECTION);
	}

	@Override
	public void updateAuthSession(AuthSession authSession) {
		mongoTemplate.save(authSession, AUTH_SESSION_COLLECTION);
	}
	
	@Override
	public void removeAuthSession(AuthSession authSession) {
		mongoTemplate.remove(authSession);
	}
}
