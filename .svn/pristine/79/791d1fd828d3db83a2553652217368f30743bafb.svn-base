package com.wellcell.inet.Web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.util.Log;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.DataProvider.PhoneDataProvider;
import com.wellcell.inet.Database.LocalCache;
import com.wellcell.inet.Database.SqliteHelper;

@SuppressWarnings("deprecation")
public class WebUtil
{
	private String TAG = "WebUtil";
	
	private final static String CharSet = "UTF-8";	//固定编码方式
	public static String LmtServer = "http://113.108.151.23:9998/Handler1.ashx";	//网管服务器
	public static String OldServer = "http://113.108.151.26:8680/json/Andriod/"; 	//原有服务器地址
	private static String DataSrv = "http://113.108.151.22:8999/RUEI_Handler.ashx";	//测试数据接口
	//private static String NewServer = "http://113.108.151.26:2045/inetServer/";	//虚拟机地址
	
	private static String NewServer = "http://14.146.229.118:2045/inetInterface-test/";	//新服务器地址
	private static String KQIServer = "http://120.88.8.66:8080/inetAppPro/";	//新服务器地址
	//private static String NewServer = "http://14.146.229.118:2045/inetInterface/"; //新服务器地址
	//private static String NewServer = "http://192.168.1.115:8080/inetInterface/"; //新服务器地址
	//private static String NewServer = "http://202.103.124.97:10000/inetServer/"; //湖南服务器地址
	//private static String NewServer = "http://10.17.7.31:8080/inetInterface/"; //新服务器地址
	public static String SrvUrl = NewServer; 	//新服务器地址

	//static List<TaskInfo> m_listTask;

	//-----------------------------------------------------------------------------
	
	/**
	 * 功能: 获取本机的外网IP
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getWebIP()
	{
		String strUrl = KQIServer + "getIp.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 功能: 登陆验证
	 * 参数: strServerUrl: 服务器地址
	 * 		strUser: 用户名
	 * 		strPwd: 密码
	 * 		nFlag: OA鉴权是否通过;0:未通过,1:通过
	 * 返回值:
	 * 说明:采用新的登录验证服务器
	 */
	public static String loginNew(String strUser, String strPwd,int nFlag)
	{
		String srvUrl = SrvUrl + "login.action";
		String strRet = "";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("name", strUser));
			listPars.add(new BasicNameValuePair("pwd", strPwd));
			//if(nFlag >= 0) 
			//	listPars.add(new BasicNameValuePair("flag", nFlag + ""));

