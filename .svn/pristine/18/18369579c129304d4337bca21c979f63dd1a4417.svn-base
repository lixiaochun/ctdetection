package com.wellcell.inet.SignalTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wellcell.inet.DataProvider.PhoneDataProvider;
import com.wellcell.inet.entity.AddrInfo;

//信号测试结果
public class SignalTestRet
{
	private String TAG = "SignalTestRet";

	private String m_strUser = ""; // 账户
	private String m_strIMSI = "";
	private String m_strIMEI = "";
	private String m_strDevID = ""; // 终端设备号: 厂家 终端型号
	private String m_strModleNum = ""; // 终端信号
	// 基本信息
	private String m_strJobID = ""; // 任务ID
	private long m_lStartTime = 0; // 任务开始时间
	private long m_lEndTime = 0; // 任务结束时间
	private String m_strHotID = ""; // 热点id

	private Map<LinkCell, List<SignalInfo>> m_mapDataRet = null; // 结果集

	public SignalTestRet(Context context)
	{
		//m_strUser = UserInfoHelper.getUser(context); // 用户名
		m_strIMSI = PhoneDataProvider.getIMSI(context); // IMSI
		m_strIMEI = PhoneDataProvider.getIMEI(context); // IMEI
		m_strDevID = PhoneDataProvider.getModel(); // 终端设备号

		try
		{
			String[] strVals = m_strDevID.split("\\s+"); // 任意空格
			m_strModleNum = strVals[strVals.length > 1 ? strVals.length - 1 : 0];
		}
		catch (Exception e)
		{
		}

		m_mapDataRet = new HashMap<LinkCell, List<SignalInfo>>();
	}

	/**
	 * 功能: 提取测试信息 
	 * 参数: signalInfo: 小区信号信息 
	 * 		strAddr: 地址信息
	 *  返回值: 
	 *  说明:
	 */
	public void getCellSignalInfo(TelStrengthInfo signalInfo, AddrInfo addrInfo)
	{
		long lCurTime = System.currentTimeMillis(); //当前时间
		if (lCurTime - m_lEndTime < 1000) //限定最小采集间隔
			return;

		//Log.i("SignalTestRet", "Interval=" + (lCurTime - m_lEndTime));
		m_lEndTime = lCurTime; // 保存最后的时间

		// 提取小区信息
		LinkCell curCell = new LinkCell(signalInfo, addrInfo, m_lEndTime);
		SignalInfo curSignalInfo = new SignalInfo(m_lEndTime, signalInfo);

		if (!m_mapDataRet.containsKey(curCell)) // 不存在
		{
			List<SignalInfo> listSignal = new ArrayList<SignalInfo>();
			listSignal.add(curSignalInfo); // 添加新的信号信息
			m_mapDataRet.put(curCell, listSignal);
		}
		else// 存在
		{
			m_mapDataRet.get(curCell).add(curSignalInfo);
		}
	}
	
	//更新结束时间
	public void updateEndTime()
	{
		m_lEndTime = System.currentTimeMillis();
	}

	/**
	 * 功能: 初始化 
	 * 参数: 
	 * 返回值: 
	 * 说明:
	 */
	public void Init(String strHotID, long lStartTime)
	{
		m_strHotID = strHotID;
		m_lStartTime = lStartTime;

		m_mapDataRet.clear(); // 清空数据集
	}

	/**
	 * 功能: 是否测试完毕 
	 * 参数: dTestTime: 测试时长
	 *  返回值: 
	 *  说明:
	 */
	public boolean bComplete(double dTestTime)
	{
		if (System.currentTimeMillis() - m_lStartTime > dTestTime)
			return true;
		else
			return false;
	}

