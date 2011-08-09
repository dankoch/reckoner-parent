package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.BasicQuery;

import com.reckonlabs.reckoner.contentservices.factory.MongoDbQueryFactory;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public class ReckoningRepoImpl implements ReckoningRepoCustom {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public static final String RECKONING_COLLECTION = "reckoning";
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningRepoImpl.class);
	
	public void insertNewReckoning (Reckoning reckoning) {
		mongoTemplate.insert(reckoning, RECKONING_COLLECTION);
	}
	
	public List<Reckoning> getReckoningSummariesByPostingDate (Integer page, Integer size, Date beforeDate, Date afterDate) {
		BasicQuery query = null;
		
		if (beforeDate != null && afterDate != null) {
			query = new BasicQuery(MongoDbQueryFactory.buildReckoningPostedBetweenDateQuery(beforeDate, afterDate),
					MongoDbQueryFactory.buildReckoningSummaryFields());
		} else if (beforeDate != null) {
			query = new BasicQuery(MongoDbQueryFactory.buildReckoningPostedBeforeDateQuery(beforeDate),
					MongoDbQueryFactory.buildReckoningSummaryFields());			
		} else if (afterDate != null) {
			query = new BasicQuery(MongoDbQueryFactory.buildReckoningPostedAfterDateQuery(afterDate),
					MongoDbQueryFactory.buildReckoningSummaryFields());			
		} else {
			return null;
		}
		
		if (page != null && size != null) {
			query.setLimit(page.intValue());
			query.setSkip(page.intValue() * page.intValue());
		}
		
		return mongoTemplate.find(query, Reckoning.class);
	}
	
	public List<Reckoning> getReckoningSummariesByClosingDate (Integer page, Integer size, Date beforeDate, Date afterDate) {
		BasicQuery query = null;
		
		if (beforeDate != null && afterDate != null) {
			query = new BasicQuery(MongoDbQueryFactory.buildReckoningClosedBetweenDateQuery(beforeDate, afterDate),
					MongoDbQueryFactory.buildReckoningSummaryFields());
		} else if (beforeDate != null) {
			query = new BasicQuery(MongoDbQueryFactory.buildReckoningClosedBeforeDateQuery(beforeDate),
					MongoDbQueryFactory.buildReckoningSummaryFields());			
		} else if (afterDate != null) {
			query = new BasicQuery(MongoDbQueryFactory.buildReckoningClosedAfterDateQuery(afterDate),
					MongoDbQueryFactory.buildReckoningSummaryFields());			
		} else {
			return null;
		}
		
		if (page != null && size != null) {
			query.setLimit(page.intValue());
			query.setSkip(page.intValue() * page.intValue());
		}
		
		return mongoTemplate.find(query, Reckoning.class);
	}
	
	public List<Reckoning> getReckoningSummaries (Integer page, Integer size) {
		BasicQuery query = null;
		
		query = new BasicQuery(MongoDbQueryFactory.buildValidReckoningQuery(), 
				MongoDbQueryFactory.buildReckoningSummaryFields());
		
		if (page != null && size != null) {
			query.setLimit(size.intValue());
			query.setSkip(page.intValue() * size.intValue());
		}
		
		return mongoTemplate.find(query, Reckoning.class);
	}

	@Override
	public List<Reckoning> getReckoningSummariesByTag(String tag, Integer page, Integer size) {
		BasicQuery query = null;

		query = new BasicQuery(MongoDbQueryFactory.buildReckoningTagQuery(tag), 
				MongoDbQueryFactory.buildReckoningSummaryFields());
		
		if (page != null && size != null) {
			query.setLimit(size.intValue());
			query.setSkip(page.intValue() * size.intValue());
		}
		
		return mongoTemplate.find(query, Reckoning.class);
	}

}
