package com.wellcell.inet.Task.Video;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.view.SurfaceHolder;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Common.CGlobal.TestState;
import com.wellcell.inet.Task.AbsTask;
import com.wellcell.inet.Task.TaskRankPar;
import com.wellcell.inet.Task.TempAidInfo;
import com.wellcell.inet.Task.TaskPar.VideoPar;
import com.wellcell.inet.Task.Video.VideoObject.VideoCbType;
import com.wellcell.inet.Task.Video.VideoObject.VideoMsgCallBack;
import com.wellcell.inet.Task.Video.VideoObject.VideoState;

//视频测试
public class VideoTask extends AbsTask implements VideoMsgCallBack
{
	private Context m_Context;
	
	private VideoPar m_videoPar;	//配置参数
	private VideoObject m_videoObj;	//视频测试对象
	private List<VideoRet> m_listVideoRet = new ArrayList<VideoRet>();	//结果集
	//public JSONArray m_jsonArr = new JSONArray();	//结果数组
	
	private long m_lVideoDur;	//视频长度(如果完成播放则记录为播放时长:s)
		
	private int m_nUid;
	private int m_nInterval = 100;	//时间间隔
	
	private long m_lStartSize = 0;	//开始流量
	
	public double m_dSpeedSum = 0;	//速率累加总和
	public int m_nSpeedCountSum = 0;	//速率计数总和

	//--------------------------------------------------------------------------------
	//任务级别结果
	public long m_lSizeSum = -1;		//总流量(byte)
	private long m_lSizeAvg = -1;		//平均流量(MB)
	public int m_nCountSuc;		//成功次数
	public int m_nCountSum = 0;	//测试次数
	
	//连接时延
	private long m_lLinkDelayAvg = -1;	//平均
	private long m_lLinkDelayMax = Integer.MIN_VALUE;	//最大
	private long m_lLinkDelayMin = Integer.MAX_VALUE;	//最小
	
	//播放时延
	private long m_lPlayDelayAvg = -1;	//平均
	private long m_lPlayDelayMax = Integer.MIN_VALUE;	//最大
	private long m_lPlayDelayMin = Integer.MAX_VALUE;	//最小
	
	private double m_dSpeedAvg = -1;		//平均速率
	public double m_dSpeedMax = Integer.MIN_VALUE;	//最大速率
	private double m_dSpeedMin = Integer.MAX_VALUE;	//最小速率
	
	//卡顿
	private int m_nStuckCountAvg = 0;	//平均卡顿次数
	private long m_lStuckTimeAvg = 0;	//平均卡顿时长
	//--------------------------------------------------------------------------------
	
	public VideoTask(Context context, VideoPar par, boolean bAuto,
			String strTaskID,boolean bAnteTest,boolean bBtsInfo,SurfaceHolder suface)
	{
		super(TaskType.VIDEO, context, bAuto,strTaskID,bAnteTest,bBtsInfo);
		
		m_Context = context;
		m_videoPar = par;
		m_videoObj = new VideoObject(m_Context,m_videoPar,this,suface);
		m_nUid = CGlobal.getUID(m_Context);	//获取User ID
		
		m_aidInfo = new TempAidInfo(context.getApplicationContext());
	}
	
