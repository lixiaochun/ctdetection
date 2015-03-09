package com.wellcell.inet.DataProvider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import android.R.string;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.wellcell.inet.SignalTest.TelStrengthInfo;

public class PhoneDataProvider
{
	static TelephonyManager m_telMag;
	static ConnectivityManager m_connMag;
	static PowerManager m_powerMag; // 电源管理
	static AudioManager m_audioMag; // 音频控制
	static WakeLock m_wakeLock; // 休眠锁
	static CellLocation m_cellLoc;

	static float m_fBatteryLev = 0; // 电源

	public static enum NetWorkType // 网络类型
	{
		eUnknow, // 未知
		eWifi, // wifi
		eCDMA, // 电信2G
		eEDGE, // GSM
		eEVDO, // 电信3G
		eGPRS, // GPRS
		eLTE, // LTE
		eHSDPA, eHSPA, eHSUPA, eUMTS, eHSPAex, eIDen
	}

	//CPU/内存 占用率信息
	public static class CMDetail
	{
		private long lCpuTotal; //CPU总运行时间
		private long lCpuIdel; //CPU空闲时间

		private long lMemTotal; //内存总量
		private long lMemFree; //空闲内存

		public CMDetail(long total, long idel)
		{
			lCpuTotal = total;
			lCpuIdel = idel;
		}

		//更新内存信息
		public void updateMemInfo(long total, long free)
		{
			lMemTotal = total;
			lMemFree = free;
		}

		//获取占用率
		public double getUseage(CMDetail obj)
		{
			double dRet = 0;
			long lSpece = lCpuTotal - obj.lCpuTotal; //CPU时间间隔
			if (lSpece != 0)
				dRet = 100.0 * (lSpece - (lCpuIdel - obj.lCpuIdel)) / lSpece;

			return dRet;
		}

		//获取内存利用率
		public double getMenUsage()
		{
			if (lMemTotal != 0)
				return 100.0 * (lMemTotal - lMemFree) / lMemTotal;

			return 0;
		}
	};

	//获取CPU时间信息
	public static CMDetail getCpuTimeInfo()
	{
		String[] strInfos = null;
		try
		{
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1024);
			String load = bfReader.readLine();
			bfReader.close();
			strInfos = load.split(" ");
		}
		catch (IOException ex)
		{
			return null;
		}
		long lTotal = 0;
		long lIdle = 0;
		try
		{
			lTotal = Long.parseLong(strInfos[2]) + Long.parseLong(strInfos[3]) + Long.parseLong(strInfos[4]) + Long.parseLong(strInfos[6]) + Long.parseLong(strInfos[5]) + Long.parseLong(strInfos[7]) + Long.parseLong(strInfos[8]);
			lIdle = Long.parseLong(strInfos[5]);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
		return new CMDetail(lTotal, lIdle);
	}

	public static CMDetail getMemInfo(CMDetail detail)
	{
		String[] totalInfos = null;
		String[] freeInfos = null;
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/meminfo")), 1024);
			String load = reader.readLine();
			totalInfos = load.split(" ");
			load = reader.readLine();
			freeInfos = load.split(" ");
			reader.close();
		}
		catch (IOException ex)
		{
		}

		long totalTmp = 0;
		long freeTmp = 0;
		try
		{
			for (String str : totalInfos)
			{
				if (str.contains("M") || str.contentEquals("") || str.contains("k"))
					continue;
				else
				{
					totalTmp = Long.parseLong(str) / 1024;
					break;
				}
			}

			for (String str : freeInfos)
			{
				if (str.contains("M") || str.contentEquals("") || str.contains("k"))
					continue;
				else
				{
					freeTmp = Long.parseLong(str) / 1024;
					break;
				}
			}

		}
		catch (ArrayIndexOutOfBoundsException e)
		{
		}
		finally
		{
			if (freeTmp != 0 && totalTmp != 0 && detail != null)
			{
				detail.updateMemInfo(totalTmp, freeTmp);
			}
		}

