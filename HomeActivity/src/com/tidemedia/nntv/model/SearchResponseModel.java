package com.tidemedia.nntv.model;

import java.util.List;

public class SearchResponseModel extends BaseResponseModel {
	public List<SearchResultItem> result;
	
	public static class SearchResultItem {
		public int type;
		public String id;
		public String title;
		public String time;
	}
}
