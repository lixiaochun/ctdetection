package com.wellcell.inet.Task;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

//业务配置
public class TaskPar
{
	//nslookup配置参数
	public static class NslookupPar
	{
		public String m_strDest = "";

		public NslookupPar()
		{
			super();
		}

		public NslookupPar(String strJson)
		{
			JSONObject obj;
			try
			{
				obj = new JSONObject(strJson);
				m_strDest = obj.getString("DEST_ADDR");
			}
			catch (JSONException e)
			{
			}
		}
	}

	//ping配置参数
	public static class PingPar
	{
		public String m_strDest = ""; //目标地址
		public int m_nCount = 5; //测试次数
		public int m_nPackSize = 32; //包 大小

		public PingPar()
		{
		}

		public PingPar(String strJson)
		{
			JSONObject obj;
			try
			{
				obj = new JSONObject(strJson);

				m_strDest = obj.getString("DEST_ADDR");
				m_nCount = obj.getInt("REPEATCOUNT");
				m_nPackSize = obj.getInt("PACKAGE_SIZE");
			}
			catch (JSONException e)
			{
			}
		}
	}

	//Web配置参数
	public static class WebPar
	{
		private static String[] WebNames = { "网易", "新浪", "搜狐", "人民网", "凤凰网", "新浪微博", "苹果", "腾讯", "百度", "淘宝" };
		private static String[] URLS = { "www.163.com", "www.sina.com.cn", "www.sohu.com", "www.people.com.cn", "www.ifeng.com", "weibo.com/u/2964768094/home?wvr=5", "www.apple.com", "www.qq.com", "www.baidu.com", "www.taobao.com" };
		public String m_strName; //名称

		public String m_strUrl; //网址
		public int m_nSport; //端口号
		public int m_nTimeout; //超时时间(ms)
		public int m_nCount; //测试次数
		public int m_nProgAlarm; //时延标准

		//public BroswerType m_type;	//

		public WebPar(int nType)
		{
			m_nSport = 80;
			m_nTimeout = 30000;
			m_nCount = 1;

			setType(nType);
		}

		public WebPar(String strJson)
		{
			m_nSport = 80;

			try
			{
				JSONObject obj = new JSONObject(strJson);

				m_strUrl = obj.getString("DEST_WEBSITE");
				m_nTimeout = obj.getInt("TIMEOUT");
				m_nCount = obj.getInt("REPEATCOUNT");
				m_nProgAlarm = obj.getInt("PROGRESS_ALARM");
				//m_type = BroswerType.valueOf(obj.getString("BROSWERTYPE"));
			}
			catch (JSONException e)
			{
			}

			m_strUrl = m_strUrl.replace("http://", "");
			for (int i = 0; i < URLS.length; i++)
			{
				if (URLS[i].contains(m_strUrl) || m_strUrl.contains(URLS[i]))
				{
					m_strName = WebNames[i];
					break;
				}
			}
		}

		//设置网页类型
		public void setType(int nType)
		{
			m_strName = WebNames[nType];
			m_strUrl = URLS[nType];
		}
	}

	//FTP配置参数
	public static class FtpPar
	{
		public String m_strSrv; //服务器地址
		public int m_nPort;
		public String m_strUser;
		public String m_strPwd;
		public String m_strRemoteFile; //需要下载的文件
		public int m_nTimeout; //超时时间
		public int m_nCount; //测试次数
		public int m_nThreadNum; //线程数

		public FtpPar(String strJson)
		{
			try
			{
				JSONObject obj = new JSONObject(strJson);
				m_strSrv = obj.getString("IP");
				m_nPort = obj.getInt("PORT");
				m_strUser = obj.getString("USERNAME");
				m_strPwd = obj.getString("PASSWORD");
				m_strRemoteFile = obj.getString("FILENAME");
				m_nTimeout = obj.getInt("TIMEOUT");
				m_nCount = obj.getInt("REPEATCOUNT");
				m_nThreadNum = obj.getInt("THREADCOUNT");
			}
			catch (JSONException e)
			{
			}
		}
	}

	//微博配置参数
	public static class WeiboPar
	{
		public int m_nTimeout; //超时时间
		public int m_nCount; //测试次数

		//public WeiboFunction m_fun;	//

		public WeiboPar(String strJson)
		{
			try
			{
				JSONObject obj = new JSONObject(strJson);
				m_nTimeout = obj.getInt("TIMEOUT");
				m_nCount = obj.getInt("REPEATCOUNT");
				//m_fun = WeiboFunction.valueOf(obj.getString("WEIBOFUNCTION"));
			}
			catch (JSONException e)
			{
			}
		}
	}

