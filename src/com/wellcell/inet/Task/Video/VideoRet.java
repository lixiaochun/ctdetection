package com.wellcell.inet.Task.Video;

import org.json.JSONArray;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Task.TaskPar.VideoPar;

//视频单次测试结果
public class VideoRet
{
	public static final int Video_Init = 0;		//初始值
	public static final int Video_Suc = 1;		//成功
	public static final int Video_LinkTO = -1;	//连接超时
	public static final int Video_LinkError = -2;	//连接错误
	public static final int Video_BufTO = -3;	//缓冲超时
	public static final int Video_BufError = -4;	//缓冲错误
	public static final int Video_PlayError = -5;	//播放错误
	public static final int Video_Unknow = -6;	//未知错误
	public static final int Video_PlayTO = -7;	//播放超时

	private VideoPar m_videoPar;
	//------------------------------------------------------
	
	public int m_nRet = Video_Init;	//测试结果
	public long m_lStartTime;		//测试时间
	public boolean m_bSuc;			//是否成功
	public long m_lLinkDelay;	//连接时延(ms)[设置URL-->准备播放]
	public long m_lPlayDelay;	//播放时延(ms)
	private double m_dPlayRate;	//播放比特率
	
	private int m_nPlayDur;	//播放时长(s)
	public long m_lVideoDur;	//视频长度(如果完成播放则记录为播放时长:s)
	//public long m_lCurPos;	//当前播放位置(回调附加使用)
	
	//卡顿
	public int m_nStuckCount;	//卡顿次数
	public long m_lStuckTimeSum;//卡顿时间(ms)
	
	//-------------------------------------------------
	private long m_lLinkSucTime;//连接成功时间
	public long m_lPlayTime;	//播放时间

	private long m_lStuckTimePre;	//上一次卡顿时间
	private long m_lStuckPosPre;	//上一次卡顿位置

	//单位统一为:Byte/s
	public double m_dSpeedCur;	//当前速率
	public double m_dSpeedSum;	//速率累加值
	public int m_nSpeedCount;	//速率计数
	public double m_dSpeedAvg;	//平均速率
	public double m_dSpeedMax;	//最大速率
	public double m_dSpeedMin;	//最小速率
	
	public int m_nSizeSum;	//总流量(Byte)
	
	public VideoRet(VideoPar par)
	{
		m_videoPar = par;
		Init();
		
		m_dPlayRate = m_videoPar.m_dPlayRate;	//播放比特率
		m_nPlayDur = m_videoPar.m_nPlayDur;		//播放时长
	}
	
	//初始化
	public void Init()
	{
		m_nRet = Video_Init;
		m_lStartTime = 0;
		m_bSuc = false;
		m_lLinkDelay = -1;
		m_lPlayDelay = -1;
		m_dPlayRate = 0;
		
		//m_lCurPos = 0;
		
		m_nStuckCount = 0;
		m_lStuckTimeSum = 0;
		
		m_lLinkSucTime = 0;
		m_lPlayTime = 0;
		
		m_lStuckTimePre = 0;
		m_lStuckPosPre = 0;
		
		m_dSpeedCur = 0;
		m_dSpeedSum = 0;
		m_nSpeedCount = 0;
		m_dSpeedAvg = -1;
		m_dSpeedMax = Integer.MIN_VALUE;
		m_dSpeedMin = Integer.MAX_VALUE;
		
		m_nSizeSum = 0;
	}
	
	//设置开始时间
	public void setStartTime()
	{
		m_lStartTime = System.currentTimeMillis();
	}
	
	/**
	 * 功能: 速率收集处理
	 * 参数:dSpeed: KB/s
	 * 返回值:
	 * 说明: 速率统一转换成Byte/s
	 */
	public void addSpeed(double dSpeed)
	{
		m_dSpeedCur = dSpeed * 1000;	//转成Bps
		m_dSpeedSum += m_dSpeedCur;	//速率累加
		m_nSpeedCount++;
		
		//最大速率
		if(m_dSpeedMax < m_dSpeedCur)
			m_dSpeedMax = m_dSpeedCur;
		
		//最小速率
		if(m_dSpeedMin > m_dSpeedCur)
			m_dSpeedMin = m_dSpeedCur;
	}
	
