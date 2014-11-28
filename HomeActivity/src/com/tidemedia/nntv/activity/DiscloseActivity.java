package com.tidemedia.nntv.activity;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.DiscloseResponseModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.ListUtil;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.ValidateUtil;
import com.tidemedia.nntv.view.MyGridView;

public class DiscloseActivity extends BaseActivity implements OnClickListener {
	public static final String TAG = DiscloseActivity.class.getSimpleName();
	private static final int REQUEST_CODE_PICK_PICTURE = 0;
	private static final int REQUEST_CODE_PICK_VIDEO = 1;
	private static final int REQUEST_CODE_UPLOAD_PICTURE = 2;
	private static final int REQUEST_CODE_UPLOAD_VIDEO = 3;

	private EditText discloser;
	private EditText phone;
	private EditText discloseContent;
	//private ImageView picture;
	private ImageView video;
	private Button submitBtn;
	private String imageUrl;
	private String videoUrl;
	private List<String> urlList = new ArrayList<String>();
	private List<Bitmap> bitMapList = new ArrayList<Bitmap>();
	private MyGridView photoGridView;
	private GridAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disclose);
		urlList.add("");
		bitMapList.add(null);
		photoGridView = (MyGridView)findViewById(R.id.photoGridView);
		discloser = (EditText) findViewById(R.id.discloser);
		phone = (EditText) findViewById(R.id.phone);
		discloseContent = (EditText) findViewById(R.id.discloseContent);
		//picture = (ImageView) findViewById(R.id.picture);
		video = (ImageView) findViewById(R.id.vedio);
		submitBtn = (Button) findViewById(R.id.submitBtn);

		//picture.setOnClickListener(this);
		video.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.title)).setText("我要爆料");
		adapter = new GridAdapter();
		photoGridView.setAdapter(adapter);
		photoGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				if(position == urlList.size() - 1){
					pickPickture();
				}
			}
			
		});
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.picture:
			pickPickture();
			break;*/
		case R.id.vedio:
			pickVideo();
			break;
		case R.id.submitBtn:
			submit();
			break;
		}
	}

	private void pickPickture() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_CODE_PICK_PICTURE);
	}
	
	private void pickVideo() {
		Intent intent = new Intent();
		intent.setType("video/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_CODE_PICK_VIDEO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PICK_PICTURE) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, FileUploadActivity.class);
				intent.putExtra("path", getPath(data.getData()));
				intent.putExtra("fileType", FileUploadActivity.FILE_TYPE_IMAGE);
				startActivityForResult(intent, REQUEST_CODE_UPLOAD_PICTURE);
			}
		} else if (requestCode == REQUEST_CODE_PICK_VIDEO) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, FileUploadActivity.class);
				intent.putExtra("path", getPath(data.getData()));
				intent.putExtra("fileType", FileUploadActivity.FILE_TYPE_VIDEO);
				startActivityForResult(intent, REQUEST_CODE_UPLOAD_VIDEO);
			}
		} else if (requestCode == REQUEST_CODE_UPLOAD_PICTURE) {
			if (resultCode == RESULT_OK) {
				imageUrl = data.getStringExtra("imageUrl");
				videoUrl = "";
				Bitmap image = data.getParcelableExtra("thumb");
				buildData(imageUrl, image);
				adapter.notifyDataSetChanged();
				/*picture.setImageBitmap(image);
				picture.setOnClickListener(null);*/
				video.setVisibility(View.GONE);
			}
		} else if (requestCode == REQUEST_CODE_UPLOAD_VIDEO) {
			if (resultCode == RESULT_OK) {
				videoUrl = data.getStringExtra("videoUrl");
				imageUrl = "";
				urlList.clear();
				Bitmap image = data.getParcelableExtra("thumb");
				video.setImageBitmap(image);
				video.setOnClickListener(null);
				photoGridView.setVisibility(View.GONE);
			}
		}
	}

	private void buildData(String url, Bitmap bitMap){
		urlList.remove(urlList.size() - 1);
		bitMapList.remove(bitMapList.size() - 1);
		urlList.add(url);
		bitMapList.add(bitMap);
		urlList.add("");
		bitMapList.add(null);
	}
	
	private String getPath(Uri uri) {
		if (uri == null) {
			return null;
		}
		String path = null;
		String scheme = uri.getScheme();
		if ("content".equals(scheme)) {
			Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
	        cursor.moveToFirst();
	        path = cursor.getString(0);
	        cursor.close();
		} else if("file".equals(scheme)) {
			path = uri.getPath();
		}
		return path;
	}
	
	
	

	private void submit() {
		if (validate()) {
			String encodeName = discloser.getText().toString();
			String encodeContent = discloseContent.getText().toString();
			try {
				encodeName = URLEncoder.encode(encodeName, "UTF-8");
				encodeContent = URLEncoder.encode(encodeContent, "UTF-8");
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			String url = Constants.SBUMIT_DISCLOSE + "?name=" + encodeName + "&tel=" + phone.getText().toString() + "&content=" + encodeContent;
			if(ListUtil.isNotEmpty(urlList) && urlList.size() > 0){
				String totalUrl = "";
				for (String currentUrl : urlList) {
					if(StringUtil.isNotEmpty(currentUrl)){
						totalUrl += currentUrl + ",";
					}
				}
				url += "&image=" + totalUrl;
				url = url.substring(0, url.length() - 1);
			}else if(StringUtil.isNotEmpty(videoUrl)){
				url += "&video=" + videoUrl;
			}
			/*HashMap<String, String> postParameter = new HashMap<String, String>();
			postParameter.put("name", discloser.getText().toString());
			postParameter.put("tel", phone.getText().toString());
			postParameter.put("content", discloseContent.getText().toString());
			postParameter.put("video", videoUrl);
			postParameter.put("image", imageUrl)*/;
			ThreadManager.exeTask(this, 0, null, url);
		}
	}
	
	class GridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return urlList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return urlList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(DiscloseActivity.this);
			imageView.setScaleType(ScaleType.CENTER);
			if(null == bitMapList.get(position)){
				imageView.setImageResource(R.drawable.ic_pic);
			}else{
				imageView.setImageBitmap(bitMapList.get(position));
			}
			return imageView;
		}
		
	}

	private boolean validate() {
		if (discloser.getText().toString().trim().length() == 0) {
			showToast("爆料人不能为空");
			discloser.requestFocus();
			return false;
		}
		String phoneNum = phone.getText().toString().trim();
		if (phoneNum.length() == 0) {
			showToast("爆料人手机号码不能为空");
			phone.requestFocus();
			return false;
		}
		if (!ValidateUtil.isMobile(phoneNum)) {
			showToast("爆料人手机号码格式不正确");
			phone.requestFocus();
			return false;
		}
		if (discloseContent.getText().toString().trim().length() == 0) {
			showToast("内容不能为空");
			discloseContent.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		Log.i(TAG, "" + resultData);
		if (!StringUtil.isEmpty(resultData)) {
			DiscloseResponseModel json = StringUtil.fromJson(resultData, DiscloseResponseModel.class);
			if (json != null && json.getStatus() == 1) {
				showToast(json.getMessage());
				finish();
			}
		}
	}
}
