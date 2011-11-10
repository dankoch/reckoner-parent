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
import com.reckonlabs.reckoner.domain.ApprovalStatusEnum;
import com.reckonlabs.reckoner.domain.content.Content;
import com.reckonlabs.reckoner.domain.content.ContentTypeEnum;
import com.reckonlabs.reckoner.domain.notes.Comment;
import com.reckonlabs.reckoner.domain.notes.Favorite;
import com.reckonlabs.reckoner.domain.notes.Flag;
import com.reckonlabs.reckoner.domain.reckoning.Reckoning;
import com.reckonlabs.reckoner.domain.utility.DBUpdateException;

public class ContentRepoImpl implements ContentRepoCustom {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public static final String CONTENT_COLLECTION = "content";
	
	private static final Logger log = LoggerFactory
			.getLogger(ContentRepoImpl.class);

	@Override
	public void insertNewContent(Content content) {
		mongoTemplate.insert(content, CONTENT_COLLECTION);
	}

	@Override
	public void updateContent(Content content) {
		mongoTemplate.save(content, CONTENT_COLLECTION);
	}

	@Override
	public void mergeContent(Content content) throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildIdQuery(content.getId())), 
				MongoDbQueryFactory.buildContentUpdate(content), CONTENT_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());
	}

	@Override
	public List<Content> getContentItems(ContentTypeEnum contentType,
			Date postedBeforeDate, Date postedAfterDate,
			List<String> includeTags, String submitterId, 
			ApprovalStatusEnum approvalStatus,
			String sortBy, Boolean ascending, Integer page, Integer size,
			Boolean randomize) {
		
		BasicQuery query = null;
		
		query = new BasicQuery(MongoDbQueryFactory.buildContentQuery(contentType, postedBeforeDate, postedAfterDate, 
				includeTags, submitterId, approvalStatus, randomize),
				MongoDbQueryFactory.buildContentSummaryFields());
		
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

		return mongoTemplate.find(query, Content.class);
	}

	@Override
	public Long getContentCount(ContentTypeEnum contentType,
			Date postedBeforeDate, Date postedAfterDate,
			List<String> includeTags, String submitterId, 
			ApprovalStatusEnum approvalStatus) {
		
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildContentQuery(contentType, postedBeforeDate, postedAfterDate, 
				includeTags, submitterId, approvalStatus, null),
				MongoDbQueryFactory.buildContentSummaryFields());
		
		return mongoTemplate.getCollection(CONTENT_COLLECTION).count(query.getQueryObject());
	}

	@Override
	public void insertContentComment(Comment comment, String contentId)
			throws DBUpdateException {
		comment.setCommentId(new ObjectId().toString());
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildIdQuery(contentId)), 
				MongoDbQueryFactory.buildCommentInsert(comment), CONTENT_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}

	@Override
	public void insertContentFavorite(Favorite favorite, String contentId)
			throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildIdQuery(contentId)), 
				MongoDbQueryFactory.buildFavoriteUpdate(favorite), CONTENT_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}

	@Override
	public void insertContentFlag(Flag flag, String contentId)
			throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildIdQuery(contentId)), 
				MongoDbQueryFactory.buildFlagUpdate(flag), CONTENT_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}
	
	@Override
	public List<Content> getFavoritedContents(Date favoritedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFavoritedQuery(favoritedAfter),
				MongoDbQueryFactory.buildContentFlagFavoriteFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Content.class);	
	}

	@Override
	public List<Content> getFlaggedContents(Date flaggedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFlaggedQuery(flaggedAfter),
				MongoDbQueryFactory.buildContentFlagFavoriteFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Content.class);	
	}
	
	@Override
	public List<Content> getFavoritedContentComments(Date favoritedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFavoritedCommentQuery(favoritedAfter),
				MongoDbQueryFactory.buildContentFlagFavoriteFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Content.class);	
	}

	@Override
	public List<Content> getFlaggedContentComments(Date flaggedAfter, Integer page, Integer size) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildFlaggedCommentQuery(flaggedAfter),
				MongoDbQueryFactory.buildContentFlagFavoriteFields());	
		
		if (page != null && size != null) {
			query.limit(size.intValue());
			query.skip(page.intValue() * size.intValue());
		}
		
		query.sort().on("postingDate", Order.DESCENDING);
		return mongoTemplate.find(query, Content.class);	
	}

	@Override
	public void incrementContentViews(String reckoningId)
			throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildIdQuery(reckoningId)), 
				MongoDbQueryFactory.incrementViews(), CONTENT_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}

	@Override
	public void updateComment(Comment comment) throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildCommentIdQuery(comment.getCommentId())), 
				MongoDbQueryFactory.buildCommentUpdate(comment), CONTENT_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}

	@Override
	public void deleteComment(String commentId) throws DBUpdateException {
		WriteResult result = mongoTemplate.updateFirst(new BasicQuery(MongoDbQueryFactory.buildCommentIdQuery(commentId)), 
				MongoDbQueryFactory.deleteByCommentId(commentId), CONTENT_COLLECTION);
		
		if (result.getError() != null) throw new DBUpdateException(result.getError());	
	}

	@Override
	public boolean confirmContentExists(String contentId) {
		BasicQuery query = new BasicQuery(MongoDbQueryFactory.buildIdQuery(contentId), 
				MongoDbQueryFactory.buildIdField());
		if (!mongoTemplate.find(query, Content.class).isEmpty()) {
			return true;	
		}
		return false;
	}
}