	//Dial配置参数
	public static class DialPar
	{
		public String m_strNum; //拨打号码
		public int m_nTOAcc; //接通超时
		public int m_nTORel; //释放超时
		public int m_nCount; //测试次数

		public DialPar(String strJson)
		{
			try
			{
				JSONObject obj = new JSONObject(strJson);

				m_strNum = obj.getString("DIALNUMBER");
				m_nTOAcc = obj.getInt("DIAL_TIMEOUT");
				m_nTORel = obj.getInt("RELEASE_TIMEOUT");
				m_nCount = obj.getInt("REPEATCOUNT");
			}
			catch (JSONException e)
			{
			}
		}
	}

	//视频
	public static class VideoPar
	{
		private final String[] VideoType = { "其他", "自有服务器视频", "优酷", "土豆", "爱奇艺", "搜狐", "腾讯" };
		private String m_strVideoName = null; //视频名称
		public int m_nType; //1：自有服务器其URL，2：优酷，3：土豆，4：爱奇艺，5：搜狐视频，6：腾讯，0：其他
		public String m_strUrl; //视频地址
		public double m_dPlayRate; //播放比特率
		public int m_nTimeout; //超时时间(ms)
		public int m_nCount; //测试次数
		//public int m_nBufPercent;	//缓冲百分比
		public int m_nPlayDur; //播放时长(s)

		public VideoPar()
		{
			m_nType = 0;
			m_nTimeout = 60000;
			m_nCount = 1;
			m_nPlayDur = 30;
		}

		public VideoPar(String strJson)
		{
			try
			{
				JSONObject obj = new JSONObject(strJson);

				m_strVideoName = obj.getString("VideoName");
				m_nType = obj.getInt("VideoType");
				m_strUrl = obj.getString("VideoURL");
				m_dPlayRate = obj.getDouble("PlayRate");
				m_nTimeout = obj.getInt("TimeOut");
				m_nCount = obj.getInt("TestCount");
				m_nPlayDur = obj.getInt("PlayDur");
			}
			catch (JSONException e)
			{
			}
		}

		//获取视频类型
		public String getVideoType()
		{
			String strRet = "其他";
			if (m_nType < VideoType.length)
				strRet = VideoType[m_nType];

			return strRet;
		}

		//获取服务器IP
		public String getSrvIP()
		{
			String strIP = "";
			try
			{
				String strAddr = m_strUrl;
				String strHead = "http://";
				if (strAddr.startsWith(strHead))
					strAddr = strAddr.substring(strHead.length());

				int nIndex = strAddr.indexOf("/");
				if (nIndex > 0)
					strAddr = strAddr.substring(0, nIndex);

				nIndex = strAddr.indexOf(":");
				if (nIndex > 0)
					strAddr = strAddr.substring(0, nIndex);

				strIP = strAddr;
				String strTemp = InetAddress.getByName(strAddr).getHostAddress();
				if (strTemp != null && strTemp.length() > 0)
					strIP = strTemp;
			}
			catch (Exception e)
			{
			}

			return strIP;
		}

		//获取视频名称
		public String getVideoName()
		{
			if (m_strVideoName == null || m_strVideoName.length() == 0)
			{
				String strName = "";
				try
				{
					int nIndex = m_strUrl.lastIndexOf("/");
					if (nIndex > 0)
						strName = m_strUrl.substring(nIndex + 1, m_strUrl.length());
				}
				catch (Exception e)
				{
				}

				m_strVideoName = strName;
			}

			return m_strVideoName;
		}
	}

	//Traceroute配置参数
	public static class TraceroutePar
	{
		private String m_strDest = ""; //目标地址
		private String m_strDestIP = null; //目标IP
		public int m_nHops = 5; //跳转次数
		public double m_dTimeout = 2; //超时时间(ms)

		public TraceroutePar()
		{
		}

		public TraceroutePar(String strJson)
		{
			JSONObject obj;
			try
			{
				obj = new JSONObject(strJson);

				/*m_strDest = obj.getString("DEST_ADDR");
				m_nHoos = obj.getInt("REPEATCOUNT");
				m_nPackSize = obj.getInt("PACKAGE_SIZE");
				*/
			}
			catch (JSONException e)
			{
			}
		}

		//设置目标地址
		public void setDest(String strDst)
		{
			if (!m_strDest.equals(strDst))
			{
				m_strDest = strDst;
				m_strDestIP = null; //初始化目标IP
			}
		}

		//获取目标IP
		public String getDestIP()
		{
			try
			{
				if (m_strDestIP == null)
					m_strDestIP = InetAddress.getByName(m_strDest).getHostAddress().toString();
			}
			catch (UnknownHostException e)
			{
			}

			return m_strDestIP;
		}

		public String getDest()
		{
			return m_strDest;
		}
	}

}
