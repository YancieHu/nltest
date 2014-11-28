package com.tidemedia.nntv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.SearchResponseModel.SearchResultItem;

public class SearchListAdapter extends BaseAdapter {
	private List<SearchResultItem> list;
	private LayoutInflater inflater;
	
	public SearchListAdapter(Context context, List<SearchResultItem> list) {
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public SearchResultItem getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null	) {
			convertView = inflater.inflate(R.layout.search_list_item, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		SearchResultItem disclose = list.get(position);
		if (disclose != null) {
			holder.title.setText(disclose.title);
		} else {
			holder.title.setText(null);
		}
		return convertView;
	}
	
	private class ViewHolder {
		TextView title;
	}

}
