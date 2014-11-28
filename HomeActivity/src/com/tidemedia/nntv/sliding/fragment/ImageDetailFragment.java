package com.tidemedia.nntv.sliding.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.ImgResModel.ImgDetail;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;

public class ImageDetailFragment extends BaseFragment {
	private List<ImgDetail> mList;
	private ViewPager viewPager;
	private List<View> viewList = new ArrayList<View>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		viewPager = new ViewPager(getActivity());
		initData(inflater);
		viewPager.setAdapter(pageAdapter);
		return viewPager;
	}
	
	private void initData(LayoutInflater inflater){
		int totalSize = mList.size();
		for (int i = 0; i < totalSize; i++) {
			ImgDetail imgDetail = mList.get(i);
			View subView = inflater.inflate(R.layout.image_detail_layout, null);
			final ImageView detailImage = (ImageView)subView.findViewById(R.id.detailImage);
			TextView size = (TextView)subView.findViewById(R.id.size);
			TextView title = (TextView)subView.findViewById(R.id.title);
			TextView intro = (TextView)subView.findViewById(R.id.intro);
			size.setText((i + 1)+ "/" + totalSize);
			title.setText(imgDetail.getTitle());
			intro.setText(imgDetail.getIntro());
			if(StringUtil.isNotEmpty(imgDetail.getImage_url())){
				String url = imgDetail.getImage_url();
				
				Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {
					@Override
					public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
						if(imageDrawable!=null && detailImage!=null){
							detailImage.setImageBitmap(imageDrawable);
						}
					}
				});
				if (bitmap == null) {
					detailImage.setImageResource(R.drawable.default_img);
				} else {
					detailImage.setImageBitmap(bitmap);
				}
			}
			viewList.add(subView);
		}
	}
	
	public ImageDetailFragment(List<ImgDetail> pList){
		this.mList = pList;
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
}
