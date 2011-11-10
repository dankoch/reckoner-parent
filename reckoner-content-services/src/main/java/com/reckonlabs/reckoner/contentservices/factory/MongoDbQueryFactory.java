package com.reckonlabs.reckoner.contentservices.factory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;

import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.reckoning.ReckoningTypeEnum;
import com.reckonlabs.reckoner.domain.reckoning.Vote;
import com.reckonlabs.reckoner.domain.utility.DateUtility;

public final class MongoDbQueryFactory {
	
	public static String DELETE_SENTINEL = "null";
	
	public static DBObject buildAcceptedReckoningQuery() {
		BasicDBObject query = new BasicDBObject("approved", true);
		query.append("rejected", false);
		
		return query;
	}
	
	public static DBObject buildPendingReckoningQuery() {
		BasicDBObject query = new BasicDBObject("approved", false);
		query.append("rejected", false);
		
		return query;
	}
	
	public static DBObject buildRejectedReckoningQuery() {
		BasicDBObject query = new BasicDBObject("rejected", true);
		
		return query;
	}
	
	public static DBObject buildAcceptedAndPendingReckoningQuery() {
		BasicDBObject query = new BasicDBObject("rejected", false);
		
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
	
	public static DBObject buildIdQuery (String id) {
		return new BasicDBObject("id", new ObjectId(id));
	}
	
	public static DBObject buildIdExistsQuery (String id) {
		return new BasicDBObject("id", new BasicDBObject ("$exists", new ObjectId(id)));
	}
	
	public static DBObject buildCommentIdQuery (String id) {
		return new BasicDBObject("comments.commentId", id);
	}
	
	public static DBObject buildCommentIdExistsQuery (String id) {
		return new BasicDBObject("comments.commentId", new BasicDBObject ("$exists", id));
	}
	
	public static DBObject buildPostedAfterDateQuery (Date afterDate) {
		return new BasicDBObject("postingDate", new BasicDBObject ("$gt", afterDate));
	}
	
	public static DBObject buildPostedBeforeDateQuery (Date beforeDate) {
		return new BasicDBObject("postingDate", new BasicDBObject ("$lt", beforeDate));
	}
	
	public static DBObject buildPostedBetweenDateQuery (Date beforeDate, Date afterDate) {
		BasicDBObject innerQuery = new BasicDBObject ("$lt", beforeDate);
		innerQuery.append("$gt", afterDate);
		
		return new BasicDBObject("postingDate", innerQuery);
	}
	
	public static DBObject buildClosedAfterDateQuery (Date afterDate) {
		return new BasicDBObject("closingDate", new BasicDBObject ("$gt", afterDate));
	}
	
	public static DBObject buildClosedBeforeDateQuery (Date beforeDate) {
		return new BasicDBObject("closingDate", new BasicDBObject ("$lt", beforeDate));
	}
	
	public static DBObject buildClosedBetweenDateQuery (Date beforeDate, Date afterDate) {
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
	
	public static DBObject buildReckoningTagsQuery (List<String> includeTags) {
		DBObject innerQuery = new BasicDBObject("$in", includeTags);
		return new BasicDBObject("tags", innerQuery);
	}
	
	public static DBObject buildExcludeReckoningTagsQuery (List<String> excludeTags) {
		DBObject innerQuery = new BasicDBObject("$in", excludeTags);
		DBObject notQuery = new BasicDBObject("$not", innerQuery);
		return new BasicDBObject("tags", notQuery);
	}
	
	public static DBObject buildIncludeExcludeReckoningTagsQuery (List<String> includeTags, List<String> excludeTags) {
		DBObject combinedQuery = new BasicDBObject("$in", includeTags);
		
		DBObject innerQuery = new BasicDBObject("$in", excludeTags);
		DBObject excludeQuery = new BasicDBObject("$not", innerQuery);	
		combinedQuery.putAll(excludeQuery);
		
		return new BasicDBObject("tags", combinedQuery);
	}
	
	public static DBObject buildHighlightedQuery(boolean highlighted) {
		return new BasicDBObject ("highlighted", highlighted);
	}
	
	public static DBObject buildRandomQuery(double randIndex) {
		return new BasicDBObject("randomSelect", new BasicDBObject ("$lte", randIndex));
	}
	
	public static DBObject buildOppositeRandomReckoningQuery(double randIndex) {
		return new BasicDBObject("randomSelect", new BasicDBObject ("$gte", randIndex));
	}
	
	public static DBObject buildAnswerIndexExists (Integer index) {
		return new BasicDBObject("answers.index", index);
	}
	
	public static DBObject buildVotedOnByUser (String userId) {
		return new BasicDBObject("answers.votes." + userId, true);
	}
	
	public static DBObject buildCommentedOnByUser (String userId) {
		return new BasicDBObject("comments.posterId", userId);
	}
	
	public static DBObject buildCommentByIdQuery (String commentId) {
		return new BasicDBObject("comments.commentId", commentId);
	}
	
	public static DBObject buildPostedByQuery (String submitterId) {
		return new BasicDBObject("submitterId", submitterId);
	}
	
	public static DBObject buildApprovalStatusQuery (ApprovalStatusEnum approvalStatus) {
		if (approvalStatus == ApprovalStatusEnum.APPROVED) {
			return buildAcceptedReckoningQuery();
		} else if (approvalStatus == ApprovalStatusEnum.PENDING) {
			return buildPendingReckoningQuery();
		} else if (approvalStatus == ApprovalStatusEnum.REJECTED) {
			return buildRejectedReckoningQuery();
		} else if (approvalStatus == ApprovalStatusEnum.APPROVED_AND_PENDING) {
			return buildAcceptedAndPendingReckoningQuery();
		} 
		
		return new BasicDBObject();
	}
	
	public static DBObject buildContentTypeQuery (ContentTypeEnum contentType) {
		return new BasicDBObject ("contentType", contentType);
	}
	
	public static DBObject buildReckoningQuery (ReckoningTypeEnum type, Date postedBeforeDate, Date postedAfterDate,
			Date closedBeforeDate, Date closedAfterDate, List<String> includeTags, List<String> excludeTags,
			Boolean highlighted, String submitterId, ApprovalStatusEnum approvalStatus, Boolean randomize) {

		DBObject mainQuery = buildApprovalStatusQuery(approvalStatus);
				
		// Add in the necessary OPEN/CLOSED checks, and make sure that the date parameters are correct for any 
		// subsequent date processing.
		if (type == ReckoningTypeEnum.CLOSED) {
			if (closedBeforeDate == null || closedBeforeDate.after(DateUtility.now())) {
				closedBeforeDate = DateUtility.now();				
			}
		} else if (type == ReckoningTypeEnum.OPEN) {
			if (closedAfterDate == null || closedAfterDate.before(DateUtility.now())) {
				closedAfterDate = DateUtility.now();				
			}
		} 
		
		if (postedBeforeDate != null && postedAfterDate != null) {
			mainQuery.putAll(buildPostedBetweenDateQuery(postedBeforeDate, postedAfterDate));
		}
		else if (postedBeforeDate != null) {
			mainQuery.putAll(buildPostedBeforeDateQuery(postedBeforeDate));
		}
		else if (postedAfterDate != null) {
			mainQuery.putAll(buildPostedAfterDateQuery(postedAfterDate));
		}
		
		if (closedBeforeDate != null & closedAfterDate != null) {
			mainQuery.putAll(buildClosedBetweenDateQuery(closedBeforeDate, closedAfterDate));
		}		
		else if (closedBeforeDate != null) {
			mainQuery.putAll(buildClosedBeforeDateQuery(closedBeforeDate));
		}
		else if (closedAfterDate != null) {
			mainQuery.putAll(buildClosedAfterDateQuery(closedAfterDate));
		}
		
		if (includeTags != null && excludeTags != null) {
			mainQuery.putAll(buildIncludeExcludeReckoningTagsQuery(includeTags, excludeTags));
		}
		else if (includeTags != null) {
			mainQuery.putAll(buildReckoningTagsQuery(includeTags));
		} 
		else if (excludeTags != null) {
			mainQuery.putAll(buildExcludeReckoningTagsQuery(excludeTags));
		}
		
		if (highlighted != null && highlighted.booleanValue()) {
			mainQuery.putAll(buildHighlightedQuery(highlighted.booleanValue()));
		}
		
		if (submitterId != null) {
			mainQuery.putAll(buildPostedByQuery(submitterId));
		}
		
		if (randomize != null && randomize.booleanValue()) {
			mainQuery.putAll(buildRandomQuery(new Random().nextDouble()));
		}
		
		return mainQuery;
	}
	
	public static DBObject buildRandomReckoningQuery (ReckoningTypeEnum type, double randIndex, boolean direction) {
		DBObject mainQuery = buildRandomQuery(randIndex);
		if (direction) {
			mainQuery = buildOppositeRandomReckoningQuery(randIndex);
		}
		
		mainQuery.putAll(buildApprovalStatusQuery(ApprovalStatusEnum.APPROVED));
		
		if (type == ReckoningTypeEnum.CLOSED) {
			mainQuery.putAll(buildClosedReckoningQuery());
		} else if (type == ReckoningTypeEnum.OPEN) {
			mainQuery.putAll(buildOpenReckoningQuery());
		} 
		
		return mainQuery;
	}
	
	public static DBObject buildFavoritedQuery (Date favoritedSince) {
		DBObject mainQuery = new BasicDBObject("favorites", new BasicDBObject ("$exists", true));
		if (favoritedSince != null) {
			mainQuery = new BasicDBObject("favorites.favoriteDate", new BasicDBObject ("$gt", favoritedSince));
		}
		
		mainQuery.putAll(buildApprovalStatusQuery(ApprovalStatusEnum.APPROVED));
		
		return mainQuery;
	}
	
	public static DBObject buildFlaggedQuery (Date flaggedSince) {
		DBObject mainQuery = new BasicDBObject("flags", new BasicDBObject ("$exists", true));
		if (flaggedSince != null) {
			mainQuery = new BasicDBObject("flags.flagDate", new BasicDBObject ("$gt", flaggedSince));
		}
		
		mainQuery.putAll(buildApprovalStatusQuery(ApprovalStatusEnum.APPROVED));
		
		return mainQuery;
	}
	
	public static DBObject buildFavoritedCommentQuery (Date favoritedSince) {
		DBObject mainQuery = new BasicDBObject("comments.favorites", new BasicDBObject ("$exists", true));
		if (favoritedSince != null) {
			mainQuery = new BasicDBObject("comments.favorites.favoriteDate", new BasicDBObject ("$gt", favoritedSince));
		}
		
		mainQuery.putAll(buildApprovalStatusQuery(ApprovalStatusEnum.APPROVED));
		
		return mainQuery;
	}
	
	public static DBObject buildFlaggedCommentQuery (Date flaggedSince) {
		DBObject mainQuery = new BasicDBObject("comments.flags", new BasicDBObject ("$exists", true));
		if (flaggedSince != null) {
			mainQuery = new BasicDBObject("comments.flags.flagDate", new BasicDBObject ("$gt", flaggedSince));
		}
		
		mainQuery.putAll(buildApprovalStatusQuery(ApprovalStatusEnum.APPROVED));
		
		return mainQuery;
	}
	
	public static Update buildReckoningUpdate(Reckoning reckoning) {
		Update reckoningUpdate = new Update();
		
		Map<String, Object> reckonMap = reckoning.toHashMap();
		for (Map.Entry<String, Object> entry: reckonMap.entrySet()) {
			if (entry.getValue() != null) {
				if (entry.getValue().equals(DELETE_SENTINEL)) {
					reckoningUpdate.set(entry.getKey(), "");
				} else {
					reckoningUpdate.set(entry.getKey(), entry.getValue());
				}
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
	
	public static Update buildCommentInsert(Comment comment) {
		Update commentInsert = new Update();
		commentInsert.push("comments", comment).inc("commentIndex", 1);
		
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
	
	public static Update incrementViews() {
		Update viewIncrement = new Update();
		viewIncrement.inc("views", 1);
		
		return viewIncrement;
	}
	
	public static Update deleteByCommentId (String commentId) {		
		Update deleteComment = new Update();
		deleteComment.pull("comments", new BasicDBObject("commentId", commentId)).inc("commentIndex", -1);
		
		return deleteComment;
	}
	
	public static DBObject buildExcludeVotesFields() {
		BasicDBObject fields = new BasicDBObject("answers.votes", 0);
		
		return fields;
	}
	
	public static DBObject buildReckoningSummaryFields() {
		BasicDBObject fields = new BasicDBObject("answers.votes", 0);
		fields.append("comments", 0);
		fields.append("favorites", 0);
		fields.append("flags", 0);
		
		return fields;
	}
	
	public static DBObject buildReckoningSummaryFieldsPlusComments() {
		BasicDBObject fields = new BasicDBObject("answers.votes", 0);
		fields.append("favorites", 0);
		fields.append("flags", 0);
		
		return fields;
	}	
	
	public static DBObject buildReckoningVotedFields() {
		BasicDBObject fields = new BasicDBObject("comments", 0);
		fields.append("favorites", 0);
		fields.append("flags", 0);
		
		return fields;
	}	
	
	public static DBObject buildIdField() {
		BasicDBObject fields = new BasicDBObject("id", 1);
		
		return fields;
	}
	
	public static DBObject buildReckoningIdAndAnswerIndexFields() {
		BasicDBObject fields = new BasicDBObject("id", 1);
		fields.append("answers.index", 1);
		fields.append("approved", 1);
		fields.append("rejected", 1);
		fields.append("closingDate", 1);
		
		return fields;
	}
	
	/////////////////////////////////////////////////////////////////////////
	// FUNCTIONS USED FOR CONTENT (AS OPPOSED TO RECKONINGS) ARE INCLUDED BELOW
	/////////////////////////////////////////////////////////////////////////
	
	public static Update buildContentUpdate(Content content) {
		Update contentUpdate = new Update();
		
		Map<String, Object> contentMap = content.toHashMap();
		for (Map.Entry<String, Object> entry: contentMap.entrySet()) {
			if (entry.getValue() != null) {
				contentUpdate.set(entry.getKey(), entry.getValue());
			}
		}
		
		return contentUpdate;
	}
	
	public static DBObject buildContentQuery (ContentTypeEnum type, Date postedBeforeDate, Date postedAfterDate,
			List<String> includeTags, String submitterId, ApprovalStatusEnum approvalStatus, Boolean randomize) {

		DBObject mainQuery = buildApprovalStatusQuery(approvalStatus);
		
		if (type != null) {
			mainQuery.putAll(buildContentTypeQuery(type));
		}
		
		if (postedBeforeDate != null && postedAfterDate != null) {
			mainQuery.putAll(buildPostedBetweenDateQuery(postedBeforeDate, postedAfterDate));
		}
		else if (postedBeforeDate != null) {
			mainQuery.putAll(buildPostedBeforeDateQuery(postedBeforeDate));
		}
		else if (postedAfterDate != null) {
			mainQuery.putAll(buildPostedAfterDateQuery(postedAfterDate));
		}
		
		if (includeTags != null) {
			mainQuery.putAll(buildReckoningTagsQuery(includeTags));
		}
		
		if (submitterId != null) {
			mainQuery.putAll(buildPostedByQuery(submitterId));
		}
		
		if (randomize != null && randomize.booleanValue()) {
			mainQuery.putAll(buildRandomQuery(new Random().nextDouble()));
		}
		
		return mainQuery;
	}
	
	public static DBObject buildContentSummaryFields() {
		BasicDBObject fields = new BasicDBObject("comments", 0);
		fields.append("favorites", 0);
		fields.append("flags", 0);
		fields.append("commentary", 0);
		fields.append("commentary_user_id", 0);
		
		return fields;
	}
	
	public static DBObject buildContentFlagFavoriteFields() {
		BasicDBObject fields = new BasicDBObject("comments", 0);
		fields.append("commentary", 0);
		fields.append("commentary_user_id", 0);
		
		return fields;
	}
}
