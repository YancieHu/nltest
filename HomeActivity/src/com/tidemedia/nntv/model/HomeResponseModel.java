package com.tidemedia.nntv.model;

import java.util.List;

public class HomeResponseModel extends BaseResponseModel {

	private Result result;
	
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public class Result{
		List<NewsModel> top;
		List<NewsModel> list;
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