	@Override
	protected void runningTask()
	{
		// 开启测试
		VideoRet videoRet;
		for (int i = 0; i < m_videoPar.m_nCount && m_curState == TestState.eTesting; i++)
		{
			sendMessage(String.format("\t\t\t视频测试开始\n正在打开 %1s...",m_videoPar.getVideoName()));

			videoRet = VideoTest();	//开始测试
			m_listVideoRet.add(videoRet);	//保存结果
			BuildJsonRec(videoRet);

			sendMessage(String.format("测试完成，%1s\n连接延时：%2s毫秒\n播放延时：%3s秒\n平均速率(KQI)：%4s\n" 
					+ "峰值速率(KQI)：%5s\n卡顿次数：%6s\n卡顿时间：%7s秒\n流量：%8s\n", 
					videoRet.m_bSuc ? "成功" : "失败",CGlobal.getMsString(videoRet.m_lLinkDelay), 
					CGlobal.getSecondString(videoRet.m_lPlayDelay), CGlobal.getSpeedString(videoRet.m_dSpeedAvg),
					CGlobal.getSpeedString(videoRet.m_dSpeedMax), videoRet.m_nStuckCount,
					videoRet.m_lStuckTimeSum / 1000,CGlobal.getSizeString(videoRet.m_nSizeSum)));

			if(i != m_videoPar.m_nCount - 1)
				CGlobal.Sleep(2000);//间隔时延
		}
		//-----------------------------------------------------------------------------
		statistRet();	//完成后统计
		//------------------------------------------------------------------------------
	}
	
	//开始视频测试
	private VideoRet VideoTest()
	{
		m_lStartSize = CGlobal.getCurTrafficRx(m_nUid);	//开始流量大小
		m_aidInfo.updateStartInfo();	//更新信息
		
		m_videoObj.startTest();	//开始测试
		
		//等待测试完毕
		while(!m_videoObj.IsFinished())
		{
			CGlobal.Sleep(m_nInterval);
		}
		
		VideoRet videoRet = m_videoObj.getVideoRet();
		videoRet.m_nSizeSum = (int)(CGlobal.getCurTrafficRx(m_nUid) - m_lStartSize);	//单次总流量
		m_aidInfo.getCpuUsageInfo(1);	//结束CPU时间信息
		
		return videoRet;
	}
	
	//拼装单挑记录
	private void BuildJsonRec(VideoRet videoRet)
	{
		JSONArray jsonRec = new JSONArray();	//单条
		jsonRec = m_aidInfo.getJsonVal(jsonRec);
		jsonRec = videoRet.getJsonArr(jsonRec);
		if(m_jsonArrRec == null)
			m_jsonArrRec = new JSONArray();
		
		m_jsonArrRec.put(jsonRec);
	}
	
	//结果统计
	private void statistRet()
	{
		long lLinkDelaySum = 0;	//连接时延总和
		long lPlayDelaySum = 0;	//播放时延总和
		m_dSpeedSum = 0;	//速率累加总和
		m_nSpeedCountSum = 0;	//速率计数总和
		int nStuckCountSum = 0;	//卡顿次数总和
		long lStuckTimeSum = 0;	//卡顿时间总和
		
		VideoRet videoRet;
		for (int i = 0; i < m_listVideoRet.size(); i++)
		{
			videoRet = m_listVideoRet.get(i);
			m_nCountSum++;
			
			if(videoRet.m_bSuc) //成功
			{
				m_lSizeSum += videoRet.m_nSizeSum;
				m_nCountSuc++;
				
				//视频时长
				if(m_videoPar.m_nPlayDur == 0)
					m_lVideoDur = videoRet.m_lVideoDur;
				
				//连接时延
				lLinkDelaySum += videoRet.m_lLinkDelay;
				if(m_lLinkDelayMax < videoRet.m_lLinkDelay)
					m_lLinkDelayMax = videoRet.m_lLinkDelay;
				
				if(m_lLinkDelayMin > videoRet.m_lLinkDelay)
					m_lLinkDelayMin = videoRet.m_lLinkDelay;
				
				//播放时延
				lPlayDelaySum += videoRet.m_lPlayDelay;
				if(m_lPlayDelayMax < videoRet.m_lPlayDelay)
					m_lPlayDelayMax = videoRet.m_lPlayDelay;
				
				if(m_lPlayDelayMin > videoRet.m_lPlayDelay)
					m_lPlayDelayMin = videoRet.m_lPlayDelay;
				
				//速率
				m_dSpeedSum += videoRet.m_dSpeedSum;
				m_nSpeedCountSum += videoRet.m_nSpeedCount;
				
				if(m_dSpeedMax < videoRet.m_dSpeedMax)
					m_dSpeedMax = videoRet.m_dSpeedMax;
				
				if(m_dSpeedMin > videoRet.m_dSpeedMin)
					m_dSpeedMin = videoRet.m_dSpeedMin;
				
				//卡顿
				nStuckCountSum += videoRet.m_nStuckCount;
				lStuckTimeSum += videoRet.m_lStuckTimeSum;
			}
		}
		
		//均值
		if(m_nCountSuc > 0)
		{
			m_lSizeAvg = m_lSizeSum / m_nCountSuc;	//平均流量
			m_lLinkDelayAvg = lLinkDelaySum / m_nCountSuc;	//平均连接时延
			m_lPlayDelayAvg = lPlayDelaySum / m_nCountSuc;	//平均播放时延
			m_nStuckCountAvg = nStuckCountSum / m_nCountSuc;//平均卡顿次数	
			m_lStuckTimeAvg = lStuckTimeSum / m_nCountSuc;	//平均卡顿时间
		}
		
		//平均速率
		if(m_nSpeedCountSum > 0)
			m_dSpeedAvg = m_dSpeedSum / m_nSpeedCountSum;
		
		//最大最小值处理
		m_lLinkDelayMax = CGlobal.getInvalidVal(m_lLinkDelayMax);
		m_lLinkDelayMin = CGlobal.getInvalidVal(m_lLinkDelayMin);
		m_lPlayDelayMax = CGlobal.getInvalidVal(m_lPlayDelayMax);
		m_lPlayDelayMin = CGlobal.getInvalidVal(m_lPlayDelayMin);
		m_dSpeedMax = CGlobal.getInvalidVal(m_dSpeedMax);
		m_dSpeedMin = CGlobal.getInvalidVal(m_dSpeedMin);
	}
	
