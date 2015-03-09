package com.wellcell.inet.Task.Ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Handler;
import android.os.Message;

import com.wellcell.inet.Common.CGlobal.TestState;
import com.wellcell.inet.Task.TaskPar.PingPar;

//ping测试封装
public class PingObject
{
	//消息返回
	private Handler m_hHandler;
	private int m_nWhat;
	
	private StringBuffer m_bfLog;
	private TestState m_TestState = TestState.eReady;
	
	public PingObject(Handler handler,int what)
	{
		m_hHandler = handler;
		m_nWhat = what;
	}
	
	//接收发送消息
	private void sendMessage(String strMsg)
	{
		if(strMsg == null)
			return;
		
		//保存结果信息
		if(m_bfLog != null)
		{
			m_bfLog.append(strMsg);
			m_bfLog.append("\n");
		}
		
		if(m_hHandler != null)
			m_hHandler.sendMessage(Message.obtain(m_hHandler, m_nWhat, m_bfLog.toString()));
	}
	
	//执行ping命令
	public void execPing(PingPar par)
	{
		m_bfLog = new StringBuffer();
		m_TestState = TestState.eTesting;
		try
		{
			String strCmdPing = "ping -c " + par.m_nCount + " -s " + par.m_nPackSize + " " + par.m_strDest;
			Process m_proCmd = Runtime.getRuntime().exec(strCmdPing);

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
		m_TestState = TestState.eStoped;	//测试结束
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
		if(m_TestState == TestState.eReady || m_TestState == TestState.eStoped)
			return false;
		
		return true;
	}
}
