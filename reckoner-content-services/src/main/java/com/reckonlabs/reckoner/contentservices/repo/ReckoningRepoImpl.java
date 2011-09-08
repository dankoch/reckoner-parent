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

import com.reckonlabs.reckoner.contentservices.factory.MongoDbQueryFactory;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

public class ReckoningRepoImpl implements ReckoningRepoCustom {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public static final String RECKONING_COLLECTION = "reckoning";
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningRepoImpl.class);
	
	
	public void insertNewReckoning (Reckoning reckoning) {
		mongoTemplate.insert(reckoning, RECKONING_COLLECTION);
	}
	
	public void updateReckoning (Reckoning reckoning) {
		mongoTemplate.save(reckoning, RECKONING_COLLECTION);
	}
	
	public void approveReckoning (String id, String approver, Date postingDate, Date closingDate) throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(id)), 
				MongoDbQueryFactory.buildApprovalUpdate(approver, postingDate, closingDate), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());
	}
	
	public void rejectReckoning (String id, String rejecter) throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(id)), 
				MongoDbQueryFactory.buildRejectionUpdate(rejecter), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());
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
			query.limit(page.intValue());
			query.skip(page.intValue() * page.intValue());
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
			query.limit(page.intValue());
			query.skip(page.intValue() * page.intValue());
		}
		
		return mongoTemplate.find(query, Reckoning.class);
	}
	
	public List<Reckoning> getReckoningSummaries (Integer page, Integer size) {
		BasicQuery query = null;
		
		query = new BasicQuery(MongoDbQueryFactory.buildValidReckoningQuery(), 
				MongoDbQueryFactory.buildReckoningSummaryFields());
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		return mongoTemplate.find(query, Reckoning.class);
	}

	@Override
	public List<Reckoning> getReckoningSummariesByTag(String tag, Integer page, Integer size) {
		BasicQuery query = null;

		query = new BasicQuery(MongoDbQueryFactory.buildReckoningTagQuery(tag), 
				MongoDbQueryFactory.buildReckoningSummaryFields());
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		return mongoTemplate.find(query, Reckoning.class);
	}

	@Override
	public void insertReckoningComment(Comment comment, String reckoningId)
			throws DBUpdateException {
		
		comment.setId(new ObjectId().toString());
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId)), 
				MongoDbQueryFactory.buildReckoningCommentUpdate(comment), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());		
	}
	
	@Override
	public void insertReckoningVote(Vote vote, Integer answerIndex,
			String reckoningId) throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId)), 
				MongoDbQueryFactory.buildReckoningVoteUpdate(vote, answerIndex), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());		
		
	}
	
	@Override
	public boolean confirmReckoningExists(String reckoningId) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId), 
				MongoDbQueryFactory.buildReckoningIdField());
		if (!mongoTemplate.find(query, Reckoning.class).isEmpty()) {
			return true;	
		}
		return false;
	}
	
	@Override
	public boolean confirmReckoningAndAnswerExists(String reckoningId, int answerIndex) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId), 
				MongoDbQueryFactory.buildReckoningIdAndAnswerIndexFields());
		List<Reckoning> reckoning = mongoTemplate.find(query, Reckoning.class);
		
		if (reckoning.isEmpty()) {
			return false;	
		} else if (reckoning.get(0).getAnswers().size() <= answerIndex) {
			return false;
		}
		
		return true;
	}

}
