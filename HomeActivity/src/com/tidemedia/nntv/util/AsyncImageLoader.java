package com.tidemedia.nntv.util;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tidemedia.nntv.common.TvApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
	//private static Map<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	private Map<String,SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
//	 static ThreadPool threadPool_image = new ThreadPool(2);
	public ExecutorService pool = null;//Executors.newFixedThreadPool(2); 
	private boolean allowLoad = true;

	private final static String imageCacheFolderName = "imageCache";
	private final static int MAX_WIDTH_PIXEL = 1024;
	private final static int MAX_HEIGHT_PIXEL = 1024;
	private final static String TAG = "AsyncImageLoader";
	
	private AsyncImageLoader(){
		
	};
	
	private static AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
	
	public static AsyncImageLoader getInstance(){
		return asyncImageLoader;
	}
	
	public void restore(){
		this.allowLoad = true;
	}
	public void lock()
	{
		this.allowLoad = false;
	}
	public void unlock()
	{
		this.allowLoad = true;
		//
	}

	public void clearImageMap() {
		// if(imageCache!=null && imageCache.size()>100){//当缓存大于100的时候
		imageCache.clear();
		// }
	}

	public static void clearImageCacheDir(){
		File file = new File(TvApplication.getInstance().getDirPath(imageCacheFolderName));
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				item.delete();
			}
			//file.delete();
		}
		