	//Video过程数据回调
	@Override
	public void onReceived(String msg)
	{
		sendMessage(msg);
	}

	//任务信息
	@Override
	protected String getTaskDetailReport()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("视频：%1s\n", m_videoPar.getVideoName()));
		sb.append(String.format("类型：%1s\n", m_videoPar.getVideoType()));
		sb.append(String.format("播放时长：%1s秒\n", m_videoPar.m_nPlayDur));
		sb.append(String.format("超时时间：%1s毫秒\n", m_videoPar.m_nTimeout));

		sb.append(String.format("成功次数/总次数：%1s/%2s\n", m_nCountSuc, m_nCountSum));
		//连接延时
		sb.append(String.format("最大/平均/最小连接延时：\n%1s/%2s/%3s毫秒\n", 
				CGlobal.getMsString(m_lLinkDelayMax), 
				CGlobal.getMsString(m_lLinkDelayAvg), 
				CGlobal.getMsString(m_lLinkDelayMin)));
		//播放延时
		sb.append(String.format("最大/平均/最小播放延时：\n%1s/%2s/%3s秒\n", 
				CGlobal.getSecondString(m_lPlayDelayMax), 
				CGlobal.getSecondString(m_lPlayDelayAvg), 
				CGlobal.getSecondString(m_lPlayDelayMin)));
		
		//速率
		sb.append(String.format("最大/平均/最小速率(KQI)：\n%1s/%2s/%3s Kbps\n", 
				CGlobal.getSpeedKbps(m_dSpeedMax), 
				CGlobal.getSpeedKbps(m_dSpeedAvg), 
				CGlobal.getSpeedKbps(m_dSpeedMin)));

		sb.append(String.format("总流量：%1s\n", CGlobal.getSizeString(m_lSizeSum)));
		sb.append(String.format("平均流量（只计算成功的）：%1s\n", CGlobal.getSizeString(m_lSizeAvg)));

		return sb.toString();
	}

	//业务评价
	@Override
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

	//总体报告中的业务测试报告
	@Override
	public String getSimpReport()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("成功次数/总次数：%1s/%2s\n", m_nCountSuc, m_nCountSum));
		//连接延时
		sb.append(String.format("最大/平均/最小连接延时：\n%1s/%2s/%3s 毫秒\n", 
				CGlobal.getMsString(m_lLinkDelayMax), 
				CGlobal.getMsString(m_lLinkDelayAvg), 
				CGlobal.getMsString(m_lLinkDelayMin)));
		//播放延时
		sb.append(String.format("最大/平均/最小播放延时：\n%1s/%2s/%3s 秒\n", 
				CGlobal.getSecondString(m_lPlayDelayMax), 
				CGlobal.getSecondString(m_lPlayDelayAvg), 
				CGlobal.getSecondString(m_lPlayDelayMin)));
		
		//速率
		sb.append(String.format("最大/平均/最小速率(KQI)：\n%1s/%2s/%3s Kbps\n", 
				CGlobal.getSpeedKbps(m_dSpeedMax), 
				CGlobal.getSpeedKbps(m_dSpeedAvg), 
				CGlobal.getSpeedKbps(m_dSpeedMin)));

		sb.append(String.format("总流量：%1s\n", CGlobal.getSizeString(m_lSizeSum)));
		sb.append(String.format("平均流量（只计算成功的）：%1s\n", CGlobal.getSizeString(m_lSizeAvg)));
		
		return sb.toString();
	}
	
	//获取任务结果json
	@Override
	protected JSONArray getTaskDataJson()
	{
		JSONArray obj = new JSONArray();
		
		obj.put(m_strTID);
		obj.put(m_videoPar.getVideoName());
		obj.put(m_videoPar.m_nType);
		obj.put(m_videoPar.m_strUrl);
		obj.put(m_videoPar.m_dPlayRate + "");
		obj.put( m_videoPar.m_nTimeout + "");
		obj.put( m_videoPar.m_nCount + "");
		
		if(m_videoPar.m_nPlayDur != 0)
			obj.put( m_videoPar.m_nPlayDur + "");
		else
			obj.put(m_lVideoDur + "");

		//流量
		obj.put(m_lSizeSum + "");
		obj.put(m_lSizeAvg + "");
		obj.put(m_nCountSuc + "");
		
		//连接时延
		obj.put(m_lLinkDelayMax + "");	
		obj.put(m_lLinkDelayMin + "");	
		obj.put(m_lLinkDelayAvg + "");	
		
		//播放时延
		obj.put(m_lPlayDelayMax + "");
		obj.put(m_lPlayDelayMin + "");
		obj.put(m_lPlayDelayAvg + "");

		//速率
		obj.put(CGlobal.floatFormatString(m_dSpeedMax,2));
		obj.put(CGlobal.floatFormatString(m_dSpeedMin,2));
		obj.put(CGlobal.floatFormatString(m_dSpeedAvg ,2));
		
		//卡顿
		obj.put(m_nStuckCountAvg + "");	//平均卡顿次数
		obj.put(m_lStuckTimeAvg + "");	//平均卡顿市场
		
		return obj;
	}

	//获取过程记录json
	@Override
	protected JSONArray getTaskRecJson(JSONArray objRec)
	{
		if(objRec == null)
			objRec = new JSONArray();
		
		for (int i = 0; i < m_listVideoRet.size(); i++)
		{
			objRec.put(m_listVideoRet.get(i).getJson(m_strTID));
		}
		return objRec;
	}

	@Override
	protected double getSpeedAvg()
	{
		if(m_dSpeedAvg < 0)
			return -1;
		
		return m_dSpeedAvg * 8.0 / 1000;
	}

	@Override
	protected double getDelayAvg()
	{
		return m_lLinkDelayAvg + m_lPlayDelayAvg;	//连接时延+播放时延
	}

	@Override
	protected double getSucRate()
	{
		if(m_nCountSum > 0)
			return m_nCountSuc * 1.0 / m_nCountSum * 100.0;
		
		return 0;
	}

	@Override
	public void onUpdateInfo(VideoCbType type,Object obj)
	{
		
	}
}
