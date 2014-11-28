package com.tidemedia.nntv.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.LiveResponseModel.Live;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;
import com.tidemedia.nntv.util.StringUtil;

public class LiveAdapter extends BaseAdapter {
	
	private List<Live> mList;
	private Context mContext;
	private LayoutInflater mInflator;
	private View mListView;
	
	public LiveAdapter(Context pContext, List<Live> imgList, View pListView) {
		super();
		this.mContext = pContext;
		mList = imgList;
		mInflator = LayoutInflater.from(mContext);
		this.mListView = pListView;
	}

	public List<Live> getList(){
		return mList;
	}

	@Override
	public int getCount() {
		if (null != mList && mList.size() > 0) {
			return mList.size();
		}
		return 0;
	}

	@Override
	public Live getItem(int position) {
		if (null != mList && mList.size() > position) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder{
		ImageView columnImage,ic_video;
		TextView columnTitle, columnIntro;
	}
	
	public void notifyDataSetChanged(List<Live> list) {
		mList.addAll(list);
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.column_first_item_layout, null);
			holder = new ViewHolder();
			holder.columnImage = (ImageView)convertView.findViewById(R.id.columnImage);
			holder.columnTitle = (TextView)convertView.findViewById(R.id.columnTitle);
			holder.columnIntro = (TextView)convertView.findViewById(R.id.columnIntro);
			holder.ic_video = (ImageView)convertView.findViewById(R.id.ic_video);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.columnIntro.setVisibility(View.GONE);
		holder.ic_video.setVisibility(View.GONE);
		Live live = mList.get(position);
		if(StringUtil.isNotEmpty(live.getImage_url())){
			String url = live.getImage_url();
			holder.columnImage.setTag(url);
			
			Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {
				@Override
				public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
					ImageView imageView = (ImageView)mListView.findViewWithTag(imageUrl);
					if(imageDrawable!=null && imageView!=null){
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
		holder.columnTitle.setText(live.getTitle());
		return convertView;
	}
}
