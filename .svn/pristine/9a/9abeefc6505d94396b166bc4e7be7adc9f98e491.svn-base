package com.wellcell.inet.Database;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/*缓存信息
 * 说明: 以Values和ExpireData的json保存信息
 */
public class LocalCache 
{
	static final String tag = "LocalCache";
	
	//有效期
	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	public static final long WEEK = DAY * 7;
	public static final long MONTH = DAY * 30;
	public static final long YEAR = MONTH * 12;

	public static final String[] cacheNames = new String[] { "如翼热点信息","信号强度格式", "三重测试评价标准", 
		"三重任务配置", "用户权限信息","三重自动测试配置", "天馈测量BSC列表","告警信息缓存", "登陆用户名、密码", "性能图表配置", 
		"自定义基站查询历史", "华为天馈单板信息" };

	//-----------------------------------------------------------------------------------------------
	//所有SharedPreferences的name
	public static final String SP_USER = "User";				// 登陆名字记录
	public static final String SP_LoginInfo = "LoginInfo";		// 登陆信息
	public static final String SP_HotInfo = "HotInfo";			// 如翼热点信息
	public static final String SP_TASK_CUST = "TASK";			//客户经理测试任务列表
	public static final String SP_SignalStrengthFar = "SignalStrengthFar";	// 信号强度解析格式
	public static final String SP_ChartConfig = "ChartConfig";				// 性能图表配置
	public static final String SP_TASK = "TASK";				//所有测试任务
	public static final String SP_TaskRank = "TaskRank";		//测试评价体系
	public static final String SP_SignalAction = "SignalAction";	//信号测试最后操作
	public static final String SP_UpdateCount = "UpdateCount";	//取消的升级次数
	public static final String SP_Setting = "SettingPar";		//设置配置信息

	// 三重自动测试配置
	public static final String SP_AutoTestConfig = "AutoTestConfig";
	// 天馈BSC列表
	public static final String SP_LMT = "LMT";
	// 告警的缓存
	public static final String SP_ALARM = "alarm_cache";
	
	
	// 自定义基站搜索历史
	public static final String SP_SearchHistory = "Search";
	// 华为天馈里面的单板信息
	public static final String SP_BTSBrdInfoHW = "BTSBrdInfoHW";
	//---------------------------------------Key值-------------------------------------------------
	public static final String Key_UpdataCount = "UpdateCount";	//取消更新次数

	public static final String[] cacheSpNames = new String[] { SP_HotInfo,SP_SignalStrengthFar,
		SP_TaskRank, SP_TASK, SP_LoginInfo,SP_AutoTestConfig, SP_LMT, SP_ALARM, SP_USER, SP_ChartConfig,
		SP_SearchHistory, SP_BTSBrdInfoHW };

	public String m_strValue;
	public Date m_dateExpire;	//有效期时间
	
	public LocalCache(String json) 
	{
		try
		{
			JSONObject obj = new JSONObject(json);
			m_strValue = obj.getString("value");
			m_dateExpire = new Date(obj.getLong("expireDate"));
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}

	public LocalCache(String value, long expiredTime)
	{
		this.m_strValue = value;
		this.m_dateExpire = new Date(new Date().getTime() + expiredTime);
	}

	public static void clear(Context context, String spName)
	{
		Log.d(tag, "清理" + spName);
		SharedPreferences sp = context.getSharedPreferences(spName,	Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	//判断缓存数据是否过期
	public boolean isExpire() 
	{
		if (m_dateExpire.getTime() < new Date().getTime()) 
			return true;
		else 
			return false;
	}

	//--------------------------用户登录缓存-------------------------------------------------
	/**
	 * 功能:保存帐号密码 
	 * 参数:	strUser: 用户名 
	 * 		strPwd: 密码
	 * 		bChecked: 是否保存
	 *  返回值: 说明:
	 */
	public static void saveLoginInfo(Context context,String strUser, String strPwd,boolean bChecked)
	{
		try
		{
			SharedPreferences spUserinfo = context.getSharedPreferences(LocalCache.SP_USER, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = spUserinfo.edit();
			editor.putString(LocalCache.SP_USER, strUser);		//帐号
			editor.putString("Password", strPwd);				//密码
			editor.putBoolean("isChecked", bChecked);			//是否保存
			editor.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	//------------------------------------------------------------------------------------
	/**
	 * 功能:获取指定SharePreference中指定Key的值
	 * 参数:	context: 
	 * 		strSPName: SharePreference名称
	 * 		key:  
	 * 返回值:
	 * 说明:
	 */
	public static String getKeyValFromLocalCache(Context context,String strSPName,String key)
	{
		SharedPreferences sp = context.getSharedPreferences(strSPName, Activity.MODE_PRIVATE);
		return getValueFromSharePreference(sp, key);
	}
	/**
	 * 功能:从sharepreference读取指定key的value
	 * 参数: sp:
	 * 		key: 
	 * 返回值:
	 * 说明:
	 */
	public static String getValueFromSharePreference(SharedPreferences sp,String key) 
	{
		String strData = sp.getString(key, "");
		if (strData.length() > 0)
		{
			try 
			{
				LocalCache cache = new LocalCache(strData);

				//是否过期
				if (cache.isExpire())
				{
					Log.d(tag, String.format("%1s，已经过期", key));
					return "";
				} 
				else 
				{
					//Log.d(tag,String.format("%1s，过期时间：%2s", key,
					//				Tools.sdf.format(cache.m_dateExpire)));
					return cache.m_strValue;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
		}
		Log.d(tag, String.format("%1s，没有缓存", key));
		return "";
	}

	//------------------------------------------------------------------------------------------
	public static void saveKeyValToLocalCache(Context context,String strSPName,String key,
			String strValue,long expireTime)
	{
		try
		{
			SharedPreferences sp = context.getSharedPreferences(strSPName, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			saveValueToSharePreference(editor, key, expireTime, strValue);
			editor.commit();	//提交修改
		}
		catch (Exception e)
		{
		}
		
	}
	/**
	 * 功能:保存数据到缓存
	 * 参数:editor:
	 * 		key:
	 * 		expireTime: 有效期
	 * 		strValue: json数据
	 * 返回值:
	 * 说明:
	 */
	public static void saveValueToSharePreference(SharedPreferences.Editor editor, String key, 
			long expireTime,String strValue) 
	{
		LocalCache cache = new LocalCache(strValue, expireTime);
		editor.putString(key, cache.toJson());
	}

	public String toJson() 
	{
		try 
		{
			JSONObject obj = new JSONObject();
			obj.put("value", m_strValue);
			obj.put("expireDate", m_dateExpire.getTime());
			return obj.toString();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			return "";
		}
	}
}
