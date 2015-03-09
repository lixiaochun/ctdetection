package com.wellcell.inet.Task.Ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;

import android.content.Context;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Task.AbsTask;
import com.wellcell.inet.Task.TaskPar.PingPar;
import com.wellcell.inet.Task.TaskRankPar;

public class PingTestTask extends AbsTask
{
	final String tag = "PingTestTask";

	public PingPar m_pingPar;	//配置参数 

	//时延
	public double m_dDelayMax = 0;	
	public double m_dDelayMin = 0;
	public double m_dDelayAvg = 0;

	public int m_nCountSuc = 0;	//成功次数
	public int m_nTestCount = 0;//实际测试总次数

	private Process m_process;

	public PingTestTask(Context context, PingPar par,boolean bAuto,String strTaskID,boolean bAnteTest,boolean bBtsInfo)
	{
		super(TaskType.PING, context, bAuto,strTaskID,bAnteTest,bBtsInfo);
		m_pingPar = par;
	}

	public PingTestTask()
	{
	}

	@Override
	protected void runningTask() //执行测试
	{
		sendMessage("Ping " + m_pingPar.m_strDest + " 开始...\n");
		execPing(m_pingPar);
	}

	@Override
	public void recordStop()
	{
		super.recordStop();

		// 解析结果
		// 01-14 14:55:47.792: D/PingTestTask(9435): 5 packets transmitted, 5
		// received, 0% packet loss, time 4008ms
		Pattern pattern = Pattern.compile("(\\d+) packets transmitted, (\\d+) received,");
		Matcher matcher = pattern.matcher(getLog());
		if (matcher.find())
		{
			this.m_nTestCount = Integer.parseInt(matcher.group(1));	//总测试次数
			this.m_nCountSuc = Integer.parseInt(matcher.group(2));	//成功次数
		}

		// 01-14 17:53:24.147: D/PingTestTask(9804): rtt min/avg/max/mdev =
		// 141.693/199.444/223.633/29.454 ms
		pattern = Pattern.compile("rtt min\\/avg\\/max\\/mdev = (\\d+.?\\d+)\\/(\\d+.?\\d+)\\/(\\d+.?\\d+)\\/(\\d+.?\\d+) ms");
		matcher = pattern.matcher(getLog());
		if (matcher.find())
		{
			this.m_dDelayMin = Double.parseDouble(matcher.group(1));
			this.m_dDelayAvg = Double.parseDouble(matcher.group(2));
			this.m_dDelayMax = Double.parseDouble(matcher.group(3));
		}
		sendMessage(String.format("\n任务结果：\n最长时延：%1s毫秒\n最短时延：%2s毫秒\n平均时延：%3s毫秒\n" 
				+"丢包率：%4s%%\n成功打开次数：%d\n总尝试次数：%d\n", 
				m_dDelayMax, m_dDelayMin, m_dDelayAvg, getLostPackRate(),m_nCountSuc, m_nTestCount));
	};

	@Override
	protected void beforeStop()
	{
		super.beforeStop();

		if (m_process != null)
			m_process.destroy();
	}

	private class GetExecError implements Runnable
	{
		InputStreamReader ir;

		GetExecError(InputStreamReader ir)
		{
			this.ir = ir;
		}

