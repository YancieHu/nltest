package com.tidemedia.nntv.model;

import java.util.List;

public class NewsResponseModel extends BaseResponseModel {
	
	private NewsResponse result;
	
	public NewsResponse getResult() {
		return result;
	}

	public void setResult(NewsResponse result) {
		this.result = result;
	}

	public class NewsResponse{
		private List<NewsModel> top;
		private List<NewsModel> list;
		public List<NewsModel> getTop() {
			return top;
		}
		public void setTop(List<NewsModel> top) {
			this.top = top;
		}
		public List<NewsModel> getList() {
			return list;
		}
		public void setList(List<NewsModel> list) {
			this.list = list;
		}
	}
}
