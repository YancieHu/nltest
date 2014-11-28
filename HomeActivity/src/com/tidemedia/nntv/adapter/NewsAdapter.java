package com.tidemedia.nntv.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.model.NewsResponseModel;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;
import com.tidemedia.nntv.util.ListUtil;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.view.ChildViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public class NewsAdapter extends BaseAdapter implements OnPageChangeListener {
	
	private Context mContext;
	private LayoutInflater mInflator;
	private List<NewsModel> topList = new ArrayList<NewsModel>();
	private List<NewsModel> mList = new ArrayList<NewsModel>();
	private View mListView;
	private List<ImageView> viewList = new ArrayList<ImageView>();
	TextView topTitle;
	
	private String from = "";
	
	public NewsAdapter(Context pContext, NewsResponseModel mNewsResponseModel, View pListView) {
		super();
		this.mContext = pContext;
		if (mNewsResponseModel != null && mNewsResponseModel.getResult() != null) {
			if (mNewsResponseModel.getResult().getTop() != null) {
				topList = mNewsResponseModel.getResult().getTop();
			}
			if (mNewsResponseModel.getResult().getList() != null) {
				mList = mNewsResponseModel.getResult().getList();
			}
		}
		mInflator = LayoutInflater.from(mContext);
		this.mListView = pListView;
	}
	
	public List<NewsModel> getList(){
		return mList;
	}

	@Override
	public int getCount() {
//		if (null != mList && mList.size() > 0) {
//			return mList.size();
//		}
//		return 0;
		if (ListUtil.isNotEmpty(topList)) {
			if (null != mList && mList.size() > 0) {
				return mList.size() + 1;
			} else {
				return 1;
			}
		} else {
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
	
	class ViewHolder{
		RelativeLayout relativeLayout;
		FrameLayout topLayout;
		TextView topTitle;
		LinearLayout downLayout;
		ImageView downImageView, videoMark;
		TextView downTextView;
		ChildViewPager newsGallery;
		CirclePageIndicator indicator;
	}


	public void notifyDataSetChanged(List<NewsModel> list) {
		mList.addAll(list);
		super.notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.news_item_layout, null);
			holder = new ViewHolder();
			holder.newsGallery = (ChildViewPager)convertView.findViewById(R.id.newsGallery);
			holder.indicator = (CirclePageIndicator)convertView.findViewById(R.id.indicator);
			holder.topLayout = (FrameLayout)convertView.findViewById(R.id.topLayout);
			holder.topTitle = (TextView) convertView.findViewById(R.id.topTitle);
			holder.downLayout = (LinearLayout)convertView.findViewById(R.id.downLayout);
			//holder.topImageView = (ImageView)convertView.findViewById(R.id.topImageView);
			holder.downImageView = (ImageView)convertView.findViewById(R.id.downImageView);
			//holder.topTextView = (TextView)convertView.findViewById(R.id.topTextView);
			holder.downTextView = (TextView)convertView.findViewById(R.id.downTextView);
			holder.videoMark = (ImageView)convertView.findViewById(R.id.videoMark);
			holder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.relativeLayout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// 存在轮播图的情况
		if (ListUtil.isNotEmpty(topList)) {
			// 轮播图
			if(0 == position){
				topTitle = holder.topTitle;
				holder.downLayout.setVisibility(View.GONE);
				if(ListUtil.isNotEmpty(topList)){
					viewList.clear();
					holder.topLayout.setVisibility(View.VISIBLE);
					for (final NewsModel newsModel : topList) {
						final ImageView imageView = new ImageView(mContext);
						imageView.setScaleType(ScaleType.FIT_XY);
						if(StringUtil.isNotEmpty(newsModel.getImage_url())){
							String url = newsModel.getImage_url();
							Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {
								@Override
								public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
									if(imageDrawable!=null && imageView!=null){
										imageView.setImageBitmap(imageDrawable);
									}
								}
							});
							if (bitmap == null) {
								imageView.setImageResource(R.drawable.default_img);
							} else {
								imageView.setImageBitmap(bitmap);
							}
						}
						imageView.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(mContext, NewsActivity.class);
								intent.putExtra("newsId", newsModel.getId());
								if ("travel".equals(from) || "food".equals(from)) {
									intent.putExtra("toFlag", from);
								} else {
									intent.putExtra("toFlag", "news");
								}
								mContext.startActivity(intent);
							}
						});
						viewList.add(imageView);
					}
					topTitle.setText(topList.get(0).getTitle());
					holder.newsGallery.setAdapter(pageAdapter);
					holder.newsGallery.setOnPageChangeListener(this);
					holder.indicator.setViewPager(holder.newsGallery);
					holder.indicator.setOnPageChangeListener(this);
				}else{
					holder.topLayout.setVisibility(View.GONE);
				}
			}else{
				NewsModel newsModel = mList.get(position - 1);
				holder.topLayout.setVisibility(View.GONE);
				holder.downLayout.setVisibility(View.VISIBLE);
				if(StringUtil.isNotEmpty(newsModel.getImage_url())){
					holder.relativeLayout.setVisibility(View.VISIBLE);
					String url = newsModel.getImage_url();
					holder.downImageView.setTag(url);
					
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
		                holder.downImageView.setImageResource(R.drawable.default_150_70);
					} else {
						holder.downImageView.setImageBitmap(bitmap);
					}
				}else{
					holder.relativeLayout.setVisibility(View.GONE);
				}
				holder.downTextView.setText(newsModel.getTitle());
				if(newsModel.isIs_video()){
					holder.videoMark.setVisibility(View.VISIBLE);
				}else{
					holder.videoMark.setVisibility(View.GONE);
				}
			}
		} else {
			NewsModel newsModel = mList.get(position);
			holder.topLayout.setVisibility(View.GONE);
			holder.downLayout.setVisibility(View.VISIBLE);
//			if(StringUtil.isNotEmpty(newsModel.getImage_url())){
				holder.relativeLayout.setVisibility(View.VISIBLE);
				String url = newsModel.getImage_url();
				holder.downImageView.setTag(url);
				
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
	                holder.downImageView.setImageResource(R.drawable.default_150_70);
				} else {
					holder.downImageView.setImageBitmap(bitmap);
				}
//			}else{
//				holder.relativeLayout.setVisibility(View.GONE);
//			}
			holder.downTextView.setText(newsModel.getTitle());
			if(newsModel.isIs_video()){
				holder.videoMark.setVisibility(View.VISIBLE);
			}else{
				holder.videoMark.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}
	
	private PagerAdapter pageAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewList.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(viewList.get(position));
			return viewList.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(viewList.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
		topTitle.setText(topList.get(position).getTitle());
	}
	
	public void setFrom(String from) {
		this.from = from;
	}

}
