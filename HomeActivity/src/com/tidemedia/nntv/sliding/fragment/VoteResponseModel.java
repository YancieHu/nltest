package com.tidemedia.nntv.sliding.fragment;

import java.util.List;

public class VoteResponseModel {
	public String status;
	public String message;
	public Result result;
	
	public static class Result {
		public int sum;
		public List<PullItem> poll_items;
	}
	
	public static class PullItem {
		public String poll_item_id;
		public int number;
		public int percent;
	}
}
