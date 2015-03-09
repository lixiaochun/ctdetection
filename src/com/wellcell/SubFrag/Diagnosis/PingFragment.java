package com.wellcell.SubFrag.Diagnosis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wellcell.ctdetection.R;
import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Task.TaskPar.PingPar;
import com.wellcell.inet.Task.Ping.PingObject;

//PING测试
public class PingFragment extends Fragment implements OnClickListener
{
	private EditText m_etPackageSize;
	private EditText m_etPackageCount;
	private EditText m_etTimeout;
	private AutoCompleteTextView m_autoAddr;
	private Button m_btnTest; //开始测试

	private ScrollView m_scrollRet;
	private ProgressBar m_pbLoading;
	private TextView m_tvResult;

	private PingPar m_pingPar; //ping配置
	private PingObject m_pingObj;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_ping, null);
		// initial

		m_etPackageSize = (EditText) view.findViewById(R.id.package_size);
		m_etPackageCount = (EditText) view.findViewById(R.id.package_count);
		m_etTimeout = (EditText) view.findViewById(R.id.timeout);

		m_autoAddr = (AutoCompleteTextView) view.findViewById(R.id.address);
		m_autoAddr.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, CGlobal.WebAddrs));

		m_btnTest = (Button) view.findViewById(R.id.ping_test);
		m_btnTest.setOnClickListener(this);

		m_tvResult = (TextView) view.findViewById(R.id.result);
		m_scrollRet = (ScrollView) view.findViewById(R.id.scroll_result);
		m_pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);

		m_pingPar = new PingPar();
		m_pingObj = new PingObject(m_hHandler, 4); //ping测试对象

		return view;
	}

	private Handler m_hHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0: //完成测试
				setCtrlsEnable(true);
				break;
			case 4:
				if (msg.obj != null)
				{
					if (m_pbLoading.getVisibility() == View.VISIBLE)
						m_pbLoading.setVisibility(View.GONE);
					m_tvResult.setText((String) msg.obj);
					m_scrollRet.fullScroll(android.view.View.FOCUS_DOWN);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().getActionBar().setTitle("ping");
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		getActivity().getActionBar().setTitle("ping");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ping_test: //开始
			if (!m_pingObj.isRunning())
			{
				setCtrlsEnable(false);
				getPar();
				new Thread(pingTest).start();
			}
			break;
		default:
			break;
		}
	}

	//ping测试线程
	private Runnable pingTest = new Runnable()
	{
		@Override
		public void run()
		{
			m_pingObj.execPing(m_pingPar);
			m_hHandler.sendMessage(Message.obtain(m_hHandler, 0)); //结束
		}
	};

	//获取配置参数
	private void getPar()
	{
		try
		{
			//网址
			String strTemp = m_autoAddr.getText().toString();
			if (strTemp.equalsIgnoreCase(""))
				strTemp = CGlobal.WebAddrs[0];

			m_pingPar.m_strDest = strTemp;

			//测试次数
			strTemp = m_etPackageCount.getText().toString();
			if (strTemp.equalsIgnoreCase(""))
				strTemp = "5";

			m_pingPar.m_nCount = Integer.parseInt(strTemp);

			//包大小
			strTemp = m_etPackageSize.getText().toString();
			if (strTemp.equalsIgnoreCase(""))
				strTemp = "56";

			m_pingPar.m_nPackSize = Integer.parseInt(strTemp);
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * 功能: 设置控件是否可用
	 * 参数: bVisible: true:可用,false: 不可用
	 * 返回值:
	 * 说明:
	 */
	private void setCtrlsEnable(boolean bVisible)
	{
		if (!bVisible) //开始
		{
			m_btnTest.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_btn_testing));
			m_tvResult.setText("");
			m_pbLoading.setVisibility(View.VISIBLE);
		}
		else
			m_btnTest.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_btn_start));

		m_autoAddr.setEnabled(bVisible);
		m_etPackageSize.setEnabled(bVisible);
		m_etPackageCount.setEnabled(bVisible);
		m_etTimeout.setEnabled(bVisible);
	}
}