package com.reckonlabs.reckoner.domain.utility;

import java.util.List;

public final class ListPagingUtility {

	public static final List<?> pageList (List<?> list, Integer page, Integer size) {
		if (page != null && size != null) {
			int beginIndex = page * size;
			int endIndex = beginIndex + size;
			
			if (beginIndex >= list.size()) {
				list.clear();
			} else if (endIndex >= list.size()) {
				endIndex = list.size();
				list = list.subList(beginIndex, endIndex);
			} else {
				list = list.subList(beginIndex, endIndex);
			}
		}
		
		return list;
	}
}
