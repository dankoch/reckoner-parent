package com.reckonlabs.reckoner.contentservices.factory;

import java.util.Date;
import java.util.Map;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;

import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningTypeEnum;
import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.utility.DateUtility;

public final class MongoDbQueryFactory {
	
	public static DBObject buildValidReckoningQuery() {
		BasicDBObject query = new BasicDBObject("approved", true);
		query.append("rejected", false);
		
		return query;
	}
	
	public static DBObject buildOpenReckoningQuery () {
		BasicDBObject dateQuery = new BasicDBObject("$gt", DateUtility.now());
		BasicDBObject mainQuery = new BasicDBObject("closingDate", dateQuery);
		
		return mainQuery;
	}
	
	public static DBObject buildClosedReckoningQuery () {
		BasicDBObject dateQuery = new BasicDBObject("$lt", DateUtility.now());
		BasicDBObject mainQuery = new BasicDBObject("closingDate", dateQuery);
		
		return mainQuery;
	}
	
	public static DBObject buildReckoningIdQuery (String id) {
		return new BasicDBObject("id", new ObjectId(id));
	}
	
	public static DBObject buildReckoningIdExistsQuery (String id) {
		return new BasicDBObject("id", new BasicDBObject ("$exists", new ObjectId(id)));
	}
	
	public static DBObject buildCommentIdQuery (String id) {
		return new BasicDBObject("comments.commentId", id);
	}
	
	public static DBObject buildCommentIdExistsQuery (String id) {
		return new BasicDBObject("comments.commentId", new BasicDBObject ("$exists", id));
	}
	
	public static DBObject buildReckoningPostedAfterDateQuery (Date afterDate) {
		return new BasicDBObject("postingDate", new BasicDBObject ("$gt", afterDate));
	}
	
	public static DBObject buildReckoningPostedBeforeDateQuery (Date beforeDate) {
		return new BasicDBObject("postingDate", new BasicDBObject ("$lt", beforeDate));
	}
	
	public static DBObject buildReckoningPostedBetweenDateQuery (Date beforeDate, Date afterDate) {
		BasicDBObject innerQuery = new BasicDBObject ("$lt", beforeDate);
		innerQuery.append("$gt", afterDate);
		
		return new BasicDBObject("postingDate", innerQuery);
	}
	
	public static DBObject buildReckoningClosedAfterDateQuery (Date afterDate) {
		return new BasicDBObject("closingDate", new BasicDBObject ("$gt", afterDate));
	}
	
	public static DBObject buildReckoningClosedBeforeDateQuery (Date beforeDate) {
		return new BasicDBObject("closingDate", new BasicDBObject ("$lt", beforeDate));
	}
	
	public static DBObject buildReckoningClosedBetweenDateQuery (Date beforeDate, Date afterDate) {
		BasicDBObject innerQuery = new BasicDBObject ("$lt", beforeDate);
		innerQuery.append("$gt", afterDate);
		
		return new BasicDBObject("closingDate", innerQuery);
	}
	
	public static DBObject buildReckoningByUserIdQuery (String userId) {
		return new BasicDBObject("submitterId", userId);
	}
	
	public static DBObject buildReckoningTagQuery (String tag) {
		return new BasicDBObject("tags", tag);
	}
	
	public static DBObject buildAnswerIndexExists (Integer index) {
		return new BasicDBObject("answers.index", index);
	}
	
	public static DBObject buildRandomReckoningQuery (ReckoningTypeEnum type, double randIndex, boolean direction) {
		BasicDBObject randomQuery = new BasicDBObject("$lte", randIndex);
		if (direction) {
			randomQuery = new BasicDBObject("$gte", randIndex);
		}
		
		BasicDBObject mainQuery = new BasicDBObject("randomSelect", randomQuery);
		mainQuery.putAll(buildValidReckoningQuery());
		
		if (type == ReckoningTypeEnum.CLOSED) {
			mainQuery.putAll(buildClosedReckoningQuery());
		} else if (type == ReckoningTypeEnum.OPEN) {
			mainQuery.putAll(buildOpenReckoningQuery());
		} 
		
		return mainQuery;
	}
	
	public static Update buildReckoningUpdate(Reckoning reckoning) {
		Update reckoningUpdate = new Update();
		
		Map<String, Object> reckonMap = reckoning.toHashMap();
		for (Map.Entry<String, Object> entry: reckonMap.entrySet()) {
			if (entry.getValue() != null && !entry.getValue().equals("")) {
				reckoningUpdate.set(entry.getKey(), entry.getValue());
			}
		}
		
		return reckoningUpdate;
	}
	
	public static Update buildApprovalUpdate(String approver, Date postingDate, Date closingDate) {
		Update approvalUpdate = new Update();
		approvalUpdate.set("approved", true);
		approvalUpdate.set("rejected", false);
		approvalUpdate.set("approverId", approver);
		approvalUpdate.set("postingDate", postingDate);
		approvalUpdate.set("closingDate", closingDate);
		
		return approvalUpdate;
	}
	
	public static Update buildRejectionUpdate(String rejecter) {
		Update rejectionUpdate = new Update();
		rejectionUpdate.set("approved", false);
		rejectionUpdate.set("rejected", true);
		rejectionUpdate.set("approverId", rejecter);
		rejectionUpdate.unset("closingDate");
		
		return rejectionUpdate;
	}
	
	public static Update buildReckoningCommentUpdate(Comment comment) {
		Update commentInsert = new Update();
		commentInsert.push("comments", comment);
		
		return commentInsert;
	}
	
	public static Update buildFavoriteUpdate(Favorite favorite) {
		Update favoriteInsert = new Update();
		favoriteInsert.push("favorites", favorite);
		
		return favoriteInsert;
	}
	
	public static Update buildFlagUpdate(Flag flag) {
		Update flagInsert = new Update();
		flagInsert.push("flags", flag);
		
		return flagInsert;
	}
	
	public static Update buildReckoningVoteUpdate(String voterId, Integer answerIndex) {
		Update voteInsert = new Update();
		voteInsert.push("answers." + answerIndex + ".votes." + voterId, true).inc("answers." + answerIndex + ".voteTotal", 1);
		
		return voteInsert;
	}
	
	public static Update buildCommentUpdate(Comment comment) {
		Update voteInsert = new Update();
		voteInsert.set("comments.$", comment);
		
		return voteInsert;
	}
	
	/* Awaiting MongoDB to fix SERVER-831 for the 2.1 release.  Multi-tiered conditional positioning statements
	 * are not currently supported in 1.9.  DO NOT USE THAT FIX IS APPLIED AND WE'VE MIGRATED.
	 * 
	public static Update buildCommentFavoriteUpdate(Favorite favorite) {
		Update voteInsert = new Update();
		voteInsert.push("comments.$.favorites", favorite);
		
		return voteInsert;
	}
	
	public static Update buildCommentUpdate(Flag flag) {
		Update voteInsert = new Update();
		voteInsert.push("comments.$.flag", flag);
		
		return voteInsert;
	}*/
	
	public static DBObject buildReckoningSummaryFields() {
		BasicDBObject fields = new BasicDBObject("answers.votes", 0);
		fields.append("comments", 0);
		fields.append("favorites", 0);
		fields.append("flags", 0);
		
		return fields;
	}
	
	public static DBObject buildReckoningIdField() {
		BasicDBObject fields = new BasicDBObject("id", 1);
		
		return fields;
	}
	
	public static DBObject buildReckoningIdAndAnswerIndexFields() {
		BasicDBObject fields = new BasicDBObject("id", 1);
		fields.append("answers.index", 1);
		
		return fields;
	}
}