	/**
	 * 功能:格式化结果,输出json格式字符串 
	 * 参数: nNetworkType: 当前的活动网络类型 
	 * 返回值: 
	 * 说明:
	 */
	public String toRetStringEx(int nNetworkType)
	{
		m_strJobID = m_strDevID + "_" + m_lStartTime; // 任务ID

		JSONObject jsonRet = new JSONObject();	//总结果
		JSONArray jsonCell = new JSONArray();	//小区结果
		JSONArray jsonSignal = new JSONArray();	//信号结果
		
		JSONArray arrTemp;

		// cell
		List<SignalInfo> listSignal = null;
		LinkCell curCell = null;
		Iterator iter = m_mapDataRet.entrySet().iterator();
		Entry entry = null;
		while (iter.hasNext())
		{
			entry = (Entry) iter.next();
			curCell = (LinkCell) entry.getKey();
			
			//小区信息
			arrTemp = curCell.getCellJson(m_strJobID);
			jsonCell.put(arrTemp); 

			listSignal = (List<SignalInfo>) entry.getValue();
			for (int i = 0; i < listSignal.size(); i++)
			{
				//信号信息
				arrTemp = listSignal.get(i).getSignalJson(curCell.m_strLinkID);
				jsonSignal.put(arrTemp);
			}
		}
		
		//组装结果
		try
		{
			jsonRet.put("job", getJobJson(nNetworkType));  //job
			jsonRet.put("cell", jsonCell);
			jsonRet.put("signal", jsonSignal);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return jsonRet.toString();
	}
	
	//获取jobInfo的json
	private JSONArray getJobJson(int nNetworkType)
	{
		JSONArray jsonArr = new JSONArray();
		jsonArr.put(m_strJobID);
		jsonArr.put("5");
		jsonArr.put("null");
		jsonArr.put(m_lStartTime + "");
		jsonArr.put(m_lEndTime + "");
		jsonArr.put(m_strUser);
		jsonArr.put(m_strIMSI);
		jsonArr.put(m_strIMEI);
		jsonArr.put(m_strDevID);
		jsonArr.put(m_strHotID);
		jsonArr.put(nNetworkType + "");
		return jsonArr;
	}
	//===============================================================================
	// 小区信息
	public class LinkCell
	{
		private String m_strLinkID = ""; // 连接ID(目前用终端信号_时间戳)

		// LTE
		private String m_strMCC = "";
		private String m_strMNC = "";
		private String m_strCI = "";
		private String m_strPCI = "";
		private String m_strTAC = "";

		// CDMA
		private String m_strCID = "";
		private String m_strSID = "";
		private String m_strNID = "";

		// GSM
		private String m_strCellIDGsm = "";
		private String m_strLac = "";

		// 详细地址信息
		private String m_strAddr = ""; 		//详细信息
		private String m_strProv = ""; 		// 省份
		private String m_strCity = ""; 		// 城市
		private String m_strDistrict = ""; 	// 区/县
		private String m_strStreet = ""; 	// 街道
		private String m_strStreetNum = ""; // 街道号

		public LinkCell(TelStrengthInfo signalInfo, AddrInfo addrInfo, long lTime)
		{
			m_strLinkID = m_strModleNum + "_" + lTime; // 使用时间戳作为ID

			//小区信息在提取时已经作了有效性处理
			//LTE
			if(signalInfo.IsValidInfo(0)) //有效性判断
			{
				m_strMCC = signalInfo.m_strMcc;
				m_strMNC = signalInfo.m_strMnc;
				m_strCI = signalInfo.m_strCi;
				m_strPCI = signalInfo.m_strPci;
				m_strTAC = signalInfo.m_strTac;
			}

			//CDMA
			if(signalInfo.IsValidInfo(1) || signalInfo.IsValidInfo(2))
			{
				m_strCID = signalInfo.m_strCID;
				m_strSID = signalInfo.m_strSID;
				m_strNID = signalInfo.m_strNID;
			}

			//GSM
			if(signalInfo.IsValidInfo(3))
			{
				m_strCellIDGsm = signalInfo.m_strCIDGsm;
				m_strLac = signalInfo.m_strLacGsm;
			}

			//地址信息
			if (addrInfo != null)
			{
				m_strAddr = addrInfo.toString();
				m_strProv = addrInfo.m_strProv;
				m_strCity = addrInfo.m_strCity;
				m_strDistrict = addrInfo.m_strDistrict;
				m_strStreet = addrInfo.m_strStreet;
				m_strStreetNum = addrInfo.m_strStreetNum;
			}
		}

		@Override
		public int hashCode()
		{
			return m_strMCC.hashCode() + m_strMNC.hashCode() + m_strCI.hashCode() + m_strPCI.hashCode() 
					+ m_strTAC.hashCode() + m_strCID.hashCode() + m_strSID.hashCode() + m_strNID.hashCode() 
					+ m_strCellIDGsm.hashCode() + m_strLac.hashCode() + m_strAddr.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			LinkCell other = (LinkCell) o;
			if (!m_strMCC.equals(other.m_strMCC))
				return false;

			if (!m_strMNC.equals(other.m_strMNC))
				return false;

			if (!m_strCI.equals(other.m_strCI))
				return false;

			if (!m_strPCI.equals(other.m_strPCI))
				return false;

			if (!m_strTAC.equals(other.m_strTAC))
				return false;

			if (!m_strCID.equals(other.m_strCID))
				return false;

			if (!m_strSID.equals(other.m_strSID))
				return false;

			if (!m_strSID.equals(other.m_strSID))
				return false;

			if (!m_strCellIDGsm.equals(other.m_strCellIDGsm))
				return false;

			if (!m_strLac.equals(other.m_strLac))
				return false;

			if (!m_strAddr.equals(other.m_strAddr))
				return false;

			return true;
		}

		/**
		 * 功能: 拼装json格式 
		 * 参数: 
		 * 返回值: 
		 * 说明:
		 */
		//获取cell信息json
		public JSONArray getCellJson(String jobID)
		{
			JSONArray obj = new JSONArray();
			obj.put(m_strLinkID);
			obj.put(jobID);
			obj.put(m_strMCC);
			obj.put(m_strMNC);
			obj.put(m_strCI);
			obj.put(m_strPCI);
			obj.put(m_strTAC);
			obj.put(m_strCID);
			obj.put(m_strSID);
			obj.put(m_strNID);
			obj.put(m_strCellIDGsm);
			obj.put(m_strLac);
			obj.put(m_strProv);
			obj.put(m_strCity);
			obj.put(m_strDistrict);
			obj.put(m_strStreet);
			obj.put(m_strStreetNum);
			return obj;
		}
	}

	// 信号信息
	public class SignalInfo
	{
		private long m_lCurTime; // 当前时间

		private double m_dLon;
		private double m_dLat;

		// LTE
		private boolean m_bValidLte;	//LTE信号值是否有效
		private double m_dRSRP;
		private double m_dRSRQ;
		private double m_dRSSI;
		private double m_dRSSNR;
		private int m_nCQI;

		// evdo
		private Boolean m_bValidEvdo; //DO信号是否有效
		private double m_dRx3G;
		private double m_dEcio3G;
		private double m_dSnr3G;
		
		//cdma
		private Boolean m_bValidCdma; //CDMA信号是否有效
		private double m_dRx2G;
		private double m_dEcio2G;

		// GSM
		private boolean m_bValidgsm;	//Gsm信号是否有效
		private double m_dRxGsm;

		//构造获取元素值
		public SignalInfo(long lTime, TelStrengthInfo signalInfo)
		{
			m_lCurTime = lTime;
			m_dLon = signalInfo.m_dLonDev; // 经纬度
			m_dLat = signalInfo.m_dLatDev;

			m_bValidLte = signalInfo.IsValidInfo(0);	//有效标志
			m_dRSRP = signalInfo.m_dRsrp;
			m_dRSRQ = signalInfo.m_dRsrq;
			m_dRSSI = signalInfo.m_dRssiLte;
			m_dRSSNR = signalInfo.m_dSinrLte;
			m_nCQI = signalInfo.m_nCqiLte;

			m_bValidEvdo = signalInfo.IsValidInfo(1);
			m_dRx3G = signalInfo.m_dRx3G;
			m_dEcio3G = signalInfo.m_dEcio3G;
			m_dSnr3G = signalInfo.m_dSnr3G;

			m_bValidCdma = signalInfo.IsValidInfo(2);
			m_dRx2G = signalInfo.m_dRx2G;
			m_dEcio2G = signalInfo.m_dEcio2G;

			m_bValidgsm = signalInfo.IsValidSignal(3);
			m_dRxGsm = signalInfo.m_dRxGsm;
		}

		@Override
		public boolean equals(Object o)
		{
			return super.equals(o);
		}

		//获取信号json
		public JSONArray getSignalJson(String linkID)
		{
			JSONArray jsonArr = new JSONArray();
			
			//3G信号有效性判断
			String strRx3G = TelStrengthInfo.getRxString(m_dRx3G);
			String strEcio3G = TelStrengthInfo.getEcioString(m_dEcio3G);
			if(strRx3G.length() == 0 || strEcio3G.length() == 0) 
			{
				strRx3G = "";
				strEcio3G = "";
			}

			jsonArr.put(m_lCurTime + "");
			jsonArr.put(linkID);
			
			//LTE
			if(m_bValidLte)
			{
				jsonArr.put(TelStrengthInfo.getRsrpString(m_dRSRP));
				jsonArr.put(TelStrengthInfo.getRsrqString(m_dRSRQ));
				jsonArr.put(TelStrengthInfo.getRssiString(m_dRSSI));
				jsonArr.put(TelStrengthInfo.getSinrString(m_dRSSNR));
				jsonArr.put(TelStrengthInfo.getIDString(m_nCQI));
			}
			else	//无效
			{
				jsonArr.put("");
				jsonArr.put("");
				jsonArr.put("");
				jsonArr.put("");
				jsonArr.put("");
			}
			
			if(m_bValidEvdo) //do
			{
				jsonArr.put(strRx3G);
				jsonArr.put(strEcio3G);
				jsonArr.put(TelStrengthInfo.getSnrString(m_dSnr3G,m_dRx3G));
			}
			else
			{
				jsonArr.put("");
				jsonArr.put("");
				jsonArr.put("");
			}
			
			if(m_bValidCdma) //1x
			{
				jsonArr.put(TelStrengthInfo.getRxString(m_dRx2G));
				jsonArr.put(TelStrengthInfo.getEcioString(m_dEcio2G));
			}
			else
			{
				jsonArr.put("");
				jsonArr.put("");
			}
			
			if(m_bValidgsm) //gsm
				jsonArr.put(TelStrengthInfo.getRxString(m_dRxGsm));
			else
				jsonArr.put("");

			jsonArr.put(m_dLon + "");
			jsonArr.put(m_dLat + "");

			return jsonArr;
		}
	}
}