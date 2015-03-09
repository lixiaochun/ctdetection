package com.wellcell.inet.SignalTest;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.wellcell.inet.Database.LocalCache;
import com.wellcell.inet.Web.WebUtil;

//小区信号参数
//说明:针对不同的机型类适配其获取的指标参数;
//小区无效,对应的所有配置参数为零
//信号无效,对应的所有配置参数为零
public class SignalStrengthPar
{
	private static String SIGNALPAR = "SignalPar";	//sharedpreference关键字
	//LTE参数
	public int m_nMccPar = 1;
	public int m_nMncPar = 1;
	public int m_nCiPar = 1;
	public int m_nPciPar = 1;
	public int m_nTacPar = 1;
	public double m_dRsrpPar = 1;
	public double m_dRsrqPar = 1;
	public double m_dRssiPar = 1;
	public double m_dSinrPar = 0.1;
	public double m_dCqiPar = 1;
	
	//3G参数
	public int m_nCidPar = 1;
	public int m_nSidPar = 1;
	public int m_nNidPar = 1;
	public double m_dRxPar3G = 1;
	public double m_dEcioPar3G = 0.1;
	public double m_dSnrPar = 1;
	
	//2G参数
	public double m_dRxPar2G = 1;
	public double m_dEcioPar2G = 0.1;
	
	//GSM
	public int m_nCellIDPar = 1;
	public int m_nLacPar = 1;
	public double m_dRxParGsm = 1;
	
	public int m_nErrorRep = 1;	//错误汇报标志,0:不上报,1:上报(默认为1)
	public int m_nDecode = 0;	//解码权限,0:无,1:有(默认为0)
	
	public SignalStrengthPar()
	{
	}

	//解析json字符串
	public SignalStrengthPar(String strJson)
	{
		try
		{
			JSONObject jsonObj = new JSONObject(strJson);
			//LTE
			m_nMccPar = jsonObj.getInt("MCC");
			m_nMncPar = jsonObj.getInt("MNC");
			m_nCiPar = jsonObj.getInt("CI");
			m_nPciPar = jsonObj.getInt("PCI");
			m_nTacPar = jsonObj.getInt("TAC");
			m_dRsrpPar = jsonObj.getDouble("RSRP");
			m_dRsrqPar = jsonObj.getDouble("RSRQ");
			m_dRssiPar = jsonObj.getDouble("RSSI");
			m_dSinrPar = jsonObj.getDouble("RSSNR");
			m_dCqiPar = jsonObj.getDouble("CQI");
			
			//CDMA
			m_nCidPar = jsonObj.getInt("CID");
			m_nSidPar = jsonObj.getInt("SID");
			m_nNidPar = jsonObj.getInt("NID");		
			m_dRxPar3G = jsonObj.getDouble("Rx3G");
			m_dEcioPar3G = jsonObj.getDouble("Ecio3G");
			m_dSnrPar = jsonObj.getDouble("SNR");
			m_dRxPar2G = jsonObj.getDouble("Rx2G");
			m_dEcioPar2G = jsonObj.getDouble("Ecio2G");
			
			//GSM
			m_nCellIDPar = jsonObj.getInt("CellID");
			m_nLacPar = jsonObj.getInt("LAC");
			m_dRxParGsm = jsonObj.getDouble("RxGsm");
			
			m_nErrorRep = jsonObj.getInt("ErrorReport");
			m_nDecode = jsonObj.getInt("Decode");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 功能: 从服务器或本地缓存获取信号强度格式化参数 
	 * 参数: 
	 * 返回值: 
	 * 说明:
	 */
	public static SignalStrengthPar getSignalStrengthFar(Context context)
	{
		// 从缓存获取
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_SignalStrengthFar, Context.MODE_PRIVATE);
		String strJson = LocalCache.getValueFromSharePreference(sp, SIGNALPAR);

		if (strJson == null || strJson.length() == 0)
		{
			int nCount = 3;	//重复次数
			while (nCount-- > 0)
			{
				// 从服务器获取
				strJson = WebUtil.getSignalStrengthPar();

				if (strJson != null && strJson.length() > 0)
					break;
			}

			// 保存到本地
			SharedPreferences.Editor editor = sp.edit();
			LocalCache.saveValueToSharePreference(editor, SIGNALPAR, LocalCache.WEEK, strJson);
			editor.commit();
		}

		return new SignalStrengthPar(strJson);
	}

	//清空缓存
	public static void clear(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(LocalCache.SP_SignalStrengthFar, Context.MODE_PRIVATE);
		sp.edit().clear();
		sp.edit().commit();
	}
}
