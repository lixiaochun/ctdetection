package com.wellcell.inet.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Web.WebUtil;

//测试数据本地化-->SQLite
public class TestDataLoal
{
	public static String SrcMark = ",";
	public SqliteHelper m_sqlHelper;
	
	public TestDataLoal(Context context)
	{
		m_sqlHelper = new SqliteHelper(context);
	}
	
	public void close()
	{
		//关闭数据库
		if(m_sqlHelper != null)
			m_sqlHelper.closeDb();
	}
	
	/**
	 * 功能:重新上传数据
	 * 参数:nUpLoadNetWork: 上传网络类型
	 * 返回值:
	 * 说明:
	 */
	public int reUpLoadTestData(int nUPNetType)
	{
		int i = 0;
		
		String strSql = "select * from " + DatabaseHelper.JobInfo;
		Cursor cursor = m_sqlHelper.Query(strSql);
		if(cursor.moveToFirst())
		{
			do
			{
				if(upLoadJobData(cursor,nUPNetType))	//上传任务数据
					deleteJobData(cursor.getString(cursor.getColumnIndex("JobID")));	//删除任务数据
			}
			while (cursor.moveToNext());
			cursor.close();
		}
		return i; 
	}
	
	/**
	 * 功能:上传单次任务数据
	 * 参数:	cursor: JobInfo数据
	 * 		nUPNetType: 当前上传网络类型
	 * 返回值:
	 * 说明:
	 */
	private boolean upLoadJobData(Cursor cursor,int nUPNetType)
	{
		String strJson = getJobDataJson(cursor, nUPNetType);
		if(strJson == null)	//有JobInfo数据,没有业务数据
			return true;
		
		String strRet = "";// = WebUtil.uploadTestData(strJson, ModuleIndex.eCQT, 1);
		if(strRet.equals("ok"))
			return true;
		else
			return false;
	}
	
