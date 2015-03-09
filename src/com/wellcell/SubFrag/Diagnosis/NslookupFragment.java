package com.wellcell.SubFrag.Diagnosis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wellcell.ctdetection.R;
import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Common.CGlobal.TestState;
import com.wellcell.inet.Task.TaskPar.NslookupPar;

public class NslookupFragment extends Fragment implements OnClickListener, OnInstallStateListener
{
	TextView textResult;
	AutoCompleteTextView address;
	ScrollView mScroll;
	private ProgressBar mProgress;
	private Button btn_test;
	// execute command
	private NslookupPar mNslookupPar;
	private NslookupObject mNslookupObject;

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

		// abount command
		mNslookupPar = new NslookupPar();
		mNslookupObject = new NslookupObject(handler, 4); //ping测试对象
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().getActionBar().setTitle("DNS");
		if (!InstallBusyboxTask.installed())
			new InstallBusyboxTask(getActivity()).setOnInstallStateListener(this).execute();
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		if (mNslookupObject.isRunning())
			return;
		textResult.setText("");
		mProgress.setVisibility(View.VISIBLE);
		mNslookupPar.m_strDest = address.getText().toString().equalsIgnoreCase("") ? CGlobal.WebAddrs[0] : address.getText().toString();
		new Thread(nslookuptest).start();
		InputMethodManager mInput = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		mInput.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		// disable edit
		address.setEnabled(false);
		btn_test.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_btn_testing));
	}

	private Runnable nslookuptest = new Runnable()
	{
		@Override
		public void run()
		{
			mNslookupObject.execNslookup(mNslookupPar);
			handler.sendMessage(Message.obtain(handler, 0)); //结束
		}
	};
	private final Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 4:
				if (msg.obj != null)
				{
					if (mProgress.isShown())
						mProgress.setVisibility(View.GONE);
					textResult.setText((String) msg.obj);
					mScroll.fullScroll(android.view.View.FOCUS_DOWN);
				}
				break;
			case 0:
				// enable edit
				address.setEnabled(true);
				btn_test.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_btn_start));
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		getActivity().getActionBar().setTitle("DNS");
	}

	@Override
	public void onReceiveInstallState(boolean state)
	{
		// TODO Auto-generated method stub
		btn_test.setEnabled(state);
		if (state)
			Toast.makeText(getActivity(), "安装busybox成功", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getActivity(), "安装busybox失败，无法执行命令", Toast.LENGTH_SHORT).show();
	}
}

class NslookupObject
{
	//消息返回
	private Handler m_hHandler;
	private int m_nWhat;

	private StringBuffer m_bfLog;
	private TestState m_TestState = TestState.eReady;

	public NslookupObject(Handler handler, int what)
	{
		m_hHandler = handler;
		m_nWhat = what;
	}

	//接收发送消息
	private void sendMessage(String strMsg)
	{
		if (strMsg == null)
			return;

		//保存结果信息
		if (m_bfLog != null)
		{
			m_bfLog.append(strMsg);
			m_bfLog.append("\n");
		}

		if (m_hHandler != null)
			m_hHandler.sendMessage(Message.obtain(m_hHandler, m_nWhat, m_bfLog.toString()));
	}

	//执行Nslookup命令
	public void execNslookup(NslookupPar par)
	{
		m_bfLog = new StringBuffer();
		m_TestState = TestState.eTesting;
		try
		{
			String strCmdNslookup = "busybox nslookup " + par.m_strDest;
			Process m_proCmd = Runtime.getRuntime().exec(strCmdNslookup);

			// 开启读取错误
			GetExecError getExecError = new GetExecError(new InputStreamReader(m_proCmd.getErrorStream()));
			new Thread(getExecError).start();

			// 读取输出
			InputStreamReader ir = new InputStreamReader(m_proCmd.getInputStream());
			BufferedReader br = new BufferedReader(ir);

			String line = "";
			while ((line = br.readLine()) != null)
			{
				sendMessage(line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		m_TestState = TestState.eStoped; //测试结束
	}

	private class GetExecError implements Runnable
	{
		InputStreamReader inputSR;

		GetExecError(InputStreamReader ir)
		{
			this.inputSR = ir;
		}

		@Override
		public void run()
		{
			try
			{
				// 读取错误
				BufferedReader br = new BufferedReader(inputSR);
				String strLine = "";
				while ((strLine = br.readLine()) != null)
				{
					sendMessage(strLine);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	//是否正在运行
	public boolean isRunning()
	{
		if (m_TestState == TestState.eReady || m_TestState == TestState.eStoped)
			return false;

		return true;
	}
}

class InstallBusyboxTask extends AsyncTask<Void, Void, Boolean>
{
	private static String BusyboxBinPath = "/data/data/com.wellcell.ctdetection/busybox";
	private Context context;
	private OnInstallStateListener listener;
	private static boolean installed = false;

	public InstallBusyboxTask(Context context)
	{
		this.context = context;
	}

	public static boolean installed()
	{
		return installed;
	}

	public InstallBusyboxTask setOnInstallStateListener(OnInstallStateListener listener)
	{
		this.listener = listener;
		return this;
	}

	@Override
	protected Boolean doInBackground(Void... arg0)
	{
		// TODO Auto-generated method stub
		try
		{
			File busyboxFile = new File(BusyboxBinPath);
			if (busyboxFile.exists())
				return true;
			/**拷贝Assets的bin文件到软件安装后的私有文件夹内*/
			InputStream localInputStream = context.getAssets().open("busybox");
			int size = localInputStream.available();
			byte[] bytes = new byte[size];
			localInputStream.read(bytes);
			FileOutputStream localFileOutputStream = new FileOutputStream(busyboxFile);
			localFileOutputStream.write(bytes, 0, size);
			localFileOutputStream.close();
			localInputStream.close();
			/**给权限*/
			Process process = Runtime.getRuntime().exec("/system/bin/chmod 744 " + BusyboxBinPath);
			process.waitFor();
			process.destroy();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
		catch (InterruptedException e)
		{
			return false;
		}
	}

	protected void onPostExecute(Boolean result)
	{
		listener.onReceiveInstallState(result);
		installed = result;
	}
}

interface OnInstallStateListener
{
	public void onReceiveInstallState(boolean state);
}