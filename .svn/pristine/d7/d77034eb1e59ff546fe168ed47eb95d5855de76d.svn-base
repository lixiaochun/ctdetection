package com.wellcell.inet.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
	//业务数据表名
	public static final String JobInfo = "JobInfo";
	public static final String TaskInfo = "TaskInfo";
	public static final String NetWorkInfo = "NetWorkInfo";
	public static final String AnteInfo = "AntennaInfo";
	public static final String PingInfo = "PingInfo";
	public static final String WebInfo = "WebInfo";
	public static final String FtpInfo = "FtpInfo";
	public static final String WeiboInfo = "WeiboInfo";
	public static final String DialInfo = "DialInfo";
	public static final String VideoInfo = "VideoInfo";

	//信号测量表名
	public static final String LinkCellInfo = "LinkCell";
	public static final String SignalInfo = "SignalInfo";

	public static final String InetLog = "InetLog"; //日志表
	public static final String BtsInfo = "BtsInfo"; //基站信息

	public final static int VERSION = 1; // 数据库默认版本为1

	// 构造函数。实现SQLiteOpenHelper的构造函数
	public DatabaseHelper(Context context, String name, CursorFactory cursorFactory, int version)
	{
		super(context, name, cursorFactory, version);
	}

	// 构造函数.调用四个参数的构造函数
	public DatabaseHelper(Context context, String name, int version)
	{
		this(context, name, null, version);
	}

	// 构造函数.调用2个参数的构造函数
	public DatabaseHelper(Context context, String name)
	{
		this(context, name, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) //创建数据库
	{
		db.execSQL(getCreateJobInfoSql()); //创建JobInfo
		db.execSQL(getCreateTaskInfoSql()); //创建TaskInfo
		db.execSQL(getCreateNetWorkInfoSql()); //创建NetWorkInfo
		db.execSQL(getCreateAnteInfoSql()); //创建AnteInfo
		db.execSQL(getCreatePingInfoSql()); //PingInfo
		db.execSQL(getCreateWebInfoSql()); //WebInfo
		db.execSQL(getCreateFtpInfoSql()); //FtpInfo
		db.execSQL(getCreateWeiboInfoSql());//WeiboInfo
		db.execSQL(getCreateDialInfoSql()); //DialInfo

		//创建信号测量表
		db.execSQL(getCreateLinkInfoSql());
		db.execSQL(getCreateSignalInfoSql());

		db.execSQL(getCreateBtsInfoSql()); //BtsInfo
		db.execSQL(getCreateInetLogSql());	//日志表

		// 建立索引
		//db.execSQL("CREATE INDEX IF NOT EXISTS TB_BTS_INFO_idx_cid ON TB_BTS_INFO(SID,NID,CID);");
		//db.execSQL("CREATE INDEX IF NOT EXISTS TB_BTS_INFO_idx_cid ON TB_BTS_INFO(MAP_KEY);");
	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
	}

	//--------------------------------------------------------
	//创建JobInfo表Sql语句
	private String getCreateJobInfoSql()
	{
		StringBuffer sb = new StringBuffer();

		sb.append(" CREATE TABLE IF NOT EXISTS " + JobInfo); //不存在则创建
		sb.append("(");
		sb.append("[JobID] [varchar](100) NOT NULL,");
		sb.append("[JobType] [int] NULL,");
		sb.append("[JobSubType] [int] NULL,");
		sb.append("[StartTime] [bigint] NULL,");
		sb.append("[EndTime] [bigint] NULL,");
		sb.append("[UserName] [varchar](50) NULL,");
		sb.append("[IMSI] [varchar](50) NULL,");
		sb.append("[IMEI] [varchar](50) NULL,");
		sb.append("[DEVID] [varchar](50) NULL,");
		sb.append("[HostId] [varchar](50) NULL,");
		sb.append("[Floor] [int] NULL,");
		sb.append("[UploadNetType] [int] NULL");
		sb.append(")");

		return sb.toString();
	}

	//创建TaskInfo表Sql语句
	private String getCreateTaskInfoSql()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" CREATE TABLE IF NOT EXISTS " + TaskInfo); //不存在则创建
		sb.append("(");
		sb.append("[JobID] [varchar](100) NOT NULL,");
		sb.append("[TID] [varchar](50) NOT NULL,");
		sb.append("[TaskType] [int] NULL,");
		sb.append("[ISAUTO] [int] NULL,");
		sb.append("[TASK_STARTTIME] [bigint] NULL,");
		sb.append("[TASK_ENDTIME] [bigint] NULL,");
		sb.append("[LOSTLINKCOUNT] [int] NULL,");
		sb.append("[SWITCHTO1XCOUNT] [int] NULL,");
		sb.append("[NetWorkType] [int] NULL,");
		sb.append("[NETWORK_DISCONNECT_TIME] [int] NULL,");
		sb.append("[NETWORK_CONNECT_TIME] [int] NULL,");
		sb.append("[NETWORK_CONNECT_1_NAME] [varchar](50) NULL,");
		sb.append("[NETWORK_CONNECT_1_TYPE] [int] NULL,");
		sb.append("[NETWORK_CONNECT_1_SUBTYPE] [int] NULL,");
		sb.append("[NETWORK_CONNECT_1_TIME] [int] NULL,");
		sb.append("[NETWORK_CONNECT_2_NAME] [varchar](50) NULL,");
		sb.append("[NETWORK_CONNECT_2_TYPE] [int] NULL,");
		sb.append("[NETWORK_CONNECT_2_SUBTYPE] [int] NULL,");
		sb.append("[NETWORK_CONNECT_2_TIME] [int] NULL,");
		sb.append("[NETWORK_CONNECT_3_NAME] [varchar](50) NULL,");
		sb.append("[NETWORK_CONNECT_3_TYPE] [int] NULL,");
		sb.append("[NETWORK_CONNECT_3_SUBTYPE] [int] NULL,");
		sb.append("[NETWORK_CONNECT_3_TIME] [int] NULL,");
		sb.append("[LonRel] [varchar](50) NULL,");
		sb.append("[LatRel] [varchar](50) NULL");
		sb.append(")");

		return sb.toString();
	}

	//创建NetWorkInfo表Sql语句
	private String getCreateNetWorkInfoSql()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" CREATE TABLE IF NOT EXISTS " + NetWorkInfo); //不存在则创建
		sb.append("(");
		sb.append("[JobID] [varchar](100) NOT NULL,");
		sb.append("[TID] [varchar](50) NOT NULL,");

		sb.append("[CityLte_Acc] [varchar](50) NULL,");
		sb.append("[BtsNoLte_Acc] [varchar](50) NULL,");
		sb.append("[BtsNameLte_Acc] [varchar](50) NULL,");
		sb.append("[SectorLte_Acc] [varchar](50) NULL,");
		sb.append("[MCC_Acc] [nvarchar](50) NULL,");
		sb.append("[MNC_Acc] [nvarchar](50) NULL,");
		sb.append("[CI_Acc] [varchar](50) NULL,");
		sb.append("[PCI_Acc] [varchar](50) NULL,");
		sb.append("[TAC_Acc] [varchar](50) NULL,");

		sb.append("[CityCdma_Acc] [varchar](50) NULL,");
		sb.append("[BscCdma_Acc] [varchar](50) NULL,");
		sb.append("[BtsNoCdma_Acc] [varchar](50) NULL,");
		sb.append("[BtsNameCdma_Acc] [varchar](50) NULL,");
		sb.append("[SectorCdma_Acc] [varchar](50) NULL,");
		sb.append("[CID_Acc] [varchar](50) NULL,");
		sb.append("[SID_Acc] [varchar](50) NULL,");
		sb.append("[NID_Acc] [varchar](50) NULL,");
		sb.append("[CELLID_Acc] [varchar](50) NULL,");
		sb.append("[LAC_Acc] [varchar](50) NULL,");

		sb.append("[CityLte_Rel] [varchar](50) NULL,");
		sb.append("[BtsNoLte_Rel] [varchar](50) NULL,");
		sb.append("[BtsNameLte_Rel] [varchar](50) NULL,");
		sb.append("[SectorLte_Rel] [varchar](50) NULL,");
		sb.append("[MCC_Rel] [nvarchar](50) NULL,");
		sb.append("[MNC_Rel] [nvarchar](50) NULL,");
		sb.append("[CI_Rel] [varchar](50) NULL,");
		sb.append("[PCI_Rel] [varchar](50) NULL,");
		sb.append("[TAC_Rel] [varchar](50) NULL,");

		sb.append("[CityCdma_Rel] [varchar](50) NULL,");
		sb.append("[BscCdma_Rel] [varchar](50) NULL,");
		sb.append("[BtsNoCdma_Rel] [varchar](50) NULL,");
		sb.append("[BtsNameCdma_Rel] [varchar](50) NULL,");
		sb.append("[SectorCdma_Rel] [varchar](50) NULL,");
		sb.append("[CID_Rel] [varchar](50) NULL,");
		sb.append("[SID_Rel] [varchar](50) NULL,");
		sb.append("[NID_Rel] [varchar](50) NULL,");
		sb.append("[CELLID_Rel] [varchar](50) NULL,");
		sb.append("[LAC_Rel] [varchar](50) NULL,");

		sb.append("[StandLte] [int] NULL,");
		sb.append("[SumLte] [int] NULL,");
		sb.append("[RsrpSum] [numeric](18, 2) NULL,");
		sb.append("[RsrpCount] [int] NULL,");
		sb.append("[RsrpRank0] [int] NULL,");
		sb.append("[RsrpRank1] [int] NULL,");
		sb.append("[RsrpRank2] [int] NULL,");
		sb.append("[RsrpRank3] [int] NULL,");
		sb.append("[RsrpRank4] [int] NULL,");

		sb.append("[RsrqSum] [numeric](18, 2) NULL,");
		sb.append("[RsrqCount] [int] NULL,");
		sb.append("[RsrqRank0] [int] NULL,");
		sb.append("[RsrqRank1] [int] NULL,");
		sb.append("[RsrqRank2] [int] NULL,");
		sb.append("[RsrqRank3] [int] NULL,");
		sb.append("[RsrqRank4] [int] NULL,");

		sb.append("[RssiSum] [numeric](18, 2) NULL,");
		sb.append("[RssiCount] [int] NULL,");
		sb.append("[RssiRank0] [int] NULL,");
		sb.append("[RssiRank1] [int] NULL,");
		sb.append("[RssiRank2] [int] NULL,");
		sb.append("[RssiRank3] [int] NULL,");
		sb.append("[RssiRank4] [int] NULL,");

		sb.append("[SinrSum] [numeric](18, 2) NULL,");
		sb.append("[SinrCount] [int] NULL,");
		sb.append("[SinrRank0] [int] NULL,");
		sb.append("[SinrRank1] [int] NULL,");
		sb.append("[SinrRank2] [int] NULL,");
		sb.append("[SinrRank3] [int] NULL,");
		sb.append("[SinrRank4] [int] NULL,");

		sb.append("[CqiSum] [numeric](18, 2) NULL,");
		sb.append("[CqiCount] [int] NULL,");
		sb.append("[CqiRank0] [int] NULL,");
		sb.append("[CqiRank1] [int] NULL,");
		sb.append("[CqiRank2] [int] NULL,");
		sb.append("[CqiRank3] [int] NULL,");
		sb.append("[CqiRank4] [int] NULL,");

		sb.append("[StandEvdo] [int] NULL,");
		sb.append("[SumEvdo] [int] NULL,");
		sb.append("[Rx3gSum] [numeric](18, 2) NULL,");
		sb.append("[Rx3gCount] [int] NULL,");
		sb.append("[Rx3gRank0] [int] NULL,");
		sb.append("[Rx3gRank1] [int] NULL,");
		sb.append("[Rx3gRank2] [int] NULL,");
		sb.append("[Rx3gRank3] [int] NULL,");
		sb.append("[Rx3gRank4] [int] NULL,");

		sb.append("[Ecio3gSum] [numeric](18, 2) NULL,");
		sb.append("[Ecio3gCount] [int] NULL,");
		sb.append("[Ecio3gRank0] [int] NULL,");
		sb.append("[Ecio3gRank1] [int] NULL,");
		sb.append("[Ecio3gRank2] [int] NULL,");
		sb.append("[Ecio3gRank3] [int] NULL,");
		sb.append("[Ecio3gRank4] [int] NULL,");

		sb.append("[SnrSum] [numeric](18, 2) NULL,");
		sb.append("[SnrCount] [int] NULL,");
		sb.append("[SnrRank0] [int] NULL,");
		sb.append("[SnrRank1] [int] NULL,");
		sb.append("[SnrRank2] [int] NULL,");
		sb.append("[SnrRank3] [int] NULL,");
		sb.append("[SnrRank4] [int] NULL,");

		sb.append("[StandCdma] [int] NULL,");
		sb.append("[SumCdma] [int] NULL,");
		sb.append("[Rx2gSum] [numeric](18, 2) NULL,");
		sb.append("[Rx2gCount] [int] NULL,");
		sb.append("[Rx2gRank0] [int] NULL,");
		sb.append("[Rx2gRank1] [int] NULL,");
		sb.append("[Rx2gRank2] [int] NULL,");
		sb.append("[Rx2gRank3] [int] NULL,");
		sb.append("[Rx2gRank4] [int] NULL,");

		sb.append("[Ecio2gSum] [numeric](18, 2) NULL,");
		sb.append("[Ecio2gCount] [int] NULL,");
		sb.append("[Ecio2gRank0] [int] NULL,");
		sb.append("[Ecio2gRank1] [int] NULL,");
		sb.append("[Ecio2gRank2] [int] NULL,");
		sb.append("[Ecio2gRank3] [int] NULL,");
		sb.append("[Ecio2gRank4] [int] NULL,");

		sb.append("[StandGsm] [int] NULL,");
		sb.append("[SumGsm] [int] NULL,");
		sb.append("[RxGsmSum] [numeric](18, 2) NULL,");
		sb.append("[RxGsmCount] [int] NULL,");
		sb.append("[RxGsmRank0] [int] NULL,");
		sb.append("[RxGsmRank1] [int] NULL,");
		sb.append("[RxGsmRank2] [int] NULL,");
		sb.append("[RxGsmRank3] [int] NULL,");
		sb.append("[RxGsmRank4] [int] NULL");
		sb.append(")");

		return sb.toString();
	}

	//创建AnteInfo表Sql语句
	private String getCreateAnteInfoSql()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" CREATE TABLE IF NOT EXISTS " + AnteInfo); //不存在则创建
		sb.append("(");
		sb.append("[TID] [varchar](50) NOT NULL,");

		sb.append("[ROT] [varchar](50) NULL,");
		sb.append("[RSSI] [varchar](50) NULL,");
		sb.append("[USERNUM] [varchar](50) NULL,");
		sb.append("[VSWR] [varchar](50) NULL,");

		sb.append("[ROT_CARR_1] [varchar](50) NULL,");
		sb.append("[ROT_MAIN_1] [numeric](18, 8) NULL,");
		sb.append("[ROT_DIV_1] [numeric](18, 8) NULL,");
		sb.append("[ROT_CARR_2] [varchar](50) NULL,");
		sb.append("[ROT_MAIN_2] [numeric](18, 8) NULL,");
		sb.append("[ROT_DIV_2] [numeric](18, 8) NULL,");
		sb.append("[ROT_CARR_3] [varchar](50) NULL,");
		sb.append("[ROT_MAIN_3] [numeric](18, 8) NULL,");
		sb.append("[ROT_DIV_3] [numeric](18, 8) NULL,");
		sb.append("[ROT_CARR_4] [varchar](50) NULL,");
		sb.append("[ROT_MAIN_4] [numeric](18, 8) NULL,");
		sb.append("[ROT_DIV_4] [numeric](18, 8) NULL,");
		sb.append("[ROT_CARR_5] [varchar](50) NULL,");
		sb.append("[ROT_MAIN_5] [numeric](18, 8) NULL,");
		sb.append("[ROT_DIV_5] [numeric](18, 8) NULL,");
		sb.append("[ROT_CARR_6] [varchar](50) NULL,");
		sb.append("[ROT_MAIN_6] [numeric](18, 8) NULL,");
		sb.append("[ROT_DIV_6] [numeric](18, 8) NULL,");

		sb.append("[USERNUM_CARR_1] [varchar](50) NULL,");
		sb.append("[USERNUM_1] [numeric](18, 8) NULL,");
		sb.append("[USERNUM_CARR_2] [varchar](50) NULL,");
		sb.append("[USERNUM_2] [numeric](18, 8) NULL,");
		sb.append("[USERNUM_CARR_3] [varchar](50) NULL,");
		sb.append("[USERNUM_3] [numeric](18, 8) NULL,");
		sb.append("[USERNUM_CARR_4] [varchar](50) NULL,");
		sb.append("[USERNUM_4] [numeric](18, 8) NULL,");
		sb.append("[USERNUM_CARR_5] [varchar](50) NULL,");
		sb.append("[USERNUM_5] [numeric](18, 8) NULL,");
		sb.append("[USERNUM_CARR_6] [varchar](50) NULL,");
		sb.append("[USERNUM_6] [numeric](18, 8) NULL,");

		sb.append("[RSSI_CARR_1] [varchar](50) NULL,");
		sb.append("[RSSI_MAIN_1] [numeric](18, 8) NULL,");
		sb.append("[RSSI_DIV_1] [numeric](18, 8) NULL,");
		sb.append("[RSSI_CARR_2] [varchar](50) NULL,");
		sb.append("[RSSI_MAIN_2] [numeric](18, 8) NULL,");
		sb.append("[RSSI_DIV_2] [numeric](18, 8) NULL,");
		sb.append("[RSSI_CARR_3] [varchar](50) NULL,");
		sb.append("[RSSI_MAIN_3] [numeric](18, 8) NULL,");
		sb.append("[RSSI_DIV_3] [numeric](18, 8) NULL,");
		sb.append("[RSSI_CARR_4] [varchar](50) NULL,");
		sb.append("[RSSI_MAIN_4] [numeric](18, 8) NULL,");
		sb.append("[RSSI_DIV_4] [numeric](18, 8) NULL,");
		sb.append("[RSSI_CARR_5] [varchar](50) NULL,");
		sb.append("[RSSI_MAIN_5] [numeric](18, 8) NULL,");
		sb.append("[RSSI_DIV_5] [numeric](18, 8) NULL,");
		sb.append("[RSSI_CARR_6] [varchar](50) NULL,");
		sb.append("[RSSI_MAIN_6] [numeric](18, 8) NULL,");
		sb.append("[RSSI_DIV_6] [numeric](18, 8) NULL");
		sb.append(")");
		return sb.toString();
	}

	//创建PingInfo表Sql语句
	private String getCreatePingInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + PingInfo);
		sb.append("(");
		sb.append("[TID] [varchar](50) NULL,");
		sb.append("[DEST_ADDR] [varchar](100) NULL,");
		sb.append("[PAK_SIZE] [int] NULL,");
		sb.append("[RepeatCount] [int] NULL,");
		sb.append("[MAX_DELAY] [int] NULL,");
		sb.append("[MIN_DELAY] [int] NULL,");
		sb.append("[AVG_DELAY] [int] NULL,");
		sb.append("[SUC_COUNT] [int] NULL,");
		sb.append("[ALL_COUNT] [int] NULL");

		sb.append(")");
		return sb.toString();
	}

	//创建WebInfo表Sql语句
	private String getCreateWebInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + WebInfo);
		sb.append("(");
		sb.append("[TID] [varchar](50) NULL,");

		sb.append("[DEST_WEBSITE] [varchar](100) NULL,");
		sb.append("[TIMEOUT] [int] NULL,");
		sb.append("[REPEATCOUNT] [int] NULL,");
		sb.append("[BROSWERTYPE] [int] NULL,");
		sb.append("[MAX_TIME] [int] NULL,");
		sb.append("[MIN_TIME] [int] NULL,");
		sb.append("[AVG_TIME] [int] NULL,");
		sb.append("[LinkSucCount] [int] NULL,");
		sb.append("[SUC_COUNT] [int] NULL,");
		sb.append("[ALL_COUNT] [int] NULL,");

		// 流量
		sb.append("[SUM_SIZE] [int] NULL,");
		sb.append("[SUC_SIZE] [int] NULL,");
		sb.append("[AVG_SPEED] [numeric](18,8) NULL,");

		// 延时
		sb.append("[MAX_DELAY] [int] NULL,");
		sb.append("[MIN_DELAY] [int] NULL,");
		sb.append("[AVG_DELAY] [int] NULL,");
		sb.append("[PROGRESS_ALARM] [int] NULL");

		sb.append(")");
		return sb.toString();
	}

	//创建FtpInfo表Sql语句
	private String getCreateFtpInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + FtpInfo);
		sb.append("(");
		sb.append("[TID] [varchar](50) NULL,");

		sb.append("[SERVERADDRESS] [varchar](100) NULL,");
		sb.append("[SERVERPORT] [int] NULL,");
		sb.append("[USER] [varchar](10) NULL,");
		sb.append("[FILENAME] [varchar](100) NULL,");
		sb.append("[TIMEOUT] [int] NULL,");
		sb.append("[REPEATCOUNT] [int] NULL,");

		sb.append("[MAX_TIME] [int] NULL,");
		sb.append("[MIN_TIME] [int] NULL,");
		sb.append("[AVG_TIME] [int] NULL,");

		// 流量
		sb.append("[SERVERFILESIZE] [bigint] NULL,");
		sb.append("[MAX_FILESIZE] [bigint] NULL,");
		sb.append("[MIN_FILESIZE] [bigint] NULL,");
		sb.append("[AVG_FILESIZE] [bigint] NULL,");

		sb.append("[SUC_COUNT] [int] NULL,");
		sb.append("[ALL_COUNT] [int] NULL,");

		sb.append("[MAX_SPEED] [numeric](18,8) NULL,");
		sb.append("[MIN_SPEED] [numeric](18,8) NULL,");
		sb.append("[AVG_SPEED] [numeric](18,8) NULL,");
		sb.append("[SpeedCount] [int] NULL");
		sb.append(")");
		return sb.toString();
	}

	//创建WeiboInfo表Sql语句
	private String getCreateWeiboInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + WeiboInfo);
		sb.append("(");
		sb.append("[TID] [varchar](50) NULL,");

		sb.append("[TIMEOUT] [int] NULL,");
		sb.append("[REPEATCOUNT] [int] NULL,");
		sb.append("[Type] [int] NULL,");
		sb.append("[MAX_TIME] [int] NULL,");
		sb.append("[MIN_TIME] [int] NULL,");
		sb.append("[AVG_TIME] [int] NULL,");
		sb.append("[SUC_COUNT] [int] NULL,");
		sb.append("[ALL_COUNT] [int] NULL,");

		// 流量
		sb.append("[SUM_SIZE] [int] NULL,");
		sb.append("[SUC_SIZE] [int] NULL");
		sb.append(")");
		return sb.toString();
	}

	//创建DialInfo表Sql语句
	private String getCreateDialInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + DialInfo);
		sb.append("(");
		sb.append("[TID] [varchar](50) NULL,");

		sb.append("[DIALNUMBER] [varchar](20) NULL,");
		sb.append("[DIAL_TIMEOUT] [int] NULL,");
		sb.append("[RELEASE_TIMEOUT] [int] NULL,");
		sb.append("[REPEATCOUNT] [int] NULL,");

		sb.append("[MAX_ACC_TIME] [int] NULL,");
		sb.append("[MIN_ACC_TIME] [int] NULL,");
		sb.append("[AVG_ACC_TIME] [int] NULL,");
		sb.append("[MAX_RLS_TIME] [int] NULL,");
		sb.append("[MIN_RLS_TIME] [int] NULL,");
		sb.append("[AVG_RLS_TIME] [int] NULL,");
		sb.append("[CON_SUC_COUNT] [int] NULL,");
		sb.append("[CON_DROP_COUNT] [int] NULL,");
		sb.append("[SUC_COUNT] [int] NULL,");
		sb.append("[ALL_COUNT] [int] NULL");
		sb.append(")");
		return sb.toString();
	}

	//-------------------------------------------------------------------
	//创建LinkInfo表Sql语句
	private String getCreateLinkInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + LinkCellInfo);
		sb.append("(");
		sb.append("[LinkID] [varchar](100) NULL,");
		sb.append("[JobID] [varchar](100) NULL,");

		sb.append("[CityLte] [varchar](50) NULL,");
		sb.append("[BtsNoLte] [varchar](50) NULL,");
		sb.append("[BtsNameLte] [varchar](50) NULL,");
		sb.append("[SectorLte] [varchar](50) NULL,");
		sb.append("[MCC] [int] NULL,");
		sb.append("[MNC] [int] NULL,");
		sb.append("[CI] [varchar](20) NULL,");
		sb.append("[PCI] [varchar](20) NULL,");
		sb.append("[TAC] [varchar](20) NULL,");

		sb.append("[CityCdma] [varchar](50) NULL,");
		sb.append("[BscCdma] [varchar](50) NULL,");
		sb.append("[BtsNoCdma] [varchar](50) NULL,");
		sb.append("[BtsNameCdma] [varchar](50) NULL,");
		sb.append("[SectorCdma] [varchar](50) NULL,");
		sb.append("[CID] [varchar](20) NULL,");
		sb.append("[SID] [varchar](20) NULL,");
		sb.append("[NID] [varchar](20) NULL,");
		sb.append("[CELLID] [varchar](20) NULL,");
		sb.append("[LAC] [varchar](20) NULL,");

		sb.append("[Province] [varchar](10) NULL,");
		sb.append("[City] [varchar](10) NULL,");
		sb.append("[District] [varchar](10) NULL,");
		sb.append("[Street] [varchar](100) NULL,");
		sb.append("[StreetNum] [varchar](20) NULL");
		sb.append(")");
		return sb.toString();
	}

	//创建SignalInfo表Sql语句
	private String getCreateSignalInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + SignalInfo);
		sb.append("(");
		sb.append("[ID] [int] IDENTITY(1,1) NOT NULL,");
		sb.append("[Time] [bigint] NULL,");
		sb.append("[LinkID] [varchar](100) NULL,");

		sb.append("[RSRP] [numeric](18, 2) NULL,");
		sb.append("[RSRQ] [numeric](18, 2) NULL,");
		sb.append("[RSSI] [numeric](18, 2) NULL,");
		sb.append("[RSSNR] [numeric](18, 2) NULL,");
		sb.append("[CQI] [numeric](18, 2) NULL,");
		sb.append("[3GRx] [numeric](18, 2) NULL,");
		sb.append("	[3GEcio] [numeric](18, 2) NULL,");
		sb.append("[3GSNR] [numeric](18, 2) NULL,");
		sb.append("[2GRx] [numeric](18, 2) NULL,");
		sb.append("[2GEcio] [numeric](18, 2) NULL,");
		sb.append("[GSMRx] [numeric](18, 2) NULL,");
		sb.append("[Lon] [numeric](18, 5) NULL,");
		sb.append("[Lat] [numeric](18, 5) NULL");
		sb.append(")");
		return sb.toString();
	}

	//创建日志表
	private String getCreateInetLogSql()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" CREATE TABLE IF NOT EXISTS " + InetLog);
		sb.append("(");
		sb.append("[Time] [bigint] NULL,");
		sb.append("[UserID] [varchar](50) NULL,");
		sb.append("[PhoneNum] [varchar](50) NULL,");
		sb.append("[IMSI] [varchar](50) NULL,");
		sb.append("[IMEI] [varchar](50) NULL,");
		sb.append("[Model] [varchar](50) NULL,");
		sb.append("[VersionCode] [varchar](50) NULL,");
		sb.append("[Operator] [int] NULL,");
		sb.append("[NetWorkType] [int] NULL,");
		sb.append("[MCC] [varchar](50) NULL,");
		sb.append("[MNC] [varchar](50) NULL,");
		sb.append("[CI] [varchar](50) NULL,");
		sb.append("[SID] [varchar](50) NULL,");
		sb.append("[NID] [varchar](50) NULL,");
		sb.append("[CID] [varchar](50) NULL,");
		sb.append("[CIGsm] [varchar](50) NULL,");
		sb.append("[TAC] [varchar](50) NULL,");
		sb.append("[PreModule] [varchar](50) NULL,");
		sb.append("[Module] [varchar](50) NULL,");
		sb.append("[Delay] [int] NULL,");
		sb.append("[Level] [int] NULL,");
		sb.append("[Reserve] [varchar](50) NULL");
		sb.append(")");
		return sb.toString();
	}

	//创建BtsInfo表Sql语句
	private String getCreateBtsInfoSql()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE IF NOT EXISTS " + BtsInfo);
		sb.append("(");
		sb.append("[ID] integer primary key autoincrement ,");
		sb.append("[CITYNAME] [varchar](50),");
		sb.append("[CITY_ID] [varchar](50),");
		sb.append("[BSCNO] [varchar](50),");
		sb.append("[BTSNAME] [varchar](50) NULL,");
		sb.append("[BTSNO] [varchar](50) NULL,");
		sb.append("[SID] [varchar](50) NULL,");
		sb.append("[NID] [varchar](50) NULL,");
		sb.append("[CID] [varchar](50) NULL,");
		sb.append("[CELLID] [varchar](50) NULL,");
		sb.append("[PILOT_PN] [varchar](50) NULL,");
		sb.append("[LATITUDE] [numeric](18, 8) NULL,");
		sb.append("[LONGITUDE] [numeric](18, 8) NULL,");
		sb.append("[NETWORKTYPE] [varchar](50),");
		sb.append("[MAP_KEY] [varchar](50) NULL,");
		sb.append("[INSERT_DATE] [datetime] NULL");
		sb.append(")");
		return sb.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		switch (oldVersion)
		{
		case 1:
		case 2:
		case 3:
		case 4:
		case 6:
		case 7:
			db.execSQL("DROP TABLE TB_TASK_PING");
			db.execSQL("DROP TABLE TB_TASK_WEB");
			db.execSQL("DROP TABLE TB_TASK_IM");
			db.execSQL("DROP TABLE TB_TASK_FTP");

			db.execSQL(getCreateWebInfoSql());
			db.execSQL(getCreatePingInfoSql());
			db.execSQL(getCreateWeiboInfoSql());
			db.execSQL(getCreateFtpInfoSql());
			break;
		case 8:// 8版本的没有独立发布，和9版本合并在一起
			db.execSQL(getCreateDialInfoSql());
		case 9:
			db.execSQL("DROP TABLE TB_BTS_INFO");
			db.execSQL(getCreateBtsInfoSql());
			break;
		case 5:
			db.execSQL("ALTER TABLE TB_BTS_INFO ADD COLUMN INSERT_DATE datetime");
			break;
		default:
			break;
		}
	}
}