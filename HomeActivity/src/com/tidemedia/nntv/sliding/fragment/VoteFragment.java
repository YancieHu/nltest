package com.tidemedia.nntv.sliding.fragment;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.VoteListResponseModel.Item;
import com.tidemedia.nntv.model.VoteListResponseModel.VoteEntity;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.fragment.VoteResponseModel.PullItem;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.DimenUtil;
import com.tidemedia.nntv.util.IOUtil;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;

public class VoteFragment extends BaseFragment {
	private static final String TAG = BaseFragment.class.getSimpleName();
	private static final String VOTE_LIST = "vote_list";
	private Context context;
	private VoteEntity entity;
	private Button submitBtn;
	private RadioGroup radioGroup;
	private ImageView cover;
	private TextView title;
	private TextView voteTime;
	private TextView voteState;
	private TextView votePersonNum;
	private LinearLayout voteItemsContainer;
	private LinearLayout voteResultLayout;
	
	static int[] colors = {
		0xffff0000,
		0xff00ff00,
		0xff0000ff,
		0xffffff00,
		0xff00ffff,
		0xffff00ff,
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		Bundle args = getArguments();
		entity = (VoteEntity) args.getSerializable("VoteEntity");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.vote_layout, container, false);
		cover = (ImageView) view.findViewById(R.id.cover);
		title = (TextView) view.findViewById(R.id.title);
		voteTime = (TextView) view.findViewById(R.id.voteTime);
		voteState = (TextView) view.findViewById(R.id.voteState);
		votePersonNum = (TextView) view.findViewById(R.id.votePersonNum);
		voteItemsContainer = (LinearLayout) view.findViewById(R.id.voteItemsContainer);
		submitBtn = (Button) view.findViewById(R.id.submitBtn);
		voteResultLayout = (LinearLayout) view.findViewById(R.id.voteResult);
		
		if (entity != null) {
			Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(entity.image_url, new ImageCallback() {
				@Override
				public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
					if(imageDrawable!=null){
						cover.setImageBitmap(imageDrawable);
					}
				}
			});
			if (bitmap == null) {
				cover.setImageResource(R.drawable.ic_nntv_launcher);
			} else {
				cover.setImageBitmap(bitmap);
			}
			
