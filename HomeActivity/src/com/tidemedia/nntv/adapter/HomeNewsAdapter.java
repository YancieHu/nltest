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
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;

public class HomeNewsAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<NewsModel> list;
	
	public HomeNewsAdapter(Context context, List<NewsModel> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public NewsModel getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.home_news_item, parent, false);
			holder = new ViewHolder();
			holder.cover = (ImageView) convertView.findViewById(R.id.cover);
			holder.videoMark = (ImageView) convertView.findViewById(R.id.videoMark);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NewsModel news = list.get(position);
		final ImageView cover = holder.cover;
		final TextView title = holder.title;
		if (news == null) {
			cover.setImageResource(0);
			title.setText(null);
		} else {
			Bitmap bmp = AsyncImageLoader.getInstance().loadDrawable(news.getImage_url(), new ImageCallback() {
				
				@Override
				public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
					if (imageDrawable != null) {
						cover.setImageBitmap(imageDrawable);
					}
				}
			});
			if (bmp != null) {
				cover.setImageBitmap(bmp);
			} else {
				cover.setImageResource(R.drawable.default_img);
			}
			title.setText(news.getTitle());
			if (news.isIs_video()) {
				holder.videoMark.setVisibility(View.VISIBLE);
			} else {
				holder.videoMark.setVisibility(View.GONE);
			}
		}
		return convertView;
	}
	
	class ViewHolder {
		ImageView cover;
		ImageView videoMark;
		TextView title;
	}

}
