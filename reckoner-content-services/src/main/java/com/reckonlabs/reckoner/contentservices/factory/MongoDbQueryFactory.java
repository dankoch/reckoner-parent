package com.reckonlabs.reckoner.contentservices.factory;

import java.util.Date;

import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;

import com.reckonlabs.reckoner.domain.utility.DateUtility;

public final class MongoDbQueryFactory {

	public static DBObject buildValidReckoningQuery() {
		return new BasicDBObject("rejected", false);
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
	
	public static DBObject buildReckoningTagQuery (String tag) {
		return new BasicDBObject("tags", tag);
	}
	
	public static DBObject buildReckoningSummaryFields() {
		BasicDBObject fields = new BasicDBObject("id", 1);
		fields.append("question", 1);
		fields.append("postingDate", 1);
		fields.append("closingDate", 1);
		fields.append("submitterId", 1);
		
		return fields;
	}
}
