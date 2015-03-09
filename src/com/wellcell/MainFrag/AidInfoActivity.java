package com.wellcell.MainFrag;

import android.os.Bundle;

import com.wellcell.ctdetection.CommonActivity;
import com.wellcell.ctdetection.R;

public class AidInfoActivity extends CommonActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// container
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.mainlayout, new AidInfoFragment()).commit();
			getActionBar().setTitle(R.string.AidInfo);
			setModule(MainModule.eAidInfo);
		}
	}
}