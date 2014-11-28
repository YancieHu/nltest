package com.tidemedia.nntv.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.SubColumnResponseModel.SubColumn;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;

public class ColumnSecondAdapter extends BaseAdapter {

	private Context mContext;
	private List<SubColumn> mList;
	private ListView mListView;
	private LayoutInflater inflater;

	public ColumnSecondAdapter(Context pContext, List<SubColumn> pList,
			ListView pListView) {
		this.mContext = pContext;
		this.mList = pList;
		this.mListView = pListView;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	public void notifyDataSetChanged(List<SubColumn> list) {
		mList.addAll(list);
		super.notifyDataSetChanged();
	}

	class ViewHolder {
		ImageView columnImage, ic_video;
		TextView columnTitle, columnIntro;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.column_first_item_layout, null);
			holder = new ViewHolder();
			holder.columnImage = (ImageView) convertView
					.findViewById(R.id.columnImage);
			holder.columnTitle = (TextView) convertView
					.findViewById(R.id.columnTitle);
			holder.columnIntro = (TextView) convertView
					.findViewById(R.id.columnIntro);
			holder.ic_video = (ImageView) convertView
					.findViewById(R.id.ic_video);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SubColumn subColumn = mList.get(position);
		holder.columnTitle.setText(subColumn.getTitle());
		holder.columnIntro.setVisibility(View.GONE);
		//holder.columnIntro.setText(subColumn.getIntro());
		if(subColumn.isIs_video()){
			holder.ic_video.setVisibility(View.VISIBLE);
		}else{
			holder.ic_video.setVisibility(View.GONE);
		}
		String url = subColumn.getImage_url();
		if (StringUtil.isNotEmpty(url)) {
			holder.columnImage.setTag(url);

			Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(url,
					new ImageCallback() {
						@Override
						public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
							ImageView imageView = (ImageView) mListView.findViewWithTag(imageUrl);
							if (imageDrawable != null && imageView != null) {
								imageView.setImageBitmap(imageDrawable);
							}
						}
					});
			if (bitmap == null) {
				holder.columnImage.setImageResource(R.drawable.default_img);
			} else {
				holder.columnImage.setImageBitmap(bitmap);
			}
		}
		return convertView;
	}

}
