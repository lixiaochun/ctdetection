package com.wellcell.inet.Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.wellcell.inet.Web.WebUtil;

/*数据存储
 * 	 1只有保存BTS信息
	 2增加保存任务信息Task
	 3、4、5增加任务信息的字段
	 6基站信息增加日期字段
	 7增加楼层字段
	 8增加网络信息字段
	 9增加拨打测试表
	 10基站信息表，增加网络类型字段
 */
public class SqliteHelper
{
	private static final String SrcMark = ",";
	
	private static int SDKVer = Build.VERSION.SDK_INT;	//SDK版本
	
	private static final String tag = "SqliteHelper";
	private static final long DAY = 1000 * 60 * 60 * 24;
	private static final long ExpireTime = DAY * 30;		//数据过期时间
	
	public static final String DB_NAME = "InetDB";	//数据库名
	private static final int DB_VERSION = 10;
	
	private SQLiteDatabase m_sqLiteDb;
	private DatabaseHelper m_helperDb;

	public SqliteHelper(Context context)
	{
		m_helperDb = new DatabaseHelper(context, DB_NAME, DB_VERSION);
		m_sqLiteDb = m_helperDb.getWritableDatabase();
	}

	@Override
	protected void finalize() throws Throwable
	{
		m_sqLiteDb.close();
		super.finalize();
	}

	/**
	 * 功能: 执行sql,并返回结果
	 * 参数: strSql: 
	 * 返回值: Cursor
	 * 说明:
	 */
	public Cursor Query(String strSql)
	{
		Cursor cursor = null;
		try
		{
			cursor = m_sqLiteDb.rawQuery(strSql, null);
		}
		catch (Exception e)
		{
		}
		return cursor;
	}

