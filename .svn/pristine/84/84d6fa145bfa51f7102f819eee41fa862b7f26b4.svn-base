package com.wellcell.MainFrag;

import android.os.Bundle;

import com.wellcell.ctdetection.CommonActivity;
import com.wellcell.ctdetection.R;

public class SettingActivity extends CommonActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// container
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.mainlayout, new SettingFragment()).commit();
			getActionBar().setTitle(R.string.Setting);
			setModule(MainModule.eSetting);
		}
	}
}