	//组装一次任务数据
	private String getJobDataJson(Cursor cursor,int nUPNetType)
	{
		JSONObject objRet = new JSONObject();
		JSONArray objJob = new JSONArray();
		JSONArray objTask = new JSONArray();
		JSONArray objNetWork = new JSONArray();
		JSONArray objAnte = new JSONArray();
		JSONArray objPing = new JSONArray();
		JSONArray objWeb = new JSONArray();
		JSONArray objFtp = new JSONArray();
		JSONArray objWeibo = new JSONArray();
		JSONArray objDial = new JSONArray();
		JSONArray objVideo = new JSONArray();
		
		int nIndex = 0;	//索引
		
		//JobInfo
		String strJobID = cursor.getString(nIndex++);
		objJob.put(strJobID);
		objJob.put(cursor.getInt(nIndex++) + "");
		objJob.put(cursor.getInt(nIndex++) + "");
		objJob.put(cursor.getInt(nIndex++) + "");
		objJob.put(cursor.getInt(nIndex++) + "");
		objJob.put(cursor.getString(nIndex++));
		objJob.put(cursor.getString(nIndex++));
		objJob.put(cursor.getString(nIndex++));
		objJob.put(cursor.getString(nIndex++));
		objJob.put(cursor.getString(nIndex++));
		objJob.put(cursor.getInt(nIndex++));
		objJob.put(nUPNetType + "");	//上传网络
		
		int nTaskType;	//业务类型
		String strTID;	//业务ID
		
		//查询JobTask
		String strSql = "select * from " + DatabaseHelper.TaskInfo;
		cursor = m_sqlHelper.Query(strSql);
		if(cursor.moveToFirst())
		{
			do
			{
				strTID = cursor.getString(cursor.getColumnIndex("TID"));
				nTaskType = cursor.getInt(cursor.getColumnIndex("TaskType"));
				
				objTask.put(getTaskInfoJson(cursor));		//业务数据
				objNetWork.put(getNetWorkInfoJson(strTID));	//无线网路
				objAnte.put(getAnteInfoJson(strTID));		//天馈
				
				switch (nTaskType)
				{
				case 1:	//ping
					objPing.put(getPingInfoJson(strTID));
					break;
				case 2:	//Web
					objWeb.put(getWebInfoJson(strTID));
					break;
				case 3:	//FTP
					objFtp.put(getFtpInfoJson(strTID));
					break;
				case 4:	//dial
					objDial.put(getDialInfoJson(strTID));
					break;
				case 5:	//Video
					objVideo.put(getVideoInfoJson(strTID));
					break;
				case 6:	//Weibo
					objWeibo.put(getWeiboInfoJson(strTID));
					break;
				default:
					break;
				}
			}
			while (cursor.moveToNext()); //遍历任务里所有业务
		}
		
		//数据有效性判断,没有业务的不上传
		if(objTask.length() >= 0)
			return null;
		
		//添加到结果集里
		try
		{
			objRet.put("job", objJob);
			objRet.put("task", objTask);
			objRet.put("network", objNetWork);
			objRet.put("ante", objAnte);
			objRet.put("ping", objPing);
			objRet.put("web", objWeb);
			objRet.put("ftp", objFtp);
			objRet.put("weibo", objWeibo);
			objRet.put("dial", objDial);
			objRet.put("video", objVideo);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return objRet.toString();
	}
	
	//获取taskInfo记录的Json
	private JSONArray getTaskInfoJson(Cursor cursor)
	{
		JSONArray obj = new JSONArray();
		
		for (int i = 0; i < cursor.getColumnCount(); i++)
		{
			obj.put(SqliteHelper.getCursorVal(cursor, i));
		}
		
		return obj;
	}
	
	//获取NetWorkInfo记录的json
	private JSONArray getNetWorkInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.NetWorkInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//获取AnteInfo记录的json
	private JSONArray getAnteInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.AnteInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//获取PingInfo记录的json
	private JSONArray getPingInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.PingInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//获取WebInfo记录的json
	private JSONArray getWebInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.WebInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//获取FtpInfo记录的json
	private JSONArray getFtpInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.FtpInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//获取WeiboInfo记录的json
	private JSONArray getWeiboInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.WeiboInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//获取DialInfo记录的json
	private JSONArray getDialInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.DialInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//获取VideoInfo记录的json
	private JSONArray getVideoInfoJson(String strTID)
	{
		JSONArray obj = new JSONArray();
		StringBuffer sb = new StringBuffer();
		
		sb.append("select * from " + DatabaseHelper.VideoInfo);
		sb.append(" where TID=" + SqliteHelper.getStrFormat(strTID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());
		if(cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getColumnCount(); i++)
			{
				obj.put(SqliteHelper.getCursorVal(cursor, i));
			}
		}
		return obj;
	}
	
	//--------------------------------------------------------------------
	/**
	 * 功能:存储业务数据记录
	 * 参数:	task: 
	 * 返回值:
	 * 说明:
	 */
/*	public void AddTaskRecord(AbsTask task)
	{
		//JobInfo记录不存在
		if(IsJobInfoExisted(task.m_strJobID))
			return;
		
		updateJobEndTime(task.m_strJobID,task.m_lEndTime);	//更新JobInfo结束时间
		
		addTaskInfo(task);		//TaskInfo
		addNetWorkInfo(task);	//NetWorkInfo
		addAnteInfo(task);		//AnteInfo
		
		//业务数据
		switch (task.m_taskType)
		{
		case PING:
			addPingInfo(task);
			break;
		case WEBSITE:
			addWebInfo(task);
			break;
		case FTP:
			addFtpInfo(task);
			break;
		case IM:
		case TencentWeibo:
			addWeiboInfo(task);
			break;
		case DIAL:
			addDialInfo(task);
			break;
		case VIDEO:
			addVideoInfo(task);
			break;
		}
	}
	
	//存储JobInfo信息
	public void addJobInfo(JobTest jobTest)
	{
		//已经存在
		if(IsJobInfoExisted(jobTest.m_strJobID))
			return;
		
		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO " + DatabaseHelper.JobInfo);
		sb.append(" VALUES(");
		sb.append(SqliteHelper.getStrFormat(jobTest.m_strJobID) + SrcMark);
		sb.append(jobTest.m_strJobType + SrcMark);
		sb.append(jobTest.m_strSubType + SrcMark);
		sb.append(jobTest.m_lStartTime + SrcMark);
		sb.append(jobTest.m_lEndTime + SrcMark);
		sb.append(SqliteHelper.getStrFormat(jobTest.m_strUser) + SrcMark);
		sb.append(SqliteHelper.getStrFormat(jobTest.m_strIMSI) + SrcMark);
		sb.append(SqliteHelper.getStrFormat(jobTest.m_strIMEI) + SrcMark);
		sb.append(SqliteHelper.getStrFormat(jobTest.m_strModel) + SrcMark);
		sb.append(SqliteHelper.getStrFormat(jobTest.m_strHotID) + SrcMark);
		sb.append(jobTest.m_strFloor + SrcMark);
		sb.append("");	//上传网络类型
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());	//执行SQL
	}
	
	//存储TaskInfo信息
	private void addTaskInfo(AbsTask task)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO " + DatabaseHelper.TaskInfo);
		sb.append(" VALUES(");
		sb.append(SqliteHelper.getStrFormat(task.m_strJobID) + SrcMark);
		sb.append(SqliteHelper.getStrFormat(task.m_strTID) + SrcMark);
		sb.append(task.getTaskTypeString() + SrcMark);
		sb.append(task.getAutoString() + SrcMark);
		sb.append(task.m_lStartTime + SrcMark);
		sb.append(task.m_lEndTime + SrcMark);
		sb.append(task.m_nLostLinkCount + SrcMark);
		sb.append(task.m_nSwitchTo1XCount + SrcMark);
		sb.append(task.m_nNetWorkAcc + SrcMark);
		sb.append(task.m_nDisconnTime + SrcMark);
		sb.append(task.m_nConnTime + SrcMark);
		
		//网络1
		for (int i = 0; i < task.m_netInfos.length; i++)
		{
			sb.append(SqliteHelper.getStrFormat(task.m_netInfos[i].strName) + SrcMark);	//名称
			sb.append(task.m_netInfos[i].nType + SrcMark);		//类型
			sb.append(task.m_netInfos[i].nSubType + SrcMark);	//子类型
			sb.append(task.m_netInfos[i].nTime + SrcMark);		//在线时长
		}
	
		//释放经纬度
		sb.append(SqliteHelper.getStrFormat(task.m_strLonRel) + SrcMark);
		sb.append(SqliteHelper.getStrFormat(task.m_strLatRel));
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储NetWorkInfo信息
	private void addNetWorkInfo(AbsTask task)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO " + DatabaseHelper.NetWorkInfo);
		sb.append(" VALUES(");
		sb.append(getStrFormat(task.m_strJobID) + SrcMark);
		sb.append(getStrFormat(task.m_strTID) + SrcMark);
		
		//-----------------接入---------------
		//LTE接入基站(空字符)
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);

		//LTE接入小区
		sb.append(getStrFormat(task.m_cellAcc.m_strMcc) + SrcMark);
		sb.append(getStrFormat(task.m_cellAcc.m_strMnc) + SrcMark);
		sb.append(getStrFormat(task.m_cellAcc.m_strCi) + SrcMark);
		sb.append(getStrFormat(task.m_cellAcc.m_strPci) + SrcMark);
		sb.append(getStrFormat(task.m_cellAcc.m_strTac) + SrcMark);
	
		//CDMA接入基站(空字符)
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);

		//CDMA接入小区
		sb.append(getStrFormat(task.m_cellAcc.m_strCID) + SrcMark);
		sb.append(getStrFormat(task.m_cellAcc.m_strSID) + SrcMark);
		sb.append(getStrFormat(task.m_cellAcc.m_strNID) + SrcMark);
		
		//GSM
		sb.append(getStrFormat(task.m_cellAcc.m_strCellIdCdma) + SrcMark);
		sb.append(getStrFormat(task.m_cellAcc.m_strLacGsm) + SrcMark);
		//--------------释放------------------
		//LTE基站(空字符)
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);

		//LTE接入小区
		sb.append(getStrFormat(task.m_cellRel.m_strMcc) + SrcMark);
		sb.append(getStrFormat(task.m_cellRel.m_strMnc) + SrcMark);
		sb.append(getStrFormat(task.m_cellRel.m_strCi) + SrcMark);
		sb.append(getStrFormat(task.m_cellRel.m_strPci) + SrcMark);
		sb.append(getStrFormat(task.m_cellRel.m_strTac) + SrcMark);
	
		//CDMA接入基站(空字符)
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);
		sb.append(getStrFormat("") + SrcMark);

		//CDMA接入小区
		sb.append(getStrFormat(task.m_cellRel.m_strCID) + SrcMark);
		sb.append(getStrFormat(task.m_cellRel.m_strSID) + SrcMark);
		sb.append(getStrFormat(task.m_cellRel.m_strNID) + SrcMark);
		
		//GSM
		sb.append(getStrFormat(task.m_cellRel.m_strCellIdCdma) + SrcMark);
		sb.append(getStrFormat(task.m_cellRel.m_strLacGsm) + SrcMark);
		//---------------------------------------------
		//RSRP
		sb.append(task.m_nStandLte + SrcMark);			//达标数
		sb.append(task.m_nSumLte + SrcMark);			//记录总数
		sb.append(CGlobal.floatFormatString(task.m_rankRsrp.m_dSum,2) + SrcMark);	//加权分子
		sb.append(task.m_rankRsrp.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankRsrp.m_nPart0 + SrcMark);
		sb.append(task.m_rankRsrp.m_nPart1 + SrcMark);
		sb.append(task.m_rankRsrp.m_nPart2 + SrcMark);
		sb.append(task.m_rankRsrp.m_nPart3 + SrcMark);
		sb.append(task.m_rankRsrp.m_nPart4 + SrcMark);
		
		//RSRQ
		sb.append(CGlobal.floatFormatString(task.m_rankRsrq.m_dSum,2) + SrcMark);	//加权分子
		sb.append(task.m_rankRsrq.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankRsrq.m_nPart0 + SrcMark);
		sb.append(task.m_rankRsrq.m_nPart1 + SrcMark);
		sb.append(task.m_rankRsrq.m_nPart2 + SrcMark);
		sb.append(task.m_rankRsrq.m_nPart3 + SrcMark);
		sb.append(task.m_rankRsrq.m_nPart4 + SrcMark);

		//RSSI
		sb.append(CGlobal.floatFormatString(task.m_rankRssi.m_dSum,2) + SrcMark);	//加权分子
		sb.append(task.m_rankRssi.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankRssi.m_nPart0 + SrcMark);
		sb.append(task.m_rankRssi.m_nPart1 + SrcMark);
		sb.append(task.m_rankRssi.m_nPart2 + SrcMark);
		sb.append(task.m_rankRssi.m_nPart3 + SrcMark);
		sb.append(task.m_rankRssi.m_nPart4 + SrcMark);

		//SINR
		sb.append(CGlobal.floatFormatString(task.m_rankSinr.m_dSum,2) + SrcMark);	//加权分子
		sb.append(task.m_rankSinr.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankSinr.m_nPart0 + SrcMark);
		sb.append(task.m_rankSinr.m_nPart1 + SrcMark);
		sb.append(task.m_rankSinr.m_nPart2 + SrcMark);
		sb.append(task.m_rankSinr.m_nPart3 + SrcMark);
		sb.append(task.m_rankSinr.m_nPart4 + SrcMark);
		
		//CQI
		sb.append(CGlobal.floatFormatString(task.m_rankCqi.m_dSum,2) + SrcMark);		//加权分子
		sb.append(task.m_rankCqi.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankCqi.m_nPart0 + SrcMark);
		sb.append(task.m_rankCqi.m_nPart1 + SrcMark);
		sb.append(task.m_rankCqi.m_nPart2 + SrcMark);
		sb.append(task.m_rankCqi.m_nPart3 + SrcMark);
		sb.append(task.m_rankCqi.m_nPart4 + SrcMark);
		//--------------cdma-----------------
		//Rx3g
		sb.append(task.m_nStandEvdo + SrcMark);				//达标数
		sb.append(task.m_nSumEvdo + SrcMark);
		sb.append(CGlobal.floatFormatString(task.m_rankRx3g.m_dSum,2) + SrcMark);		//加权分子
		sb.append(task.m_rankRx3g.m_nCount + SrcMark);		//记录数
		sb.append(task.m_rankRx3g.m_nPart0 + SrcMark);
		sb.append(task.m_rankRx3g.m_nPart1 + SrcMark);
		sb.append(task.m_rankRx3g.m_nPart2 + SrcMark);
		sb.append(task.m_rankRx3g.m_nPart3 + SrcMark);
		sb.append(task.m_rankRx3g.m_nPart4 + SrcMark);

		//Ecio3g
		sb.append(CGlobal.floatFormatString(task.m_rankEcio3g.m_dSum,2) + SrcMark);		//加权分子
		sb.append(task.m_rankEcio3g.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankEcio3g.m_nPart0 + SrcMark);
		sb.append(task.m_rankEcio3g.m_nPart1 + SrcMark);
		sb.append(task.m_rankEcio3g.m_nPart2 + SrcMark);
		sb.append(task.m_rankEcio3g.m_nPart3 + SrcMark);
		sb.append(task.m_rankEcio3g.m_nPart4 + SrcMark);

		//SNR
		sb.append(CGlobal.floatFormatString(task.m_rankSnr.m_dSum,2) + SrcMark);		//加权分子
		sb.append(task.m_rankSnr.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankSnr.m_nPart0 + SrcMark);
		sb.append(task.m_rankSnr.m_nPart1 + SrcMark);
		sb.append(task.m_rankSnr.m_nPart2 + SrcMark);
		sb.append(task.m_rankSnr.m_nPart3 + SrcMark);
		sb.append(task.m_rankSnr.m_nPart4 + SrcMark);

		//Rx2g
		sb.append(task.m_nStandCdma + SrcMark);			//达标数
		sb.append(task.m_nSumCdma + SrcMark);
		sb.append(CGlobal.floatFormatString(task.m_rankRx2g.m_dSum,2) + SrcMark);	//加权分子
		sb.append(task.m_rankRx2g.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankRx2g.m_nPart0 + SrcMark);
		sb.append(task.m_rankRx2g.m_nPart1 + SrcMark);
		sb.append(task.m_rankRx2g.m_nPart2 + SrcMark);
		sb.append(task.m_rankRx2g.m_nPart3 + SrcMark);
		sb.append(task.m_rankRx2g.m_nPart4 + SrcMark);

		//Ecio2g
		sb.append(CGlobal.floatFormatString(task.m_rankEcio2g.m_dSum,2) + SrcMark);	//加权分子
		sb.append(task.m_rankEcio2g.m_nCount + SrcMark);//记录数
		sb.append(task.m_rankEcio2g.m_nPart0 + SrcMark);
		sb.append(task.m_rankEcio2g.m_nPart1 + SrcMark);
		sb.append(task.m_rankEcio2g.m_nPart2 + SrcMark);
		sb.append(task.m_rankEcio2g.m_nPart3 + SrcMark);
		sb.append(task.m_rankEcio2g.m_nPart4 + SrcMark);

		//RxGsm
		sb.append(task.m_nStandGsm + SrcMark);			//达标点数
		sb.append(task.m_nSumGsm + SrcMark);
		sb.append(CGlobal.floatFormatString(task.m_rankRxGsm.m_dSum,2) + SrcMark);	//加权分子
		sb.append(task.m_rankRxGsm.m_nCount + SrcMark);	//记录数
		sb.append(task.m_rankRxGsm.m_nPart0 + SrcMark);
		sb.append(task.m_rankRxGsm.m_nPart1 + SrcMark);
		sb.append(task.m_rankRxGsm.m_nPart2 + SrcMark);
		sb.append(task.m_rankRxGsm.m_nPart3 + SrcMark);
		sb.append(task.m_rankRxGsm.m_nPart4);
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储AnteInfo信息
	private void addAnteInfo(AbsTask task)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO " + DatabaseHelper.AnteInfo);
		sb.append(" VALUES(");
		sb.append(getStrFormat(task.m_strTID) + SrcMark);
		
		sb.append(getStrFormat("") + SrcMark);	//开始时ROT
		sb.append(getStrFormat("") + SrcMark);	//开始时RSSI
		sb.append(getStrFormat("") + SrcMark);	//开始时用户数
		sb.append(getStrFormat("") + SrcMark);	//开始时VSWR
		
		//ROT
		for (int i = 0; i < task.m_anteRot.length; i++)
		{
			sb.append(getStrFormat(task.m_anteRot[i].strCarry) + SrcMark);				//载频号
			sb.append(CGlobal.floatFormatString(task.m_anteRot[i].dMain,2) + SrcMark);	//主集
			sb.append(CGlobal.floatFormatString(task.m_anteRot[i].dDiv,2) + SrcMark);	//分集
		}
		//------------------------------
		//用户数
		for (int i = 0; i < task.m_anteUserNum.length; i++)
		{
			sb.append(getStrFormat(task.m_anteUserNum[i].strCarry) + SrcMark);				//载频号
			sb.append(CGlobal.floatFormatString(task.m_anteUserNum[i].dUserNum,2) + SrcMark);//在线用户数
		}
		//-------------------------------
		//RSSI
		for (int i = 0; i < task.m_anteRssi.length; i++)
		{
			sb.append(getStrFormat(task.m_anteRssi[i].strCarry) + SrcMark);				//载频号
			sb.append(CGlobal.floatFormatString(task.m_anteRssi[i].dMain,2) + SrcMark);	//主集
			sb.append(task.m_anteRssi[i].dDiv);		//分集
			
			if(i != task.m_anteRssi.length - 1)
				sb.append(SrcMark);
		}
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储PingInfo信息
	private void addPingInfo(AbsTask task)
	{
		PingTestTask pingTask = (PingTestTask)task;
		
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO " + DatabaseHelper.PingInfo);
		sb.append(" VALUES(");
		
		sb.append(getStrFormat(pingTask.m_strTID) + SrcMark);		//业务ID
		
		sb.append(getStrFormat(pingTask.m_pingPar.m_strDest) + SrcMark);
		sb.append(pingTask.m_pingPar.m_nPackSize + SrcMark);
		sb.append(pingTask.m_pingPar.m_nCount + SrcMark);	//配置项
		sb.append((int)pingTask.m_dDelayMax + SrcMark);
		sb.append((int)pingTask.m_dDelayMin + SrcMark);
		sb.append((int)pingTask.m_dDelayAvg + SrcMark);
		sb.append(pingTask.m_nCountSuc + SrcMark);
		sb.append(pingTask.m_nTestCount);		//实际测试次数		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储WebInfo信息
	private void addWebInfo(AbsTask task)
	{
		WebTestTaskEx webTask = (WebTestTaskEx)task;
		StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO " + DatabaseHelper.WebInfo);
		sb.append(" VALUES(");
		
		sb.append(getStrFormat(webTask.m_strTID) + SrcMark);
		sb.append(getStrFormat(webTask.m_webPar.m_strUrl) + SrcMark);
		sb.append(webTask.m_webPar.m_nTimeout + SrcMark);
		sb.append(webTask.m_webPar.m_nCount + SrcMark);
		sb.append(0 + SrcMark);	//浏览类型
		//用时
		sb.append(webTask.m_nOpenDelayMax + SrcMark);
		sb.append(webTask.m_nOpenDelayMin + SrcMark);
		sb.append(webTask.m_nOpenDelayAvg + SrcMark);
		sb.append(webTask.m_nLinkSucCount + SrcMark);	//连接成功次数
		
		sb.append(webTask.m_nSucCount + SrcMark);
		sb.append(webTask.m_nTestCount + SrcMark);
		sb.append(webTask.m_lSizeSum + SrcMark);	//总流量
		sb.append(webTask.m_lSizeAvg + SrcMark);	//平均流量
		sb.append(CGlobal.floatFormatString(webTask.m_dSpeedAvg,2) + SrcMark);	//平均速率
		
		// 增加延迟字段
		sb.append(webTask.m_nLinkDelayMax + SrcMark);
		sb.append(webTask.m_nLinkDelayMin + SrcMark);
		sb.append(webTask.m_nLinkDelayAvg + SrcMark);
		sb.append(webTask.m_webPar.m_nProgAlarm);	//时延门限		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储FtpInfo信息
	private void addFtpInfo(AbsTask task)
	{
		FtpTestTask ftpTask = (FtpTestTask)task;
		StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO " + DatabaseHelper.FtpInfo);
		sb.append(" VALUES(");
		
		sb.append(getStrFormat(ftpTask.m_strTID) + SrcMark);
		
		sb.append(getStrFormat(ftpTask.m_FtpPar.m_strSrv) + SrcMark);
		sb.append(ftpTask.m_FtpPar.m_nPort + SrcMark);
		sb.append(getStrFormat(ftpTask.m_FtpPar.m_strUser) + SrcMark);
		sb.append(getStrFormat(ftpTask.m_FtpPar.m_strRemoteFile) + SrcMark);
		sb.append(ftpTask.m_FtpPar.m_nTimeout + SrcMark);
		sb.append(ftpTask.m_FtpPar.m_nCount + SrcMark);

		sb.append(ftpTask.m_nTimeMax + SrcMark);
		sb.append(ftpTask.m_nTimeMin + SrcMark);
		sb.append(ftpTask.m_nTimeAvg + SrcMark);
		// 增加流量字段
		sb.append(ftpTask.m_lRemoteFileSize + SrcMark);
		sb.append(ftpTask.m_lDownSizeMax + SrcMark);
		sb.append(ftpTask.m_lDownSizeMin + SrcMark);
		sb.append(ftpTask.m_lDownSizeAvg + SrcMark);
		
		sb.append(ftpTask.m_nCountSuc + SrcMark);
		sb.append(ftpTask.m_nTestCount + SrcMark);

		sb.append(CGlobal.floatFormatString(ftpTask.m_dSpeedMax,2) + SrcMark);
		sb.append(CGlobal.floatFormatString(ftpTask.m_dSpeedMin,2) + SrcMark);
		sb.append(CGlobal.floatFormatString(ftpTask.m_dSpeedAvg,2) + SrcMark);
		sb.append(ftpTask.m_nSpeedCount);
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储WeiboInfo信息
	private void addWeiboInfo(AbsTask task)
	{
		WeiboTask weiboTask = (WeiboTask)task;
		StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO " + DatabaseHelper.WeiboInfo);
		sb.append(" VALUES(");
		
		sb.append(getStrFormat(weiboTask.m_strTID) + SrcMark);
		sb.append(weiboTask.m_WeiboPar.m_nTimeout + SrcMark);
		sb.append(weiboTask.m_WeiboPar.m_nCount + SrcMark);
		sb.append(weiboTask.m_WeiboPar.m_fun.ordinal() + SrcMark);
		
		sb.append(weiboTask.m_nTimeMax + SrcMark);
		sb.append(weiboTask.m_nTimeMin + SrcMark);
		sb.append(weiboTask.m_nTimeAvg + SrcMark);
		
		sb.append(weiboTask.m_nCountSuc + SrcMark);
		sb.append(weiboTask.m_nTestCount + SrcMark);
		
		// 增加流量字段
		sb.append(weiboTask.m_lSizeSum + SrcMark);
		sb.append(weiboTask.m_lSizeAvg);
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储DialInfo信息
	private void addDialInfo(AbsTask task)
	{
		DialTestTask dialTask = (DialTestTask)task;
		StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO " + DatabaseHelper.DialInfo);
		sb.append(" VALUES(");
		
		sb.append(getStrFormat(dialTask.m_strTID) + SrcMark);
		sb.append(dialTask.m_DialPar.m_strNum + SrcMark);
		sb.append(dialTask.m_DialPar.m_nTOAcc + SrcMark);
		sb.append(dialTask.m_DialPar.m_nTORel + SrcMark);
		sb.append(dialTask.m_DialPar.m_nCount + SrcMark);

		sb.append(dialTask.m_nDurAccMax + SrcMark);
		sb.append(dialTask.m_nDurAccMin + SrcMark);
		sb.append(dialTask.m_nDurAccAvg + SrcMark);
		
		sb.append(dialTask.m_nDurRelMax + SrcMark);
		sb.append(dialTask.m_nDurRelMin + SrcMark);
		sb.append(dialTask.m_nDurRelAvg + SrcMark);
		
		sb.append(dialTask.m_nConnSucCount + SrcMark);	//连接成功次数
		sb.append(dialTask.m_nDropCount + SrcMark);		//掉话次数
		sb.append(dialTask.n_nCountSuc + SrcMark);		//成功次数
		sb.append(dialTask.m_nTestCount);
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//存储VideoInfo信息
	private void addVideoInfo(AbsTask task)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO " + DatabaseHelper.VideoInfo);
		sb.append(" VALUES(");
		sb.append("");
		sb.append(")");
		
		m_sqlHelper.exeSql(sb.toString());
	}
	*/
	/**
	 * 功能: 指定ID的JobInfo记录是否存在
	 * 参数:
	 * 返回值:true: 存在,false: 不存在
	 * 说明:
	 */
	private boolean IsJobInfoExisted(String strJobID)
	{
		String strSql = "select * from " + DatabaseHelper.JobInfo + " where JobID=" + getStrFormat(strJobID);
		Cursor cursor = m_sqlHelper.Query(strSql);
		
		if(cursor.getCount() > 0)
			return true;
		
		return false;
	}
	
	//更新JobInfo记录的任务结束时间
	private void updateJobEndTime(String strJobID,long lEndTime)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("update " + DatabaseHelper.JobInfo);
		sb.append(" set EndTime=" + lEndTime);
		sb.append(" where JobID=" + getStrFormat(strJobID));
		m_sqlHelper.exeSql(sb.toString());
	}
	
