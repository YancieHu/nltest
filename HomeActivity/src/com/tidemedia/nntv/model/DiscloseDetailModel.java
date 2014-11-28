package com.tidemedia.nntv.model;

import java.util.List;

public class DiscloseDetailModel {
	public String status;
	public String message;
	public List<DisClose> result;
	
	public static class DisClose {
		public String id;
		public String uname;
		public String image_url;
		public String content;
		public String mobile;
		public boolean is_video;
		public String video_url;
		public String time;
	}
}