			title.setText(entity.title);
			voteTime.setText(entity.date);
			voteState.setText("");
			votePersonNum.setText(entity.total() + "人参与");
			if ("0".equals(entity.is_multiple)) {
				radioGroup = new RadioGroup(context);
				if (entity.items != null) {
					for (Item item : entity.items) {
						RadioButton b = new RadioButton(getActivity());
						b.setButtonDrawable(R.drawable.selector_checkbox);
						b.setId(item.id);
						b.setText(item.title);
						radioGroup.addView(b);
					}
				}
				voteItemsContainer.addView(radioGroup);
			} else {
				if (entity.items != null) {
					for (Item item : entity.items) {
						CheckBox b = new CheckBox(context);
						b.setButtonDrawable(R.drawable.selector_checkbox);
						b.setId(item.id);
						b.setText(item.title);
						voteItemsContainer.addView(b);
					}
				}
			}
			submitBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if ("0".equals(entity.is_multiple)) {
						int id = radioGroup.getCheckedRadioButtonId();
						if (id == -1) {
							Toast.makeText(getActivity(), "请选择一个选项", Toast.LENGTH_SHORT).show();
							return;
						}
						String url = Constants.VOTE + "?vote_id=" + entity.id + "&item_id=" + radioGroup.getCheckedRadioButtonId();
						Log.i(TAG, "vote url:" + url);
						ThreadManager.exeTask(VoteFragment.this, 0, null, url);
					} else {
						StringBuilder voteItemIds = new StringBuilder();
						for (Item item : entity.items) {
							CheckBox cb = (CheckBox) voteItemsContainer.findViewById(item.id);
							if (cb.isChecked()) {
								voteItemIds.append(item.id).append(",");
							}
						}
						if (voteItemIds.length() != 0) {
							voteItemIds.deleteCharAt(voteItemIds.length() - 1);
						}
						String url = Constants.VOTE + "?vote_id=" + entity.id + "&item_id=" + voteItemIds;
						Log.i(TAG, "vote url:" + url);
						ThreadManager.exeTask(VoteFragment.this, 0, null, url);
					}
				}
			});
		}
		
		if (haveVoted(entity.id)) {
			String json = getCachedVoteResult(entity.id);
			if (json != null) {
				onCallbackFromThread(json, 0);
			} else {
				showVoteResult(entity.items);
			}
		}
		return view;
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		
		Log.i(TAG, "vote result:" + resultData);
		if (!StringUtil.isEmpty(resultData)) {
			VoteResponseModel json = StringUtil.fromJson(resultData, VoteResponseModel.class);
			if (json != null && json.result != null) {
				List<PullItem> items = json.result.poll_items;
				if (items != null) {
					showVoteResult(items, json.result.sum);
					saveVoteResult(entity.id, resultData);
				}
			}
		}
	}
	
	private int getSumPerson(List<Item> items) {
		if (items == null) {
			return 0;
		}
		int count = 0;
		for (Item item : items) {
			count += item.number;
		}
		return count;
	}
	
	private void showVoteResult(List<PullItem> items, int sum) {
		voteItemsContainer.setVisibility(View.GONE);
		submitBtn.setVisibility(View.GONE);
		voteResultLayout.setVisibility(View.VISIBLE);
		votePersonNum.setText(sum + "人参与");
		for (int i = 0; i < items.size(); i++) {
			PullItem item = items.get(i);
			addItem(i + 1, getVoteItemTitle(item.poll_item_id), item.number * 100 / sum);
		}
	}
	
	private void showVoteResult(List<Item> items) {
		voteItemsContainer.setVisibility(View.GONE);
		submitBtn.setVisibility(View.GONE);
		voteResultLayout.setVisibility(View.VISIBLE);
		int sum = getSumPerson(items);
		votePersonNum.setText(sum + "人参与");
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			int percent = sum == 0 ? 0 : item.number * 100 / sum;
			addItem(i + 1, getVoteItemTitle("" + item.id), percent);
		}
	}
	
	private void addItem(int position, String title, int percent) {
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		TextView titleView = new TextView(context);
		titleView.setText(position + "、" + title);
		layout.addView(titleView);
		
		LinearLayout ratioLayout = new LinearLayout(context);
		LinearLayout.LayoutParams ratioll = new LinearLayout.LayoutParams(-1, -2);
		ratioll.topMargin = DimenUtil.dp2px(context, 2);
		ratioLayout.setLayoutParams(ratioll);
		ratioLayout.setOrientation(LinearLayout.HORIZONTAL);
		layout.addView(ratioLayout);
		
		TextView supportRatio = new TextView(context);
		LinearLayout.LayoutParams supportRatioll = new LinearLayout.LayoutParams(0, -2);
		supportRatioll.weight = percent;
		supportRatio.setLayoutParams(supportRatioll);
		supportRatio.setBackgroundColor(colors[position % colors.length]);
		supportRatio.setGravity(Gravity.RIGHT);
		
		TextView supportRatioText = new TextView(context);
		LinearLayout.LayoutParams supportRatioTextll = new LinearLayout.LayoutParams(0, -2);
		supportRatioTextll.weight = 100 - percent;
		supportRatioText.setLayoutParams(supportRatioTextll);
		
		if (percent > 40) {
			supportRatio.setText(percent + "%");
		} else {
			supportRatioText.setText(percent + "%");
		}
		ratioLayout.addView(supportRatio);
		ratioLayout.addView(supportRatioText);
		
		voteResultLayout.addView(layout);
	}
	
	private void saveVoteResult(int id, String json) {
		SharedPreferences pref = context.getSharedPreferences(VOTE_LIST, Context.MODE_PRIVATE);
		pref.edit().putBoolean("" + id, true).commit();
		IOUtil.save(getVoteResultCachePath(id), json);
	}
	
	private boolean haveVoted(int id) {
		SharedPreferences pref = context.getSharedPreferences(VOTE_LIST, Context.MODE_PRIVATE);
		return pref.getBoolean("" + id, false);
	}
	
	private String getCachedVoteResult(int id) {
		return IOUtil.readContent(getVoteResultCachePath(id));
	}
	
	private String getVoteResultCachePath(int id) {
		return context.getCacheDir().getAbsolutePath() + File.separator + "VoteResultJson" + id;
	}
	
	private String getVoteItemTitle(String id) {
		if (entity != null && entity.items != null) {
			for (Item item : entity.items) {
				if (item != null && ("" + item.id).equals(id)) {
					return item.title;
				}
			}
		}
		return null;
	}
}
