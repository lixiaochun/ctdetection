package com.wellcell.inet.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.DataProvider.PhoneDataProvider;
import com.wellcell.inet.Web.WebUtil;

//小区信号值收集,采集所有本机获取原始值
public class SignalLog
{
	private static boolean m_bUploaded = false;	//是否已经上传,启动一次app只上传一次
	private static List<SignalLog> m_listSignalLogs = new ArrayList<SignalLog>(); //原始值集合
	
	private String m_strModel;			//手机型号
	private String m_strRom;			//手机rom版本号
	private long m_lTime;				//采集时间
	private int m_nDataSrc;				//数据来源;0:本机直接获取,1:解码
	//----------------LTE--------------------
	public String m_strMccOrg = "";		//国家代码
	public String m_strMncOrg = "";		//网络号
	public String m_strCiOrg = "";		//扇区混合信息
	public String m_strPciOrg = "";		//Physical Cell Id 
	public String m_strTacOrg = "";		//TAC
	public double m_dRsrpOrg = 0;		//RSRP
	public double m_dRsrqOrg = 0;		//RSRQ
	public double m_dRssiLteOrg = 0;	//RSSI
	public double m_dSinrLteOrg = 0;	//SINR
	public int m_nCqiLteOrg = 0;		//CQI
	
	//---------------CDMA---------------------------------
	public String m_strCIDOrg = "";		//CID
	public String m_strSIDOrg = "";		//SID
	public String m_strNIDOrg = "";		//NID
	public double m_dRx3GOrg = -120;	//信号场强
	public double m_dEcio3GOrg = -120;	//ECIO 载干比
	public double m_dSnr3GOrg = -120;	//SINR
	public double m_dRx2GOrg = 0;		//信号场强
	public double m_dEcio2GOrg = 0;		//ECIO 载干比
	//-----------------GSM---------------------------------
	public String m_strCIGsmOrg = "";	//Cell id
	public String m_strLacGsmOrg = "";	//local area code 区域码
	public double m_dRxGsmOrg = -120;
	
	public SignalLog()
	{
		m_strModel = PhoneDataProvider.getModel();
		m_strRom = PhoneDataProvider.getRomVersion();
		//m_lTime = System.currentTimeMillis();
	}
	
	public SignalLog(SignalLog signalOrg,int nSrc)
	{
		m_strModel = signalOrg.m_strModel;
		m_strRom = signalOrg.m_strRom;
		m_lTime = System.currentTimeMillis();
		m_nDataSrc = nSrc;
		
		m_strMccOrg = signalOrg.m_strMccOrg;
		m_strMncOrg = signalOrg.m_strMncOrg;
		m_strCiOrg = signalOrg.m_strCiOrg;
		m_strPciOrg = signalOrg.m_strPciOrg;
		m_strTacOrg = signalOrg.m_strTacOrg;
		m_dRsrpOrg = signalOrg.m_dRsrpOrg;
		m_dRsrqOrg = signalOrg.m_dRsrqOrg;
		m_dRssiLteOrg = signalOrg.m_dRssiLteOrg;
		m_dSinrLteOrg = signalOrg.m_dSinrLteOrg;
		m_nCqiLteOrg = signalOrg.m_nCqiLteOrg;
		
		m_strCIDOrg = signalOrg.m_strCIDOrg;
		m_strSIDOrg = signalOrg.m_strSIDOrg;
		m_strNIDOrg = signalOrg.m_strNIDOrg;
		m_dRx3GOrg = signalOrg.m_dRx3GOrg;
		m_dEcio3GOrg = signalOrg.m_dEcio3GOrg;
		m_dSnr3GOrg = signalOrg.m_dSnr3GOrg;
		m_dRx2GOrg = signalOrg.m_dRx2GOrg;
		m_dEcio2GOrg = signalOrg.m_dEcio2GOrg;
		
		m_strCIGsmOrg = signalOrg.m_strCIGsmOrg;
		m_strLacGsmOrg = signalOrg.m_strLacGsmOrg;
		m_dRxGsmOrg = signalOrg.m_dRxGsmOrg;
	}
	
	//转换成json
	public JSONArray getJsonArray()
	{
		JSONArray jsonArr = new JSONArray();
		
		jsonArr.put(m_strModel);
		jsonArr.put(m_strRom);
		jsonArr.put(m_lTime + "");
		jsonArr.put(m_nDataSrc + "");

		//LTE
		jsonArr.put(m_strMccOrg);
		jsonArr.put(m_strMncOrg);
		jsonArr.put(m_strCiOrg);
		jsonArr.put(m_strPciOrg);
		jsonArr.put(m_strTacOrg);
		jsonArr.put(CGlobal.floatFormatString(m_dRsrpOrg, 2));
		jsonArr.put(CGlobal.floatFormatString(m_dRsrqOrg,2));
		jsonArr.put(CGlobal.floatFormatString(m_dRssiLteOrg,2));
		jsonArr.put(CGlobal.floatFormatString(m_dSinrLteOrg, 2));
		jsonArr.put(m_nCqiLteOrg + "");
		//CDMA
		jsonArr.put(m_strCIDOrg);
		jsonArr.put(m_strSIDOrg);
		jsonArr.put(m_strNIDOrg);
		jsonArr.put(CGlobal.floatFormatString(m_dRx3GOrg,2));
		jsonArr.put(CGlobal.floatFormatString(m_dEcio3GOrg,2));
		jsonArr.put(CGlobal.floatFormatString(m_dSnr3GOrg,2));
		jsonArr.put(CGlobal.floatFormatString(m_dRx2GOrg,2));
		jsonArr.put(CGlobal.floatFormatString(m_dEcio2GOrg,2));
		//GSM
		jsonArr.put(m_strCIGsmOrg);
		jsonArr.put(m_strLacGsmOrg);	
		jsonArr.put(CGlobal.floatFormatString(m_dRxGsmOrg,2));

		return jsonArr;
	}
	
	//获取所有记录Json上传字符串
	public static String getSignalLog()
	{
		JSONObject obj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		for (int i = 0; i < m_listSignalLogs.size(); i++)
		{
			jsonArr.put(m_listSignalLogs.get(i).getJsonArray());
		}
		
		try
		{
			obj.put("SignalLog", jsonArr);
		}
		catch (JSONException e)
		{
			return "";
		}
		return obj.toString();
	}
	
	/**
	 * 功能: 获取小区信号原始值
	 * 参数: nSrc: 数据来源;0:本机获取,1:解码
	 * 返回值:
	 * 说明:
	 */
	public static void addSignalLog(SignalLog signalLog,int nSrc)
	{
		if(m_bUploaded == true) //已经上传
			return;
		
		SignalLog newLog = new SignalLog(signalLog,nSrc);

		//只保存10条记录
		while(m_listSignalLogs.size() > 9)
		{
			m_listSignalLogs.remove(0);
		}
		
		m_listSignalLogs.add(newLog);	//添加新纪录
	}
	
	//上传到服务器
	public static void uploadSignalLog()
	{
		if(m_listSignalLogs.size() <= 0 || m_bUploaded == true)
			return;
		
		String strRet = WebUtil.uploadLogs(getSignalLog(),0,-1);	//上传
		if(strRet.equals("ok")) //上传成功
			m_bUploaded = true;
	}
}
