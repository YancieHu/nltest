package com.tidemedia.nntv.activity;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.CategoryResponse;
import com.tidemedia.nntv.model.CategoryResponse.Category;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.MainActivity;
import com.tidemedia.nntv.util.ListUtil;
import com.tidemedia.nntv.util.StringUtil;

public class HomeActivity extends BaseActivity {

	private List<Category> nList;
	private List<Category> pList;

	private String mPushType;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(HomeActivity.this, MainActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("nList", (Serializable) nList);
			bundle.putSerializable("pList", (Serializable) pList);
			// if (!CommonUtils.isNull(mPushType)) {
			// bundle.putString("push_type", mPushType);
			// }
			intent.putExtras(bundle);
			startActivity(intent);
			HomeActivity.this.finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getActionBar().hide();
		this.getWindow().setBackgroundDrawableResource(R.drawable.start);

		Intent intent = getIntent();
		if (null != intent) {
			mPushType = intent.getStringExtra("push_type");
		}

		getNCategory();

		// 极光设置tag
		// Set<String> tags = new HashSet<String>();
		// tags.add("nntest");
		// JPushInterface.setTags(this, tags, new TagAliasCallback() {
		//
		// @Override
		// public void gotResult(int arg0, String arg1, Set<String> arg2) {
		// }
		// });

	}

	private void getNCategory() {
		ThreadManager.exeTask(this, APIContants.GET_NEWS_CAT, null,
				Constants.GET_NEWS_CAT);
	}

	private void getPCategory() {
		ThreadManager.exeTask(this, APIContants.GET_IMG_CAT, null,
				Constants.GET_IMG_CAT);
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (StringUtil.isNotEmpty(resultData)) {
			CategoryResponse categoryResponse = StringUtil.fromJson(resultData,
					CategoryResponse.class);
			if (1 != categoryResponse.getStatus()) {
				this.finish();
				return;
			}
			if (taskId == APIContants.GET_NEWS_CAT) {
				getPCategory();
				nList = categoryResponse.getResult().getList();
			} else {
				pList = categoryResponse.getResult().getList();
				if (ListUtil.isNotEmpty(nList) && ListUtil.isNotEmpty(pList)) {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(3000);
								handler.sendEmptyMessage(0);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						};
					}.start();
				}
			}
		}
	}
}
