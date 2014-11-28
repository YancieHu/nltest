package com.tidemedia.nntv.model;

import java.util.List;

public class CommentListResponseModel extends BaseResponseModel {
	public List<Comment> result;
	
	public static class Comment {
		public String id;
		public String userid;
		public String username;
		public String content;
		public String time;
	}
}
