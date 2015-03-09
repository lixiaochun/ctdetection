package com.wellcell.SubFrag.AidInfo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.wellcell.ctdetection.DetectionApp;
import com.wellcell.ctdetection.R;
import com.wellcell.inet.Common.CGlobal.TestState;
import com.wellcell.inet.DataProvider.PhoneDataProvider;
import com.wellcell.inet.SignalTest.SignalStrengthPar;
import com.wellcell.inet.SignalTest.SignalTestRet;
import com.wellcell.inet.SignalTest.TelStrengthInfo;
import com.wellcell.inet.Web.WebUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//大众版分页--信号测量
public class CustSignalFragment extends Fragment
{
	final static String TAG = "CustSignalFragment";
	private int m_nImgIndex = 0; // 图片索引下标
	// 五个等级的强度标识
	final private int[] GradeA = { R.drawable.ga0, R.drawable.ga1, R.drawable.ga2, R.drawable.ga3 };
	final private int[] GradeB = { R.drawable.gb0, R.drawable.gb1, R.drawable.gb2, R.drawable.gb3 };
	final private int[] GradeC = { R.drawable.gc0 };
	final private int[] GradeD = { R.drawable.gd0, };
	final private int[] GradeE = { R.drawable.ge0, R.drawable.ge1, R.drawable.ge2, R.drawable.ge3 };

	final private String[] SignalInfo = { "无信号", "信号太棒了", "信号很给力", "信号一般呀", "信号有点差", "信号不行啊" };

	private Context m_contextApp;
	private DetectionApp m_inetApp = null;
	//private CustTestActivity m_ParentActivity; //父窗口

	// 头布局
	private RelativeLayout m_reLayoutSum;
	private RelativeLayout m_reLayoutCdma;
	private RelativeLayout m_reLayoutGsm;

	private LinearLayout m_lineSpecLte;
	private LinearLayout m_lineSpecCdma;
	private LinearLayout m_lineSpecGsm;

	// 信号头->最顶头
	private ImageView m_ivHead4g;
	private ImageView m_ivHead3g;
	private ImageView m_ivHead2g;
	private ImageView m_ivHeadGsm;
	private TextView m_tvLteType;
	private TextView m_tvTestState; //测试状态

	//汇总信息
	private ImageView m_ivSignal; //图标
	private TextView m_tvNetWorkType; //网络类型
	private TextView m_tvSupportSpeed; //支持速率

	// 图标->动态展示
	private ImageView m_ivSignalCdma;
	private ImageView m_ivSignalGsm;
	private TextView m_tvSignalInfoCdma;
	private TextView m_tvSignalInfoGsm;

	// 4g
	private TextView m_tvPci4g;
	private TextView m_tvCi4g;
	private TextView m_tvMcc4g;
	private TextView m_tvMnc4g;
	private TextView m_tvTac4g;
	private TextView m_tvRsrq4g;
	private TextView m_tvRssnr4g;
	private TextView m_tvRsrp4g;
	private TextView m_tvRssi4g;
	private TextView m_tvCqi4g;

	// cdma
	private TextView m_tvCid3g;
	private TextView m_tvSid3g;
	private TextView m_tvNid3g;
	private TextView m_tvRx2g;
	private TextView m_tvEcio2g;
	private TextView m_tvRx3g;
	private TextView m_tvEcio3g;
	private TextView m_tvSnr3g;

	// GSM
	private TextView m_txCellIdGsm;
	private TextView m_txLacGsm;
	private TextView m_txRxGsm;

	// 测试项
	private TestState m_nCurTestState = TestState.eReady; // 当前测试状态
	private int m_nDur = 3; //已上传停留秒数

	private double m_dTestTime = 15000; // 测试时间

	//private String m_strHotName = null; //热点名称
	//private String m_strHotID = null; // 热点ID

	private SignalTestRet m_signalTestRet; // 信号测量结果

	private TelephonyManager m_telMag = null;
	private StateListener m_listenCellInfo = null; //小区信号监听器

	private TelStrengthInfo m_telStreInfo; //所有基站信号信息

