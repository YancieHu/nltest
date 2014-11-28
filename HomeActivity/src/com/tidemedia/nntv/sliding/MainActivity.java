package com.tidemedia.nntv.sliding;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.CommentActivity;
import com.tidemedia.nntv.common.Preferences;
import com.tidemedia.nntv.model.CategoryResponse.Category;
import com.tidemedia.nntv.sliding.fragment.AccountFragment;
import com.tidemedia.nntv.sliding.fragment.HomeFragment;
import com.tidemedia.nntv.sliding.fragment.MenuFragment;
import com.tidemedia.nntv.util.CommonUtils;
import com.tidemedia.nntv.util.PreferencesUtil;

/**
 * 
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @since Mar 12, 2013
 * @version 1.0.0
 */
public class MainActivity extends SlidingActivity{
	
	 private List<Category> nList;
	 private List<Category> pList;
	 private String flag = "";
	 private MenuFragment menuFragment;
	 private AlertDialog finishAlert;
	 
//	 private String mPushType;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_content);
        getData();
        initActionBar();
     // set the Behind View
        setBehindContentView(R.layout.frame_menu);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        menuFragment = new MenuFragment(nList, pList);
        fragmentTransaction.replace(R.id.menu, menuFragment);
        
        fragmentTransaction.replace(R.id.content, new HomeFragment());
        fragmentTransaction.commit();
        
//        handler.sendEmptyMessageDelayed(0, 50);
        
        initSlidingMenu();
    }
    
//    private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (!CommonUtils.isNull(mPushType)) {
//	        	Preference preference = new Preference(MainActivity.this);
//	        	preference.setKey(mPushType);
//	        	menuFragment.performClick(preference);
//	        }
//		}
//    	
//    };
    
    private void getData(){
    	nList = (List<Category>)this.getIntent().getSerializableExtra("nList");
    	pList = (List<Category>)this.getIntent().getSerializableExtra("pList");
    	
//    	mPushType = this.getIntent().getStringExtra("push_type");
    }
    
    @Override
    protected void onResume() {
//    	flag = this.getIntent().getStringExtra("toLogin");
    	if(menuFragment.index == 11){
    		menuFragment.discloseBtn.setVisibility(View.GONE);
//        	menuFragment.index = 9;
            setActionTitle("登陆/注册");
            getActionBar().getCustomView().findViewById(R.id.actionImage).setVisibility(View.GONE);
            menuFragment.mFrameLayout.setVisibility(View.VISIBLE);
            menuFragment. mViewPager.setVisibility(View.GONE);
            menuFragment.mViewPager2.setVisibility(View.GONE);
            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            FragmentManager fragmentManager = this.getFragmentManager();
            fragmentManager.beginTransaction()
//            .replace(R.id.content, accountFragment == null ?new AccountFragment():accountFragment ,"ACCOUNT")
            .replace(R.id.content, new AccountFragment())
            .commit();
    	}
    	flag = null;
    	super.onResume();
    }
    
    private void initActionBar(){
    	ActionBar actionBar = this.getActionBar();
        actionBar.setCustomView(R.layout.title_layout);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.show();
        findViewById(android.R.id.home).setVisibility(View.GONE);
        actionBar.getCustomView().findViewById(R.id.menuImage).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				toggle();
			}
		});
        //setActionTitle("首页");
        getActionBar().getCustomView().findViewById(R.id.actionTitle).setVisibility(View.VISIBLE);
    }
    
    public void setActionTitle(String title) {
    	((TextView) getActionBar().getCustomView().findViewById(R.id.actionTitle)).setText(title);
    }
    
    private void initSlidingMenu() {

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidth(50);
        sm.setShadowDrawable(R.drawable.shadow);
        //设置滑动距离
        sm.setBehindOffset(350);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置slding menu的几种手势模式
        //TOUCHMODE_FULLSCREEN 全屏模式，在content页面中，滑动，可以打开sliding menu
        //TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开slding ,你需要在屏幕边缘滑动才可以打开slding menu
        //TOUCHMODE_NONE 自然是不能通过手势打开啦
       // sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        //使用左上方icon可点，这样在onOptionsItemSelected里面才可以监听到R.id.home
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	if(menuFragment.index == 6){
    		getMenuInflater().inflate(R.menu.vote, menu);
    	}else{
    		getMenuInflater().inflate(R.menu.activity_main, menu);
    	}
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_comment:
			/*if(null != shareModel){
				Intent intent = new Intent(this, CommentActivity.class);
				intent.putExtra("newsId", shareModel.getId());
				startActivity(intent);
			}*/
        	String id = PreferencesUtil.getPreference(this, Preferences.VOTE_TYPE, Preferences.VOTE_KEY);
        	Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("newsId", id);
			startActivity(intent);
			return true;
        case android.R.id.home:
            //toggle就是程序自动判断是打开还是关闭
            toggle();
//          getSlidingMenu().showMenu();// show menu
//          getSlidingMenu().showContent();//show content
            return true;
        case R.id.action_disclose:
//        	Intent intent = new Intent(this, DiscloseActivity.class);
//        	startActivity(intent);
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected AlertDialog dialog() {

		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		finishAlert = builder.create();
		return finishAlert;
	}
	
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_MENU:
    		toggle();
    		return true;
    	case KeyEvent.KEYCODE_BACK:
    		boolean isMenuShowing = getSlidingMenu().isMenuShowing();
    		if (isMenuShowing) {
    			toggle();
    		} else {
    			/*long time = System.currentTimeMillis();
    			if (time - lastPressBackTime > finishThrehold) {
    				Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
    			} else {
    				finish();
    			}
    			lastPressBackTime = time;*/
    			dialog().show();
    		}
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    long lastPressBackTime;
    long finishThrehold = 2000;
}
