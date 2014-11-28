package com.tidemedia.nntv.model;

import java.io.Serializable;
import java.util.List;

public class VoteListResponseModel extends BaseResponseModel {
	public List<VoteEntity> result;
	
	public static class VoteEntity implements Serializable {
		private static final long serialVersionUID = 77746954577105761L;
		public int id;
		public String title;
		public String image_url;
		public String date;
		public String is_multiple;
		public List<Item> items;
		
		public int total() {
			if (items == null) {
				return 0;
			}
			int count = 0;
			for (Item item : items) {
				count += item.number;
			}
			return count;
		}
	}
	
	public static class Item implements Serializable {
		private static final long serialVersionUID = 3554575393097209539L;
		public int id;
		public String title;
		public int number;
	}
}