		@Override
		public void run()
		{
			try
			{
				// 读取错误
				BufferedReader br = new BufferedReader(ir);
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

	//执行ping命令
	protected void execPing(PingPar par)
	{
		try
		{
			String strCmdPing = "ping -c " + par.m_nCount + " -s " + par.m_nPackSize + " " + par.m_strDest;
			m_process = Runtime.getRuntime().exec(strCmdPing);

			// 开启读取错误
			GetExecError getExecError = new GetExecError(new InputStreamReader(m_process.getErrorStream()));
			new Thread(getExecError).start();

			// 读取输出
			InputStreamReader ir = new InputStreamReader(m_process.getInputStream());
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
	}
	
	//获取丢包率
	private double getLostPackRate()
	{
		double dRet = 0;
		if(m_nTestCount > 0)
			dRet = 100.00 * (m_nTestCount - m_nCountSuc)/m_nTestCount;
		
		if(dRet != 0)
			dRet = CGlobal.floatFormat(dRet, 2);
		
		return dRet;
	}

	@Override
	protected JSONArray getTaskDataJson() //返回业务数据json
	{
		JSONArray obj = new JSONArray();
		
		obj.put(m_strTID);		//业务ID
		obj.put(m_pingPar.m_strDest);
		obj.put(m_pingPar.m_nPackSize + "");
		obj.put(m_pingPar.m_nCount + "");	//配置项
		obj.put((int)m_dDelayMax + "");
		obj.put((int)m_dDelayMin + "");
		obj.put((int)m_dDelayAvg + "");
		obj.put(m_nCountSuc + "");
		obj.put(m_nTestCount + "");		//实际测试次数
		
		return obj;
	}

	@Override
	//任务信息
	protected String getTaskDetailReport()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("目标地址：%1s\n", m_pingPar.m_strDest));
		sb.append(String.format("包大小：%1s\n", m_pingPar.m_nPackSize));
		// sb.append(String.format("重复次数：%1s\n", this.repeatCount));

		sb.append(String.format("成功次数/总次数：%1s/%2s\n", this.m_nCountSuc, this.m_nTestCount));

		sb.append(String.format("最大/平均/最小时延：\n%1s/%2s/%3s秒\n", CGlobal.getSecondString(this.m_dDelayMax),
				CGlobal.getSecondString(this.m_dDelayAvg), CGlobal.getSecondString(this.m_dDelayMin)));

		return sb.toString();
	}

	@Override
	//业务总体报告
	public String getSimpReport()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("成功次数/总次数：%d/%d\n", this.m_nCountSuc, this.m_nTestCount));

		sb.append(String.format("最大/平均/最小时延：\n%1s/%2s/%3s秒\n", CGlobal.getSecondString(this.m_dDelayMax),
				CGlobal.getSecondString(this.m_dDelayAvg), CGlobal.getSecondString(this.m_dDelayMin)));

		return sb.toString();
	}

	@Override
	//业务的评价
	protected String getTaskScoreReport()
	{
		if(m_taskRankPar == null)
			m_taskRankPar = TaskRankPar.getInsant(m_Context);
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("评价：%1s（%2s）\n", m_taskRankPar.getTaskRankName(m_nTaskVal), m_nTaskVal));
		sb.append(String.format("平均速率得分：%1s\n", getSpeedValString()));
		sb.append(String.format("平均时延得分：%1s\n", getDelayValString()));
		sb.append(String.format("成功率得分：%1s\n", getSucRateValString()));
		return sb.toString();
	}

	@Override
	protected double getSpeedAvg()
	{
		return -1;
	}

	@Override
	protected double getDelayAvg()
	{
		return m_dDelayAvg;
	}

	@Override
	protected double getSucRate()
	{
		return m_nCountSuc * 1.0 / m_nTestCount * 100.0;
	}

	@Override
	protected JSONArray getTaskRecJson(JSONArray objRec)
	{
		return null;
	}
}

/*	@Override
protected void addJsonObj(JSONObject obj)
{
	try
	{
		obj.put("DEST_ADDR", m_pingPar.m_strDest);
		obj.put("PAK_SIZE", m_pingPar.m_nPackSize);
		obj.put("REPEATCOUNT", m_pingPar.m_nCount);
		obj.put("MAX_DELAY", this.m_dDelayMax);
		obj.put("MIN_DELAY", this.m_dDelayMin);
		obj.put("AVG_DELAY", this.m_dDelayAvg);
		obj.put("SUC_COUNT", this.m_nCountSuc);
		obj.put("ALL_COUNT", this.m_nTestCount);
	}
	catch (JSONException e)
	{
		e.printStackTrace();
	}
}*/
