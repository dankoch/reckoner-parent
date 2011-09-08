package com.reckonlabs.reckoner.contentservices.factory;

import java.util.Date;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;

import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.utility.DateUtility;

public final class MongoDbQueryFactory {
	
	public static DBObject buildValidReckoningQuery() {
		return new BasicDBObject("rejected", false);
	}
	
	public static DBObject buildReckoningIdQuery (String id) {
		return new BasicDBObject("id", new ObjectId(id));
	}
	
	public static DBObject buildReckoningIdExistsQuery (String id) {
		return new BasicDBObject("id", new BasicDBObject ("$exists", new ObjectId(id)));
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
	
	public static Update buildReckoningVoteUpdate(Vote vote, Integer answerIndex) {
		Update voteInsert = new Update();
		voteInsert.push("answers." + answerIndex + ".votes", vote).inc("answers." + answerIndex + ".voteTotal", 1);
		
		return voteInsert;
	}
	
	public static DBObject buildReckoningSummaryFields() {
		BasicDBObject fields = new BasicDBObject("id", 1);
		fields.append("question", 1);
		fields.append("postingDate", 1);
		fields.append("closingDate", 1);
		fields.append("submitterId", 1);
		
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
