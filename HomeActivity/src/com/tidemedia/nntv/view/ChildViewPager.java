package com.tidemedia.nntv.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ChildViewPager extends ViewPager {

	public ChildViewPager(Context context) {  
        super(context);  
    }  
  
    public ChildViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    //PointF downPoint = new PointF();  
   // OnSingleTouchListener onSingleTouchListener;  
    
    private float xDistance, yDistance, xLast, yLast;
	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    xDistance = yDistance = 0f;
                    xLast = ev.getX();
                    yLast = ev.getY();
                    break;
            case MotionEvent.ACTION_MOVE:
                    final float curX = ev.getX();
                    final float curY = ev.getY();

                    xDistance += Math.abs(curX - xLast);
                    yDistance += Math.abs(curY - yLast);
                    xLast = curX;
                    yLast = curY;

                    if (xDistance < yDistance) {;
                            return false;   //表示向下传递事件
                    }
            }

            return super.onInterceptTouchEvent(ev);
    }
  
   /* @Override  
    public boolean onTouchEvent(MotionEvent evt) {  
        switch (evt.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            // 记录按下时候的坐标  
            downPoint.x = evt.getX();  
            downPoint.y = evt.getY();  
            if (this.getChildCount() > 1) { //有内容，多于1个时  
                // 通知其父控件，现在进行的是本控件的操作，不允许拦截  
                getParent().requestDisallowInterceptTouchEvent(true);  
            }  
            break;  
        case MotionEvent.ACTION_MOVE:  
            if (this.getChildCount() > 1) { //有内容，多于1个时  
                // 通知其父控件，现在进行的是本控件的操作，不允许拦截  
                getParent().requestDisallowInterceptTouchEvent(true);  
            }  
            break;  
        case MotionEvent.ACTION_UP:  
            // 在up时判断是否按下和松手的坐标为一个点  
            if (PointF.length(evt.getX() - downPoint.x, evt.getY()  
                    - downPoint.y) < (float) 5.0) {  
                onSingleTouch(this);  
                return true;  
            }  
            break;  
        }  
        return super.onTouchEvent(evt);  
    }  
  
    public void onSingleTouch(View v) {  
        if (onSingleTouchListener != null) {  
            onSingleTouchListener.onSingleTouch(v);  
        }  
    }  
  
    public interface OnSingleTouchListener {  
        public void onSingleTouch(View v);  
    }  
  
    public void setOnSingleTouchListener(  
            OnSingleTouchListener onSingleTouchListener) {  
        this.onSingleTouchListener = onSingleTouchListener;  
    }  */

}
