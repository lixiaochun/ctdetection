package com.wellcell.MainFrag;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wellcell.ctdetection.R;
import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.DataProvider.PhoneDataProvider;

//关于主界面
public class AboutFragment extends AbsMainFragment implements OnClickListener
{
	@Override
	View buildView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.about, null);
		view.findViewById(R.id.btn_website).setOnClickListener(this);
		view.findViewById(R.id.btn_update).setOnClickListener(this);
		((TextView)view.findViewById(R.id.btn_update)).setText("版本更新          " + PhoneDataProvider.getCurVerInfo(getActivity()));
		view.findViewById(R.id.btn_share).setOnClickListener(this);
		m_bSingleFrag = true;
		
		return view;
	}

	@Override
	public void onClick(View arg0)
	{
		switch (arg0.getId())
		{
		case R.id.btn_update:
			Toast.makeText(getActivity(), "当前版本为最新版本，无需更新！", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_website:
			Uri uri = Uri.parse("http://www.wellcell.com.cn");
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
			break;
		case R.id.btn_share:
			CGlobal.ShareApp(getActivity());
			break;
		default:
			break;
		}
	}
}
