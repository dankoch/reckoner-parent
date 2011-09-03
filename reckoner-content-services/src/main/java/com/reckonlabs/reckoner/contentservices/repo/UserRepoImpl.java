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
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.user.User;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

public class UserRepoImpl implements UserRepoCustom {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public static final String USER_COLLECTION = "user";
	
	private static final Logger log = LoggerFactory
			.getLogger(UserRepoImpl.class);

	@Override
	public void insertNewUser(User user) {
		mongoTemplate.insert(user, USER_COLLECTION);
	}

	@Override
	public void updateUser(User user) {
		mongoTemplate.save(user, USER_COLLECTION);
	}

}
