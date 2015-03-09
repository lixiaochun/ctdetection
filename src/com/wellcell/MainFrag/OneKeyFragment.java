package com.wellcell.MainFrag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wellcell.ctdetection.R;
import com.wellcell.inet.view.FragmentPage2;

//一键测试主界面
public class OneKeyFragment extends AbsMainFragment
{
	@Override
	View buildView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.maimfragment, null);
		
		m_classfrags = new Class[]{ FragmentPage2.class, FragmentPage2.class,FragmentPage2.class};
		m_nImgViews = new int[]{ R.drawable.btn_webcceshi,R.drawable.btn_zaixianshipin,R.drawable.btn_cesu};//, R.drawable.tab_selfinfo_btn, R.drawable.tab_square_btn, R.drawable.tab_more_btn };
		m_strTabLables = new String[]{ "浏览", "视频" ,"测速"};
		return view;
	}
}
