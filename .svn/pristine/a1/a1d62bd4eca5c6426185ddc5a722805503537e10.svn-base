package com.wellcell.inet.Common;

import android.app.ProgressDialog;
import android.content.Context;

//进度对话框
public class InetDlg
{
	public static enum DlgType //对话框类型
	{
		eLogin,			//登录信息
		eTaskList,		//等待任务列表
		eTestLoading,	//
		eSignal,		//信号测量
		eGetBts,		//运行数据->获取当前bts
		eBaiduMap, 		//百度地图
		eBtsDown, 		//基站数据下载
		eBtsSave,		//保存基站数据
		eAnteTest		//天馈测量
	}
	
	/**
	 * 功能: 显示对话框
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static ProgressDialog ShowDlg(ProgressDialog dlg,Context context,DlgType type)
	{
		String strTitle = "";	//title
		String strMsg = "";		//msg
		switch (type)
		{
		case eLogin:
			strTitle = "正在发送登陆请求";
			strMsg = "请稍候...";
			break;
		case eTaskList:
			strTitle = "正在配置任务";
			strMsg = "请稍候...\n" + "如果等待过长，参考:\n" + "1、切换APN（CTNET/CTWAP）重试\n" 
					+ "2、直接取消微博类测试";
			break;
		case eTestLoading:
			strTitle = "正在配置任务";
			strMsg = "请稍候...";
			break;
		case eSignal:	//信号测量
			strTitle = "正在获取小区信息";
			strMsg = "请稍候...";
			break;
		case eGetBts:
			strTitle = "正在读取当前基站信息";
			strMsg = "请稍候...";
			break;
		case eAnteTest:
			strTitle = "正在请求开始任务";
			strMsg = "请稍候...";
			break;
		default:
			return null;
		}
		
		try
		{
			if (dlg == null)
			{
				dlg = new ProgressDialog(context);
				dlg.setTitle(strTitle);
				dlg.setMessage(strMsg);
				dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			}
			dlg.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return dlg;
	}
	
	//关闭对话框
	public static void DismissDlg(ProgressDialog dlg)
	{
		try
		{
			if (dlg != null)
				dlg.dismiss();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
 