package com.wellcell.inet.Log;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.wellcell.inet.DataProvider.PhoneDataProvider;
import com.wellcell.inet.Database.DatabaseHelper;
import com.wellcell.inet.Database.SqliteHelper;
import com.wellcell.inet.Web.WebUtil;

//用户操作日志
public class InetLog
{
	private static String TAG = "InetLog";
	private static final String SrcMark = ",";
	
	//日志层次
	public static enum LogLevel
	{
		eMainLog,		//登录和主界面
		eModuleLog,		//具体功能模块
		eActivityLog,	//具体窗口
		eButtonLog		//测试开始按钮
	}
	
	//所有窗口名称
	public static enum ActivityName
	{
		eLogin,				//登录界面
		eMainActivity,		//主界面
		eHotPointSelComb,	//热点选择
		eFloorSel,			//楼层选择
		eTaskList,			//任务列表
		eTaskRet,			//CQT测试结果
		eDTActivity,		//DT窗口
		eAutoTest,			//定点引爆
		eSignalTest,		//信号测量专业版
		eCustTest,			//大众版
		eCustSignal,		//大众版--信号测量
		eCustTask,			//大众版--感知测试
		eTraceLmtTab,		//天馈
		eTracHW,			//CDMA华为天馈测量
		eTracZTE,			//CDMA中兴天馈测量
		eTracLucent,		//CDMA朗讯天馈测量
		eTracHW_LTE,		//LTE华为天馈测量
		eTracZTE_LTE,		//LTE中兴天馈测量
		eTracLucent_LTE,	//LTE朗讯天馈测量
		eBtsInfo,			//运行数据/综合查询
		eBtsInfo_Search,	//搜索查询
		eBtsInfo_Base,		//基站信息
		eBtsInfo_Ante,		//天馈信息
		eBtsInfo_Alarm,		//告警信息
		eBtsInfo_KPI,		//性能信息
		eBtsMap,			//地图窗口
		eSetting,			//设置窗口
	}
	
	//所有应用按钮
	public static enum ButtonLog
	{
		eStartCQT,				//开始CQT测试
		eStartDT,				//开始DT测试
		eStopDT,				//停止DT测试
		eStartAuto,				//开始自动测试
		eStopAuto,				//停止自动测试
		eStartSignal,			//开始信号测量
		eStopSignal,			//停止信号测试
		eStartSignalCust,		//开始大众版信号测量
		eStopSignalCust,		//停止大众版信号测试
		eStartTaskCust,			//开始大众版信号测量
		eStopTaskCust,			//停止大众版信号测试
		eStartTracHW,			//CDMA华为天馈测量
		eStartTracZTE,			//CDMA中兴天馈测量
		eStartTracLucent,		//CDMA朗讯天馈测量
		eStartTracHW_LTE,		//LTE华为天馈测量
		eStartTracZTE_LTE,		//LTE中兴天馈测量
		eStartTracLucent_LTE,	//LTE朗讯天馈测量
		eStartOffLineMap,		//离线地图
		eStartBtsData,			//下载基站信息
		eStartClearBts,			//清除基站信息
		eStartSetAuto,			//自动测试设置
		eStartCreateDB,			//重建三重测试数据库
		eStartClearCache,		//清理缓存
		eStartAuthSina,			//新浪授权
		eStartAuthTx,			//腾讯授权
		eStartUpdate,			//检查更新
		eStartShared,			//分享
		eStartAbout,			//关于
		eYesGps,				//打开GPS
		eNoGps,					//不打开GPS
	}
	
	//时间
	private Map<ActivityName, Long> m_mapStartTime;	//各个模块对应的开始时间
	
	private String m_strUserID = "";		//用户ID
	private String m_strPhoneNum = "";		//手机号码
	private String m_strImsi = "";
	private String m_strImei = "";
	private String m_strModel = "";
	private String m_strVerCode = "";		//VersionCode
	
	private SqliteHelper m_sqlHelp;
	
