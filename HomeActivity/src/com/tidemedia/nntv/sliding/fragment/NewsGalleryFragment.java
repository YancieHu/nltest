package com.tidemedia.nntv.sliding.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.ImageDetailActivity;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.model.ImgResonseModel.ImgModel;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;

public class NewsGalleryFragment extends BaseFragment implements
		OnClickListener {
	private NewsModel news;
	private ImageView cover;
	private ImageView videoMark;
	private TextView title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			news = (NewsModel) args.getSerializable("NewsModel");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_gallery_layout, container,
				false);
		cover = (ImageView) view.findViewById(R.id.cover);
		videoMark = (ImageView) view.findViewById(R.id.videoMark);
		title = (TextView) view.findViewById(R.id.title);
		if (news != null) {
			Bitmap bmp = AsyncImageLoader.getInstance().loadDrawable(
					news.getImage_url(), new ImageCallback() {

						@Override
						public void imageLoaded(Bitmap imageDrawable,
								String imageUrl) {
							if (imageDrawable != null) {
								cover.setImageBitmap(imageDrawable);
							}
						}
					});
			if (bmp != null) {
				cover.setImageBitmap(bmp);
			} else {
				cover.setImageResource(R.drawable.ic_nntv_launcher);
			}
			title.setText(news.getTitle());
			if (news.isIs_video()) {
				videoMark.setVisibility(View.VISIBLE);
				view.setOnClickListener(this);
			} else {
				videoMark.setVisibility(View.GONE);
				view.setOnClickListener(pictureOnClickListener);
			}
		}
		return view;
	}

	OnClickListener pictureOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
			intent.putExtra("id", news.getId());
			ImgModel imgModel = new ImgModel();
			imgModel.setId(news.getId());
			imgModel.setTitle(news.getTitle());
			imgModel.setImage_url(news.getImage_url());
			imgModel.setTime(news.getTime());
			intent.putExtra("imageItem", imgModel);
			intent.putExtra("toFlag", "home");
			intent.putExtra("from", "home_gallery");
			startActivity(intent);
		}
	};

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), NewsActivity.class);
		intent.putExtra("newsId", news.getId());
		intent.putExtra("toFlag", "home");
		intent.putExtra("from", "home_gallery");
		startActivity(intent);
	}
}
