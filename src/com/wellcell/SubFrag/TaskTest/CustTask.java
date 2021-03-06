package com.wellcell.SubFrag.TaskTest;

import com.wellcell.inet.TaskList.TaskInfo;


//大众版测试--业务测试列表/结果
public class CustTask
{
	private String m_strTaskID; //业务ID
	private boolean m_bChecked = true; //是否选中
	private String m_strTask; //业务名称
	private String m_strAvgSpeed = "-"; //平均速率
	private String m_strSize = "-"; //总流量
	private String m_strDelay = "-"; //时延

	public CustTask(String strTaskName)
	{
		m_strTask = strTaskName;
	}

	public CustTask(TaskInfo taskInfo)
	{
		if (taskInfo != null)
		{
			m_strTaskID = taskInfo.m_strTaskID;
			m_bChecked = taskInfo.m_bChecked;
			
			if(taskInfo.m_strTaskType.equals("WEBSITE"))	//网页
			{
				m_strTask = taskInfo.m_strTaskName;
				m_strTask = m_strTask.replace("网页测试", "");
				m_strTask = m_strTask.replace("大众版-", "");
				m_strTask = "网页:" + m_strTask;
			}
			else if(taskInfo.m_strTaskType.equals("FTP"))
			{
				m_strTask = taskInfo.m_strTaskName;
				if(m_strTask.contains("下载"))
					m_strTask = "FTP:下载";
				else if(m_strTask.contains("上传"))
					m_strTask = "FTP:上传";
			}
			else if(taskInfo.m_strTaskType.equals("HTTP"))
			{
				m_strTask = taskInfo.m_strTaskName;
				if(m_strTask.contains("下载"))
					m_strTask = "HTTP:下载";
				else if(m_strTask.contains("上传"))
					m_strTask = "HTTP:上传";
			}
		}
	}
	
	//初始化
	public void Init()
	{
		m_strAvgSpeed = "-"; //平均速率
		m_strSize = "-"; //总流量
		m_strDelay = "-"; //时延
	}

	public String getTaskID()
	{
		return m_strTaskID;
	}
	
	public String getTaskName()
	{
		return m_strTask;
	}

	public boolean IsChecked()
	{
		return m_bChecked;
	}
	
	public void setChecked(boolean bChecked)
	{
		 m_bChecked = bChecked;
	}

	public String getAvgSpeed()
	{
		return m_strAvgSpeed;
	}

	public void setAvgSpeed(String strAvgSpeed)
	{
		this.m_strAvgSpeed = strAvgSpeed;
	}

	public String getSize()
	{
		return m_strSize;
	}

	public void setSize(String strSize)
	{
		this.m_strSize = strSize;
	}

	public String getDelay()
	{
		return m_strDelay;
	}

	//设置时延
	public void setDelay(String strDelay)
	{
		this.m_strDelay = strDelay;
	}
}
