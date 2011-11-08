package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Order;

import com.reckonlabs.reckoner.contentservices.factory.MongoDbQueryFactory;
import com.reckonlabs.reckoner.domain.message.Message;
import com.reckonlabs.reckoner.domain.message.ReckoningServiceList;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningTypeEnum;
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
	
	// Performs an 'upsert' on the given Reckoning.  
	// If a matching ID is found, it's overwritten with the provided Reckoning.  Otherwise, a new one is inserted.
	public void updateReckoning (Reckoning reckoning) {
		mongoTemplate.save(reckoning, RECKONING_COLLECTION);
	}
	
	// Merges the provided reckoning into the existing reckoning found with the given ID.
	// Does nothing if no matches are found.
	public void mergeReckoning (Reckoning reckoning) throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoning.getId())), 
				MongoDbQueryFactory.buildReckoningUpdate(reckoning), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());
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

	public List<Reckoning> getReckoningSummaries (ReckoningTypeEnum reckoningType, 
			Date postedBeforeDate, Date postedAfterDate,
			Date closedBeforeDate, Date closedAfterDate, 
			List<String> includeTags, List<String> excludeTags, 
			Boolean highlighted,
			String submitterId,
			ReckoningApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, Integer page, Integer size, Boolean randomize) {
		BasicQuery query = null;
		
		query = new BasicQuery(MongoDbQueryFactory.buildReckoningQuery(reckoningType, postedBeforeDate, postedAfterDate,
				closedBeforeDate, closedAfterDate, includeTags, excludeTags, highlighted, submitterId, approvalStatus, randomize),
				MongoDbQueryFactory.buildReckoningSummaryFields());
		
		if (size != null) {
			if (page == null) { page = 0; }
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		if (randomize != null && randomize.booleanValue()) {
			query.sort().on("randomSelect", Order.DESCENDING);
		}
		else if (sortBy != null && sortBy != "") {
			if (ascending != null && ascending.booleanValue()) {
				query.sort().on(sortBy, Order.ASCENDING);
			} else {
				query.sort().on(sortBy, Order.DESCENDING);				
			}
		}

		return mongoTemplate.find(query, Reckoning.class);		
	}
	
	public Long getReckoningCount(ReckoningTypeEnum reckoningType, 
			Date postedBeforeDate, Date postedAfterDate,
			Date closedBeforeDate, Date closedAfterDate, 
			List<String> includeTags, List<String> excludeTags,
			Boolean highlighted, 
			String submitterId,
			ReckoningApprovalStatusEnum approvalStatus) {
		
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildReckoningQuery(reckoningType, postedBeforeDate, postedAfterDate,
				closedBeforeDate, closedAfterDate, includeTags, excludeTags, highlighted, submitterId, approvalStatus, null),
				MongoDbQueryFactory.buildReckoningSummaryFields());	
		
		return mongoTemplate.getCollection(RECKONING_COLLECTION).count(query.getQueryObject());		
	}
	
	public List<Reckoning> getRandomReckoningSummary(ReckoningTypeEnum type) {
		List<Reckoning> returnList = null;
		double randIndex = new Random().nextDouble();
		
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildRandomReckoningQuery(type, 
				randIndex, false), MongoDbQueryFactory.buildReckoningSummaryFields());
		query.sort().on("randomSelect", Order.DESCENDING);
		query.limit(1);
		
		returnList = mongoTemplate.find(query, Reckoning.class);
		
		// If no random reckoning turned up searching one direction from the random index,
		// try the other direction.
		if (returnList == null || returnList.isEmpty()) {
			query = new BasicQuery(MongoDbQueryFactory.buildRandomReckoningQuery(type, 
					randIndex, true), MongoDbQueryFactory.buildReckoningSummaryFields());
			query.sort().on("randomSelect", Order.ASCENDING);
			query.limit(1);
			
			returnList = mongoTemplate.find(query, Reckoning.class);
		}
		
		return returnList;
	}
	
	@Override
	public List<Reckoning> getUserVotedReckonings(String userId) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildVotedOnByUser(userId),
				MongoDbQueryFactory.buildReckoningVotedFields());	
		
		query.sort().on("closingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Reckoning.class);	
	}
	
	@Override
	public List<Reckoning> getUserCommentedReckonings(String userId) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildCommentedOnByUser(userId),
				MongoDbQueryFactory.buildReckoningSummaryFieldsPlusComments());	
		
		query.sort().on("closingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Reckoning.class);	
	}
	
	@Override
	public List<Reckoning> getFavoritedReckonings(Date favoritedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFavoritedReckoningQuery(favoritedAfter),
				MongoDbQueryFactory.buildExcludeVotesFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Reckoning.class);	
	}

	@Override
	public List<Reckoning> getFlaggedReckonings(Date flaggedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFlaggedReckoningQuery(flaggedAfter),
				MongoDbQueryFactory.buildExcludeVotesFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Reckoning.class);	
	}
	
	@Override
	public List<Reckoning> getFavoritedReckoningComments(Date favoritedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFavoritedCommentQuery(favoritedAfter),
				MongoDbQueryFactory.buildExcludeVotesFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Reckoning.class);	
	}

	@Override
	public List<Reckoning> getFlaggedReckoningComments(Date flaggedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFlaggedCommentQuery(flaggedAfter),
				MongoDbQueryFactory.buildExcludeVotesFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Reckoning.class);	
	}
	
	@Override
	public void insertReckoningComment(Comment comment, String reckoningId)
			throws DBUpdateException {
		
		comment.setCommentId(new ObjectId().toString());
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId)), 
				MongoDbQueryFactory.buildReckoningCommentUpdate(comment), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());		
	}
	
	@Override
	public void insertReckoningVote(String voterId, Integer answerIndex,
			String reckoningId) throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId)), 
				MongoDbQueryFactory.buildReckoningVoteUpdate(voterId, answerIndex), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());		
		
	}
	
	@Override
	public void insertReckoningFavorite(Favorite favorite, String reckoningId)
			throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId)), 
				MongoDbQueryFactory.buildFavoriteUpdate(favorite), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());		
	}

	@Override
	public void insertReckoningFlag(Flag flag, String reckoningId)
			throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId)), 
				MongoDbQueryFactory.buildFlagUpdate(flag), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}

	/* Awaiting fix for SERVER-831 in MongoDB 2.1.
	 * 
	@Override
	public void insertCommentFavorite(Favorite favorite, String commentId)
			throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildCommentIdQuery(commentId)), 
				MongoDbQueryFactory.buildCommentFavoriteUpdate(favorite), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
		
	}

	@Override
	public void insertCommentFlag(Flag flag, String commentId)
			throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildCommentIdQuery(commentId)), 
				MongoDbQueryFactory.buildCommentFlagUpdate(flag), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	} */
	
	@Override
	public void incrementReckoningViews(String reckoningId) throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId)), 
				MongoDbQueryFactory.incrementReckoningViews(), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}
	
	@Override
	public void updateComment(Comment comment) 
			throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildCommentIdQuery(comment.getCommentId())), 
				MongoDbQueryFactory.buildCommentUpdate(comment), RECKONING_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());			
	}
	
	@Override
	public void deleteComment(String commentId) 
			throws DBUpdateException {
		
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildCommentIdQuery(commentId)), 
				MongoDbQueryFactory.deleteByCommentId(commentId), RECKONING_COLLECTION);
		
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
	public boolean confirmReckoningIsVotingEligible(String reckoningId,
			int answerIndex) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildReckoningIdQuery(reckoningId), 
				MongoDbQueryFactory.buildReckoningIdAndAnswerIndexFields());
		List<Reckoning> reckoning = mongoTemplate.find(query, Reckoning.class);
		
		if (reckoning.isEmpty()) {
			return false;	
		} else if (reckoning.get(0).getAnswers().size() <= answerIndex) {
			return false;
		} else if (!reckoning.get(0).isOpen())
			return false;
			
		return true;
	}
}
