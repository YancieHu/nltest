package com.tidemedia.nntv.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.adapter.NewsAdapter.ViewHolder;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.model.ImgResonseModel.ImgModel;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;
import com.tidemedia.nntv.view.CustomListView;

public class ImgeAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflator;
	private List<ImgModel> mList = new ArrayList<ImgModel>();
	private View mListView;
	
	public ImgeAdapter(Context pContext, List<ImgModel> imgList, View pListView) {
		super();
		this.mContext = pContext;
		mList = imgList;
		mInflator = LayoutInflater.from(mContext);
		this.mListView = pListView;
	}
	
	public List<ImgModel> getList(){
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
	
	class ViewHolder{
		ImageView imageView;
		TextView imgDesc;
	}
	
	public void notifyDataSetChanged(List<ImgModel> list) {
		mList.addAll(list);
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.image_item_layout, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
			holder.imgDesc = (TextView)convertView.findViewById(R.id.imgDesc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImgModel imgModel = mList.get(position);
		if(null != imgModel){
			if(StringUtil.isNotEmpty(imgModel.getImage_url())){
				String url = imgModel.getImage_url();
				holder.imageView.setTag(url);
				
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
	                holder.imageView.setImageResource(R.drawable.default_img);
				} else {
					holder.imageView.setImageBitmap(bitmap);
				}
			}
			holder.imgDesc.setText(imgModel.getTitle());
		}
		return convertView;
	}

}
