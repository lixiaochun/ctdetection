package com.wellcell.inet.TaskList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Common.CGlobal.ModuleIndex;
import com.wellcell.inet.Database.LocalCache;
import com.wellcell.inet.Web.WebUtil;

//业务列表及其配置
public class TaskUtil
{
	private static String CQT_taskList = "CQT_TaskList";	//CQT测试类表
	private static String Auto_taskList = "Auto_TaskList";	//定点引爆列表
	private static String Cus_taskList = "Cus_TaskList";	//客户经理测试列表
	private static String Last_taskTime = "LastTaskTime";	//最后测试时间
	private static String Toggle_service = "ServiceToggle";

	public static boolean getServiceToggle(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		return sp.getBoolean(Toggle_service, false);
	}

	public static void setServiceToggle(Context context, boolean isServiceOn)
	{
		/*SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(Toggle_service, isServiceOn);
		editor.commit();

		Intent service = new Intent(context, AutoTestTaskService.class);
		String strLog;
		if (isServiceOn)
		{
			strLog = "打开三重自动测试";
			context.startService(service);
		}
		else
		{
			strLog = "关闭三重自动测试";
			context.stopService(service);
		}
		*/
		//logThread thread = new logThread(context, strLog);
		//new Thread(thread).start();
	}

	// 获取上次测试时间
	public static long getLastTaskTime(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		return sp.getLong(Last_taskTime, 0);
	}

	// 保存测试时间
	public static void saveLastTaskTime(Context context, long taskTime)
	{
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putLong(Last_taskTime, taskTime);
		editor.commit();
	}

	/**
	 * 功能:	获取任务列表
	 * 参数:	context:
	 * 		moduleType: 模块类型
	 * 返回值:任务列表
	 * 说明:
	 */
	public static List<TaskInfo> getTaskList(Context context, ModuleIndex moduleType)
	{
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);

		String strJson = "";
		int nType = 0;		//模块类型
		switch (moduleType)
		{
		case eCQT:	//CQT
			strJson = LocalCache.getValueFromSharePreference(sp, CQT_taskList);
			nType = 0;
			break;
		case eDT:
			nType = 1;
			break;
		case eAuto:	//定点引爆
			strJson = LocalCache.getValueFromSharePreference(sp, Auto_taskList);
			nType = 2;
			break;	
		case eCust:	//客户经理测试
			strJson = "[{\"isChecked\":\"true\",\"taskID\":\"15\",\"taskName\":\"大众版-FTP下载测试\",\"taskType\":\"FTP\"}]";
			//,{\"isChecked\":\"true\",\"taskID\":\"20\",\"taskName\":\"大众版-FTP上传测试\",\"taskType\":\"FTP\"}
			//LocalCache.getValueFromSharePreference(sp, Cus_taskList);
			nType = 3;
			break;
		case eProbback:	//问题热点反馈
			nType = 4;
			break;
		default:
			break;
		}

		//从服务器获取
		if (strJson.length() < 3) //排除"[]"
		{
			int nCount = 3;	//重复次数
			while (strJson.length() < 3 && nCount-- > 0)
			{
				strJson = WebUtil.getTaskList(context,nType, ""); //服务器获取
				if (strJson != null && strJson.length() > 0) //获取成功
				{
					saveTaskList(context, strJson, moduleType); //保存到本地
					break;
				}
				else
					CGlobal.Sleep(1000);	//间隔
			}
		}
		
		List<TaskInfo> listTasks = new ArrayList<TaskInfo>();
		if(strJson.length() == 0)
			return listTasks;
		
		//解析
		JSONArray arrJson;
		try
		{
			arrJson = new JSONArray(strJson);
			for (int i = 0; i < arrJson.length(); i++)
			{
				listTasks.add(new TaskInfo(arrJson.getJSONObject(i)));
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return listTasks;
	}

	/**
	 * 功能: 获取指定业务的配置参数
	 * 参数: taskID: 业务ID
	 * 返回值:
	 * 说明:
	 */
	public static String getTaskPara(Context context, String taskID)
	{
		//本地获取
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		String strPar = LocalCache.getValueFromSharePreference(sp, taskID);
		
		if (strPar.length() == 0) //服务器获取
		{
			int nCount = 3;
			while (strPar.length() == 0 && nCount-- > 0)
			{
				strPar = WebUtil.getTaskPara(taskID); //请求获取配置参数

				if (strPar.length() == 0) //失败
					CGlobal.Sleep(2000);
			}

			//保存配置
			SharedPreferences.Editor editor = sp.edit();
			LocalCache.saveValueToSharePreference(editor, taskID, LocalCache.DAY, strPar);
			editor.commit();
		}

		return strPar;
	}

	/**
	 * 功能: 保存指定模块的任务列表
	 * 参数:	json: 列表json
	 * 		type: 模块类型
	 * 返回值:
	 * 说明:
	 */
	public static void saveTaskList(Context context, List<TaskInfo> taskList, ModuleIndex type)
	{
		JSONArray arr = new JSONArray();
		for (TaskInfo info : taskList)
		{
			arr.put(info.toJsonObj());
		}
		saveTaskList(context, arr.toString(), type);
	}

	public static void saveTaskList(Context context, String json, ModuleIndex type)
	{
		// 保存到SharePreferences
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		switch (type)
		{
		case eCQT:
			LocalCache.saveValueToSharePreference(editor, CQT_taskList, LocalCache.DAY, json);
			break;
		case eDT:
			break;
		case eAuto:
			LocalCache.saveValueToSharePreference(editor, Auto_taskList, LocalCache.DAY, json);
			break;
		case eCust: //客户经理
			LocalCache.saveValueToSharePreference(editor, Cus_taskList, LocalCache.DAY, json);
			break;
		case eProbback:
			break;
		default:
			break;
		}
		editor.commit();
	}
	
	// 清空缓存--任务列表
	public static void clearTaskList(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 功能: 从服务器获取指定模块的所有业务配置参数
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
/*	public static void getTaskParaFromServer(Context context, ModuleIndex type)
	{
		List<TaskInfo> taskList = getTaskList(context, type);

		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TASK, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		for (TaskInfo taskInfo : taskList)
		{
			// 获取默认参数
			String paraJson = "";
			int retryCount = 3;
			while (paraJson.length() == 0 && retryCount > 0)
			{
				paraJson = WebUtil.getTaskPara(taskInfo.m_strTaskID);

				retryCount--;
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			LocalCache.saveValueToSharePreference(editor, taskInfo.m_strTaskID, Tools.DAY, paraJson);
		}

		editor.commit();
	}*/
}
