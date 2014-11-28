package com.tidemedia.nntv.activity;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;

public class PlayerActivity extends BaseActivity implements
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback {

	private static final String TAG = "MediaPlayerDemo";
	private VideoView mVideoView;
	private MediaPlayer mMediaPlayer;
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	private MediaController mMediaController;
	private boolean mFromLive = false;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.live_layout);
		mVideoView = (VideoView) findViewById(R.id.surface);
		Log.d("action", "oncreate");
		
		Intent intent = getIntent();
		if (null != intent) {
			mFromLive = intent.getBooleanExtra("from_live", false);
		}
		
		String photo = this.getIntent().getStringExtra("photo");
		String preview = this.getIntent().getStringExtra("preview");
		String path = this.getIntent().getStringExtra("liveUrl");
		// photo =
		// "http://images.missyuan.com/attachments/day_071123/20071123_b2bfefe1ec56e2df6582vCGIZG81gbyM.png";

		if (photo != null && photo.length() > 0) {

			final ImageView image1 = (ImageView) findViewById(R.id.imageView);
			mVideoView.setVisibility(View.INVISIBLE);
			/*
			 * Bitmap bitmap =getHttpBitmap(photo); //从网上取图片
			 * image1.setImageBitmap(bitmap); //设置Bitmap
			 * image1.setVisibility(View.VISIBLE);
			 */
			image1.setVisibility(View.VISIBLE);
			Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(photo,
					new ImageCallback() {
						@Override
						public void imageLoaded(Bitmap imageDrawable,
								String imageUrl) {
							if (imageDrawable != null && image1 != null) {
								image1.setImageBitmap(imageDrawable);
							}
						}
					});
			if (bitmap == null) {
				image1.setImageResource(R.drawable.default_img);
			} else {
				image1.setImageBitmap(bitmap);
			}

			Log.d("action", "photo " + photo);
			playRadio(path);
		} else {
			final ImageView image1 = (ImageView) findViewById(R.id.imageView);
			Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(preview,
					new ImageCallback() {
						@Override
						public void imageLoaded(Bitmap imageDrawable,
								String imageUrl) {
							if (imageDrawable != null && image1 != null) {
								image1.setImageBitmap(imageDrawable);
							}
						}
					});
			if (bitmap == null) {
				image1.setImageResource(R.drawable.default_img);
			} else {
				image1.setImageBitmap(bitmap);
			}
			playVideo(path);
		}
		
		/*ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.title)).setText("直播");*/
	}

	private void playRadio(String path) {
		mMediaPlayer = new MediaPlayer(this);
		try {
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					mMediaPlayer.start();
				}
			});
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
			e.printStackTrace();
		}
		// mMediaPlayer.setDisplay(holder);

	}

	private void playVideo(String path) {
		doCleanUp();
		try {
			// path = "http://live.jmtv.cn/tslsChannelLive/kenj3I1.m3u8";//radio
			Log.d("action", "play video " + path);
			// path =
			// "http://121.17.124.39/videofile3.cutv.com/originfile/010002_t/2013/09/05/A15/A15fgfffhgimnikkjjkkgf_cusd.mp4";
			// path =
			// "http://jztv.hls.cutv.com/cutvlive/5l2i1m0/hls/live_sd.m3u8";

			// Create a new media player and set the listeners
			// mMediaPlayer = new MediaPlayer(this);
			// new MediaPlayer(this).set
			mVideoView.setVideoPath(path);
			// mMediaPlayer.setDisplay(holder);
			// mVideoView.prepare();
			mVideoView.setOnBufferingUpdateListener(this);
			mVideoView.setOnCompletionListener(this);
			mVideoView.setOnPreparedListener(this);
			// mVideoView.setOnVideoSizeChangedListener(this);
			// 设置显示名称
			mMediaController = new MediaController(this);
			mMediaController.setFileName("test");
			
			if (mFromLive) {
				// 全屏,直播进来的话，直接全屏
				mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
			}
			
			mVideoView.setMediaController(mMediaController);
			mVideoView.requestFocus();

			// mMediaPlayer.getMetadata();
			setVolumeControlStream(AudioManager.STREAM_MUSIC);

		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
		}
	}

	public void onBufferingUpdate(MediaPlayer arg0, int percent) {
		Log.d(TAG, "onBufferingUpdate percent:" + percent);

	}

	public void onCompletion(MediaPlayer arg0) {
		Log.d(TAG, "onCompletion called");
	}

	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.v(TAG, "onVideoSizeChanged called");
		if (width == 0 || height == 0) {
			Log.e(TAG, "invalid video width(" + width + ") or height(" + height
					+ ")");
			return;
		}
		mIsVideoSizeKnown = true;

		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	public void onPrepared(MediaPlayer mediaplayer) {
		
		ImageView image1 = (ImageView) findViewById(R.id.imageView);
		image1.setVisibility(View.GONE);
		
		Log.d(TAG, "onPrepared called");
		mIsVideoReadyToBePlayed = true;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
		Log.d(TAG, "surfaceChanged called");

	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		Log.d(TAG, "surfaceDestroyed called");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated called");

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaPlayer();
		doCleanUp();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
		doCleanUp();
	}

	private void releaseMediaPlayer() {
		if (mVideoView != null) {
			mVideoView.stopPlayback();
			mVideoView = null;
		}
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void doCleanUp() {

	}

	private void startVideoPlayback() {
		Log.v(TAG, "startVideoPlayback");
		mVideoView.start();
	}
}
