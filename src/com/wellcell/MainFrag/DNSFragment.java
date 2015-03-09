package com.wellcell.MainFrag;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wellcell.ctdetection.R;
import com.wellcell.inet.Common.CGlobal;

public class DNSFragment extends Fragment implements OnClickListener
{
	TextView textResult;
	AutoCompleteTextView address;
	ScrollView mScroll;
	private ProgressBar mProgress;
	private Button btn_test;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_nslookup, null);
		// initial
		btn_test = (Button) view.findViewById(R.id.nslookup_test);
		btn_test.setOnClickListener(this);
		textResult = (TextView) view.findViewById(R.id.result);
		address = (AutoCompleteTextView) view.findViewById(R.id.address);
		address.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, CGlobal.WebAddrs));
		mScroll = (ScrollView) view.findViewById(R.id.scroll_result);
		mProgress = (ProgressBar) view.findViewById(R.id.pb_loading);
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().getActionBar().setTitle("DNS");
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		getActivity().getActionBar().setTitle("DNS");
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		try
		{
			textResult.setText(InetAddress.getByName(address.getText().toString()).getHostAddress());
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			textResult.setText("解析失败");
		}
	}
}