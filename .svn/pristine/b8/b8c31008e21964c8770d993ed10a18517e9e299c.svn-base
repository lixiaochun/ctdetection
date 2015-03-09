package com.wellcell.MainFrag;

import io.vov.vitamio.LibsChecker;
import android.os.Bundle;

import com.wellcell.ctdetection.CommonActivity;
import com.wellcell.ctdetection.R;

public class TaskTestActivity extends CommonActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// container
		if (savedInstanceState == null)
			
		{
			getSupportFragmentManager().beginTransaction().add(R.id.mainlayout, new TaskTestFragment()).commit();
			getActionBar().setTitle(R.string.TaskTest);
			setModule(MainModule.eTaskTest);
		}
		new Thread(loadVitamioLibrary).start(); // vitamio检测
	}

	// --------------------------------------------------------------------------------------------------------------------
	// 检测解码包的代码（解压解码包，Vitamio会根据当前CPU的类型自动解压相应平台的库）
	private Runnable loadVitamioLibrary = new Runnable()
	{
		public void run()
		{
			LibsChecker.checkVitamioLibs(TaskTestActivity.this);// vitamio库检测
		}
	};
}