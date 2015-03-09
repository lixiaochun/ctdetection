package com.wellcell.inet.Task;

import java.lang.reflect.Field;

import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.wellcell.inet.DataProvider.PhoneDataProvider;
import com.wellcell.inet.Database.LocalCache;
import com.wellcell.inet.Web.WebUtil;

//评分准则
public class TaskRankPar
{
	//缓存key
	private static String RankParLte = "RankPar_LTE";
	private static String RankParCdma = "RankPar_CDMA";
	private static String RankParUnknow = "RankPar_Unknow";
	
	//速率1
	public int SpeedLow1;	//下限
	public int SpeedUp1;	//上限
	public int SpeedVal1;	//比重
	//速率2
	public int SpeedLow2;
	public int SpeedUp2;
	public int SpeedVal2;
	//速率3
	public int SpeedLow3;
	public int SpeedUp3;
	public int SpeedVal3;
	//速率4
	public int SpeedLow4;
	public int SpeedUp4;
	public int SpeedVal4;
	//速率5
	public int SpeedLow5;
	public int SpeedUp5;
	public int SpeedVal5;

	//时延1
	public int DelayLow1;
	public int DelayUp1;
	public int DelayVal1;
	//时延2
	public int DelayLow2;
	public int DelayUp2;
	public int DelayVal2;
	//时延3
	public int DelayLow3;
	public int DelayUp3;
	public int DelayVal3;
	//时延4
	public int DelayLow4;
	public int DelayUp4;
	public int DelayVal4;
	//时延5
	public int DelayLow5;
	public int DelayUp5;
	public int DelayVal5;

	//成功率1
	public int DropLinkLow1;
	public int DropLinkUp1;
	public int DropLinkVal1;
	//成功率2
	public int DropLinkLow2;
	public int DropLinkUp2;
	public int DropLinkVal2;
	//成功率3
	public int DropLinkLow3;
	public int DropLinkUp3;
	public int DropLinkVal3;
	//成功率4
	public int DropLinkLow4;
	public int DropLinkUp4;
	public int DropLinkVal4;
	//成功率5
	public int DropLinkLow5;
	public int DropLinkUp5;
	public int DropLinkVal5;
	//-----------------------------------------------
	//各业务项速率比重
	public int SpeedWeb;
	public int SpeedFtp;
	public int SpeedWeibo;
	public int SpeedPing;
	public int SpeedDial;
	public int SpeedVideo;

	//各项业务时延比重
	public int DelayWeb;
	public int DelayFtp;
	public int DelayWeibo;
	public int DelayPing;
	public int DelayDial;
	public int DelayVideo;

	//各项业务成功率比重
	public int DropLinkWeb;
	public int DropLinkFtp;
	public int DropLinkWeibo;
	public int DropLinkPing;
	public int DropLinkDial;
	public int DropLinkVideo;
	//------------------------------------------
	//总评价等级
	public int SumLow1;
	public int SumUp1;
	public String SumVal1;
	public int SumLow2;
	public int SumUp2;
	public String SumVal2;
	public int SumLow3;
	public int SumUp3;
	public String SumVal3;
	public int SumLow4;
	public int SumUp4;
	public String SumVal4;
	public int SumLow5;
	public int SumUp5;
	public String SumVal5;

	//各业务总分比重
	public int SumWeb = 1;
	public int SumFtp = 1;
	public int SumWeibo = 1;
	public int SumPing = 1;
	public int SumDial = 1;
	public int SumVideo = 1;

	public TaskRankPar(String json)
	{
		try
		{
			JSONObject obj = new JSONObject(json);

			Field[] fields = getClass().getFields();
			for (Field field : fields)
			{
				try
				{
					field.set(this, obj.get(field.getName()));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	//获取对应网络的评分标准的名称
	public static String getRankName(Context context)
	{
		int networkType = PhoneDataProvider.getNetworkType(context);
		switch (networkType)
		{
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return RankParCdma;
		case TelephonyManager.NETWORK_TYPE_LTE:
			return RankParLte;
		default:
			return RankParUnknow;
		}
	}

	public static TaskRankPar getInsant(Context context)
	{
		String strKey = getRankName(context);
		// 从缓存读取
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TaskRank, Context.MODE_PRIVATE);
		String strJson = LocalCache.getValueFromSharePreference(sp, strKey);

		int nCount = 3;
		while (nCount-- > 0)
		{
			if (strJson != null && strJson.length() > 0)
				break;
			
			strJson = WebUtil.getTaskRankPar(strKey);	// 从服务器获取
			//strJson = DataEncrypt.getUngzDecrypt_DES(strJson, DataEncrypt.KEYS[6]);	//解密
			if (strJson != null && strJson.length() > 0)
			{
				// 保存
				SharedPreferences.Editor editor = sp.edit();
				LocalCache.saveValueToSharePreference(editor, strKey, LocalCache.DAY, strJson);
				editor.commit();
				break;
			}
		}

		return new TaskRankPar(strJson);
	}

	//清空缓存
	public static void clear(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_TaskRank, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
	//==================================================================================================
	//获取评分等级
	public String getTaskRankName(int value)
	{
		Log.d("", "TaskValue:" + value);
		if (value >= SumLow1 && value < SumUp1)
			return SumVal1;
		else if (value >= SumLow2 && value < SumUp2)
			return SumVal2;
		else if (value >= SumLow3 && value < SumUp3)
			return SumVal3;
		else if (value >= SumLow4 && value < SumUp4)
			return SumVal4;
		else if (value >= SumLow5 && value < SumUp5)
			return SumVal5;
		else
			return "UNKNOWN";
	}

	//速率得分
	public int getSpeedValue(double value)
	{
		if (value >= SpeedLow1 && value < SpeedUp1)
			return SpeedVal1;
		else if (value >= SpeedLow2 && value < SpeedUp2)
			return SpeedVal2;
		else if (value >= SpeedLow3 && value < SpeedUp3)
			return SpeedVal3;
		else if (value >= SpeedLow4 && value < SpeedUp4)
			return SpeedVal4;
		else if (value >= SpeedLow5 && value < SpeedUp5)
			return SpeedVal5;
		else
			return 0;
	}

	//时延得分
	public int getDelayValue(double value)
	{
		if (value >= DelayLow1 && value < DelayUp1)
			return DelayVal1;
		else if (value >= DelayLow2 && value < DelayUp2)
			return DelayVal2;
		else if (value >= DelayLow3 && value < DelayUp3)
			return DelayVal3;
		else if (value >= DelayLow4 && value < DelayUp4)
			return DelayVal4;
		else if (value >= DelayLow5 && value < DelayUp5)
			return DelayVal5;
		else
			return 0;
	}

	//成功率得分
	public int getSucRateValue(double value)
	{
		if (value >= DropLinkLow1 && value < DropLinkUp1)
			return DropLinkVal1;
		else if (value >= DropLinkLow2 && value < DropLinkUp2)
			return DropLinkVal2;
		else if (value >= DropLinkLow3 && value < DropLinkUp3)
			return DropLinkVal3;
		else if (value >= DropLinkLow4 && value < DropLinkUp4)
			return DropLinkVal4;
		else if (value >= DropLinkLow5 && value < DropLinkUp5)
			return DropLinkVal5;
		else
			return 0;
	}
}
