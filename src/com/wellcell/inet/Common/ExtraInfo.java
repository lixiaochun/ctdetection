package com.wellcell.inet.Common;

//跳转所需的key
public class ExtraInfo
{
	public static final String Extra_Url = "ApkUrl";	//更新url
	
	//任务列表
	public static final int REQUESTCODE_SEL_TASK = 1;
	public static final String Extra_TaskInfo = "TaskInfo";
	public static final String Extra_Module = "Module";		//上一级module
	
	//自动测试
	public static final String Extra_Config = "config";		//配置
	public static final String Extra_ServiceStatus = "serviceStatus";	//服务状态
	public static final String Extra_TaskLog = "taskLog";	//过程日志
	
	//语音测试
	public static final String Action_NotifyStateChanged = "Action_NotifyStateChanged";	//广播action
	public static final String Extra_State = "Extra_State";
	public static final String Extra_HappenTime = "Extra_HappenTime";

	public static final String Extra_city = "BaiduMapActivity.City";	//离线地图城市名称

}
