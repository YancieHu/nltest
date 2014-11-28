package com.tidemedia.nntv.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

public class ScreenUtil 
{
	private static DisplayMetrics mMetrics = new DisplayMetrics();
	
    public ScreenUtil(Activity activity){
        activity.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }
	public ScreenUtil(WindowManager windowManager)
	{
		windowManager.getDefaultDisplay().getMetrics(mMetrics);
//		this.widthPixels = dm.widthPixels; //display.getWidth();
//		this.heightPixels = dm.heightPixels;//display.getHeight();  
	}
	public ScreenUtil(Context context){
		mMetrics = context.getResources().getDisplayMetrics();
    }
	public int getScreenHeight() {
		return mMetrics.heightPixels;
	}	

	public int getScreenWidth() {
		return mMetrics.widthPixels;
	}
	
    public int dip2px(float dpValue) {  
        final float scale = mMetrics.density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
    	DisplayMetrics c = context.getResources().getDisplayMetrics();
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 
    
    public static void saveSDcard(Bitmap bitmap,String filename){
		try {
			FileOutputStream outStream = new FileOutputStream(filename);
			if(outStream != null){
				bitmap.compress(Bitmap.CompressFormat.PNG, 50, outStream);
				outStream.flush();
				outStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