	// 定时器-->更新信息
	private TimerTask m_allTask;
	private Timer m_allTimer = new Timer();

	// 更新图片
	private TimerTask m_taskImg;
	private Timer m_timerImg = new Timer();

	//实例化
	public static CustSignalFragment newInstance()
	{
		CustSignalFragment signalFrag = new CustSignalFragment();
		return signalFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		m_contextApp = getActivity().getApplicationContext();
		m_inetApp = (DetectionApp) m_contextApp;
		//m_inetApp.m_inetLog.setStartTime(ActivityName.eSignalCust);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.signaltest_cust, null);
		bindComponment(view); //提取空间
		return view;
	}

	private final Handler m_hHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 2: //信号参数处理
				if (m_telStreInfo != null)
					m_telStreInfo.setSignalPar((SignalStrengthPar) msg.obj); //设置信号参数
				break;
			case 6: // 更新信息
				if (getActivity() == null) //防止父窗口销毁继续发送消息
					break;
				
				updateLteInfo();
				updateCDMAInfo();
				updateGSMInfo();

				showSignalSpec(m_telStreInfo); // 判断是否有信号, 根据信号显示对应的信号信息

				//完成测试状态调整
				if(m_nCurTestState == TestState.eUploaded && m_nDur-- < 0)
					showTestState(TestState.eComplete);
				break;
			case 7: // 更新图片
				if (getActivity() == null) //防止父窗口销毁继续发送消息
					break;
				