	/**
	 * 功能: 重新上传业务测试数据
	 * 参数:	context: 
	 * 		type: 业务类型
	 * 返回值:
	 * 说明:
	 */
/*	public int reuploadTask(Context context, TaskType type)
	{
		JSONArray arr = new JSONArray();
		ArrayList<String> arr_id = new ArrayList<String>();

		String sql = "";//String.format("SELECT * FROM %1s WHERE ISUPLOAD = 'false'", getTaskTableName(type));
		Cursor cursor = m_sqLiteDb.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			do
			{
				//AbsTask task = AbsTask.buildTask(type, cursor);
				//arr.put(task.toJsonObj());
			}
			while (cursor.moveToNext());
			cursor.close();
		}

		// 上传
		if (arr.length() > 0)
		{
			String json = arr.toString();
			json = GzipHelper.getGzipCompress(json);

			// 上传
			if (WebUtil.uploadTask(json, type))
			{
				Log.d("", "上传成功");
				// 更新状态
				String template = "UPDATE ";// + getTaskTableName(type) + " SET ISUPLOAD = 'true' WHERE ID = '%1s'";

				m_sqLiteDb.beginTransaction();
				for (String id : arr_id)
				{
					m_sqLiteDb.execSQL(String.format(template, id));
				}
				m_sqLiteDb.setTransactionSuccessful();
				m_sqLiteDb.endTransaction();
			}
		}

		return arr.length();
	}
*/
	//历史任务数据
	public void clearUpdatedTask()
	{
		String strTemp = "DELETE FROM %1s WHERE ISUPLOAD = 'true'";

		String strSql = String.format(strTemp, "TB_TASK_PING");
		m_sqLiteDb.execSQL(strSql);
		
		strSql = String.format(strTemp, "TB_TASK_WEB");
		m_sqLiteDb.execSQL(strSql);
		
		strSql = String.format(strTemp, "TB_TASK_IM");
		m_sqLiteDb.execSQL(strSql);
		
		strSql = String.format(strTemp, "TB_TASK_FTP");
		m_sqLiteDb.execSQL(strSql);
		
		strSql = String.format(strTemp, "TB_TASK_DIAL");
		m_sqLiteDb.execSQL(strSql);
	}

/*	public void UpdateTaskUploadStatus(AbsTask task)
	{
		String tableName = "";//getTaskTableName(task.m_taskType);
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + tableName + " SET ISUPLOAD = '" + true + "'");
		//sb.append(" WHERE ID = '" + task.m_strJobID + "'");

		m_sqLiteDb.execSQL(sb.toString());
	}
*/
	//---------------------------------------------------------
	
/*	public String GetInsertBTSINFO(BtsBasicInfo info)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO "+ DatabaseHelper.BtsInfo);
		sb.append(" (CITYNAME,CITY_ID,BSCNO,BTSNAME,BTSNO,SID,NID,CID,CELLID,PILOT_PN,LATITUDE,LONGITUDE,NETWORKTYPE,MAP_KEY,INSERT_DATE) VALUES (");
		sb.append(getStrFormat(info.m_strCityName) + SrcMark);
		sb.append(getStrFormat(info.m_strCityID) + SrcMark);
		sb.append(getStrFormat(info.m_strBscNo) + SrcMark);
		sb.append(getStrFormat(info.m_strBtsName) + SrcMark);
		sb.append(getStrFormat(info.m_strBtsNo) + SrcMark);
		sb.append(getStrFormat(info.m_strSID) + SrcMark);
		sb.append(getStrFormat(info.m_strNID) + SrcMark);
		sb.append(getStrFormat(info.m_strCID) + SrcMark);
		sb.append(getStrFormat(info.m_strCellId) + SrcMark);
		sb.append(getStrFormat(info.m_strPN) + SrcMark);
		sb.append(info.m_dLat + SrcMark);
		sb.append(info.m_dLon + SrcMark);
		sb.append(getStrFormat(info.m_strDevType) + SrcMark);
		sb.append(getStrFormat(UserInfoHelper.GetLocKey(info.m_dLat, info.m_dLon)) + SrcMark);
		sb.append(getStrFormat(Tools.sdf.format(new Date())));
		sb.append(")");
		return sb.toString();
	}

	public String GetUpdateBTSINFO(BtsBasicInfo info)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + DatabaseHelper.BtsInfo + " SET ");
		// (CITYNAME,CITY_ID,BSCNO,BTSNAME,BTSNO,SID,NID,CID,CELLID,PILOT_PN,LATITUDE,LONGITUDE,MAP_KEY)
		// VALUES (");
		sb.append("CITYNAME = " + getStrFormat(info.m_strCityName) + SrcMark);
		sb.append("CITY_ID = " + getStrFormat(info.m_strCityID) + SrcMark);
		sb.append("BSCNO = " + getStrFormat(info.m_strBscNo) + SrcMark);
		sb.append("BTSNAME = " + getStrFormat(info.m_strBtsName) + SrcMark);
		sb.append("BTSNO = " + getStrFormat(info.m_strBtsNo) + SrcMark);
		sb.append("CELLID = " + getStrFormat(info.m_strCellId) + SrcMark);
		sb.append("PILOT_PN = " + getStrFormat(info.m_strPN) + SrcMark);
		sb.append("LATITUDE = " + info.m_dLat + SrcMark);
		sb.append("LONGITUDE = " + info.m_dLon + SrcMark);
		sb.append("NETWORKTYPE = " + getStrFormat(info.m_strDevType) + SrcMark);
		sb.append("MAP_KEY = " + getStrFormat(UserInfoHelper.GetLocKey(info.m_dLat, info.m_dLon)) + SrcMark);
		sb.append("INSERT_DATE = " + getStrFormat(Tools.sdf.format(new Date())));

		sb.append(" WHERE SID = " + getStrFormat(info.m_strSID));
		sb.append(" AND NID = " + getStrFormat(info.m_strNID));
		sb.append(" AND CID = " + getStrFormat(info.m_strCID));
		return sb.toString();
	}

	private boolean isExist(BtsBasicInfo info)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(1) FROM " + DatabaseHelper.BtsInfo);
		sb.append(" WHERE SID = " + getStrFormat(info.m_strSID));
		sb.append(" AND NID = " + getStrFormat(info.m_strNID));
		sb.append(" AND CID = " + getStrFormat(info.m_strCID));

		Cursor cursor = m_sqLiteDb.rawQuery(sb.toString(), null);
		cursor.moveToFirst();
		int count = 0;
		try
		{
			count = cursor.getInt(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cursor.close();
		}

		return count > 0 ? true:false;
	}
*/
	/**
	 * 功能:保存基站数据到本地(信号测量)
	 * 参数:info: 基站信息
	 * 返回值:
	 * 说明:
	 */
/*	public void saveBtsInfoToLocal(BtsBasicInfo info)
	{
		String strSql = "";
		if (isExist(info)) //已经存在缓存
		{
			// Log.d(tag, "更新：" + info.BTSNAME);
			strSql = GetUpdateBTSINFO(info); //更新
		}
		else 
		{
			// Log.d(tag, "新增：" + info.BTSNAME);
			strSql = GetInsertBTSINFO(info); //插入
		}
		m_sqLiteDb.execSQL(strSql);
	}

	private boolean isBTSINFOExpired(String sid, String nid, String cid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT CITYNAME,CITY_ID,BSCNO,BTSNAME,BTSNO,SID,NID,CID,CELLID,PILOT_PN,LATITUDE,LONGITUDE,NETWORKTYPE,INSERT_DATE ");
		sb.append("FROM " + DatabaseHelper.BtsInfo);
		sb.append(" WHERE SID = " + getStrFormat(sid));
		sb.append(" AND NID = " + getStrFormat(nid));
		sb.append(" AND CID = " + getStrFormat(cid));

		Cursor cursor = m_sqLiteDb.rawQuery(sb.toString(), null);
		cursor.moveToFirst();

		// 解析cursor
		Date insert_date = null;
		String BTSNAME = null;
		try
		{
			BTSNAME = cursor.getString(3);
			insert_date = Tools.sdf.parse(cursor.getString(13));
			// insert_date = new
			// Date(Date.parse(cursor.getString(13).replace("-",
			// "/")));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cursor.close();
		}

		if (insert_date == null)
			insert_date = new Date(0);
		
		if (BTSNAME == null)
			BTSNAME = "";

		if (new Date().getTime() - insert_date.getTime() < ExpireTime)
		{
			Log.d(tag, String.format("%1s，插入时间:%2s", BTSNAME, Tools.sdf.format(insert_date)));
			return false;
		}
		else
		{
			Log.d(tag, String.format("%1s，已经过期", BTSNAME));
			return true;
		}
	}
*/
	/**
	 * 功能: 从本地获取小区信息
	 * 参数:	cid-->ci
	 * 		sid-->mnc
	 * 		nid-->mcc
	 * 返回值:
	 * 说明:
	 */
/*	public BtsBasicInfo getBTSInfo(String cid,String sid, String nid)
	{
		if (isBTSINFOExpired(sid, nid, cid))
			return new BtsBasicInfo();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT CITYNAME,CITY_ID,BSCNO,BTSNAME,BTSNO,SID,NID,CID,CELLID,PILOT_PN,LATITUDE,LONGITUDE,NETWORKTYPE");
		sb.append("FROM " + DatabaseHelper.BtsInfo +" WHERE ");
		sb.append("SID = " + getStrFormat(sid));
		// 阿郎区域特殊化处理
		sb.append(" AND (NID=" + getStrFormat(nid) + " or SID = '13835' or SID = '13847') ");
		sb.append("AND CID=" + getStrFormat(cid));

		Cursor cursor = m_sqLiteDb.rawQuery(sb.toString(), null);
		cursor.moveToFirst();
		BtsBasicInfo info = GETBTSINFO(cursor);
		cursor.close();
		return info;
	}

	public List<BtsBasicInfo> getBTSInfo(String mapkey, String networkType)
	{
		List<BtsBasicInfo> infos = new ArrayList<BtsBasicInfo>();

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT CITYNAME,CITY_ID,BSCNO,BTSNAME,BTSNO,");
		sb.append("'','','','','',LATITUDE,LONGITUDE,NETWORKTYPE FROM "+ DatabaseHelper.BtsInfo + " WHERE ");
		sb.append("MAP_KEY =" + getStrFormat(mapkey) + " AND NETWORKTYPE = " + getStrFormat(networkType));

		try
		{
			Cursor cursor = m_sqLiteDb.rawQuery(sb.toString(), null);
			cursor.moveToFirst();
			while (true)
			{
				infos.add(GETBTSINFO(cursor));
				if (!cursor.moveToNext())
					break;
			}
			cursor.close();	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return infos;
	}

	public BtsBasicInfo GETBTSINFO(Cursor cursor)
	{
		BtsBasicInfo info = new BtsBasicInfo();
		if (!cursor.isBeforeFirst() && !cursor.isAfterLast())
		{
			info.m_strCityName = cursor.getString(0);
			info.m_strCityID = cursor.getString(1);
			info.m_strBscNo = cursor.getString(2);
			info.m_strBtsName = cursor.getString(3);
			info.m_strBtsNo = cursor.getString(4);
			info.m_strSID = cursor.getString(5);
			info.m_strNID = cursor.getString(6);
			info.m_strCID = cursor.getString(7);
			info.m_strCellId = cursor.getString(8);
			info.m_strPN = cursor.getString(9);
			info.m_dLat = cursor.getDouble(10);
			info.m_dLon = cursor.getDouble(11);
			info.m_strDevType = cursor.getString(12);
		}
		return info;
	}
*/
	//-----------------------------------------------------------------------------------------
	/**
	 * 功能: 删除表记录
	 * 参数:strTable: 表名
	 * 		strWhere: where条件
	 * 		whereArgs: where参数
	 * 返回值:
	 * 说明:
	 */
	public void ClearTable(String strTable,String strWhere,String[] whereArgs)
	{
		m_sqLiteDb.delete(strTable, strWhere, whereArgs);
	}
	
