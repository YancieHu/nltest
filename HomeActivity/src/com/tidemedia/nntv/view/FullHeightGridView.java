package com.tidemedia.nntv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class FullHeightGridView extends GridView {

	public FullHeightGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FullHeightGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FullHeightGridView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int fullHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 1, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, fullHeightMeasureSpec);
	}
}
