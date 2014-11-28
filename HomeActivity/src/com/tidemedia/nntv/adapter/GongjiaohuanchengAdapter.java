package com.tidemedia.nntv.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.util.ListUtil;

public class GongjiaohuanchengAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflator;
	private List<TransitRouteLine> mList = new ArrayList<TransitRouteLine>();
	private View mListView;
	TextView topTitle;

	private String from = "";

	public GongjiaohuanchengAdapter(Context pContext,
			List<TransitRouteLine> routeLines, View pListView) {
		super();
		this.mContext = pContext;
		mList = routeLines;
		mInflator = LayoutInflater.from(mContext);
		this.mListView = pListView;
	}

	public List<TransitRouteLine> getList() {
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

	public void notifyDataSetChanged(List<TransitRouteLine> list) {
		// mList.addAll(list);
		mList = list;
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(
					R.layout.layout_gongjiaohuancheng_item, parent, false);
			holder = new ViewHolder();
			holder.topTitle = (TextView) convertView
					.findViewById(R.id.topTitle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TransitRouteLine newsModel = mList.get(position);
		List<TransitStep> allStep = newsModel.getAllStep();
		String text = "";
		for (TransitStep transitStep : allStep) {
			String instructions = transitStep.getInstructions();
			// VehicleInfo vehicleInfo = transitStep.getVehicleInfo();
			text += instructions + "\n";
		}
		holder.topTitle.setText(text.trim());
		return convertView;
	}
}
