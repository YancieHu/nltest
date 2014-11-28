package com.tidemedia.nntv.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.util.CommonUtils;
import com.tidemedia.nntv.util.DimenUtil;

public class FileUploadActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "FileUploadActivity";
	public static final int FILE_TYPE_IMAGE = 0;
	public static final int FILE_TYPE_VIDEO = 1;
	private ImageView pic;
	private View uploadingBar;
	private Button uploadBtn;
	private String filePath;
	private int fileType;
	private int picMaxWidth;
	private int picMaxHeight;
	private Bitmap thumb;
	private String imageUrl;
	private String videoUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_upload);
		picMaxWidth = DimenUtil.dp2px(this, 180);
		picMaxHeight = DimenUtil.dp2px(this, 100);
		pic = (ImageView) findViewById(R.id.pic);
		uploadingBar = findViewById(R.id.uploading);
		uploadBtn = (Button) findViewById(R.id.uploadBtn);
		uploadBtn.setOnClickListener(this);
		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		fileType = intent.getIntExtra("fileType", FILE_TYPE_IMAGE);
		filePath = intent.getStringExtra("path");
		File file = new File(filePath);
		if (!file.exists()) {
			finish();
			return;
		}
		createThumb();
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((TextView) findViewById(R.id.title)).setText("文件上传");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createThumb() {
		switch (fileType) {
		case FILE_TYPE_IMAGE:
			createImageThumb();
			break;
		case FILE_TYPE_VIDEO:
			createVideoThumb();
			break;
		}
	}

	private void createImageThumb() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		float widthRatio = 1f * picMaxWidth / options.outWidth;
		float heightRatio = 1f * picMaxHeight / options.outHeight;
		float ratio = widthRatio > heightRatio ? heightRatio : widthRatio;
		if (ratio < 1) {
			options.inSampleSize = Math.round(1f / ratio);
		} else {
			options.inSampleSize = 1;
		}
		options.inJustDecodeBounds = false;
		thumb = BitmapFactory.decodeFile(filePath, options);

		if (thumb == null) {
			finish();
			showToast("加载图片失败");
			return;
		}

		// add by lee 20140830 start
		// 根据exif信息旋转图片
		// 旋转图片
		int angle = CommonUtils.readPictureDegree(filePath);
		thumb = CommonUtils.rotateBitmap(angle, thumb);
		// add by lee 20140830 end

		pic.setImageBitmap(thumb);
	}

	private void createVideoThumb() {
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath,
				Thumbnails.MICRO_KIND);
		thumb = ThumbnailUtils.extractThumbnail(bitmap, picMaxWidth,
				picMaxHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		if (thumb == null) {
			finish();
			showToast("加载视频失败");
			return;
		}

		// add by lee 20140830 start
		// 根据exif信息旋转图片
		// 旋转图片
		int angle = CommonUtils.readPictureDegree(filePath);
		thumb = CommonUtils.rotateBitmap(angle, thumb);
		// add by lee 20140830 end

		pic.setImageBitmap(thumb);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.uploadBtn:
			upload();
			break;
		}
	}

	private void upload() {
		uploadingBar.setVisibility(View.VISIBLE);
		uploadBtn.setClickable(false);
		new Thread() {
			public void run() {
				String url = null;
				if (fileType == FILE_TYPE_IMAGE) {
					url = "http://180.141.89.20:2088/1.0/phone_api_upload.php?op=photo";
				} else if (fileType == FILE_TYPE_VIDEO) {
					url = "http://180.141.89.20:2088/1.0/phone_api_upload.php?op=video";
				}
				HttpPost post = new HttpPost(url);
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 60000);
				MultipartEntity entity = new MultipartEntity();
				File file = new File(filePath);
				long size = file.length();
				if (size > 20 * 1024 * 1024) {
					runOnUiThread(onFail);
					return;
				}
				entity.addPart("file", new FileBody(file));
				post.setEntity(entity);
				InputStream is = null;
				try {
					HttpResponse response = client.execute(post);
					int stateCode = response.getStatusLine().getStatusCode();
					if (stateCode == HttpStatus.SC_OK) {
						HttpEntity result = response.getEntity();
						if (result != null) {
							is = result.getContent();
							if (fileType == FILE_TYPE_IMAGE) {
								imageUrl = getContent(is);
								Log.i(TAG, "imageUrl : " + imageUrl);
							} else if (fileType == FILE_TYPE_VIDEO) {
								videoUrl = getContent(is);
								Log.i(TAG, "videoUrl : " + videoUrl);
							}
							runOnUiThread(onUploadSuccess);
						} else {
							runOnUiThread(onUploadFail);
						}
					} else {
						runOnUiThread(onUploadFail);
					}
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(onUploadFail);
				} finally {
					close(is);
				}
			}
		}.start();
	}

	private Runnable onUploadSuccess = new Runnable() {

		@Override
		public void run() {
			uploadingBar.setVisibility(View.GONE);
			Intent data = new Intent();
			switch (fileType) {
			case FILE_TYPE_IMAGE:
				data.putExtra("imageUrl", imageUrl);
				break;
			case FILE_TYPE_VIDEO:
				data.putExtra("videoUrl", videoUrl);
				break;
			}
			data.putExtra("thumb", thumb);
			setResult(RESULT_OK, data);
			finish();
		}
	};

	private Runnable onUploadFail = new Runnable() {

		@Override
		public void run() {
			showToast("上传失败");
			uploadingBar.setVisibility(View.GONE);
			uploadBtn.setClickable(true);
		}
	};

	private Runnable onFail = new Runnable() {
		@Override
		public void run() {
			showToast("文件大小超出限制");
			uploadingBar.setVisibility(View.GONE);
			uploadBtn.setClickable(true);
		}
	};

	public static String getContent(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			return new String(baos.toByteArray(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
