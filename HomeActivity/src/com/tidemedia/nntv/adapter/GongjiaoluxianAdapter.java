package com.tidemedia.nntv.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.busline.BusLineResult.BusStation;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.util.ListUtil;

public class GongjiaoluxianAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflator;
	private List<BusStation> mList = new ArrayList<BusStation>();
	private View mListView;
	TextView topTitle;

	private String from = "";

	public GongjiaoluxianAdapter(Context pContext, List<BusStation> list,
			View pListView) {
		super();
		this.mContext = pContext;
		mList = list;
		mInflator = LayoutInflater.from(mContext);
		this.mListView = pListView;
	}

	public List<BusStation> getList() {
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
		TextView imageViewGongjiaozhan;
		public ImageView imageViewClock;
	}

	public void notifyDataSetChanged(List<BusStation> list) {
		// mList.addAll(list);
		mList = list;
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(
					R.layout.layout_gongjiaoluxian_item, parent, false);
			holder = new ViewHolder();
			holder.topTitle = (TextView) convertView
					.findViewById(R.id.topTitle);
			holder.imageViewGongjiaozhan = (TextView) convertView
					.findViewById(R.id.imageViewGongjiaozhan);
			holder.imageViewClock = (ImageView) convertView
					.findViewById(R.id.imageViewClock);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BusStation newsModel = mList.get(position);
		holder.topTitle.setText(newsModel.getTitle());
		holder.imageViewGongjiaozhan.setText("" + position);
		if (position == 0) {
			holder.imageViewGongjiaozhan.setText("");
			holder.imageViewGongjiaozhan
					.setBackgroundResource(R.drawable.bus_line_07);
		} else if (position == mList.size() - 1) {
			holder.imageViewGongjiaozhan.setText("");
			holder.imageViewGongjiaozhan
					.setBackgroundResource(R.drawable.bus_line_29);
		}
		// holder.imageViewClock.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// holder.imageViewClock.setImageResource(R.drawable.bus_line_03);
		// }
		// });
		return convertView;
	}
}
