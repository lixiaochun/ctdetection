package com.wellcell.ctdetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.wellcell.MainFrag.TaskTestActivity;

public class EnvelopActivity extends Activity
{
	public static final String MESSAGE_QUIT = "QUIT";

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_envelop);
		// start
		View logo = findViewById(R.id.logo);
		View title = findViewById(R.id.title);
		Animation rise = AnimationUtils.loadAnimation(this, R.anim.rise);
		rise.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				// TODO Auto-generated method stub
				startActivity(new Intent(EnvelopActivity.this, TaskTestActivity.class));
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub
			}
		});
		Animation rise_scale = AnimationUtils.loadAnimation(this, R.anim.rise_scale);
		title.startAnimation(rise);
		logo.startAnimation(rise_scale);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (getIntent().getBooleanExtra(MESSAGE_QUIT, false)){
			finish();
		}
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(MESSAGE_QUIT, false))
			finish();
	}
}