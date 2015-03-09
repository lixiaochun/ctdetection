package com.wellcell.MainFrag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wellcell.SubFrag.TaskTest.CustTaskFragment;
import com.wellcell.SubFrag.TaskTest.HttpFragment;
import com.wellcell.SubFrag.TaskTest.VideoFragment;
import com.wellcell.ctdetection.R;

//业务测试主界面
public class TaskTestFragment extends AbsMainFragment
{
	@Override
	View buildView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.maimfragment, null);
		m_classfrags = new Class[] { CustTaskFragment.class, HttpFragment.class, VideoFragment.class };
		m_nImgViews = new int[] { R.drawable.btn_cesu, R.drawable.btn_webcceshi, R.drawable.btn_zaixianshipin };
		m_strTabLables = new String[] { "测速", "浏览", "视频" };

		return view;
	}
}
