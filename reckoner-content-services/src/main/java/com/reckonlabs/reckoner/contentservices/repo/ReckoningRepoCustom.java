package com.reckonlabs.reckoner.contentservices.repo;

import java.util.Date;
import java.util.List;

import com.reckonlabs.reckoner.domain.reckoning.Reckoning;

public interface ReckoningRepoCustom {
	
	public void insertNewReckoning (Reckoning reckoning);
	
	public List<Reckoning> getReckoningSummariesByPostingDate (Integer page, Integer size, Date beforeDate, Date afterDate);
	
	public List<Reckoning> getReckoningSummariesByClosingDate (Integer page, Integer size, Date beforeDate, Date afterDate);
	
	public List<Reckoning> getReckoningSummariesByTag (String tag, Integer page, Integer size);
	
	public List<Reckoning> getReckoningSummaries (Integer page, Integer size);
}
