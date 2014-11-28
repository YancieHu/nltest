package com.tidemedia.nntv.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.util.ListUtil;

public class JiayouzhanlistAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflator;
	private List<PoiInfo> mList = new ArrayList<PoiInfo>();
	private View mListView;
	TextView topTitle;
	private String from = "";

	public JiayouzhanlistAdapter(Context pContext, List<PoiInfo> poiList,
			View pListView) {
		super();
		mContext = pContext;
		mList = poiList;
		mInflator = LayoutInflater.from(mContext);
		this.mListView = pListView;
	}

	public List<PoiInfo> getList() {
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
		TextView topTitle, textViewDaozhequ, textViewDianhua;
		ImageView imageViewDiv, imageViewDaozhequ, imageViewDianhua;
	}

	public void notifyDataSetChanged(List<PoiInfo> list) {
		// mList.addAll(list);
		mList = list;
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.layout_jiayouzhan_item,
					parent, false);
			holder = new ViewHolder();
			holder.topTitle = (TextView) convertView
					.findViewById(R.id.topTitle);
			holder.textViewDaozhequ = (TextView) convertView
					.findViewById(R.id.textViewDaozhequ);
			holder.textViewDianhua = (TextView) convertView
					.findViewById(R.id.textViewDianhua);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PoiInfo newsModel = mList.get(position);
		holder.topTitle.setText((position + 1) + "." + newsModel.name);
		holder.textViewDaozhequ.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(mContext,
				// BNavigatorActivity.class);
				// Intent intent = new Intent(mContext, DemoMainActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,
				// RGLocationMode.NE_Locate_Mode_GPS);
				// intent.putExtras(bundle);
				// mContext.startActivity(intent);
				// RoutePlanNode startNode = new RoutePlanNode(2253951,
				// 11397348,
				// RoutePlanNode.FROM_MAP_POINT, "华侨城", "华侨城");
				// RoutePlanNode endNode = new RoutePlanNode(2248876, 11392576,
				// RoutePlanNode.FROM_MAP_POINT, "滨海苑", "滨海苑");
				// ArrayList<RoutePlanNode> nodeList = new
				// ArrayList<RoutePlanNode>(
				// 2);
				// nodeList.add(startNode);
				// nodeList.add(endNode);
				// BNRoutePlaner.getInstance().setObserver(
				// new RoutePlanObserver((Activity) mContext, null));
				// // 设置算路方式
				// BNRoutePlaner.getInstance().setCalcMode(
				// NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);
				// // 设置算路结果回调
				// BNRoutePlaner.getInstance().setRouteResultObserver(
				// mRouteResultObserver);
				// // 设置起终点并算路
				// boolean ret =
				// BNRoutePlaner.getInstance().setPointsToCalcRoute(
				// nodeList, NL_Net_Mode.NL_Net_Mode_OnLine);
				// if (!ret) {
				// Toast.makeText(mContext, "规划失败", Toast.LENGTH_SHORT).show();
				// }
			}
		});
		holder.textViewDianhua.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (newsModel.phoneNum != null
						&& newsModel.phoneNum.length() != 0) {
					Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + newsModel.phoneNum));
					mContext.startActivity(phoneIntent);
				} else {
					// Toast.makeText(mContext, "没有电话号码", Toast.LENGTH_LONG)
					// .show();
					Intent intent = new Intent(Intent.ACTION_DIAL);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			}
		});
		return convertView;
	}

}
