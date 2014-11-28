package com.tidemedia.nntv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.DiscloseListResponseModel.DiscloseEntity;

public class DisposeListAdapter extends BaseAdapter {
	private List<DiscloseEntity> list;
	private LayoutInflater inflater;
	
	public DisposeListAdapter(Context context, List<DiscloseEntity> list) {
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public DiscloseEntity getItem(int position) {
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
			convertView = inflater.inflate(R.layout.disclose_list_item, parent, false);
			holder = new ViewHolder();
			holder.discloseTitle = (TextView) convertView.findViewById(R.id.discloseTitle);
			holder.discloseTime = (TextView) convertView.findViewById(R.id.discloseTime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.discloseTitle.setText(null);
		holder.discloseTime.setText(null);
		DiscloseEntity disclose = list.get(position);
		if (disclose != null) {
			holder.discloseTitle.setText(disclose.title);
			holder.discloseTime.setText(disclose.time);
		}
		return convertView;
	}
	
	private class ViewHolder {
		TextView discloseTitle;
		TextView discloseTime;
	}

}
