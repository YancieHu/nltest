package com.tidemedia.nntv.model;

import java.util.List;

public class DiscloseListResponseModel {
	public String status;
	public String message;
	public int total;
	public List<DiscloseEntity> result;

	public static class DiscloseEntity {
		public String id;
		public String title;
		public String time;

		@Override
		public String toString() {
			return "id:" + id + ", title:" + title + ", time:" + time;
		}
	}
}