	public void ClearData()
	{
		m_sqLiteDb.delete(DatabaseHelper.BtsInfo, null, null);
	}

	public void ClearData(String city)
	{
		m_sqLiteDb.delete(DatabaseHelper.BtsInfo, "CITYNAME = " + getStrFormat(city), null);
	}

	public void beginTransaction()
	{
		m_sqLiteDb.beginTransaction();
	}

	public void setTransactionSuccessful()
	{
		m_sqLiteDb.setTransactionSuccessful();
	}

	public void endTransaction()
	{
		m_sqLiteDb.endTransaction();
	}

	public void closeDb()
	{
		m_sqLiteDb.close();
	}
	
	/**
	 * 删除没上传成功的测试结果
	 */
	public void deleteLocal(long testStartTime)
	{
		m_sqLiteDb.execSQL("DELETE FROM TB_Test_RESULT WHERE TESTSTARTTIME=" + testStartTime);
	}
	
	//格式化查询字符串
	public static String getStrFormat(String strVal)
	{
		return ("'" + strVal + "'");
	}
	
	//执行SQL语句
	public void exeSql(String strSql)
	{
		try
		{
			m_sqLiteDb.execSQL(strSql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 功能:	获取指定列的值,返回字符串
	 * 参数:	cursor: 
	 * 		nIndex: 索引
	 * 返回值:
	 * 说明:
	 */
	@SuppressLint("NewApi")
	public static String getCursorVal(Cursor cursor,int nIndex)
	{
		String strRet = "";
		
		if(SDKVer < 11)
		{
			strRet = cursor.getString(nIndex);
			return strRet;
		}
			
		try
		{
			switch(cursor.getType(nIndex))
			{
			case Cursor.FIELD_TYPE_STRING:
				strRet = cursor.getString(nIndex);
				break;
			case Cursor.FIELD_TYPE_INTEGER:
				strRet = cursor.getLong(nIndex) + "";
				break;
			case Cursor.FIELD_TYPE_FLOAT:
				strRet = cursor.getFloat(nIndex) + "";
				break;
			case Cursor.FIELD_TYPE_BLOB:
				break;
			case Cursor.FIELD_TYPE_NULL:
				break;
			default:
				break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return strRet;
	}
}