	public InetLog(Context context)
	{
		m_mapStartTime = new HashMap<ActivityName, Long>();
		
		m_sqlHelp = new SqliteHelper(context);
		
		//m_strUserID = UserInfoHelper.getUserID(context);
		m_strPhoneNum = PhoneDataProvider.getPhoneNum(context);
		m_strImsi = PhoneDataProvider.getIMSI(context);
		m_strImei = PhoneDataProvider.getIMEI(context);
		m_strModel = PhoneDataProvider.getModel();
		m_strVerCode = PhoneDataProvider.getCurVerCode(context) + "";
	}

	/**
	 * 功能: 记录日志
	 * 参数:	context: 
	 * 		strPreModule: 父模块
	 * 		strActivityName: 窗口
	 * 		eLevel: 层次
	 * 		strRev: 保留
	 * 返回值:
	 * 说明: 模块级别的日志,直接使用父模块来确定模块信息
	 */
/*	public void WriteLog(Context context,ModuleIndex eModule,ActivityName eActivity,
			LogLevel eLevel,String strRev)
	{	
		//重新获取用户ID
		//if(m_strUserID.length() <= 0)
		m_strUserID = UserInfoHelper.getUserID(context);
				
		String strPreModule = "";
		String strModule = "";
		switch (eLevel)
		{
		case eMainLog:	//登录和主界面
		case eActivityLog:	//具体窗口	
			strPreModule = getModuleName(eModule);	//父模块
			strModule = getActivityName(eActivity);	//模块
			break;
		case eModuleLog:	//具体功能模块
			strPreModule = "";//父模块
			strModule = getModuleName(eModule);		//模块
			break;
		default:
			break;
		}
		
		WriteLog(context, strPreModule, strModule,eLevel,getLogDelay(eActivity),strRev);
	}
	
	//按钮日志
	public void WriteLogBtn(Context context,ModuleIndex eModule, ButtonLog eBtnLog,String strRev)
	{	
		//重新获取用户ID
		//if(m_strUserID.length() <= 0)
		m_strUserID = UserInfoHelper.getUserID(context);
				
		//测试开始按钮
		String strPreModule = getModuleName(eModule);	//父模块
		String strModule = getBtnName(eBtnLog);		//模块
		
		WriteLog(context, strPreModule, strModule,LogLevel.eButtonLog,0,strRev);
	}
	
	private void WriteLog(Context context,String strPreModule,String strModule,
			LogLevel eLevel,long lDelay,String strRev)
	{
		StringBuffer sb = new StringBuffer("insert into " + DatabaseHelper.InetLog + " values");
		sb.append("(");
		sb.append(System.currentTimeMillis() + SrcMark);
		
		//重新获取用户ID
		//if(m_strUserID.length() <= 0)
		m_strUserID = UserInfoHelper.getUserID(context);
		
		sb.append(getStrFormat(m_strUserID) + SrcMark);
		sb.append(getStrFormat(m_strPhoneNum) + SrcMark);
		sb.append(getStrFormat(m_strImsi) + SrcMark);
		sb.append(getStrFormat(m_strImei) + SrcMark);
		sb.append(getStrFormat(m_strModel) + SrcMark);
		sb.append(getStrFormat(m_strVerCode) + SrcMark);
		
		sb.append(PhoneDataProvider.getOperator(context) + SrcMark);
		sb.append(PhoneDataProvider.getAvaiableNetworkType(context) + SrcMark);
		
		//LTE
		int[] nCells = PhoneDataProvider.getCellInfo(context, 0);
		sb.append(getStrFormat(nCells[1] + "") + SrcMark); //mcc
		sb.append(getStrFormat(nCells[0] + "") + SrcMark);	//mnc
		sb.append(getStrFormat(nCells[2] + "") + SrcMark);	//CI
		
		//cdma
		nCells = PhoneDataProvider.getCellInfo(context, 1);
		sb.append(getStrFormat(nCells[1] + "") + SrcMark); //sid
		sb.append(getStrFormat(nCells[2] + "") + SrcMark);	//nid
		sb.append(getStrFormat(nCells[0] + "") + SrcMark);	//CId
		
		//GSM
		nCells = PhoneDataProvider.getCellInfo(context, 2);
		sb.append(getStrFormat(nCells[0] + "") + SrcMark); //CIDGsm
		sb.append(getStrFormat(nCells[1] + "") + SrcMark);	//lac
		
		sb.append(getStrFormat(strPreModule) + SrcMark);	//父模块
		sb.append(getStrFormat(strModule) + SrcMark);		//模块
		sb.append(lDelay + SrcMark);						//时延
		sb.append(eLevel.ordinal() + SrcMark);				//层次
		sb.append(getStrFormat(strRev));					//保留
		sb.append(")");
		
		Log.i(TAG, sb.toString());
		m_sqlHelp.exeSql(sb.toString());
	}
*/	
	//上传所有日志
	public boolean upLoadLog()
	{
		boolean bRet = false;
		String strRetJson = getLogJson();
		if(strRetJson == null)
			return bRet;
		
		Log.i(TAG, strRetJson);
		String strRet = WebUtil.uploadLogs(strRetJson,1,-1);	//上传
		if(strRet.equals("ok")) //上传成功
			bRet = true;
	
		//上传成功后删除日志
		if(bRet)
			m_sqlHelp.ClearTable(DatabaseHelper.InetLog, null, null);
		
		return bRet;
	}
	
