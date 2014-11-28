package com.tidemedia.nntv.model;

import java.io.Serializable;
import java.util.List;

public class CategoryResponse extends BaseResponseModel {
	
	private Cresult result;
	
	public Cresult getResult() {
		return result;
	}

	public void setResult(Cresult result) {
		this.result = result;
	}

	public class Cresult {
		List<Category> list;

		public List<Category> getList() {
			return list;
		}

		public void setList(List<Category> list) {
			this.list = list;
		}

	}

	public class Category implements Serializable {
		private String title;
		private String cat_id;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getCat_id() {
			return cat_id;
		}

		public void setCat_id(String cat_id) {
			this.cat_id = cat_id;
		}

	}
}
