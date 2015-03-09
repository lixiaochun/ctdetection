package com.wellcell.ctdetection;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.SignalTest.SignalStrengthPar;
import com.wellcell.inet.SignalTest.SignalTestRet;
import com.wellcell.inet.SignalTest.TelStrengthInfo;
import com.wellcell.inet.Web.WebUtil;
import com.wellcell.inet.entity.AddrInfo;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class DetectionApp extends Application
{
	//-------------------------------------------------
	private SignalTestRet m_signalTestRet; // 信号测量结果

	private TelephonyManager m_telMag = null;
	private StateListener m_listenCellInfo = null; //小区信号监听器

	public TelStrengthInfo m_telStreInfo; //所有基站信号信息
	//--------------------------------------------------------------
	// 百度定位
	private LocationClient m_baiduLocClient = null;
	public AddrInfo m_curAddrInfo = new AddrInfo(); //当前地址
	public String m_strWebIP = "";		//外网IP
	//---------------------------------------------------------------
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		try
		{
			SDKInitializer.initialize(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//----------------------------------------------------------------------------------------------------
		m_signalTestRet = new SignalTestRet(this); // 信号测量结果
		m_telStreInfo = new TelStrengthInfo(this); // 初始化小区信号信息
		// 设置信号监听器
		m_telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		m_listenCellInfo = new StateListener();
		m_telStreInfo.m_nSdkVer = Build.VERSION.SDK_INT; // SDK版本号
		if (Build.VERSION.SDK_INT < 17) // 低版本
			m_telMag.listen(m_listenCellInfo, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		else	// 高版本
		{
			m_telMag.listen(m_listenCellInfo, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_INFO);
		}
		getFormater.run();
		//--------------------------------------------------------------------------------------------------------------
		// --------------------------------------------------------------------------------
		m_baiduLocClient = new LocationClient(this);
		m_baiduLocClient = CGlobal.setLocationOption(m_baiduLocClient); // 设置定位参数
		m_baiduLocClient.registerLocationListener(new BDLocationListener() //注册监听
				{
					@Override
					public void onReceiveLocation(BDLocation arg0)
					{
						m_curAddrInfo.getAddrInfo(arg0); //提取信息
					}
				});
		m_baiduLocClient.start(); //开始定位
		// ================================================================================
		new Thread(getWebIPfromSrv).start();	//获取IP
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();
	}
	
	private Handler m_hHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				break;
			case 2: //信号参数处理
				if (m_telStreInfo != null)
					m_telStreInfo.setSignalPar((SignalStrengthPar) msg.obj); //设置信号参数
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};

	
	// 获取信号格式化参数
	private Runnable getFormater = new Runnable()
	{
		@Override
		public void run()
		{
			SignalStrengthPar signalFar = SignalStrengthPar.getSignalStrengthFar(DetectionApp.this);
			m_hHandler.sendMessage(Message.obtain(m_hHandler, 2, signalFar));
		}
	};
	
	//获取外网IP
	private Runnable getWebIPfromSrv = new Runnable()
	{
		@Override
		public void run()
		{
			m_strWebIP = WebUtil.getWebIP();
		}
	};
	
	//获取外网IP
	public String getWebIP()
	{
		if(m_strWebIP.length() <= 0)
			new Thread(getWebIPfromSrv).start();
		
		return m_strWebIP;
	}

	
	// 监听器
	class StateListener extends PhoneStateListener
	{
		// lev 17一下小区信息
		@Override
		public void onCellLocationChanged(CellLocation location)
		{
			super.onCellLocationChanged(location);
			try
			{
				// Log.i(TAG, "onCellLocationChanged");
				m_telStreInfo.getNetWorkType(); // 更新网咯类型

				String strCellLocName = location.getClass().getName(); // 类名
				if (strCellLocName.equals(CdmaCellLocation.class.getName())) // CDMA
					m_telStreInfo.getCdmaCellInfo(location);
				else if (strCellLocName.equals(GsmCellLocation.class.getName())) // GSM
					m_telStreInfo.getGsmCellInfo(location);

				//m_inetApp.m_appTelStrenInfo = m_telStreInfo; //更新app的信号信息
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		// 17以上小区信息变更
		@Override
		public void onCellInfoChanged(List<CellInfo> cellInfo)
		{
			super.onCellInfoChanged(cellInfo);
			try
			{
				m_telStreInfo.getNetWorkType(); // 更新当前网络类型

				String strCellLocName;
				for (CellInfo cell : cellInfo)
				{
					strCellLocName = cell.getClass().getName();
					if (strCellLocName.equals(CellInfoLte.class.getName())) // lte
						m_telStreInfo.getLteCellInfoEx(cell); // 提取cell信息
					else if (strCellLocName.equals(CellInfoCdma.class.getName())) // cdma
						m_telStreInfo.getCdmaCellInfoEx(cell); // 更新基站信息
					else if (strCellLocName.equals(CellInfoGsm.class.getName())) // gsm
						m_telStreInfo.getGsmCellInfoEx(cell);
				}
				//m_inetApp.m_appTelStrenInfo = m_telStreInfo; //更新app的信号信息
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		// 信号强度
		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength)
		{
			super.onSignalStrengthsChanged(signalStrength);

			m_telStreInfo.getSignalInfo(signalStrength, m_curAddrInfo.m_dLon, m_curAddrInfo.m_dLat); // 获取信号强度信息
			//m_inetApp.m_appTelStrenInfo = m_telStreInfo; //更新app的信号信息

			/*if (m_nCurTestState == TestState.eTesting) //信号测试手机数据
			{
				//m_signalTestRet.getCellSignalInfo(m_telStreInfo,m_ParentActivity.m_curAddrInfo); // 提取数据
				if (m_signalTestRet.bComplete(m_dTestTime)) // 测试完毕
					stopSignalTest();
			}*/
		}
	}
}