	//获取上传json字符串
	private String getLogJson()
	{
		JSONObject objRet = new JSONObject();
		JSONArray arrData = new JSONArray(); 
		JSONArray obj; 
		
		String strSql = "select * from " + DatabaseHelper.InetLog;
		Cursor cursor = m_sqlHelp.Query(strSql);
		if(cursor == null)
			return null;
		
		while(cursor.moveToNext())	//遍历每一行
		{
			obj = new JSONArray();
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i)); //添加每一行记录
			}
			arrData.put(obj);
		}
		cursor.close();
		
		if(arrData.length() <= 0) //没有数据
			return null;
		
		try
		{
			objRet.put("InetLog", arrData);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
		return objRet.toString();
	}
	
	/**
	 * 功能: 设置模块的开始时间
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public void setStartTime(ActivityName eName)
	{
		long lStart = System.currentTimeMillis();
		m_mapStartTime.put(eName, lStart);
	}
	
	/**
	 * 功能: 获取模块的使用时间
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public long getLogDelay(ActivityName eName)
	{
		if(eName == null)
			return 0;
		
		long lRet = 0;
		if(m_mapStartTime.containsKey(eName))
		{
			lRet = System.currentTimeMillis() - m_mapStartTime.get(eName);	//时间差
			m_mapStartTime.remove(eName);	//删除记录
		}
		return lRet;
	}
	
	//获取模块对应的名称
/*	private String getModuleName(ModuleIndex eIndex)
	{
		if(eIndex == null)
			return "";
		
		String strRet = "";
		switch (eIndex)
		{
		case eCQT:
			strRet = "CQT";
			break;
		case eDT:
			strRet = "DT";
			break;
		case eAuto:
			strRet = "定点引爆";
			break;
		case eCust:
			strRet = "大众版测试";
			break;
		case eProbback:
			strRet = "问题热点反馈";
			break;
		case eSignal:
			strRet = "信号测量";
			break;
		case eCustTest:
			strRet = "大众版测试";
			break;
		case eAnte:
			strRet = "天馈测量";
			break;
		case eRunningData:
			strRet = "运行数据";
			break;
		case eCompQury:
			strRet = "综合查询";
			break;
		case eMap:
			strRet = "地图";
			break;
		case eSetting:
			strRet = "设置";
			break;
		default:
			break;
		}
		return strRet;
	}
	
	//获取窗口名称
	private String getActivityName(ActivityName eName)
	{
		String strRet = "";
		switch (eName)
		{
		case eLogin:
			strRet = "登录界面";
			break;
		case eMainActivity:
			strRet = "主界面";
			break;
		case eHotPointSelComb:
			strRet = "热点选择";
			break;
		case eFloorSel:
			strRet = "楼层选择";
			break;
		case eTaskList:
			strRet = "任务列表";
			break;
		case eTaskRet:
			strRet = "CQT测试结果";
			break;
		case eDTActivity:
			strRet = "DT窗口";
			break;
		case eAutoTest:
			strRet = "定点引爆";
			break;
		case eSignalTest:
			strRet = "信号测量专业版";
			break;
		case eCustTest:
			strRet = "大众版测试";
			break;
		case eCustSignal:
			strRet = "大众版信号测量";
			break;
		case eCustTask:
			strRet = "大众版感知测试";
			break;
		case eTraceLmtTab:
			strRet = "天馈";
			break;
		case eTracHW:
			strRet = "CDMA华为天馈测量";
			break;
		case eTracZTE:
			strRet = "CDMA中兴天馈测量";
			break;
		case eTracLucent:
			strRet = "CDMA朗讯天馈测量";
			break;
		case eTracHW_LTE:
			strRet = "LTE华为天馈测量";
			break;
		case eTracZTE_LTE:
			strRet = "LTE中兴天馈测量";
			break;
		case eTracLucent_LTE:
			strRet = "LTE朗讯天馈测量";
			break;
		case eBtsInfo:
			strRet = "运行数据/综合查询";
			break;
		case eBtsInfo_Search:
			strRet = "搜索查询";
			break;
		case eBtsInfo_Base:
			strRet = "基站信息";
			break;
		case eBtsInfo_Ante:
			strRet = "天馈信息";
			break;
		case eBtsInfo_Alarm:
			strRet = "告警信息";
			break;
		case eBtsInfo_KPI:
			strRet = "性能信息";
			break;
		case eBtsMap:
			strRet = "地图窗口";
			break;
		case eSetting:
			strRet = "设置窗口";
			break;
		default:
			break;
		}
		return strRet;
	}
	
	//获取按钮名称
	private String getBtnName(ButtonLog eName)
	{
		String strRet = "";
		switch (eName)
		{
		case eStartCQT:
			strRet = "开始CQT测试";
			break;
		case eStartDT:
			strRet = "开始DT测试";
			break;
		case eStopDT:
			strRet = "停止DT测试";
			break;
		case eStartAuto:
			strRet = "开始自动测试";
			break;
		case eStopAuto:
			strRet = "停止自动测试";
			break;
		case eStartSignal:
			strRet = "开始信号测量";
			break;
		case eStopSignal:
			strRet = "停止信号测量";
			break;
		case eStartSignalCust:
			strRet = "开始大众版信号测量";
			break;
		case eStopSignalCust:
			strRet = "停止大众版信号测量";
			break;
		case eStartTaskCust:
			strRet = "开始大众版感知测试";
			break;
		case eStopTaskCust:
			strRet = "停止大众版感知测试";
			break;
		case eStartTracHW:
			strRet = "开始CDMA华为天馈测量";
			break;
		case eStartTracZTE:
			strRet = "开始CDMA中兴天馈测量";
			break;
		case eStartTracLucent:
			strRet = "开始CDMA朗讯天馈测量";
			break;
		case eStartTracHW_LTE:
			strRet = "开始LTE华为天馈测量";
			break;
		case eStartTracZTE_LTE:
			strRet = "开始LTE中兴天馈测量";
			break;
		case eStartTracLucent_LTE:
			strRet = "开始LTE朗讯天馈测量";
			break;
		case eStartOffLineMap:
			strRet = "下载离线地图";
			break;
		case eStartBtsData:
			strRet = "下载基站信息";
			break;
		case eStartClearBts:
			strRet = "清除基站信息";
			break;
		case eStartSetAuto:
			strRet = "自动测试设置";
			break;
		case eStartCreateDB:
			strRet = "重建三重测试数据库";
			break;
		case eStartClearCache:
			strRet = "清理缓存";
			break;
		case eStartAuthSina:
			strRet = "新浪授权";
			break;
		case eStartAuthTx:
			strRet = "腾讯授权";
			break;
		case eStartUpdate:
			strRet = "检查更新";
			break;
		case eStartShared:
			strRet = "分享";
			break;
		case eStartAbout:
			strRet = "关于";
			break;
		case eYesGps:
			strRet = "开启GPS";
			break;
		case eNoGps:
			strRet = "不开启GPS";
			break;
		default:
			break;
		}
		return strRet;
	}
	*/
	//格式化查询字符串
	public static String getStrFormat(String strVal)
	{
		if(strVal == null)
			strVal = "";
		
		return ("'" + strVal + "'");
	}
}