	//获取平均速率
	public double getSpeedAvg()
	{
		if(m_nSpeedCount > 0)
			m_dSpeedAvg = m_dSpeedSum / m_nSpeedCount;
		
		return m_dSpeedAvg;
	}
	
	/**
	 * 功能: 卡顿处理
	 * 参数: lCurPos: 当前播放位置
	 * 返回值:
	 * 说明:
	 */
	public void StuckDeal(long lCurPos)
	{
		long lStuckTimeCur = System.currentTimeMillis();	//当前卡顿时间
		setPlayTime(lStuckTimeCur);	//卡顿时设置播放时间
		
		//同一个位置卡顿算一次
		if(m_lStuckPosPre != lCurPos) //不同的播放时间
		{
			m_nStuckCount++;
			m_lStuckPosPre = lCurPos;
		}
		
		//卡顿时间
		if(m_lStuckTimePre != 0)
		{
			long lDelay = lStuckTimeCur - m_lStuckTimePre;
			if(lDelay <= 2000)
				m_lStuckTimeSum += lDelay;
			else //超过的算一秒
				m_lStuckTimeSum += 1000;
		}
		m_lStuckTimePre = lStuckTimeCur;
	}
	
	//设置播放时间
	public void setPlayTime(long lCurTime)
	{
		if(m_lPlayTime == 0)
			m_lPlayTime = lCurTime;
	}
	
	//设置连接时间
	public void setLinkedTime()
	{
		m_lLinkSucTime = System.currentTimeMillis();
	}
	
	//测试完毕处理
	public void onCompleted()
	{
		//连接时延
		if(m_lLinkSucTime > m_lStartTime)
			m_lLinkDelay = m_lLinkSucTime - m_lStartTime;
		
		//播放时延
		if(m_lPlayTime > m_lLinkSucTime)
			m_lPlayDelay = m_lPlayTime - m_lLinkSucTime;
		
		//平均速率
		if(m_nSpeedCount > 0)
			m_dSpeedAvg = m_dSpeedSum / m_nSpeedCount;
		
		//最后一次停顿处理
		if(m_lStuckTimePre != 0)
			m_lStuckTimeSum += 1000;
	}
	
	//过程记录json
	public JSONArray getJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		obj.put(strTID);
		obj.put(m_lStartTime + "");
		obj.put(m_nRet + "");
		obj.put(m_nSizeSum + "");
		
		obj.put(m_lLinkDelay + "");
		obj.put(m_lPlayDelay + "");
		
		obj.put(CGlobal.floatFormat(m_dPlayRate, 2) + "");	//播放比特率
		obj.put(CGlobal.floatFormat(m_dSpeedAvg, 2) + "");
		
		m_dSpeedMax = CGlobal.getInvalidVal(m_dSpeedMax);
		obj.put(CGlobal.floatFormat(m_dSpeedMax, 2) + "");
		
		m_dSpeedMin = CGlobal.getInvalidVal(m_dSpeedMin);
		obj.put(CGlobal.floatFormat(m_dSpeedMin, 2) + "");
		
		if(m_nPlayDur != 0)
			obj.put(m_nPlayDur + "");	//播放时长
		else
			obj.put(m_lVideoDur + "");
		
		obj.put(m_nStuckCount + "");
		obj.put(m_lStuckTimeSum + "");
	
		return obj;
	}
	
	//获取KQIJson
	public JSONArray getJsonArr(JSONArray obj)
	{
		if(obj == null)
			obj = new JSONArray();
		
		obj.put(m_videoPar.getVideoName());
		obj.put(m_videoPar.m_strUrl);
		obj.put(m_videoPar.getSrvIP());
		
		obj.put(CGlobal.TimestampToDate(m_lStartTime));
		obj.put(CGlobal.floatFormat(m_dSpeedAvg,2) + "");
		obj.put(CGlobal.floatFormat(m_dSpeedMax,2) + "");
		
		return obj;
	}
}