				runFrameSignal(m_telStreInfo);
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		};
	};

	@Override
	public void onStart()
	{
		super.onStart();

		//m_ParentActivity = (CustTestActivity) getActivity();

		m_signalTestRet = new SignalTestRet(getActivity()); // 信号测量结果
		m_telStreInfo = new TelStrengthInfo(m_contextApp); // 初始化小区信号信息
		// 设置信号监听器
		m_telMag = (TelephonyManager) m_contextApp.getSystemService(Context.TELEPHONY_SERVICE);
		m_listenCellInfo = new StateListener();
		m_telStreInfo.m_nSdkVer = Build.VERSION.SDK_INT; // SDK版本号
		if (Build.VERSION.SDK_INT < 17) // 低版本
			m_telMag.listen(m_listenCellInfo, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		else	// 高版本
		{
			m_telMag.listen(m_listenCellInfo, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_INFO);
		}

		getFormater.run();

		// ================================================================================
		// 开定时器不断更新图表数据
		m_allTask = new TimerTask()
		{
			public void run()
			{
				m_hHandler.sendMessage(m_hHandler.obtainMessage(6));
			}
		};

		m_allTimer.schedule(m_allTask, 500, 1000);
		// -----------------------------------------------------------------------------------
		m_taskImg = new TimerTask()
		{
			@Override
			public void run()
			{
				if (getActivity() != null)
					m_hHandler.sendMessage(m_hHandler.obtainMessage(7));
			}
		};
		m_timerImg.schedule(m_taskImg, 100, 200);
	}

	// 获取信号格式化参数
	Runnable getFormater = new Runnable()
	{
		@Override
		public void run()
		{
			SignalStrengthPar signalFar = SignalStrengthPar.getSignalStrengthFar(m_contextApp);
			m_hHandler.sendMessage(Message.obtain(m_hHandler, 2, signalFar));
		}
	};

	@Override
	public void onDestroy()
	{
		//关闭信号监听
		if (m_telMag != null && m_listenCellInfo != null)
			m_telMag.listen(m_listenCellInfo, PhoneStateListener.LISTEN_NONE); //取消监听

		// 当结束程序时关掉Timer
		m_allTimer.cancel();
		m_timerImg.cancel();

		super.onDestroy();
	}

	/**
	 * 功能:控件显示控制 
	 * 参数: 
	 * 返回值: 
	 * 说明: 附带控制顶端信号强度更新,防止无效信号强度更新
	 */
	public void showSignalSpec(TelStrengthInfo telStreInfo)
	{
		// 隐藏和显示4G信息，true显示，false不显示
		int nFlag = View.GONE;
		double dSignal = -120;
		double dSignalEx = -120;

		//综述字段
		int nType = -1;
		double dSpeedSignal = -120; //支持速率计算指标

		m_reLayoutSum.setVisibility(View.VISIBLE); //显示汇总头

		// 4G
		if (telStreInfo.IsValidInfo(0))
		{
			nFlag = View.VISIBLE;
			dSignal = m_telStreInfo.m_dRsrp;

			nType = 0;
			dSpeedSignal = m_telStreInfo.m_dSinrLte;
		}
		else
		{
			nFlag = View.GONE;
			dSignal = -120;
		}
		m_lineSpecLte.setVisibility(nFlag);
		runFrameAnim(m_ivHead4g, dSignal,true); // 更新信号头图片

		// CDMA
		if (telStreInfo.IsValidInfo(1) || telStreInfo.IsValidInfo(2))
		{
			nFlag = View.VISIBLE;
			dSignal = -120;
			if (TelStrengthInfo.IsValidRx(m_telStreInfo.m_dRx3G))
			{
				dSignal = m_telStreInfo.m_dRx3G;

				if (nType < 0)
				{
					nType = 1;
					dSpeedSignal = m_telStreInfo.m_dSnr3G;
				}
			}

			dSignalEx = -120;
			if (TelStrengthInfo.IsValidRx(m_telStreInfo.m_dRx2G))
			{
				dSignalEx = m_telStreInfo.m_dRx2G;

				if (nType < 0)
					nType = 2;
			}
		}
		else
		{
			nFlag = View.GONE;
			dSignal = -120;
			dSignalEx = -120;
		}
		
		//当前网络为2/3G
		if(nType > 0)
			m_reLayoutCdma.setVisibility(View.GONE);
		else
			m_reLayoutCdma.setVisibility(nFlag);
		m_lineSpecCdma.setVisibility(nFlag);
		runFrameAnim(m_ivHead3g, dSignal,false);
		runFrameAnim(m_ivHead2g, dSignalEx,false);

		// gsm
		if (telStreInfo.IsValidInfo(3))
		{
			nFlag = View.VISIBLE;
			dSignal = m_telStreInfo.m_dRxGsm;

			if (nType < 0)
				nType = 2;
		}
		else
		{
			nFlag = View.GONE;
			dSignal = -120;
		}
		m_reLayoutGsm.setVisibility(nFlag);
		m_lineSpecGsm.setVisibility(nFlag);
		runFrameAnim(m_ivHeadGsm, dSignal,false);

		//汇总信息
		showSignalSumInfo(nType, dSpeedSignal);
	}

	/**
	 * 功能: 显示汇总信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	private void showSignalSumInfo(int nType, double dSignal)
	{
		int nColor = 0; // 颜色
		int nImgID = 0; // 图标ID
		int[] nElems;

		switch (nType)
		{
		case 0: //4G
			m_tvNetWorkType.setText("网络类型: 4G");
			nElems = getShowElem(m_telStreInfo.m_dRsrp,true);
			nColor = nElems[1];
			nImgID = nElems[2];
			break;
		case 1: //3G
			m_tvNetWorkType.setText("网络类型 : 3G");
			nElems = getShowElem(m_telStreInfo.m_dRx3G,false);
			nColor = nElems[1];
			nImgID = nElems[2];
			break;
		case 2: //2G
			m_tvNetWorkType.setText("网络类型: 2G");
			nElems = getShowElem(m_telStreInfo.m_dRx2G,false);
			nColor = nElems[1];
			nImgID = nElems[2];
			break;
		default:
			m_tvNetWorkType.setText("无网络!!");
			m_tvSupportSpeed.setText("--");
			return;
		}

		try
		{
			m_ivSignal.setBackgroundResource(nImgID); // 更新图标
			nColor = getResources().getColor(nColor);
			m_tvSupportSpeed.setTextColor(nColor); // 文字颜色
			m_tvSupportSpeed.setText(getSupportSpeed(nType, dSignal));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 功能: 获取支持速率
	 * 参数: nType: 网络类型
	 * 返回值:
	 * 说明:
	 */
	private String getSupportSpeed(int nType, double dSignal)
	{
		String strRet = "支持速率:";
		switch (nType)
		{
		case 0: //4G
			if (dSignal > 30)
				strRet += "100";
			else if (dSignal > 20 && dSignal <= 30)
				strRet += "70";
			else if (dSignal > 15 && dSignal <= 20)
				strRet += "68";
			else if (dSignal > 10 && dSignal <= 15)
				strRet += "50";
			else if (dSignal > 5 && dSignal <= 10)
				strRet += "38";
			else if (dSignal > 0 && dSignal <= 5)
				strRet += "20";
			else if (dSignal > -5 && dSignal <= 0)
				strRet += "10";
			else if (dSignal > -10 && dSignal <= -5)
				strRet += "3";
			else
				strRet = "-";

			strRet += "Mbps";
			break;
		case 1: //3G
			if (dSignal > 10.3)
				strRet += "3072.0";
			else if (dSignal > 20 && dSignal <= 30)
				strRet += "2457.6";
			else if (dSignal > 15 && dSignal <= 20)
				strRet += "1743.2";
			else if (dSignal > 10 && dSignal <= 15)
				strRet += "1228.8";
			else if (dSignal > 5 && dSignal <= 10)
				strRet += "614.4";
			else if (dSignal > 0 && dSignal <= 5)
				strRet += "153.6";
			else
				strRet = "-";

			strRet += "kbps";
			break;
		case 2: //2G
			strRet += "153.6kbps";
			break;
		default:
			strRet = "";
			break;
		}
		return strRet;
	}

	/**
	 * 功能:最顶端信号图片显示
	 *  参数: view: 
	 *  		signal: 
	 *  返回值: 
	 *  说明:signal==-120为无信号状态
	 */
	public void runFrameAnim(final ImageView view, final double signal,boolean bLTE)
	{
		int nImgID = 0;
		// 信号等级
		int nGrade = 0;
		if(bLTE)
			nGrade = TelStrengthInfo.getSignalGradeLTE(signal);
		else
			nGrade = TelStrengthInfo.getSignalGradeCDMA(signal);
		
		switch (nGrade)
		{
		case 1:
			nImgID = R.drawable.s5;
			break;
		case 2:
			nImgID = R.drawable.s4;
			break;
		case 3:
			nImgID = R.drawable.s3;
			break;
		case 4:
			nImgID = R.drawable.s2;
			break;
		case 5:
			nImgID = R.drawable.s1;
			break;
		default:
			nImgID = R.drawable.s0;
			break;
		}

		view.setBackgroundResource(nImgID);
	}

	/**
	 * 功能: 信号动态显示功能
	 *  参数: telStreInfo:信号信息 
	 *  返回值: 
	 *  说明:
	 */
	public void runFrameSignal(TelStrengthInfo telStreInfo)
	{
		int nGrade = 0; // 信号等级
		int nColor = 0; // 颜色
		int nImgID = 0; // 图标ID
		int[] nElems;

		// CDMA
		if (telStreInfo.IsValidInfo(1) || telStreInfo.IsValidInfo(2))
		{
			nElems = getShowElem(telStreInfo.m_dRx2G,false);
			nGrade = nElems[0];
			nColor = nElems[1];
			nImgID = nElems[2];

			m_ivSignalCdma.setBackgroundResource(nImgID); // 更新图标
			nColor = getResources().getColor(nColor);
			m_tvSignalInfoCdma.setTextColor(nColor); // 文字颜色
			m_tvSignalInfoCdma.setText(SignalInfo[nGrade]); // 信息
		}

		// GSM
		if (telStreInfo.IsValidInfo(3))
		{
			nElems = getShowElem(telStreInfo.m_dRxGsm,false);
			nGrade = nElems[0];
			nColor = nElems[1];
			nImgID = nElems[2];

			m_ivSignalGsm.setBackgroundResource(nImgID); // 更新图标
			nColor = getResources().getColor(nColor);
			m_tvSignalInfoGsm.setTextColor(nColor); // 文字颜色
			m_tvSignalInfoGsm.setText(SignalInfo[nGrade]); // 信息
		}

		m_nImgIndex++; // 图标更新
	}

	/**
	 * 功能: 获取信号动态显示相关元素
	 * 参数: dVal: 指标值
	 * 		bLTE: 是否是LTE
	 * 返回值:元素数组,0: Grade;1:颜色;3:图标ID
	 * 说明:
	 */
	private int[] getShowElem(double dVal,boolean bLTE)
	{
		int[] nElems = new int[3];
		if(bLTE)
			nElems[0] = TelStrengthInfo.getSignalGradeLTE(dVal);
		else
			nElems[0] = TelStrengthInfo.getSignalGradeCDMA(dVal);

		switch (nElems[0])
		{
		case 1:
			nElems[1] = R.color.green;

			// 图标ID
			if (m_nImgIndex > GradeA.length - 1)
				m_nImgIndex = 0;
			nElems[2] = GradeA[m_nImgIndex];
			break;
		case 2:
			nElems[1] = R.color.blue;

			if (m_nImgIndex > GradeB.length - 1)
				m_nImgIndex = 0;
			nElems[2] = GradeB[m_nImgIndex];
			break;
		case 3:
			nElems[1] = R.color.yellow;
			nElems[2] = GradeC[0];
			break;
		case 4:
			nElems[1] = R.color.red;
			nElems[2] = GradeD[0];
			break;
		case 5:
			nElems[1] = R.color.black;

			if (m_nImgIndex > GradeA.length - 1)
				m_nImgIndex = 0;
			nElems[2] = GradeE[m_nImgIndex];
			break;
		default:
			nElems[1] = R.color.black;
			nElems[2] = GradeD[0]; // 防出错
			break;
		}
		return nElems;
	}

	// 更新Lte信息
	private void updateLteInfo()
	{
		m_tvPci4g.setText(m_telStreInfo.m_strPci);
		m_tvTac4g.setText(m_telStreInfo.m_strTac);
		m_tvCi4g.setText(m_telStreInfo.m_strCi);
		m_tvMcc4g.setText(m_telStreInfo.m_strMcc);
		m_tvMnc4g.setText(m_telStreInfo.m_strMnc);

		// 频段号
		if (m_telStreInfo.m_dFreqLte > 0)
		{
			if (m_telStreInfo.m_dFreqLte == 2.6) // TDD
			{
				m_tvLteType.setTextColor(Color.rgb(14, 120, 191));
				m_tvLteType.setText("T");
			}
			else
			// FDD
			{
				m_tvLteType.setTextColor(Color.MAGENTA);
				m_tvLteType.setText("F");
			}
		}
		else
			m_tvLteType.setText(null);

		m_tvRsrp4g.setText(TelStrengthInfo.getRsrpString(m_telStreInfo.m_dRsrp)); // rsrp
		m_tvRsrq4g.setText(TelStrengthInfo.getRsrqString(m_telStreInfo.m_dRsrq)); // rsrq
		m_tvRssi4g.setText(TelStrengthInfo.getRssiString(m_telStreInfo.m_dRssiLte)); // RSSI
		m_tvRssnr4g.setText(TelStrengthInfo.getSinrString(m_telStreInfo.m_dSinrLte)); // sinr
		m_tvCqi4g.setText(TelStrengthInfo.getCqiString(m_telStreInfo.m_nCqiLte)); // CQI
	}

	// 更新CDMA信息
	private void updateCDMAInfo()
	{
		m_tvCid3g.setText(m_telStreInfo.m_strCID); // CID
		m_tvSid3g.setText(m_telStreInfo.m_strSID); // SID
		m_tvNid3g.setText(m_telStreInfo.m_strNID); // NID

		m_tvRx3g.setText(TelStrengthInfo.getRxString(m_telStreInfo.m_dRx3G));
		m_tvEcio3g.setText(TelStrengthInfo.getEcioString(m_telStreInfo.m_dEcio3G));
		m_tvSnr3g.setText(TelStrengthInfo.getSnrString(m_telStreInfo.m_dSnr3G, m_telStreInfo.m_dRx3G));

		m_tvRx2g.setText(TelStrengthInfo.getRxString(m_telStreInfo.m_dRx2G)); //Rx2G
		m_tvEcio2g.setText(TelStrengthInfo.getEcioString(m_telStreInfo.m_dEcio2G)); //Ecio2G
	}

	// 更新GSM数据
	public void updateGSMInfo()
	{
		m_txCellIdGsm.setText(m_telStreInfo.m_strCIDGsm); //CellID
		m_txLacGsm.setText(m_telStreInfo.m_strLacGsm); //LAC

		m_txRxGsm.setText(TelStrengthInfo.getRxString(m_telStreInfo.m_dRxGsm)); //RxGsm
	}

	// 绑定控件
	private void bindComponment(View view)
	{
		// 信号头控件
		m_ivHead4g = (ImageView) (view.findViewById(R.id.signal_4g));
		m_ivHead3g = (ImageView) (view.findViewById(R.id.signal_3g));
		m_ivHead2g = (ImageView) (view.findViewById(R.id.signal_2g));
		m_ivHeadGsm = (ImageView) (view.findViewById(R.id.signal_gsm));
		m_tvLteType = (TextView) (view.findViewById(R.id.ltetype));
		m_tvTestState = (TextView) (view.findViewById(R.id.teststate));

		// 信号动态信息
		m_ivSignal = (ImageView) view.findViewById(R.id.imgsignal);
		m_tvNetWorkType = (TextView) view.findViewById(R.id.networktype);
		m_tvSupportSpeed = (TextView) view.findViewById(R.id.support_speed);
		m_ivSignalCdma = (ImageView) view.findViewById(R.id.imgsignal_cdma);
		m_tvSignalInfoCdma = (TextView) view.findViewById(R.id.signalinfo_cdma);
		m_ivSignalGsm = (ImageView) view.findViewById(R.id.imgsignal_gsm);
		m_tvSignalInfoGsm = (TextView) view.findViewById(R.id.signalinfo_gsm);

		// 4G
		m_tvPci4g = (TextView) view.findViewById(R.id.pci_4g);
		m_tvMcc4g = (TextView) view.findViewById(R.id.mcc_4g);
		m_tvMnc4g = (TextView) view.findViewById(R.id.mnc_4g);
		m_tvTac4g = (TextView) view.findViewById(R.id.tac_4g);
		m_tvCi4g = (TextView) view.findViewById(R.id.ci_4g);
		m_tvRsrq4g = (TextView) view.findViewById(R.id.rsrq_4g);
		m_tvRssnr4g = (TextView) view.findViewById(R.id.rssnr_4g);
		m_tvCqi4g = (TextView) view.findViewById(R.id.cqi_4g);

		m_tvRsrp4g = (TextView) view.findViewById(R.id.rsrp_4g);
		m_tvRssi4g = (TextView) view.findViewById(R.id.rssi_4g);
		// -------------------------------------------------------------------
		// cdma
		m_tvCid3g = (TextView) view.findViewById(R.id.cid_3g);
		m_tvSid3g = (TextView) view.findViewById(R.id.sid_3g);
		m_tvNid3g = (TextView) view.findViewById(R.id.nid_3g);

		m_tvRx2g = (TextView) view.findViewById(R.id.rx_2g);
		m_tvEcio2g = (TextView) view.findViewById(R.id.ecio_2g);
		m_tvRx3g = (TextView) view.findViewById(R.id.rx_3g);
		m_tvEcio3g = (TextView) view.findViewById(R.id.ecio_3g);
		m_tvSnr3g = (TextView) view.findViewById(R.id.snr_3g);
		// ---------------------------------------------------------------------
		// GSM
		m_txCellIdGsm = (TextView) view.findViewById(R.id.cellid_gsm);
		m_txLacGsm = (TextView) view.findViewById(R.id.lac_gsm);
		m_txRxGsm = (TextView) view.findViewById(R.id.rx_gsm);
		// ----------------------------------------------------------------------
		// 各分块布局
		m_reLayoutSum = (RelativeLayout) view.findViewById(R.id.head_sum);
		m_reLayoutCdma = (RelativeLayout) view.findViewById(R.id.head_cdma);
		m_reLayoutGsm = (RelativeLayout) view.findViewById(R.id.head_gsm);

		m_lineSpecLte = (LinearLayout) view.findViewById(R.id.spec_4g);
		m_lineSpecCdma = (LinearLayout) view.findViewById(R.id.spec_3g);
		m_lineSpecGsm = (LinearLayout) view.findViewById(R.id.spec_gsm);
		// --------------------------------------------------------------
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

			m_telStreInfo.getSignalInfo(signalStrength, m_inetApp.m_curAddrInfo.m_dLon, m_inetApp.m_curAddrInfo.m_dLat); // 获取信号强度信息
			//m_inetApp.m_appTelStrenInfo = m_telStreInfo; //更新app的信号信息

			if (m_nCurTestState == TestState.eTesting) //信号测试手机数据
			{
				//m_signalTestRet.getCellSignalInfo(m_telStreInfo,m_ParentActivity.m_curAddrInfo); // 提取数据
				if (m_signalTestRet.bComplete(m_dTestTime)) // 测试完毕
					stopSignalTest();
			}
		}
	}
	
	//开始/停止信号测试
	public void startstopTest(String strHotID)
	{
		switch (m_nCurTestState)
		{
		case eReady:
		case eComplete: //就绪
			showTestState(TestState.eTesting);
			m_signalTestRet.Init(strHotID, System.currentTimeMillis()); // 初始化
			//m_telStreInfo.UpdateLocInfo(m_ParentActivity.m_curAddrInfo.m_dLon, m_ParentActivity.m_curAddrInfo.m_dLat);//更新经纬度
			//m_signalTestRet.getCellSignalInfo(m_telStreInfo,m_ParentActivity.m_curAddrInfo);	//记录第一条记录
			break;
		case eTesting: //-->转停止
			stopSignalTest();
			break;
		case eUploading:
		case eUploaded:
			break;
		default:
			break;
		}
	}
	
	//获取当前状态
	public TestState getCurState()
	{
		return m_nCurTestState;
	}
	
	//结束信号测试
	private void stopSignalTest()
	{
		m_signalTestRet.updateEndTime();	//更新结束时间
		showTestState(TestState.eUploading); // 上传状态
		uploadSignalData();
	}
	
	//上传信号测试数据
	private void uploadSignalData()
	{
		int nNetWorkType = PhoneDataProvider.getAvaiableNetworkType(m_contextApp); //当前网络类型
		Log.i(TAG,m_signalTestRet.toRetStringEx(nNetWorkType));
		String strRet = "";//WebUtil.uploadTestData(m_signalTestRet.toRetStringEx(nNetWorkType), ModuleIndex.eSignal,-1);
		if (strRet.equals("ok"))
			Log.i(TAG, "上传成功...");
		else
			Log.i(TAG, "上传失败...");

		showTestState(TestState.eUploaded); // 完成
	}
	
	private void showTestState(TestState testState)
	{
		m_nCurTestState = testState; // 修改当前状态
		//m_ParentActivity.updateState(FrameType.eSignal, m_nCurTestState,null);	//更新父窗口状态信息

		switch (testState)
		{
		case eReady:
		case eComplete:
			m_tvTestState.setTextSize(15);
			m_tvTestState.setText("就绪");
			//m_btnStart.setBackground(getResources().getDrawable(R.drawable.sel_btn_signal_start));	//修改为开始
			break;
		case eTesting:
			m_tvTestState.setTextSize(15);
			m_tvTestState.setText("测试");
			//m_btnStart.setBackground(getResources().getDrawable(R.drawable.sel_btn_signal_stop));
			break;
		case ePause:
			break;
		case eStoped:
			break;
		case eUploading:
			break;
		case eUploaded:
			m_nDur = 3;	//复原
			m_tvTestState.setTextSize(11);
			m_tvTestState.setText("已上传");
			break;
		default:
			break;
		}
	}
}
