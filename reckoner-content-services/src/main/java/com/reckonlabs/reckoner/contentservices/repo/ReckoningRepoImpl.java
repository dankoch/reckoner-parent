package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public class ReckoningRepoImpl implements ReckoningRepoCustom {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	public static final String RECKONING_COLLECTION = "reckonings";
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckoningRepoImpl.class);
	
	public void insertNewReckoning (Reckoning reckoning) {
		mongoTemplate.insert(reckoning, RECKONING_COLLECTION);
	}

}