//			if (avaiableSdcard()) {// 如果有sd卡，读取sd卡
//				File file = new File(TuJiaApplication.mSdcardDataDir + TuJiaApplication.mImageCachePath);
//			   if (file != null && file.exists() && file.isDirectory()) {
//			    for (File item : file.listFiles()) {
//			     item.delete();
//			    }
//			    file.delete();
//			   }
//			} else {// 如果没有sd卡，读取手机里面
//				File file = new File(TuJiaApplication.mDataDir + TuJiaApplication.mImageCachePath);
//				   if (file != null && file.exists() && file.isDirectory()) {
//				    for (File item : file.listFiles()) {
//				     item.delete();
//				    }
//				    file.delete();
//				   }
//			}

	}
	
	public static double getImageSize(){
		long size = 0;
		File file = new File(TvApplication.getInstance().getDirPath(imageCacheFolderName));
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				  FileInputStream fis;
				try {
					fis = new FileInputStream(item);
					size += fis.available();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
			}
		}
		return size / 1014 /1024;
	}
	
	public Bitmap loadDrawableFromCache(final String imageUrl) {

		Bitmap drawable = null;
        final String fileName = StringUtil.md5(imageUrl);

		//1、从内存中取bitmap
		if(imageCache.containsKey(fileName)){
			SoftReference<Bitmap> softReference = imageCache.get(fileName);
			drawable = softReference.get();
		}		
		if(drawable != null){
			LogUtil.v(TAG,"get bitmap from cache");
			return drawable;
		}
		//2、从缓存中取bitmap
		drawable = getPicByPath(TvApplication.getInstance().getDirPath(imageCacheFolderName), fileName);
		if (drawable != null) {
			SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(drawable);
			imageCache.put(fileName, softReference);
			LogUtil.v(TAG,"get bitmap from file");
			return drawable;
		}
		return null;
	}
	public Bitmap loadDrawable(final String imageUrl, final ImageCallback callback) {

		if(StringUtil.isEmpty(imageUrl))
			return null;
		Bitmap drawable = null;
        final String fileName = StringUtil.md5(imageUrl);

		//1、从内存中取bitmap
		if(imageCache.containsKey(fileName)){
			SoftReference<Bitmap> softReference = imageCache.get(fileName);
			drawable = softReference.get();
		}		
		if(drawable != null){
			LogUtil.v(TAG,"get bitmap from cache");
			return drawable;
		}
		//2、从缓存中取bitmap
		drawable = getPicByPath(TvApplication.getInstance().getDirPath(imageCacheFolderName), fileName);
		if (drawable != null) {
			SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(drawable);
			imageCache.put(fileName, softReference);
			LogUtil.v(TAG,"get bitmap from file");
			return drawable;
		}	
		//3、从网络取bitmap
		if(allowLoad){
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Bitmap) msg.obj, imageUrl);
			}
		};
		Runnable task = new Runnable() {
			public void run() {
				Bitmap drawable = loadImageFromUrl(imageUrl, fileName);
				handler.sendMessage(handler.obtainMessage(0, drawable));
			};
		};
		getThreadPool().execute(task);
		}
		return null;
	}

	/**
	 * 判断是否存在sd卡
	 * 
	 * @return
	 */
	public static boolean avaiableSdcard() {
		String status = Environment.getExternalStorageState();

		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

    /**
     * 获取图片路径，如果是null，标识该图片不在路径上
     *
     */
    public static String getPicPath(String url) {
        String filePath =  TvApplication.getInstance().getDirPath(imageCacheFolderName) + "/" + StringUtil.md5(url);
        //LogUtil.d("mmm", filePath);
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return filePath;
            }
        } catch (Exception e) {

        }

        return null;
    }

	/**
	 * 获取图片
	 * 
	 * @param picName
	 * @return
	 */
	public static Drawable getPic_Draw_ByPath(String path, String picName) {
		//picName = picName.substring(picName.lastIndexOf("/") + 1);
		String filePath = path + "/" + picName;
		return Drawable.createFromPath(filePath);
	}

	/**
	 * 获取图片
	 * 
	 * @param picName
	 * @return
	 */
	public static Bitmap getPicByPath(String path, String picName) {
		if (!"".equals(picName) && picName != null) {
			// picName = picName.substring(picName.lastIndexOf("/") + 1);
			String filePath = path + "/" + picName;

			// 判断文件是否存在
			File file = new File(filePath);
			if (!file.exists()) {// 文件不存在
				return null;
			}
		
			//Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			//return bitmap;
			
			return tryGetBitmap(filePath,-1,MAX_WIDTH_PIXEL*MAX_WIDTH_PIXEL);
			
		} else {
			return null;
		}
		
	}
	/**
	 * get Bitmap
	 * 
	 * @param imgFile
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static Bitmap tryGetBitmap(String imgFile, int minSideLength,
			int maxNumOfPixels) {
		if (imgFile == null || imgFile.length() == 0)
			return null;

		try {
			FileDescriptor fd = new FileInputStream(imgFile).getFD();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			// BitmapFactory.decodeFile(imgFile, options);
			BitmapFactory.decodeFileDescriptor(fd, null, options);

			options.inSampleSize = computeSampleSize(options, minSideLength,
					maxNumOfPixels);
			try {
				// 这里一定要将其设置回false，因为之前我们将其设置成了true
				// 设置inJustDecodeBounds为true后，decodeFile并不分配空间，即，BitmapFactory解码出来的Bitmap为Null,但可计算出原始图片的长度和宽度
				options.inJustDecodeBounds = false;

				Bitmap bmp = BitmapFactory.decodeFile(imgFile, options);
				return bmp == null ? null : bmp;
			} catch (OutOfMemoryError err) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * compute Sample Size
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	/**
	 * compute Initial Sample Size
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		// 上下限范围
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public Bitmap loadDrawable(String imageUrl) {
		if (imageCache.containsKey(imageUrl)) {
			//Bitmap softReference = imageCache.get(imageUrl);
			SoftReference<Bitmap> srf = imageCache.get(imageUrl);
			Bitmap softReference = srf.get();
			if (softReference != null) {
				return softReference;
			} else {
				return null;
			}
		}
		return null;
		
	}

	protected Bitmap loadImageFromUrl(String imageUrl, String fileName) {
		try {
			URL url = new URL(imageUrl);
			//HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//conn.setConnectTimeout(10 * 1000);
			//conn.connect();
			//InputStream is = conn.getInputStream();			
			//Bitmap bitmap = BitmapFactory.decodeStream(is);	
			//is.close();			
			//conn.disconnect();	
			
			InputStream is = (InputStream)url.getContent();			
            //BufferedInputStream bis = new BufferedInputStream(is); 
            byte[] data=readStream(is);
            
            //标记其实位置，供reset参考  
            //bis.mark(0); 
            BitmapFactory.Options opts = new BitmapFactory.Options();  
            //true,只是读图片大小，不申请bitmap内存  
            opts.inJustDecodeBounds = true;  
            BitmapFactory.decodeByteArray(data, 0, data.length, opts);  
            LogUtil.v("AsyncImageLoader", "width="+opts.outWidth+"; height="+opts.outHeight);  

            opts.inSampleSize = computeSampleSize(opts, -1,
            		MAX_WIDTH_PIXEL*MAX_WIDTH_PIXEL);
            if(opts.inSampleSize > 1)
            	LogUtil.v(TAG, "图片过大，被缩放 1/"+opts.inSampleSize);  
            //设为false，这次不是预读取图片大小，而是返回申请内存，bitmap数据  
            opts.inJustDecodeBounds = false;  
            //缓冲输入流定位至头部，mark()  
            //bis.reset();  
            //bis = new BufferedInputStream(is); 
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);  
              
            //return (bm == null) ? null : new BitmapDrawable(bm);  
            
			if(bitmap!=null){
				LogUtil.d(TAG,"get bitmap from net");
				imageCache.put(fileName, new SoftReference<Bitmap>(bitmap));
				savePic(bitmap, fileName);// 保存图片
			}
			
			is.close();  
            //bis.close();      
			return bitmap;	    
	    } catch (Exception e) {  
	    	LogUtil.v(TAG, e.getClass().getSimpleName() + " " + imageUrl);  
//	    	Map<String, String> params = new HashMap<String, String>();
//			params.put(EnumAnalyticEventParameter.Channel.toString(),
//					TuJiaApplication.mChannelCode);
//			params.put(EnumAnalyticEventParameter.DevModel.toString(),
//					android.os.Build.MODEL);
//			params.put(EnumAnalyticEventParameter.OsVersion.toString(),
//					android.os.Build.VERSION.RELEASE);			
//			params.put(EnumAnalyticEventParameter.AppVersion.toString(),
//					TuJiaApplication.mVersionName);			
//			params.put(EnumAnalyticEventParameter.Exception.toString(),
//					e.getClass().getSimpleName());			
//			FlurryAgent.logEvent(EnumAnalyticEvent.Exception.toString(), params);
	        //e.printStackTrace();  	    
		} catch (OutOfMemoryError err) {
			LogUtil.v(TAG, "OutOfMemoryError " + imageUrl);  
//			Map<String, String> params = new HashMap<String, String>();
//			params.put(EnumAnalyticEventParameter.Channel.toString(),
//					TuJiaApplication.mChannelCode);
//			params.put(EnumAnalyticEventParameter.DevModel.toString(),
//					android.os.Build.MODEL);
//			params.put(EnumAnalyticEventParameter.OsVersion.toString(),
//					android.os.Build.VERSION.RELEASE);			
//			params.put(EnumAnalyticEventParameter.AppVersion.toString(),
//					TuJiaApplication.mVersionName);			
//			FlurryAgent.logEvent(EnumAnalyticEvent.OutOfMemory.toString(), params);
			//StatService.onEvent(TuJiaApplication.getContext(), "error", "OutOfMemory");
		}
		return null;
	}
    public static byte[] readStream(InputStream inStream) throws Exception{      
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();      
        byte[] buffer = new byte[1024];      
        int len = 0;      
        while( (len=inStream.read(buffer)) != -1){      
            outStream.write(buffer, 0, len);      
        }      
        outStream.close();      
        inStream.close();      
        return outStream.toByteArray();      
    }
	public static void savePic(Bitmap bitmap, String imageUrl) {
		if (bitmap != null && imageUrl != null && !"".equals(imageUrl)) {
			if (avaiableSdcard()) {// 如果有sd卡，保存在sd卡
				savePicToSdcard(bitmap, imageUrl);
			} else {// 如果没有sd卡，保存在手机里面
				saveToDataDir(bitmap, imageUrl);
			}
		}
	}

	/**
	 * 将图片保存在sd卡
	 * 
	 * @param bitmap
	 *            图片
	 * @param picName
	 *            图片名称（同新闻id名）
	 */
	private static void savePicToSdcard(Bitmap bitmap, String picName) {

		//picName = StringUtil.md5(picName);//picName.substring(picName.lastIndexOf("/") + 1);
		String path = TvApplication.getInstance().getDirPath(imageCacheFolderName);
		File file = new File(path + "/" + picName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				outputStream.close();
			} catch (Exception e) {
				// LogUtil.e("", e.toString());
			}
		}
	}

	/**
	 * 保存文件到应用目录
	 * 
	 * @param bitmap
	 * @param fileName
	 *            文件名称
	 */
	static void saveToDataDir(Bitmap bitmap, String fileName) {
		//fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		String path = TvApplication.getInstance().getDirPath(imageCacheFolderName);
		File file = new File(path + "/" + fileName);
		FileOutputStream outputStream;
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				outputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				outputStream.close();
			} catch (Exception e) {
				LogUtil.e("", e.toString());
			}
		}
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
	/**
	 * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
	 * @return
	 */
	public ExecutorService getThreadPool(){
		if(pool == null){
			synchronized(ExecutorService.class){
				if(pool == null){
					//为了下载图片更加的流畅，我们用了2个线程来下载图片
					pool = Executors.newFixedThreadPool(2);
					LogUtil.d(TAG, "restart image load");
				}
			}
		}
		
		return pool;
		
	}
	/**
	 * 取消正在下载的任务
	 */
	public synchronized void cancelTask() {
		if(pool != null){			
			LogUtil.d(TAG, "cancel image load");
			pool.shutdownNow();
			pool = null;
		}
	}
}
