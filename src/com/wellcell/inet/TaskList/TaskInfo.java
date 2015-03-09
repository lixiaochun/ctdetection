package com.wellcell.inet.TaskList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//测试业务信息
public class TaskInfo
{
	public String m_strTaskID;		//业务ID
	public String m_strTaskType;	//业务类型
	public String m_strTaskName;	//业务名称
	public boolean m_bChecked;		//是否被选中

	public TaskInfo(JSONObject obj)
	{
		try
		{
			this.m_strTaskID = obj.getString("taskID");
			this.m_strTaskType = obj.getString("taskType");
			this.m_strTaskName = obj.getString("taskName");
			try
			{
				this.m_bChecked = obj.getBoolean("isChecked");
			}
			catch (Exception e)
			{
				this.m_bChecked = true;
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public JSONObject toJsonObj()
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("taskID", this.m_strTaskID);
			obj.put("taskType", this.m_strTaskType);
			obj.put("taskName", this.m_strTaskName);
			obj.put("isChecked", this.m_bChecked);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 功能: 解析json并发挥列表
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static List<TaskInfo> getTaskList(String json)
	{
		List<TaskInfo> taskList = new ArrayList<TaskInfo>();
		try
		{
			JSONArray arr = new JSONArray(json);
			for (int i = 0; i < arr.length(); i++)
			{
				TaskInfo taskinfo = new TaskInfo(arr.getJSONObject(i));
				taskList.add(taskinfo);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return taskList;
	}

	/**
	 * 功能: 把列表还原成json格式数据
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String toJsonString(List<TaskInfo> taskList)
	{
		JSONArray arr = new JSONArray();
		for (TaskInfo taskInfo : taskList)
		{
			arr.put(taskInfo.toJsonObj());
		}
		return arr.toString();
	}
}
