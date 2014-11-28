package com.tidemedia.nntv.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.WeatherModel;
import com.tidemedia.nntv.util.ListUtil;

public class WeatherJingdianAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflator;
	private List<WeatherModel> mList = new ArrayList<WeatherModel>();
	private View mListView;
	TextView topTitle;

	private String from = "";

	public WeatherJingdianAdapter(Context pContext, List<WeatherModel> list,
			View pListView) {
		super();
		this.mContext = pContext;
		mList = list;
		mInflator = LayoutInflater.from(mContext);
		this.mListView = pListView;
	}

	public List<WeatherModel> getList() {
		return mList;
	}

	@Override
	public int getCount() {
		if (ListUtil.isNotEmpty(mList)) {
			if (null != mList && mList.size() > 0) {
				return mList.size();
			}
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != mList && mList.size() > 0) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView topTitle;
	}

	public void notifyDataSetChanged(List<WeatherModel> list) {
		// mList.addAll(list);
		mList = list;
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.layout_weather_item,
					parent, false);
			holder = new ViewHolder();
			holder.topTitle = (TextView) convertView
					.findViewById(R.id.topTitle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		WeatherModel newsModel = mList.get(position);
		holder.topTitle.setText(newsModel.getName());
		return convertView;
	}
}
