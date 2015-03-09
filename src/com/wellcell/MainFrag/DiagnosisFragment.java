package com.wellcell.MainFrag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wellcell.SubFrag.Diagnosis.PingFragment;
import com.wellcell.SubFrag.Diagnosis.TracerouteFragment;
import com.wellcell.ctdetection.R;

//辅助诊断主界面
public class DiagnosisFragment extends AbsMainFragment
{
	@Override
	View buildView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.maimfragment, null);
		m_classfrags = new Class[] { PingFragment.class, TracerouteFragment.class, DNSFragment.class };
		m_nImgViews = new int[] { R.drawable.btn_pingceshi, R.drawable.btn_traceroute, R.drawable.btn_dns };
		m_strTabLables = new String[] { "Ping", "Traceroute", "DNS" };
		return view;
	}
}
