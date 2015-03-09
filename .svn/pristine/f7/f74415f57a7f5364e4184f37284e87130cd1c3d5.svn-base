package com.wellcell.inet.Task.Traceroute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.wellcell.inet.Common.CGlobal.TestState;
import com.wellcell.inet.Task.TaskPar.TraceroutePar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class TraceRouteObject
{
	private static boolean m_bInstalled = false; //是否已经安装命令
	private static String TracrouteBinPath = "/data/data/com.wellcell.ctdetection/traceroute";

	//消息返回
	private Handler m_hHandler;
	private int m_nWhat;

	private StringBuffer m_bfLog;
	private TestState m_TestState = TestState.eReady;	//测试状态

	public TraceRouteObject(Handler handler, int what)
	{
		m_hHandler = handler;
		m_nWhat = what;
	}

	//执行ping命令
	public void execTraceroute(TraceroutePar par)
	{
		m_bfLog = new StringBuffer();
		m_TestState = TestState.eTesting;
		try
		{
			String strIP = par.getDestIP();
			sendMessage("Host：" + par.getDest());
			if(strIP == null || strIP.length() < 0) //域名解析判断
			{
				m_TestState = TestState.eStoped; //测试结束
				sendMessage("无法解析目标系统名称：" + par.getDest());
				return;
			}
			
			String strCmdPing = TracrouteBinPath + " -m " + par.m_nHops + " -w " + par.m_dTimeout + " " + strIP;
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
		m_TestState = TestState.eStoped; //测试结束
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

	/**
	 * 功能: 拷贝安装tracroute命令
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static boolean InstallTracroute(Context context)
	{
		if (!m_bInstalled)
		{
			try
			{
				File fileTag = new File(TracrouteBinPath);
				if (fileTag.exists())
					return true;

				/**拷贝Assets的bin文件到软件安装后的私有文件夹内*/
				InputStream localInputStream = context.getAssets().open("traceroute");
				int size = localInputStream.available();
				byte[] bytes = new byte[size];
				localInputStream.read(bytes);
				FileOutputStream localFileOutputStream = new FileOutputStream(fileTag);
				localFileOutputStream.write(bytes, 0, size);
				localFileOutputStream.close();
				localInputStream.close();
				/**给权限*/
				Process process = Runtime.getRuntime().exec("/system/bin/chmod 744 " + TracrouteBinPath);
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

		return true;
	}
}
