package com.wellcell.MainFrag;

import android.os.Bundle;

import com.wellcell.ctdetection.CommonActivity;
import com.wellcell.ctdetection.R;

public class DiagnosisActivity extends CommonActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// container
		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.mainlayout, new DiagnosisFragment()).commit();
			getActionBar().setTitle(R.string.Diagnosis);
			setModule(MainModule.eDiagnosis);
		}
	}
}