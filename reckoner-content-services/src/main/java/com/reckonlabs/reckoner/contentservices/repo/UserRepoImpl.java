package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Order;

import com.reckonlabs.reckoner.contentservices.factory.MongoDbQueryFactory;
import com.reckonlabs.reckoner.domain.user.User;

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

	@Override
	public List<User> getUserSummaries(Boolean active, String sortBy,
			Boolean ascending, Integer page, Integer size) {
		
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildUserQuery(active),
				MongoDbQueryFactory.buildUserSummaryFields());
		
		if (size != null) {
			if (page == null) { page = 0; }
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		else if (sortBy != null && sortBy != "") {
			if (ascending != null && ascending.booleanValue()) {
				query.sort().on(sortBy, Order.ASCENDING);
			} else {
				query.sort().on(sortBy, Order.DESCENDING);				
			}
		}

		return mongoTemplate.find(query, User.class);		
	}
}
