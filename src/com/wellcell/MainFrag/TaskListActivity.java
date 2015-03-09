package com.wellcell.MainFrag;

import android.os.Bundle;

import com.wellcell.ctdetection.CommonActivity;
import com.wellcell.ctdetection.R;

public class TaskListActivity extends CommonActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// container
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.mainlayout, new TaskListFragment()).commit();
			getActionBar().setTitle(R.string.OneKey);
			setModule(MainModule.eOneKey);
		}
	}
}