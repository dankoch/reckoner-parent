package com.reckonlabs.reckoner.domain.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateFormatAdapter extends XmlAdapter<String, Date> {

	public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	@Override
	public Date unmarshal(String dateString) throws ParseException {
		return new SimpleDateFormat(DATE_PATTERN).parse(dateString);
	}

	@Override
	public String marshal(Date date) {
		return new SimpleDateFormat(DATE_PATTERN).format(date);
	}
}