	//删除指定JobID的所有业务数据
	public void deleteJobData(String strJobID)
	{
		StringBuffer sb = new StringBuffer();
		
		//删除JobInfo
		sb.append("delete from " + DatabaseHelper.JobInfo);
		sb.append(" where JobID=" + getStrFormat(strJobID));
		m_sqlHelper.exeSql(sb.toString());
		
		//查询所有Task
		sb.setLength(0);
		sb.append("select TID from " + DatabaseHelper.TaskInfo);
		sb.append("where JobID=" + getStrFormat(strJobID));
		Cursor cursor = m_sqlHelper.Query(sb.toString());	//查询所有TID
		String strTID;
		if(cursor.moveToFirst())
		{
			do
			{
				strTID = cursor.getString(cursor.getColumnIndex("TID"));	//业务ID
				deleteTaskData(strTID);	//删除业务数据
			}while(cursor.moveToNext());//移动到下一行
			cursor.close();
		}
		
		//删除TaskInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.TaskInfo);
		sb.append(" where JobID=" + getStrFormat(strJobID));
		m_sqlHelper.exeSql(sb.toString());
	}
	
	/**
	 * 功能:	删除业务所有数据
	 * 参数:	strTID: 业务ID
	 * 返回值:
	 * 说明:
	 */
	private void deleteTaskData(String strTID)
	{
		StringBuffer sb = new StringBuffer();
		
		//删除NetWorkInfo
		sb.append("delete from " + DatabaseHelper.NetWorkInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());
		
		//删除AnteInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.AnteInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());
		
		//删除PingInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.PingInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());
		
		//删除WebInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.WebInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());
		
		//删除FtpInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.FtpInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());
		
		//删除WeiboInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.WeiboInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());

		//删除DialInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.DialInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());
		
		//删除VideoInfo
		sb.setLength(0);
		sb.append("delete from " + DatabaseHelper.VideoInfo);
		sb.append(" where TID=" + getStrFormat(strTID));
		m_sqlHelper.exeSql(sb.toString());
	}
	
	public static String getStrFormat(String strVal)
	{
		return ("'" + strVal + "'");
	}
}