			strRet = doPost(srvUrl, listPars, 15000, 15000); //提交请求
		}
		catch (Exception e)
		{
		}
		return strRet;
	}

	//采用旧的登录验证服务器
	public static String loginOld(String user, String password)
	{
		String srvUrl = OldServer + "UserCheck.aspx";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			//listPars.add(new BasicNameValuePair("P1", Tools.StrToHex(user)));
			//listPars.add(new BasicNameValuePair("P2", Tools.StrToHex(password)));
			listPars.add(new BasicNameValuePair("P3", "INET"));

			String strRet = doPost(srvUrl, listPars, 15000, 15000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 获取更新信息
	 * 参数:	curVer: 当前版本号
	 * 返回值:	
	 * 说明:
	 */
	public static String getUpdateInfo(int curVer)
	{
		String strUrl = SrvUrl + "updateApk.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			//listPars.add(new BasicNameValuePair("Function", "GetUpdateInfo"));
			listPars.add(new BasicNameValuePair("verCode", curVer + ""));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return "";
		}
	}

	//-------------------------------------------------------------------------------------------
	/**
	 * 功能: 获取百度热点ID
	 * 参数: strCellKey: SID_NID_CID
	 * 		strType: 热点类型; B:百度,N:自定义
	 * 返回值: 返回热点ID
	 * 说明:经纬度保留三位来确定热点
	 */
	public static String getHotPointID(String strHot, double dLon, double dLat, 
			String strCellKey,String strType)
	{
		String strRet = null;
		int nCount = 3; //重复次数
		while (nCount-- > 0)
		{
			strRet = getHotPointIDEx(strHot, dLon, dLat, strCellKey,strType);
			if (strRet.length() > 0)
				break;
		}

		return strRet;
	}

	private static String getHotPointIDEx(String strHot, double dLon, double dLat,
			String strCellKey,String strType)
	{
		String srvUrl = SrvUrl + "getHotPointID.action";
		
		String[] strKeys = strCellKey.split("_");
		String strCID = "";
		String strSID = "";
		String strNID = "";
		if (strKeys.length > 0)
			strSID = strKeys[0];

		if (strKeys.length > 1)
			strNID = strKeys[1];

		if (strKeys.length > 2)
			strCID = strKeys[2];

		try
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("hotName", strHot));
			params.add(new BasicNameValuePair("lon", dLon + ""));
			params.add(new BasicNameValuePair("lat", dLat + ""));
			params.add(new BasicNameValuePair("cid", strCID));
			params.add(new BasicNameValuePair("sid", strSID));
			params.add(new BasicNameValuePair("nid", strNID));
			params.add(new BasicNameValuePair("type", strType));

			String strRet = doPost(srvUrl, params, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	//----------------------------------------------------------------------------------------------
	/**
	 * 功能:请求信号格式化参数
	 * 参数:
	 * 返回值:
	 * 说明:通过终端信号和rom版本号来确定信号参数配置信息
	 */
	public static String getSignalStrengthPar()
	{
		String srvUrl = SrvUrl + "getSignalPar.action";
		try
		{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("model", PhoneDataProvider.getModel()));
			params.add(new BasicNameValuePair("rom", PhoneDataProvider.getRomVersion()));

			String strRet = doPost(srvUrl, params, 10000, 10000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 功能: 上传日志数据
	 * 参数: strJson: 结果数据
	 * 		nType: 日志类型; 0: 信号日志,1:操作日志
	 * 	  	nKey: 密钥索引,-1表示不加密
	 * 返回值:
	 * 说明:
	 */
	public static String uploadLogs(String strJson,int nType,int nKey)
	{
		String strUrl= SrvUrl + "log.action";

		List<NameValuePair> listPars = new ArrayList<NameValuePair>();
		listPars.add(new BasicNameValuePair("jsonResult", strJson));				//结果数据
		listPars.add(new BasicNameValuePair("jsonLength", strJson.length()+""));	//数据长度(校验用)
		listPars.add(new BasicNameValuePair("flag", nType + ""));		//日志类型
		if(nKey != -1)
			listPars.add(new BasicNameValuePair("EncryIndex", nKey + ""));//密钥索引
		
		String strRet = WebUtil.doPost(strUrl, listPars, 10000, 10000);
		return strRet;
	}

	//-----------------------------------任务列表及配置参数-------------------------------------------------
	/**
	 * 功能:获取各个模块的测试任务列表
	 * 参数:	Uid: 用户ID
	 * 		moduleId: 模块ID
	 * 		subType: 子模块ID(针对问题热点反馈)
	 * 返回值:
	 * 说明:
	 */
	public static String getTaskList(Context context,int nType,String subType)
	{
		String strUrl = SrvUrl + "getTaskList.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1","100036"));	//用户ID
			listPars.add(new BasicNameValuePair("P2", nType + ""));
			listPars.add(new BasicNameValuePair("P3", subType));

			String strRet = doPost(strUrl, listPars, 10000, 10000);
			if(strRet.length() < 3) //排除"[]"
				strRet = "";
			
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 功能:	获取业务配置参数
	 * 参数:taskID: 业务ID
	 * 返回值:
	 * 说明:
	 */
	public static String getTaskPara(String taskID)
	{
		String strUrl = SrvUrl + "TaskPar.action";

		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("ttid", taskID));


			String strRet = doPost(strUrl, listPars, 10000, 10000);
			if (strRet != null)
				return strRet;
			else
				return "";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 获取业务测试的评分标准
	 * 参数:strName: 模板名称
	 * 返回值:
	 * 说明:
	 */
	public static String getTaskRankPar(String strName)
	{			
		String strUrl = SrvUrl + "getTaskRankPar.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("name", strName));
			
			String strRet = doPost(strUrl, listPars, 10000, 10000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	//------------------------------------------------------------------------------------
	/**
	 * 功能: 上传测试数据到服务器
	 * 参数:	jsonRet: 结果数据
	 * 		moduleType: 模块类型
	 * 		nKey: 密钥索引,-1表示不加密
	 * 返回值:
	 * 说明: 有flag为信号测量数据,没有为业务测试数据
	 */
	public static String uploadTestData(String jsonRet,int nKey) 
	{
		String strUrl= KQIServer + "upload.action";
		
		if(nKey != -1)
			jsonRet = DataEncrypt. getGzipEncrypt_DES(jsonRet, DataEncrypt.KEYS[nKey]);	//加密压缩

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("jsonResult", jsonRet));				//结果数据
		params.add(new BasicNameValuePair("jsonLength", jsonRet.length()+""));	//数据长度(校验用)
		
		if(nKey != -1)
			params.add(new BasicNameValuePair("EncryIndex", nKey + ""));		//密钥索引
				
		String strRet = WebUtil.doPost(strUrl, params, 10000, 10000);
		return strRet;
	}
	//------------------------------------------------------------------------------------

	/**
	 * 功能: 华为天馈测量--驻波比
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startVswrOnServerLTE_HW(String bam, String user, String ne, String cell)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartVswr_LTE_HW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("ne", ne));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 华为天馈测量--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startRssiOnServerLTE_HW(String bam, String user, String ne, String cell)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartRssi_LTE_HW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("ne", ne));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 华为天馈测量--在线用户数
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startUsernumOnServerLTE_HW(String bam, String user, String ne, String cell)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartUsernum_LTE_HW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("ne", ne));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 华为停止天馈测试
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String stopOnServerLTE_HW(String bam, String ne, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "Stop_LTE_HW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("ne", ne));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 中兴天馈测量--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startRssiOnServerLTE_ZTE(String bam, String user, String ne, String cell)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartRssi_LTE_ZTE"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("ne", ne));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 中兴天馈测量--驻波比
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startVswrOnServerLTE_ZTE(String bam, String user, String ne, String cell)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartVswr_LTE_ZTE"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("ne", ne));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:停止中兴LTE天馈测试
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String stopOnServerLTE_ZTE(String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "Stop_LTE_ZTE"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 获取自动测试配置
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getAutoTestConfig(Context context, String city, String bsc, String bts, String cell)
	{
		String strUrl = DataSrv;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("type", "GetAutoTestConfig"));
			//listPars.add(new BasicNameValuePair("user", UserInfoHelper.getUserName(context)));
			listPars.add(new BasicNameValuePair("city", city));
			listPars.add(new BasicNameValuePair("bsc", bsc));
			listPars.add(new BasicNameValuePair("bts", bts));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 10000, 10000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	//--------------------------------------------------------------------------------------------------
	/**
	 * 功能:从服务器获取如翼热点信息
	 * 参数:	strKeyword:SID_NID_CID
	 * 		bFloor:是否返回楼层信息
	 * 		keyword: 搜索关键字,按热点名称帅选
	 * 返回值:
	 * 说明:先获取当前连接的小区信息
	 */
	public static String getHotInfo(String strCellKey,  int bFloor,String keyword)
	{
		String strRet = "";
		String[] strKeys = strCellKey.split("_"); //SID_NID_CID
		if (strKeys.length == 3)
		{
			strRet = getRueiHotInfo(strKeys[2], strKeys[0], strKeys[1], bFloor, keyword);
		}

		return strRet;
	}

	/**
	 * 功能:从服务器获取热点信息
	 * 参数:	cid: 
	 * 		sid:
	 * 		nid:
	 * 		bFloor:是否返回楼层信息
	 * 		keyword: 搜索关键字,按热点名称帅选
	 * 返回值:空字符为无效结果
	 * 说明:通过制定小区信息从服务器获取热点信息
	 */
	public static String getRueiHotInfo(String cid, String sid, String nid, int bFloor,String keyword)
	{
		String strUrl = SrvUrl + "HotInfo.action";
		String strRet = "";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(cid)));
			listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(sid)));
			listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(nid)));
			listPars.add(new BasicNameValuePair("P4", bFloor + ""));
			listPars.add(new BasicNameValuePair("P5", URLEncoder.encode(keyword)));

			 strRet = doPost(strUrl, listPars, 15000, 15000);
			
			if(strRet.length() == 0 || strRet.equals("null"))
				strRet = "";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			strRet = "";
		}
		return strRet;
	}

	//------------------------------------------------------------------------------------------------------
	/**
	 * 功能: 从服务器获取百度搜索关键字
	 * 参数:	strUrl: 
	 * 返回值:
	 * 说明:
	 */
	public static String getKeyword()
	{
		String strUrl = SrvUrl + "getKeywords.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("type", "GetKeywords"));

			String strRet = doPost(strUrl, listPars, 10000, 10000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 判断是否数据三重测试区域
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String isImportArea(String city, String bscno, String btsno)
	{
		String strUrl = DataSrv;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("type", "isImportArea"));
			listPars.add(new BasicNameValuePair("city", city));
			listPars.add(new BasicNameValuePair("bsc", bscno));
			listPars.add(new BasicNameValuePair("bts", btsno));

			String strRet = doPost(strUrl, listPars, 10000, 10000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 获取微博登录帐号信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getWeiboUser(Context context)
	{
		String strUrl = SrvUrl + "getWeiboUser.action" ;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			// listPars.add(new BasicNameValuePair("type", "GetWeiboUser"));
			//listPars.add(new BasicNameValuePair("City", UserInfoHelper.GetUserCityForWeibo(context)));

			String strRet = doPost(strUrl, listPars, 10000, 10000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:业务测试时获取网管信息
	 * 参数:	strUrl:服务器地址
	 * 		user:用户名
	 * 		bam:
	 * 		btsid:bts
	 * 		cell:
	 * 返回值:
	 * 说明:
	 */
	public static String getAnteInfoForTask(String user, String bam, String btsid, 
			String cell)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetInfoForTaskNew"));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("bsc", bam));
			listPars.add(new BasicNameValuePair("bts", btsid));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 60000, 60000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String getBTSBrdHW( String bam, String btsid)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetBTSBrdHW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("Btsid", btsid));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 获取告警基站
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getAlarmBts()
	{
		String strUrl = OldServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			// listPars.add(new BasicNameValuePair("Function", "GetUpdateInfo"));

			strUrl += "SiteAlarmCount.aspx";
			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 上传日志
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String writeUserLog(String user, String mdn, String imsi, String imei, 
			String ver, String sid, String nid, String cid, String msg)
	{
		String strUrl = OldServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			try
			{
				listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(user == null ? "" : user)));
				listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(mdn == null ? "" : mdn)));
				listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(imsi == null ? "" : imsi)));
				listPars.add(new BasicNameValuePair("P4", URLEncoder.encode(imei == null ? "" : imei)));
				listPars.add(new BasicNameValuePair("P5", URLEncoder.encode(ver == null ? "" : ver)));
				listPars.add(new BasicNameValuePair("P6", URLEncoder.encode(sid == null ? "" : sid)));
				listPars.add(new BasicNameValuePair("P7", URLEncoder.encode(nid == null ? "" : nid)));
				listPars.add(new BasicNameValuePair("P8", URLEncoder.encode(cid == null ? "" : cid)));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			listPars.add(new BasicNameValuePair("P9", URLEncoder.encode(msg == null ? "" : msg)));

			strUrl += "exportinfo.aspx";
			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 地市BTS配置数据
	 * 参数:
	 * 返回值:
	 * 说明:设置--基站基础数据下载
	 */
	public static String getAllBtsBasicInfo(String city)
	{
		//String strUrl = SrvUrl + "CidMapBts.aspx";
		String strUrl = SrvUrl + "CidMapBts.action";

		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", city));
			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String getBscName(String strUrl, String city, String bsc)
	{
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetBscName"));
			listPars.add(new BasicNameValuePair("City", city));
			listPars.add(new BasicNameValuePair("Bsc", bsc));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	//-------------------------------------------------------------------------------------------------------------
	/**
	 * 功能:通过服务器获取基站详细信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
/*	public static BtsBasicInfo updateBtsInfo(int nCi, int nMcc, int nMnc, Context context)
	{
		return updateBtsInfo(nCi + "", nMcc + "",nMnc + "",context);
	}
	
	public static BtsBasicInfo updateBtsInfo(String ci, String mcc, String mnc, Context context)
	{
		BtsBasicInfo btsInfo = new BtsBasicInfo();

		//先从本地缓存中获取
		SqliteHelper sqliteHelper = new SqliteHelper(context);
		try
		{
			btsInfo = sqliteHelper.getBTSInfo(mcc, mnc, ci);
		}
		catch (Exception e)
		{
			//Log.i(TAG, "UpdateLocation,缓存获取数据失败");
		}

		int nCount = 0;
		String strJson = "";
		while (btsInfo == null || btsInfo.m_strCID.equals(""))
		{
			if (nCount++ > 6)
				break;

			try
			{
				// 从网络获取信息
				if(nCount <= 3)
					strJson = WebUtil.getBTSConvertInfo(ci, mcc, mnc, context);
				else //如果新的接口没有数据,则从旧的接口提取
					strJson = WebUtil.getBTSConvertInfoEx(ci, mcc, mnc, context);

				// 解析
				if (!strJson.equals("null") && strJson.length() > 0)
				{
					JSONArray arr = new JSONArray(strJson);
					if (arr.length() < 2)
					{
						CGlobal.Sleep(500);
						continue;
					}

					btsInfo = new BtsBasicInfo(arr.getJSONArray(1));
					sqliteHelper.saveBtsInfoToLocal(btsInfo); //保存缓存
					break;
				}
			}
			catch (Exception e)
			{
				sqliteHelper.closeDb();
			}
		}
		//Log.i(TAG, "getBtsBasicInfo,获取数据完成");

		sqliteHelper.closeDb();
		return btsInfo;
	}
*/
	/**
	 * 功能:获取BTS信息
	 * 参数:
	 * 返回值:
	 * 说明:通过基站关键ID从服务器获取BTS信息
	 */
/*	public static String getBTSConvertInfo(String baseid, String sid, String nid, Context context)
	{
		String srvUrl = SrvUrl + "BtsInfo.action";
		return getBTSConvertInfo(srvUrl,baseid, sid, nid, UserInfoHelper.getUserName(context), 
				PhoneDataProvider.getPhoneNum(context), PhoneDataProvider.getIMSI(context), 
				PhoneDataProvider.getIMEI(context), PhoneDataProvider.getModel() + " " + PhoneDataProvider.getSystemName());
	}
	
	//从旧的服务器获取基站信息
	public static String getBTSConvertInfoEx(String baseid, String sid, String nid, Context context)
	{
		String srvUrl = OldServer + "BtsInfo.aspx";
		return getBTSConvertInfo(srvUrl,baseid, sid, nid, UserInfoHelper.getUserName(context), 
				PhoneDataProvider.getPhoneNum(context), PhoneDataProvider.getIMSI(context), 
				PhoneDataProvider.getIMEI(context), PhoneDataProvider.getModel() + " " + PhoneDataProvider.getSystemName());
	}

	public static String getBTSConvertInfo(String srvUrl,String baseid, String sid, String nid, 
			String user, String mdn, String imsi, String imei, String systemVer)
	{
		//String srvUrl = OldServer + "BtsInfo.aspx";
		//String srvUrl = SrvUrl + "BtsInfo.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(baseid)));
			listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(sid)));
			listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(nid)));
			try
			{
				listPars.add(new BasicNameValuePair("P4", URLEncoder.encode(user == null ? "" : user)));
				listPars.add(new BasicNameValuePair("P5", URLEncoder.encode(mdn == null ? "" : mdn)));
				listPars.add(new BasicNameValuePair("P6", URLEncoder.encode(imsi == null ? "" : imsi)));
				listPars.add(new BasicNameValuePair("P7", URLEncoder.encode(imei == null ? "" : imei)));
				listPars.add(new BasicNameValuePair("P8", URLEncoder.encode(systemVer == null ? "" : systemVer)));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			String strRet = doPost(srvUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
*/
	//-----------------------------------------------运行数据/综合查询----------------------------------------------------
	/**
	 * 功能: 基站基础信息
	 * 参数:	city: 城市名
	 * 		bsc: bsc编号
	 * 		bts: 扇区编号
	 * 返回值:
	 * 说明:运行数据/综合查询-->基站信息
	 */
	public static String getBTSInformation(String ParA, String ParB, String ParC,String flag)
	{
		//String strUrl = SrvUrl + "BtsConfig.aspx";
		String strUrl = SrvUrl + "BtsConfig.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(ParA,CharSet)));
			listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(ParB,CharSet)));
			listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(ParC,CharSet)));
			listPars.add(new BasicNameValuePair("P4", flag));
			listPars.add(new BasicNameValuePair("P5", "2"));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 功能: 天馈基础信息
	 * 参数:
	 * 返回值:
	 * 说明:运行数据/综合查询-->天馈信息
	 */
	public static String getAnteInfo(String ParA, String ParB, String ParC,String flag)
	{
		//String strUrl = SrvUrl + "Antenna.aspx";
		String strUrl = SrvUrl + "Antenna.action";

		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(ParA,CharSet)));
			listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(ParB,CharSet)));
			listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(ParC,CharSet)));
			listPars.add(new BasicNameValuePair("P4", flag));
			listPars.add(new BasicNameValuePair("P5", "2"));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			// String strRet =
			// "[[\"关联网元类型\",\"关联网元ID\",\"关联扇区ID\",\"小区类型\",\"天馈编号\",\"天馈铁塔ID\",\"是否共用信息\",\"地市\",\"所属行政区域\",\"名称\",\"经度\",\"纬度\",\"安装详细地址\",\"工程编码\",\"投产时间\",\"是否带有塔放\",\"是否美化、隐蔽天线\",\"受益网络\",\"运营商\",\"天线类别\",\"天线类型\",\"天线型号\",\"天线方向角\",\"天线挂高\",\"天线厂家\",\"天线前后比\",\"馈线类型\",\"馈线长度\",\"馈线频带\",\"馈线厂家\",\"馈线型号\",\"馈线数量\",\"塔桅类型\",\"天线增益\",\"天线极化方式\",\"天线倾角调整\",\"天线机械倾角\",\"天线电调倾角\",\"水平半功率角\",\"垂直半功率角\",\"天馈ID\",\"设备编码\"],[\"1,1\",\"20001150756,66801020743\",\"11\",\"13\",\"2002940001\",\"11\",\"0\",\"广州\",\"番禺\",\"新增测测\",\"23.2111\",\"123.212\",\"广州番禺区\",\"12a\",\"2011/05/25 23:59:59.997\",\"1\",\"1\",\"1,2\",\"1,3\",\"1\",\"1\",\"sd\",\"35.8\",\"23.4\",\"无\",\"\",\"\",\"10\",\"ser\",\"无\",\"sd\",\"2\",\"1\",\"\",\"1\",\"1\",\"50.1\",\"45.1\",\"23.1\",\"23.8\",\"174939690\",\"\"],[\"1,1\",\"20001150756,66801020743\",\"11\",\"13\",\"2002940001\",\"11\",\"0\",\"广州\",\"番禺\",\"新增测测2\",\"23.2111\",\"123.212\",\"广州番禺区\",\"12a\",\"2011/05/25 23:59:59.997\",\"1\",\"1\",\"1,2\",\"1,3\",\"1\",\"1\",\"sd\",\"35.8\",\"23.4\",\"无\",\"\",\"\",\"10\",\"ser\",\"无\",\"sd\",\"2\",\"1\",\"\",\"1\",\"1\",\"50.1\",\"45.1\",\"23.1\",\"23.8\",\"174939690\",\"\"]]";
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:获取基站告警信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getAlarmInformation( String baseid, String sid, String nid)
	{
		String strUrl = OldServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("baseid", baseid));
			listPars.add(new BasicNameValuePair("sid", sid));
			listPars.add(new BasicNameValuePair("nid", nid));

			// String strRet = doPost(strUrl, listPars, 5000, 5000);
			String strRet = "[[\"告警标识\",\"城市编号\",\"地市名称\",\"BSC编号\",\"BTS编号\",\"OMC编号\",\"网元号\",\"网元类型\",\"网元维护人\",\"网元维护人工号\",\"预处理信息\",\"预处理人工号\",\"故障单单号\",\"告警码\",\"告警原因\",\"告警时间\",\"厂家网管产生告警的时间\",\"采集时间\",\"告警附加信息\",\"断站标志\",\"基站名称\",\"网元所属的厂家\",\"基站级别\",\"基站地址\",\"站点编号\",\"站型\",\"是否影响VIP\",\"VIP级别\",\"VIP名称\",\"VIP数量\",\"VIP客户经理\",\"VIP经理电话\",\"经度\",\"纬度\",\"是否有传输告警\",\"厂家网元告警清除时间\",\"厂家网管告警清除时间\",\"采集机接收告警清除时间\",\"告警类型\",\"告警类别\",\"网元标识\",\"网元名称\",\"厂家网元名称\",\"系统时间\",\"告警级别\",\"告警上报次数\",\"设备类型\",\"故障单状态\",\"告警故障清除标志\",\"最近更改时间\",\"告警清除人\",\"清除类型\",\"系统接收告警清除时间\",\"清除原因\",\"标准告警可能原因\",\"告警详细信息\",\"网元状态\"]"
					+ ",[\"小区退服\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",]" + ",[\"动环告警\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",\"\",]]";
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	
	/**
	 * 功能: 获取基站告警信息列表
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getAlarmSimpleList(String city, String bsc, String bts)
	{
		String strUrl = OldServer + "CurrentAlarm.aspx";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(city)));
			listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(bsc)));
			listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(bts)));
			listPars.add(new BasicNameValuePair("P4", 2 + ""));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			// String strRet = "[[\"小区退服\",\"动环告警\"],[\"\",\"\"]]";
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 功能: 获取指定告警信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getAlarmDetail(String alarmid)
	{
		String strUrl = OldServer + "CurrentAlarm.aspx";

		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(alarmid)));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 功能: 获取性能数据图表配置参数
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getChartConfig()
	{
		//String strUrl = OldServer + "chartconfig.aspx";
		String strUrl = SrvUrl + "chartConfig.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 获取性能数据
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getBTSPerfData(String city, String bsc, String btsid, Date startTime)
	{
		//String strUrl = OldServer + "BTSCapability.aspx";
		String strUrl = SrvUrl + "BTSCapability.action";
		try
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(startTime);
			calendar.add(Calendar.DATE, -14);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String sTime = df.format(startTime);
			String eTime = df.format(calendar.getTime());

			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(city,CharSet)));
			listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(bsc,CharSet)));
			listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(btsid,CharSet)));
			listPars.add(new BasicNameValuePair("sTime", URLEncoder.encode(eTime,CharSet)));
			listPars.add(new BasicNameValuePair("eTime", URLEncoder.encode(sTime,CharSet)));
			listPars.add(new BasicNameValuePair("P4", "2"));

			
			String strRet = doPost(strUrl, listPars, 30000, 30000);
			// String strRet =
			// "[[\"时间\",\"地市\",\"BSC\",\"基站\",\"2G掉话率\",\"2G接入成功率\",\"话务量\",\"3G掉线率\",\"3G接入成功率\",\"3G流量\",\"RSSI主集\",\"RSSI分集\"]"
			// +
			// ",[\"2012/7/10 12:00\",\"广州\",\"GZBSC1\",\"利新大厦\",\"0.3\",\"99.5\",\"29841\",\"0.78\",\"99.11\",\"98321\",\"\",\"\"]"
			// +
			// ",[\"2012/7/10 13:00\",\"广州\",\"GZBSC1\",\"利新大厦\",\"0.35\",\"99.1\",\"31841\",\"0.58\",\"99.81\",\"108321\",\"\",\"\"]]";
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 功能: 基站模糊查询(综合查询,天馈测量)
	 * 参数: keyword: 查询关键字
	 * 		city: 地市名称
	 * 		bsc: bac编号
	 * 		type: 保留
	 * 返回值:
	 * 说明:
	 */
	public static String getSearchResult(String keyword, String city, String bsc, String type)
	{
		String strUrl = OldServer + "FuzzyQuery.aspx";
		//String strUrl = SrvUrl + "FuzzyQuery.action";
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("P1", URLEncoder.encode(keyword,CharSet)));
			listPars.add(new BasicNameValuePair("P2", URLEncoder.encode(city,CharSet)));
			listPars.add(new BasicNameValuePair("P3", URLEncoder.encode(bsc,CharSet)));
			listPars.add(new BasicNameValuePair("P6", "1"));
			listPars.add(new BasicNameValuePair("P7", "20"));
			listPars.add(new BasicNameValuePair("P8", type));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			// "[{Name:\"利新大厦\",Address:\"林和中路162-166号\"},{Name:\"广州东站\",Address:\"广州火车东站\"}]";
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------

	/**
	 * 功能: 天馈测量--获取单板列表
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getBamList(Context context)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetBamList"));
			//listPars.add(new BasicNameValuePair("City", UserInfoHelper.GetUserCity(context)));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 朗讯天馈测量--获取单板信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getBamListLucent(Context context)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetBamListLucent"));
			//listPars.add(new BasicNameValuePair("City", UserInfoHelper.GetUserCity(context)));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 中兴天馈测量--获取单板信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getBamListZTE(Context context)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetBamListZTE"));
			//listPars.add(new BasicNameValuePair("City", UserInfoHelper.GetUserCity(context)));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 华为天馈测量--获取单板信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getBamListLTE_HW(Context context)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetBamListLTE_HW"));
			//listPars.add(new BasicNameValuePair("City", UserInfoHelper.GetUserCity(context)));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 中兴天馈测量--获取单板信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getBamListLTE_ZTE( Context context)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetBamListLTE_ZTE"));
			//listPars.add(new BasicNameValuePair("City", UserInfoHelper.GetUserCity(context)));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String sendCommand(String cmd, String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "Send"));
			listPars.add(new BasicNameValuePair("Command", cmd));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 华为LTE接收天馈测试结果
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String receiveMessageLTE_HW(String bam, String user, String ne)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "Receive"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("ne", ne));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 接收天馈测量结果
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String receiveMessage(String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "Receive"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:接收中兴天馈测量信息--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String receiveMessageZTERSSI(String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "ReceiveZTERSSI"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:接收中兴天馈测量信息--驻波比
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String receiveMessageZTEVSWR(String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "ReceiveZTEVSWR"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:接收天馈测量结果信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String receiveMessage(String bam, String user, String port)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "ReceiveForPort"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Port", port));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 关闭天馈测量连接
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String closeConnection(String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "Close"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 5000);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:天馈测量--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startRssiTraceOnServer(String bam, String user, String btsid, String brdid, String time)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartRssiTrace"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Brdid", brdid));
			listPars.add(new BasicNameValuePair("Time", time));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 天馈测量--ROT
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startROTOnServer(String bam, String user, String btsid, String brdid, String time)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "GetRotHW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Brdid", brdid));
			listPars.add(new BasicNameValuePair("Time", time));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String startRssiTraceOnServerHW(String strUrl, String bam, String user, String btsid, String sectorid, String time)
	{
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartRssiTraceHW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Sectorid", sectorid));
			listPars.add(new BasicNameValuePair("Time", time));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 朗讯天馈测量--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startRssiTraceOnServerLucent(String bam, String user, String cell, String cdm, String cbr)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartRssiTraceLucent"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("cell", cell));
			listPars.add(new BasicNameValuePair("cdm", cdm));
			listPars.add(new BasicNameValuePair("cbr", cbr));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 朗讯天馈测量--在线用户数
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startActuserTraceOnServerLucent(String bam, String user, String cell, String cdm, String cbr)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartActuserTraceLucent"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("cell", cell));
			listPars.add(new BasicNameValuePair("cdm", cdm));
			listPars.add(new BasicNameValuePair("cbr", cbr));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 停止天馈测量--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String stopRssiTraceOnServer( String bam, String user, String btsid, String brdid, String port)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StopRssiTrace"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Brdid", brdid));
			listPars.add(new BasicNameValuePair("Port", port));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String stopRssiTraceOnServerHW(String strUrl, String bam, String user, String btsid, String sectorid, String port)
	{
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StopRssiTraceHW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Sectorid", sectorid));
			listPars.add(new BasicNameValuePair("Port", port));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 停止朗讯天馈测试--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String stopRssiTraceOnServerLucent(String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StopRssiTraceLucent"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 中兴天馈测量--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startRssiTraceOnServerZTE(String bam, String user, String system, String cell, String carr)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartRssiTraceZTE"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("system", system));
			listPars.add(new BasicNameValuePair("cell", cell));
			listPars.add(new BasicNameValuePair("carr", carr));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 中兴天馈测量--驻波比
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startVswrTraceOnServerZTE(String bam, String user, String system, String cell)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartVswrTraceZTECELL"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("system", system));
			listPars.add(new BasicNameValuePair("cell", cell));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:停止中兴天馈测量--RSSI
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String stopRssiTraceOnServerZTE(String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StopRssiTraceZTE"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 停止中兴天馈测量--驻波比
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String stopVswrTraceOnServerZTE( String bam, String user)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StopVswrTraceZTE"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能:天馈测量--驻波比
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String startVswrTraceOnServer(String bam, String user, String btsid, String brdid, String time)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartVswrTrace"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Brdid", brdid));
			listPars.add(new BasicNameValuePair("Time", time));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String startVswrTraceOnServerHW(String strUrl, String bam, String user, String btsid, String sectorid, String time)
	{
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StartVswrTraceHW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Sectorid", sectorid));
			listPars.add(new BasicNameValuePair("Time", time));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 停止天馈测量--驻波比
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String stopVswrTraceOnServer(String bam, String user, String btsid, String brdid, String port)
	{
		String strUrl = LmtServer;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StopVswrTrace"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Brdid", brdid));
			listPars.add(new BasicNameValuePair("Port", port));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	public static String stopVswrTraceOnServerHW(String strUrl, String bam, String user, String btsid, String sectorid, String port)
	{
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("Function", "StopVswrTraceHW"));
			listPars.add(new BasicNameValuePair("Bam", bam));
			listPars.add(new BasicNameValuePair("User", user));
			listPars.add(new BasicNameValuePair("Btsid", btsid));
			listPars.add(new BasicNameValuePair("Sectorid", sectorid));
			listPars.add(new BasicNameValuePair("Port", port));

			String strRet = doPost(strUrl, listPars, 5000, 1000 * 60 * 5);
			return strRet;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 功能: 上传测试数据到服务器
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
/*	public static boolean uploadTask(String json, AbsTask.TaskType taskType)
	{
		String strUrl = DataSrv;
		try
		{
			List<NameValuePair> listPars = new ArrayList<NameValuePair>();
			listPars.add(new BasicNameValuePair("type", "upload"));
			listPars.add(new BasicNameValuePair("json", json));
			listPars.add(new BasicNameValuePair("length", json.length() + ""));
			listPars.add(new BasicNameValuePair("taskType", taskType.name()));
			// Log.d("", taskType.name());

			String strRet = doPost(strUrl, listPars, 10000, 10000);
			if (strRet != null)
			{
				Log.i("", strRet);
				if (strRet.equals("OK"))
					return true;
				else
					return false;
			}
			else
				return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	*/

	//===============================================================================================
	/**
	 * 功能:提交请求
	 * 参数:	_url: 服务器URL
	 * 		listPars: 参数列表
	 * 		conTimeOut: 连接超时时间
	 * 		soTimeOut: 等待返回数据超时时间
	 * 返回值:
	 * 说明:
	 */
	public static String doPost(String _url, List<NameValuePair> listPars, int conTimeOut, int soTimeOut)
	{
		return doPost(_url, listPars, conTimeOut, soTimeOut, "utf8");
	}

	private static String doPost(String _url, List<NameValuePair> listPars, int conTimeOut, int soTimeOut, String encoding)
	{
		try
		{
			HttpPost httpRequest = new HttpPost(_url);
			httpRequest.setEntity(new UrlEncodedFormEntity(listPars, HTTP.UTF_8));	//指定编码方式
			
			HttpClient client = getHttpClient(conTimeOut, soTimeOut);
			HttpResponse httpResponse = client.execute(httpRequest);

			// 处理Response
			if (httpResponse != null)
			{
				InputStream in = httpResponse.getEntity().getContent();
				InputStreamReader inReader = new InputStreamReader(in, encoding);
				BufferedReader br = new BufferedReader(inReader, 8 * 1024);

				// Get Respond data from Server
				String strData = "";
				String strTemp = "";
				while ((strTemp = br.readLine()) != null)
				{
					strData += strTemp.trim();
				}
				return strData;
			}
			else
				return "";
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}

	}

	public static HttpClient getHttpClient(int conTimeOut, int soTimeOut)
	{
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, conTimeOut);	//设置连接超时
		HttpConnectionParams.setSoTimeout(httpParams, soTimeOut);			//设置等待超时
		HttpConnectionParams.setSocketBufferSize(httpParams, 2048);

		HttpClientParams.setRedirecting(httpParams, false);

		// String userAgent =
		// "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv;1.9.2) Gecko/20100115 Firefox/3.6";
		String userAgent = String.format("Mozilla/5.0 (Linux; U; Android %1s; inet.gdtel.com; %2s) DEVICE:%3s %4s", 
				Build.VERSION.RELEASE, "3.20", PhoneDataProvider.getManufacturer(), PhoneDataProvider.getModel());
		HttpProtocolParams.setUserAgent(httpParams, userAgent);

		HttpClient httpClient = new DefaultHttpClient(httpParams);
		return httpClient;
	}
	
	//==========================================无用====================================================
}
