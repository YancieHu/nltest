package com.tidemedia.nntv.activity;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.Vitamio;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.tidemedia.nntv.R;

public class LiveActivity extends BaseActivity implements Callback,
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnVideoSizeChangedListener {
	private MediaPlayer mMediaPlayer;
	private SurfaceView mPreview;
	private SurfaceHolder holder;
	private String path;
	private int screenWidth;
	private int screenHeight;
	private int mVideoWidth;
	private int mVideoHeight;
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	private ProgressDialog mPD;
	private boolean inited;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live_layout);
		Intent intent = getIntent();
		path = intent.getStringExtra("liveUrl");
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		mPreview = (SurfaceView) findViewById(R.id.surface);
		holder = mPreview.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.RGBA_8888);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initPlayer();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	private void initPlayer() {
		if (inited) {
			playVideo();
			return;
		}
		
		new AsyncTask<Object, Object, Boolean>() {
			@Override
			protected void onPreExecute() {
				mPD = new ProgressDialog(LiveActivity.this);
				mPD.setCancelable(false);
				mPD.setMessage(LiveActivity.this.getString(getResources()
						.getIdentifier("vitamio_init_decoders", "string",
								getPackageName())));
				mPD.show();
			}

			@Override
			protected Boolean doInBackground(Object... params) {
				if (Vitamio.isInitialized(LiveActivity.this)) {
					return true;
				}
				return Vitamio.initialize(LiveActivity.this, getResources()
						.getIdentifier("libarm", "raw", getPackageName()));
			}

			@Override
			protected void onPostExecute(Boolean inited) {
				LiveActivity.this.inited = inited;
				mPD.dismiss();
				if (inited) {
					playVideo();
				}
			}

		}.execute();
	}

	private void playVideo() {
		doCleanUp();
		mMediaPlayer = new MediaPlayer(this);
		try {
			mMediaPlayer.setDataSource(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mMediaPlayer.setDisplay(holder);
		mMediaPlayer.prepareAsync();
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		Toast.makeText(this, "开始播放", Toast.LENGTH_LONG).show();
	}

	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		if (width == 0 || height == 0) {
			return;
		}
		mIsVideoSizeKnown = true;
		mVideoWidth = width;
		mVideoHeight = height;
		//缩放比例
		float widthRatio = 1f * screenWidth / mVideoWidth;
		float heightRatio = 1f * screenHeight / mVideoHeight;
		float scaleRatio = widthRatio > heightRatio ? heightRatio : widthRatio;
		mVideoWidth *= mVideoWidth;
		mVideoHeight *= mVideoHeight;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mIsVideoReadyToBePlayed = true;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

	}

	private void startVideoPlayback() {
		holder.setFixedSize(mVideoWidth, mVideoHeight);
		mMediaPlayer.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMediaPlayer.pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
}