		return detail;
	}

	//=======================================================================
	
	//获取当前版本信息
	public static String getCurVerInfo(Context context)
	{
		String strRet = "";
		try
		{
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			strRet = pi.versionName;
		}
		catch (Exception e)
		{
		}
		return strRet;
	}
	
	//获取当前版本VersionCode
	public static int getCurVerCode(Context context)
	{
		int nVerCode = 0;
		try
		{
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			nVerCode = pi.versionCode;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return nVerCode;
	}

	/**
	 * 功能: 是否唤醒屏幕
	 * 参数:	bWakeScreen: 是否唤醒
	 * 返回值:
	 * 说明:
	 */
	public static void ScreenWakeLock(Context context, boolean bWakeScreen)
	{
		if (m_wakeLock == null)
		{
			Log.d("", "Acquiring wake lock");
			PowerManager pm = getPowerManager(context);
			if (!bWakeScreen)
			{
				// 不唤醒屏幕
				m_wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getClass().getCanonicalName());
			}
			else
			{
				// 唤醒屏幕
				m_wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, context.getClass().getCanonicalName());
			}
			m_wakeLock.acquire();
		}
	}

	public static void releaseWakeLock()
	{
		if (m_wakeLock != null && m_wakeLock.isHeld())
		{
			Log.d("", "Release wake lock");
			m_wakeLock.release();
			m_wakeLock = null;
		}
	}

	public static TelephonyManager getTelephonyManager(Context context)
	{
		if (m_telMag == null)
			m_telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return m_telMag;
	}

	public static AudioManager getAudioManager(Context context)
	{
		if (m_audioMag == null)
			m_audioMag = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		return m_audioMag;
	}

	public static PowerManager getPowerManager(Context context)
	{
		if (m_powerMag == null)
			m_powerMag = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

		return m_powerMag;
	}

	public static ConnectivityManager getConnectivityManager(Context context)
	{
		if (m_connMag == null)
			m_connMag = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		return m_connMag;
	}

	public static String getDataActivityDirection(int state)
	{
		switch (state)
		{
		case TelephonyManager.DATA_ACTIVITY_DORMANT:
			return "DATA_ACTIVITY_DORMANT";
		case TelephonyManager.DATA_ACTIVITY_IN:
			return "DATA_ACTIVITY_IN";
		case TelephonyManager.DATA_ACTIVITY_INOUT:
			return "DATA_ACTIVITY_INOUT";
		case TelephonyManager.DATA_ACTIVITY_NONE:
			return "DATA_ACTIVITY_NONE";
		case TelephonyManager.DATA_ACTIVITY_OUT:
			return "DATA_ACTIVITY_OUT";
		default:
			return "";
		}
	}

	public static String getDataState(Context context)
	{
		int state = getTelephonyManager(context).getDataState();
		return getDataStateString(state);
	}

	public static String getDataStateString(int state)
	{
		switch (state)
		{
		case TelephonyManager.DATA_CONNECTED:
			return "DATA_CONNECTED";
		case TelephonyManager.DATA_CONNECTING:
			return "DATA_CONNECTING";
		case TelephonyManager.DATA_DISCONNECTED:
			return "DATA_DISCONNECTED";
		case TelephonyManager.DATA_SUSPENDED:
			return "DATA_SUSPENDED";
		default:
			return "";
		}
	}

	public static String getCallState(Context context)
	{
		int state = getTelephonyManager(context).getCallState();
		return getCallStateString(state);
	}

	public static String getCallStateString(int state)
	{
		switch (state)
		{
		case TelephonyManager.CALL_STATE_IDLE:
			return "CALL_STATE_IDLE";
		case TelephonyManager.CALL_STATE_OFFHOOK:
			return "CALL_STATE_OFFHOOK";
		case TelephonyManager.CALL_STATE_RINGING:
			return "CALL_STATE_RINGING";
		default:
			return "";
		}
	}

	public static List<NeighboringCellInfo> getNeighboringCellInfo(Context context)
	{
		return getTelephonyManager(context).getNeighboringCellInfo();
	}

	public static int getCellID(Context context)
	{
		CdmaCellLocation location = (CdmaCellLocation) getTelephonyManager(context).getCellLocation();
		return location.getBaseStationId();
	}

	public static boolean getScreenState(Context context)
	{
		return getPowerManager(context).isScreenOn();
	}

	//-------------------------------------------------------------------
	/**
	 * 功能: 获取运营商类型
	 * 参数:	context: 
	 * 返回值: 0: 中国电信
	 * 		 1: 中国移动
	 * 		 2: 中国联通
	 * 说明:
	 */
	public static int getOperatorType(Context context)
	{
		int nOperator = 0;
		String strOperator = getOperatorName(context);
		strOperator = strOperator.toLowerCase(); //转小写
		if (strOperator.contains("电信") || strOperator.contains("telecom"))
			nOperator = 0;
		else if (strOperator.contains("移动") || strOperator.contains("cmcc"))
			nOperator = 1;
		else if (strOperator.contains("联通") || strOperator.contains("unicom"))
			nOperator = 2;

		return nOperator;
	}

	//获取运营商名称
	public static String getOperatorName(Context context)
	{
		try
		{
			return getTelephonyManager(context).getNetworkOperatorName();
		}
		catch (Exception e)
		{
			return "ERROR";
		}
	}

	/**
	 * 功能: 获取运营商
	 * 参数:
	 * 返回值:0：电信，1：移动，2：联通
	 * 说明:
	 */
	public static int getOperator(Context context)
	{
		int nRet = 0;
		String strImsi = getIMSI(context);
		if (strImsi.startsWith("46000") || strImsi.startsWith("46002")) //移动
			nRet = 1;
		else if (strImsi.startsWith("46001")) //联通
			nRet = 2;
		else if (strImsi.startsWith("46003")) //电信
			nRet = 0;

		return nRet;
	}

	//获取手机号码
	public static String getPhoneNum(Context context)
	{
		try
		{
			return getTelephonyManager(context).getLine1Number();
		}
		catch (Exception e)
		{
			return "ERROR";
		}
	}

	//获取IMSI
	public static String getIMSI(Context context)
	{
		String strImsi = "ERROR";
		try
		{
			strImsi = getTelephonyManager(context).getSubscriberId();
		}
		catch (Exception e)
		{
		}

		if (strImsi == null)
			strImsi = "460030810820086";

		return strImsi;
	}

	//获取设备ID(IMEI)
	public static String getIMEI(Context context)
	{
		try
		{
			return getTelephonyManager(context).getDeviceId();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR";
		}
	}

	//获取手机型号
	public static String getModel()
	{
		try
		{
			return Build.MODEL;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR";
		}
	}

	public static String getManufacturer()
	{
		try
		{
			return Build.MANUFACTURER;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR";
		}
	}

	//获取系统版本号
	public static String getSystemName()
	{
		String strRet = "ERROR";
		try
		{
			strRet = "Android " + Build.VERSION.RELEASE;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return strRet;
	}

	//获取android SDK版本号
	public static String getSystemSDK()
	{
		try
		{
			return Build.VERSION.SDK;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR";
		}
	}

	//获取系统rom版本号-->内部版本
	public static String getRomVersion()
	{
		try
		{
			return Build.DISPLAY;
		}
		catch (Exception e)
		{
			return "ERROR";
		}
	}
	
	//获取内核版本
	public static String getKernelVersion()
	{
		return System.getProperty("os.version");
	}

	//获取基带版本
	public static String getBaseBand()
	{
		String strRet = "";
		try
		{
			Class cl = Class.forName("android.os.SystemProperties");
			Object invoker = cl.newInstance();
			Method m = cl.getMethod("get", new Class[] { String.class, String.class });
			Object result = m.invoke(invoker, new Object[] { "gsm.version.baseband", "no message" });
			strRet = (String) result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return strRet;
	}
	
	public static String getSystemVersion(Context context)
	{
		return getTelephonyManager(context).getDeviceSoftwareVersion();
	}

	/*	public static CellInfo getFirstCellInfo(Context context)
		{
			return getTelephonyManager(context).getAllCellInfo().get(0);
		}
	*/
	public static CdmaCellLocation getCdmaCellLocation(Context context)
	{
		try
		{
			return (CdmaCellLocation) getTelephonyManager(context).getCellLocation();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isNetworkAvaiable(Context context)
	{
		NetworkInfo ni = getConnectivityManager(context).getActiveNetworkInfo();
		if (ni == null)
			return false;

		return ni.isAvailable();
	}

	/**
	 * 功能: 获取当前连接的小区信息
	 * 参数:	context:
	 * 		nType: 网络类型 ;0:LTE,1:CDMA,2:GSM
	 * 返回值:小区信息数组(LTE: MNC,MCC,CI,PCI,LAC; CDMA: CID,SID,NID; GSM: CellID,Lac)
	 * 说明:
	 */
	/*	public static int[] getCellInfo(Context context, int nType)
		{
			int[] nCellInfo = { -1, -1, -1, -1, -1 }; //初始化为-1
			String strName;
			try
			{
				if (Build.VERSION.SDK_INT < 17)
				{
					CellLocation curCellLoc = getTelephonyManager(context).getCellLocation();
					if(curCellLoc == null)
						return nCellInfo;
					
					strName = curCellLoc.getClass().getName();
					if (strName.equals(CdmaCellLocation.class.getName()) && nType == 1) //CDMA
					{
						CdmaCellLocation cdmaCell = (CdmaCellLocation) curCellLoc;
						nCellInfo[0] = cdmaCell.getBaseStationId(); //CID
						nCellInfo[1] = cdmaCell.getSystemId(); //SID
						nCellInfo[2] = cdmaCell.getNetworkId(); //NID
					}
					else if (strName.equals(GsmCellLocation.class.getName()) && nType == 2) //GSM
					{
						GsmCellLocation gsmCell = (GsmCellLocation) curCellLoc;
						nCellInfo[0] = gsmCell.getCid(); //cell id
						nCellInfo[1] = gsmCell.getLac(); //lac
					}
				}
				else
				{
					List<CellInfo> listCell = getTelephonyManager(context).getAllCellInfo();
					if(listCell == null)
						return nCellInfo;
					
					CellInfo curCell;
					for (int i = 0; i < listCell.size(); i++)
					{
						curCell = listCell.get(i);
						strName = curCell.getClass().getName();

						if (nType == 0) //LTE
						{
							if (strName.equals(CellInfoLte.class.getName()))
							{
								CellIdentityLte cellLte = ((CellInfoLte) curCell).getCellIdentity();
								//有可能出现两个LTE小区信息,其中一个无效
								if (cellLte.getCi() != Integer.MAX_VALUE && cellLte.getMnc() != Integer.MAX_VALUE && cellLte.getMcc() != Integer.MAX_VALUE)
								{
									nCellInfo[0] = cellLte.getMnc(); //mnc
									nCellInfo[1] = cellLte.getMcc(); //mcc
									nCellInfo[2] = cellLte.getCi(); //CI
									nCellInfo[3] = cellLte.getPci(); //PCI
									nCellInfo[4] = cellLte.getTac(); //TAC
									break;
								}
							}
						}
						else if (nType == 1) //CDMA
						{
							if (strName.equals(CellInfoCdma.class.getName()))
							{
								CellIdentityCdma cellCdma = ((CellInfoCdma) curCell).getCellIdentity();
								nCellInfo[0] = cellCdma.getBasestationId();
								nCellInfo[1] = cellCdma.getSystemId();
								nCellInfo[2] = cellCdma.getNetworkId();

								break;
							}
						}
						else if (nType == 2) //GSM
						{
							if (strName.equals(CellInfoGsm.class.getName()))
							{
								CellIdentityGsm cellGsm = ((CellInfoGsm) curCell).getCellIdentity();
								nCellInfo[0] = cellGsm.getCid(); //cell id
								nCellInfo[1] = cellGsm.getLac(); //lac

								break;
							}
						}
					}
				}	
			}
			catch (Exception e)
			{
			}
			return nCellInfo;
		}
		*/
	/**
	 * 功能: 主动获取小区/信号信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	@SuppressLint("NewApi")
	public static TelStrengthInfo getActiveCellSignal(Context context, TelStrengthInfo telStrenInfo)
	{
		if (telStrenInfo == null)
			return null;

		String strName;
		try
		{
			if (Build.VERSION.SDK_INT < 17)
			{
				CellLocation curCellLoc = getTelephonyManager(context).getCellLocation();
				if (curCellLoc == null)
					return telStrenInfo;

				strName = curCellLoc.getClass().getName();
				if (strName.equals(CdmaCellLocation.class.getName())) //CDMA
					telStrenInfo.getCdmaCellInfo(curCellLoc);

				if (strName.equals(GsmCellLocation.class.getName())) //GSM
					telStrenInfo.getGsmCellInfo(curCellLoc);
			}
			else
			{
				List<CellInfo> listCell = getTelephonyManager(context).getAllCellInfo();
				if (listCell == null)
					return telStrenInfo;

				CellInfo curCell;
				for (int i = 0; i < listCell.size(); i++)
				{
					curCell = listCell.get(i);
					strName = curCell.getClass().getName();

					if (strName.equals(CellInfoLte.class.getName())) //LTE
						telStrenInfo.getLteCellInfoEx(curCell);

					if (strName.equals(CellInfoCdma.class.getName())) //CDMA
						telStrenInfo.getCdmaCellInfoEx(curCell);

					if (strName.equals(CellInfoGsm.class.getName())) //GSM
						telStrenInfo.getGsmCellInfoEx(curCell);
				}
			}
		}
		catch (Exception e)
		{
		}
		return telStrenInfo;
	}

	// ---------------------------------------------------------------------------------
	// 获取当前网络类型(wifi,1x,3G,4G)
	public static NetworkInfo getAvaiableNetwork(Context context)
	{
		return getConnectivityManager(context).getActiveNetworkInfo();
	}

	/**
	 * 功能: 获取当前活动网络名称 
	 * 参数: 
	 * 返回值: 
	 * 说明:
	 */
	public static String getAvaiableNetworkName(Context context)
	{
		NetworkInfo ni = getConnectivityManager(context).getActiveNetworkInfo();
		if (ni == null)
			return "";

		switch (ni.getType())
		{
		case ConnectivityManager.TYPE_WIFI:
			return "WiFi";
		default:
			return ni.getSubtypeName();
		}
	}

	/**
	 * 功能: 获取当前活动网络类型 
	 * 参数: 
	 * 返回值: 自定义网络类型 
	 * 说明:
	 */
	public static int getAvaiableNetworkType(Context context)
	{
		NetworkInfo netInfo = getConnectivityManager(context).getActiveNetworkInfo();
		if (netInfo == null)
			return NetWorkType.eUnknow.ordinal();

		int nNetType = 0;
		if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) // wifi
		{
			nNetType = NetWorkType.eWifi.ordinal();
		}
		else
		{ // 手机网络
			switch (netInfo.getSubtype())
			{
			case TelephonyManager.NETWORK_TYPE_CDMA: // CDMA
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				nNetType = NetWorkType.eCDMA.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE: // EDGE
				nNetType = NetWorkType.eEDGE.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_EVDO_B: // EVDO
			case TelephonyManager.NETWORK_TYPE_EHRPD: //LTE关闭4G功能
				nNetType = NetWorkType.eEVDO.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS: // GPRS
				nNetType = NetWorkType.eGPRS.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_LTE: // LTE
				nNetType = NetWorkType.eLTE.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				nNetType = NetWorkType.eHSDPA.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				nNetType = NetWorkType.eHSPA.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				nNetType = NetWorkType.eHSUPA.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				nNetType = NetWorkType.eUMTS.ordinal();
				break;
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				break;
			case TelephonyManager.NETWORK_TYPE_IDEN:
				break;
			default:
				nNetType = NetWorkType.eUnknow.ordinal();
				break;
			}
		}

		return nNetType;
	}

	/**
	 * 功能: 获取当前活动网络名称
	 * 参数: 
	 * 返回值: 自定义网络类型 
	 * 说明:
	 */
	public static String getActNetworkName(Context context)
	{
		String strRet = "Unknow";
		NetworkInfo netInfo = getConnectivityManager(context).getActiveNetworkInfo();
		if (netInfo == null)
			return strRet;

		if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) // wifi
		{
			strRet = "WIFI";
		}
		else
		{ // 手机网络
			switch (netInfo.getSubtype())
			{
			case TelephonyManager.NETWORK_TYPE_CDMA: // CDMA
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				strRet = "CDMA 1X";
				//strRet = "2G";
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE: // EDGE
				strRet = "EDGE";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case TelephonyManager.NETWORK_TYPE_EVDO_B: // EVDO
				strRet = "EVDO";
				break;
			case TelephonyManager.NETWORK_TYPE_EHRPD:
				strRet = "Ehrpd";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS: // GPRS
				strRet = "GPRS";
				break;
			case TelephonyManager.NETWORK_TYPE_LTE: // LTE
				strRet = "LTE";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				strRet = "HSDPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				strRet = "HSPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				strRet = "HSUPA";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				strRet = "UMTS";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				strRet = "HSPAP";
				break;
			case TelephonyManager.NETWORK_TYPE_IDEN:
				strRet = "IDEN";
				break;
			default:
				strRet = "";
				break;
			}
		}

		return strRet;
	}

	//获取本地IP
	public static String getLocalIPAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch (SocketException e)
		{
			return "";
		}

		return "";
	}

	//获取外网地址
	/*public static String getWebIP()
	{
		try
		{
			URL url = new URL("http://iframe.ip138.com/ic.asp");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer sb = new StringBuffer("");

			String strTemp = "";
			while ((strTemp = br.readLine()) != null)
			{
				sb.append(strTemp + "\r\n");
			}

			br.close();
			strTemp = sb.toString();
			int start = strTemp.indexOf("[") + 1;
			int end = strTemp.indexOf("]");
			strTemp = strTemp.substring(start, end);

			return strTemp;

		}
		catch (Exception e)
		{
			return "";
		}
	}*/

	// ----------------------------------------------------------------------------------

	public static boolean isWiFiConnected(Context context)
	{
		return getConnectivityManager(context).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
	}

	public static boolean isMobileConnected(Context context)
	{
		return getConnectivityManager(context).getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
	}

	public static int getNetworkType(Context context)
	{
		return getTelephonyManager(context).getNetworkType();
	}

	/**
	 * 功能: 打开/关闭扬声器
	 * 参数: state: true:打开,false:关闭
	 * 返回值:
	 * 说明:
	 */
	public static void changeSpeaker(Context context, boolean state)
	{
		getAudioManager(context).setSpeakerphoneOn(state);
	}

	//------------------------------无用----------------------------------------------------
	public static void SetBatteryLevel(float newLevel)
	{
		m_fBatteryLev = newLevel;
	}

	public static float GetBatteryLevel()
	{
		return m_fBatteryLev;
	}

	public static CellLocation getCellLocation()
	{
		return m_cellLoc;
	}

	public static void setCellLocation(CellLocation cellLocation)
	{
		PhoneDataProvider.m_cellLoc = cellLocation;
	}

	/**
	 * 功能:进入睡眠状态
	 *  参数:context: 
	 *  time: 睡眠时间 
	 *  返回值: 
	 *  说明:
	 */
	public static void goToSleep(Context context, long time)
	{
		Log.d("", "Go to Sleep");
		getPowerManager(context).goToSleep(time);
	}

	public static String getDataActivity(Context context)
	{
		int state = getTelephonyManager(context).getDataActivity();
		return getDataActivityDirection(state);
	}
}