package com.wellcell.inet.Task.Web;

import org.json.JSONArray;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Task.TaskPar.WebPar;

//网页测试结果集
public class WebRet
{
	private final String TAG = "WebRet";
	
	private WebPar m_webPar;
	//-------------------------------------------------
	
	public long m_lTime;			//时间
	public int m_nRet;				//结果信息
	public boolean m_bSuc;			//是否成功
	
	public String m_strDstIP;		//目标IP
	public String m_strLocIP;		//本地IP
	
	public int m_nDnsParsDelay;	//DNS解析时延
	public int m_nConnDelay;		//建立连接时延
	public int m_nSendReqDelay;	//发送请求时延
	public int m_nRecResponDelay;	//接收响应时延
	
	public int m_nLinkDelay;		//连接时延(首包时延)
	public int m_nHttpStatus;		//HTTEp响应头状态
	public int m_nDownLoadDelay;	//下载时延
	public int m_nWebSize;			//网页大小
	
	public int m_nLoadDelay;		//加载时延
	public int m_nSumSize;			//总流量
	
	//--------------------------------------------
	public long m_lDnsParsTime;	//DNS解析成功时间
	public long m_lConnSucTime;	//连接成功时间
	public long m_lSendReqTime;//发送头成功时间
	public long m_lRecResponTime;//接收响应时间(解析响应头)
	//public long m_lDownLoadTime; //下载内容完毕时间
	
	public WebRet(WebPar par)
	{
		m_webPar = par;
		Init();
	}
	
	//初始化
	public void Init()
	{
		m_lTime = 0;
		m_nRet = 0;
		m_bSuc = false;
		m_strDstIP = "";
		
		m_nDnsParsDelay = 0;
		m_nConnDelay = 0;
		m_nSendReqDelay = 0;
		m_nRecResponDelay = 0;
		
		m_nLinkDelay = 0;
		m_nHttpStatus = 0;
		m_nDownLoadDelay = 0;
		m_nWebSize = 0;
		m_nLoadDelay = 0;
		m_nSumSize = 0;
		
		m_lDnsParsTime = 0;
		m_lConnSucTime = 0;
		m_lSendReqTime = 0;
		m_lRecResponTime = 0;
	}
	
	//完成后统计
	public void OnComplete()
	{
		//DNS解析时延
		if(m_lDnsParsTime != 0)
			m_nDnsParsDelay = (int)(m_lDnsParsTime - m_lTime);
		
		//建立连接时延
		if(m_lConnSucTime != 0)
			m_nConnDelay = (int)(m_lConnSucTime - m_lDnsParsTime);
		
		//发送请求时延
		if(m_lSendReqTime != 0)
			m_nSendReqDelay = (int)(m_lSendReqTime - m_lConnSucTime);
		
		
		if(m_lRecResponTime != 0)
		{
			m_nRecResponDelay = (int)(m_lRecResponTime - m_lSendReqTime); //接收响应时延
			m_nLinkDelay = (int)(m_lRecResponTime - m_lTime);	//首包时延
		}
	}
	
	//获取连接时延
	public int getLinkDelay()
	{
		return m_nLinkDelay;
	}
	
	//获取下载时延
	public int getDownLoadDelay()
	{
		return m_nDownLoadDelay;
	}
	
	//获取网页打开的时间(ms)
	public int getOpenDelay()
	{
		//Log.i(TAG, "打开时延: " + m_nLoadDelay);
		int nRet = -1;
		if(m_bSuc)
			nRet = m_nLinkDelay + m_nDownLoadDelay + m_nLoadDelay;
		
		return nRet;
	}
	
	//获取平均下载速率(byte/s)
	public double getDownLoadSpeed()
	{
		double dRet = 0;
		if(m_nDownLoadDelay != 0)
			dRet = m_nWebSize * 1000.0 / m_nDownLoadDelay;
		
		return dRet;
	}
	
	public int getWebSize()
	{
		return m_nSumSize;
	}
	
	//过程记录json
	public JSONArray getJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		obj.put(strTID);
		obj.put(m_lTime + "");
		obj.put(m_nRet + "");
		obj.put(m_nLinkDelay + "");
		obj.put(m_nHttpStatus + "");
		obj.put(m_nDownLoadDelay + "");
		obj.put(m_nWebSize + "");
		obj.put(m_nLoadDelay + "");
		obj.put(m_nSumSize);
		
		return obj;
	}
	
	//获取单次测试结果
	public String getRetInfo(String strWebName)
	{
		StringBuffer sb = new StringBuffer("网页：" + strWebName + " 测试完成，");
		sb.append(m_bSuc ? "成功" : "失败");
		sb.append("\n");
		sb.append("DNS解析延时：" + m_nDnsParsDelay + "毫秒\n");
		sb.append("建立连接时延：" + m_nConnDelay + "毫秒\n");
		sb.append("发送请求时延：" + m_nSendReqDelay + "毫秒\n");
		sb.append("接收响应时延：" + m_nRecResponDelay + "毫秒\n");
		sb.append("首包延时(KQI)：" + getLinkDelay() + "毫秒\n");
		sb.append("下载延时：" + m_nDownLoadDelay + "毫秒\n");
		sb.append("网页打开延时(KQI)：" + CGlobal.getSecondString(getOpenDelay()) + "秒\n");
		sb.append("目标地址：" + m_strDstIP + "\n");
		sb.append("本地地址：" + m_strLocIP + "\n");
		sb.append("下载文件大小："+ CGlobal.getSizeString(m_nWebSize) + "\n");
		sb.append("下载速率：" + CGlobal.getSpeedString(getDownLoadSpeed()) + "\n");
		sb.append("消耗流量：" + CGlobal.getSizeString(getWebSize()) + "\n");
		
		return sb.toString();
	}
	
	public JSONArray getJsonVal(JSONArray obj)
	{
		if(obj == null)
			obj = new JSONArray();
		
		obj.put(m_webPar.m_strName);
		obj.put(m_webPar.m_strUrl);
		obj.put(m_strDstIP);	//网页IP
		obj.put(CGlobal.TimestampToDate(m_lTime));
		obj.put(m_nLinkDelay + "");
		obj.put((getOpenDelay() * 1.00 / 1000) + "");
		obj.put(m_nDnsParsDelay + "");
		obj.put(m_nConnDelay + "");
		obj.put(m_nSendReqDelay + "");
		obj.put(m_nRecResponDelay + "");
		obj.put(m_bSuc == true ? "1" : "0");
		
		return obj;
		
	}
}
