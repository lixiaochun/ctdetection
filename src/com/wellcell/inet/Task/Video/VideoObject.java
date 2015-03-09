package com.wellcell.inet.Task.Video;


import java.util.Timer;
import java.util.TimerTask;

import com.wellcell.inet.Common.CGlobal;
import com.wellcell.inet.Task.TempAidInfo;
import com.wellcell.inet.Task.TaskPar.VideoPar;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;

//视频测试对象
public class VideoObject implements OnPreparedListener, OnCompletionListener, 
OnVideoSizeChangedListener,OnInfoListener,OnBufferingUpdateListener,OnErrorListener
{
	private static final String TAG = "VideoObj";
	
	//视频信息回调
	public interface VideoMsgCallBack
	{
		public void onReceived(String msg);
		public void onUpdateInfo(VideoCbType type,Object obj); //视频测试状态及结果信息回调
	}
	private VideoMsgCallBack m_cbVideoMsg = null;
	private SurfaceHolder m_surfaceHolder = null;
	private Context m_context = null;
	
	private MediaPlayer m_MediaPlayer;
	private VideoPar m_videoPar;		//视频参数
	private VideoRet m_videoRet;		//测试结果
	private boolean m_bFinished = false;//是否测试完毕
	private boolean m_bFirstSpeed = true;//是否是第一个速率值(剔除第一个值)
	
	private Timer m_timerOT;	//超时定时器
	
	public long m_lStartTime;		//测试时间
	private long m_lLinkSucTime = 0;//连接成功时间

	//-----------------------------------------------
	private int m_nPreBuffer = 0;	//前一个缓冲进度
	
	//视频播放状态
	public enum VideoState
	{
		eReady,		//准备就绪
		eLink,		//连接状态
		eBuffering,	//播放前缓冲
		ePlaying,	//播放
		eStop		//停止
	}
	private VideoState m_videoState = VideoState.eReady;
	
	//-------------------------------------------------------
	//回调信息类型
	public enum VideoCbType
	{
		eStatus,	//状态
		eSpeed,		//速率
		eStuck,		//卡顿
		eRet		//结果
	}
	//-------------------------------------------------------
	
	public VideoObject(Context context,VideoPar videoPar,VideoMsgCallBack videoMsg,SurfaceHolder surface)
	{
		m_context = context;
		m_videoPar = videoPar;
		m_cbVideoMsg = videoMsg;
		
		m_MediaPlayer = new MediaPlayer(m_context);
		m_surfaceHolder = surface;
		if(m_surfaceHolder != null)
		{
			m_surfaceHolder.setFormat(PixelFormat.RGBA_8888);
			//m_surfaceHolder.setKeepScreenOn(true);
			m_MediaPlayer.setDisplay(m_surfaceHolder); //设置用于显示媒体视频的SurfaceHolder。这个调用是可选的。只显示音频而不显示视频时不调用这个方法（例如后台播放）
		}
		
		m_MediaPlayer.setOnBufferingUpdateListener(this);		//更新流媒体缓存状态
		m_MediaPlayer.setOnCompletionListener(this);			//视频播放完成后调用
		m_MediaPlayer.setOnPreparedListener(this);				//视频预处理完成后调用
		m_MediaPlayer.setOnVideoSizeChangedListener(this);		//视频大小已知或更新后调用
		m_MediaPlayer.setOnInfoListener(this);		//警告或错误信息监听
		m_MediaPlayer.setOnErrorListener(this);
	}
	
	//设置配置参数
	public void setPar(VideoPar videoPar)
	{
		m_videoPar = videoPar;
	}
	
	//初始化
	public void Init()
	{
		m_bFirstSpeed = true;
		m_bFinished = false;
		m_lStartTime = 0;
		m_lLinkSucTime = 0;
		
		m_videoRet = new VideoRet(m_videoPar);	//重新生成结果集
		m_timerOT = new Timer();
		
		m_videoState = VideoState.eReady;
	}

	//开始视频测试
	public void startTest()
	{
		try
		{
			//Log.i(TAG, "设置数据源..........");
			Init();	//初始化
			m_lStartTime = System.currentTimeMillis();
			m_videoRet.setStartTime();	//开始时间
			m_videoState = VideoState.eLink;	//连接阶段
			m_MediaPlayer.reset();
			m_MediaPlayer.setDataSource(m_videoPar.m_strUrl);	//设置多媒体数据源
			m_MediaPlayer.prepareAsync();
			
			setTimeOutTimer();	//设置控制定时器
		}
		catch (Exception e)
		{
			e.printStackTrace();
			stopTest();
		}
	}
	
	//停止测试
	public void stopTest()
	{
		try
		{
			m_MediaPlayer.stop();		//停止播放
			//m_MediaPlayer.release();	//释放mediaplayer
			//m_MediaPlayer.reset();		//初始化
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		onStopTest();	//停止处理
	}
	
	//是否测试完毕
	public boolean IsFinished()
	{
		return m_bFinished;
	}
	
	//获取视频测试结果
	public VideoRet getVideoRet()
	{
		return m_videoRet;
	}
	
	//当前播放位置
	public long getCurPos()
	{
		try
		{
			return m_MediaPlayer.getCurrentPosition();
		}
		catch (Exception e)
		{
			return -1;
		}
	}
	
	//获取视频长度
	public long getVideoDuration()
	{
		try
		{
			return m_MediaPlayer.getDuration();
		}
		catch (Exception e)
		{
			return -1;
		}
	}
	
	//设置定时器
	private void setTimeOutTimer()
	{
		TimerTask tt = new TimerTask() //超时控制
		{
			@Override
			public void run()
			{
				if (!m_bFinished)
				{
					long lCurTime = System.currentTimeMillis();
					switch (m_videoState)
					{
					case eLink:	//连接阶段
						if(m_lStartTime != 0 && lCurTime - m_lStartTime > m_videoPar.m_nTimeout) //连接超时
						{
							m_videoRet.m_nRet = VideoRet.Video_LinkTO;
							if(m_cbVideoMsg != null)
								m_cbVideoMsg.onReceived("连接超时");
							
							stopTest();
						}
						break;
					case eBuffering:	//缓冲阶段
						if(m_lLinkSucTime != 0 && lCurTime - m_lLinkSucTime > m_videoPar.m_nTimeout) //缓冲超时
						{
							m_videoRet.m_nRet = VideoRet.Video_BufTO;
							if(m_cbVideoMsg != null)
								m_cbVideoMsg.onReceived("缓冲超时");
							
							stopTest();
						}
						
						//设置播放时间
						if(m_videoRet.m_lPlayTime == 0 && m_MediaPlayer.getCurrentPosition() > 0)
						{
							m_videoState = VideoState.ePlaying;	//播放状态
							if(m_cbVideoMsg != null)
							{
								m_cbVideoMsg.onReceived("缓冲完毕，开始播放...");
								m_cbVideoMsg.onUpdateInfo(VideoCbType.eStatus, m_videoState);
							}
							m_videoRet.setPlayTime(System.currentTimeMillis());	//设置播放时间
							
							m_videoRet.m_lVideoDur = m_MediaPlayer.getDuration()/1000;	//视频时长
						}
						break;
					case ePlaying:	//播放阶段
						if (m_videoPar.m_nPlayDur != 0)
						{
							//播放时长控制
							if (m_MediaPlayer.getCurrentPosition() >= m_videoPar.m_nPlayDur * 1000) //播放时间控制
							{
								m_videoRet.m_nRet = VideoRet.Video_Suc;
								m_videoRet.m_bSuc = true; //播放成功
								stopTest();
							}

							//播放超时控制(两倍播放时长)
							if(m_videoRet.m_lPlayTime != 0 && lCurTime - m_videoRet.m_lPlayTime > m_videoPar.m_nPlayDur * 2000)
							{
								m_videoRet.m_nRet = VideoRet.Video_PlayTO;
								m_videoRet.m_bSuc = true; //播放成功
								stopTest();
							}
						}
						break;
					default:
						break;
					}
				}
			}
		};
		
		try
		{
			m_timerOT.schedule(tt,100, 100);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//=======================================================================================================
	//准备播放监听(连接成功)
	@Override
	public void onPrepared(MediaPlayer mp)
	{
		//Log.i(TAG, "准备播放"  + "  buffer: " + m_nPreBuffer);
		m_lLinkSucTime = System.currentTimeMillis();
		m_videoRet.setLinkedTime();	//连接成功时间
		m_videoState = VideoState.eBuffering;	//缓冲阶段
		if (m_cbVideoMsg != null)
		{
			m_cbVideoMsg.onReceived("连接成功，正在缓冲...");
			m_cbVideoMsg.onUpdateInfo(VideoCbType.eStatus, m_videoState);
		}

		m_MediaPlayer.start();
	}
	
	//视频大小已知或更新监听
	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
	{
		if (width == 0 || height == 0)
		{
			//Log.i(TAG, "invalid video width(" + width + ") or height(" + height + ")");
			return;
		}
		
		//if(m_surfaceHolder != null)
		//	m_surfaceHolder.setFixedSize(width, height);
	}
	
	//警告或错误信息监听
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra)
	{
		switch (what)
		{
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:	//播放错误（一般视频播放比较慢或视频本身有问题会引发）
			//Log.i(TAG, "播放错误: " + extra);
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:	//暂停播放等待缓冲更多数据
			if(m_MediaPlayer.getCurrentPosition() > 0)
			{
				m_videoRet.StuckDeal(m_MediaPlayer.getCurrentPosition());	//卡顿处理
				m_videoState = VideoState.ePlaying;	//播放状态
				
				if(m_cbVideoMsg != null)
					m_cbVideoMsg.onUpdateInfo(VideoCbType.eStatus, m_videoState);
			}
			//Log.i(TAG, "开始缓存，暂停播放: " + extra + "  buffer: " + m_nPreBuffer + " Pos:" + m_MediaPlayer.getCurrentPosition() + "/" + m_MediaPlayer.getDuration());
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:	//缓冲完后继续播放
			m_videoState = VideoState.ePlaying;	//播放状态
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:	//卡顿
			Log.i(TAG, "播放卡顿: " + extra + "  buffer: " + m_nPreBuffer + " Pos:" + m_MediaPlayer.getCurrentPosition());
			m_videoRet.StuckDeal(m_MediaPlayer.getCurrentPosition());	//卡顿处理
			m_videoState = VideoState.ePlaying;	//播放状态
			if (m_cbVideoMsg != null)
				m_cbVideoMsg.onUpdateInfo(VideoCbType.eStuck, m_videoRet);
			break;
		case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:	//媒体不支持Seek，例如直播流
			//Log.i(TAG, "媒体不支持Seek: " + extra);
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:	//av_read_frame()的速度（KB/s）
			//Log.i(TAG,"速率: " + extra);
			if(m_bFirstSpeed) //第一个值剔除
			{
				m_bFirstSpeed = false;
				break;
			}
			
			m_videoRet.addSpeed(extra);	//收集速率
			
			String strSpeed = CGlobal.getSpeedString(extra * 1000);
			switch (m_videoState)
			{
			case eBuffering:
				strSpeed = "正在缓冲，速率：" + strSpeed;
				break;
			case ePlaying:
				strSpeed = "正在播放，速率：" + strSpeed;
				break;
			default:
				strSpeed = "速率：" + strSpeed;
				break;
			}
			
			if (m_cbVideoMsg != null)
			{
				m_cbVideoMsg.onReceived(strSpeed);
				m_cbVideoMsg.onUpdateInfo(VideoCbType.eSpeed, extra);
			}
			break;
		default:
			Log.i(TAG,"Unknown message type: " + what + "," + extra);
			break;
		}
		
		return false;
	}
	
	//网络视频流缓冲变化监听
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent)
	{
		if(m_nPreBuffer != percent)
		{
			m_nPreBuffer = percent;
			//Log.i(TAG,"bufferUpdate:" + percent);
		}		
	}

	/**
	 * 错误信息监听
	 * 如果处理了错误返回true，否则返回false。返回false或没有设置OnErrorListener将引发OnCompletionListener被调用
	 */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		String strTag = "";
	    switch (what)
		{
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK: //播放错误（一般视频播放比较慢或视频本身有问题会引发）
			strTag = "播放错误";
			break;
		case MediaPlayer.MEDIA_ERROR_IO: //文件或网络相关的错误
			strTag = "IO错误";
			break;
		case MediaPlayer.MEDIA_ERROR_MALFORMED: //比特流格式错误
			strTag = "格式错误";
			break;
		case MediaPlayer.MEDIA_ERROR_TIMED_OUT:	//超时
			strTag = "超时";
			break;
		case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:	//不支持格式
			strTag = "不支持格式";
			break;
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:	
			strTag = "Unknow";
			break;
		default:
			strTag = what + "";
			break;
		}
	    
	    //状态
	    String strInfo;
	    switch (m_videoState)
		{
		case eLink:	//连接状态
			m_videoRet.m_nRet = VideoRet.Video_LinkError;	
			strInfo = "连接错误：" + what;
			break;
		case eBuffering://缓冲
			m_videoRet.m_nRet = VideoRet.Video_BufError;
			strInfo = "缓冲错误：" + what;
			break;
		case ePlaying://播放
			m_videoRet.m_nRet = VideoRet.Video_PlayError;
			strInfo = "播放错误：" + what;
			break;
		default:
			m_videoRet.m_nRet = VideoRet.Video_Unknow;	//未知错误
			strInfo = "未知错误：" + what;
			break;
		}
	    
	    if(m_cbVideoMsg != null)
	    	m_cbVideoMsg.onReceived(strInfo);
	    
	    stopTest();	//停止测试
	    
	    Log.i(TAG, "onError: " + strTag + ":" + extra); 
	      
		return false;
	}

	//播放完毕监听（备注：error会调用此函数）
	@Override
	public void onCompletion(MediaPlayer mp)
	{
		//Log.i(TAG, "播放完毕");
		if(m_videoRet.m_nRet == VideoRet.Video_Init) //防止error调用
			m_videoRet.m_bSuc = true;
		
		stopTest();
	}
	
	//停止测试处理
	private void onStopTest()
	{
		m_bFinished = true;	//完成标志
		m_videoState = VideoState.eStop;
		
		try
		{
			m_timerOT.cancel();
			m_timerOT.purge();
			
			m_videoRet.onCompleted();	//完成处理
			
			if(m_cbVideoMsg != null)
				m_cbVideoMsg.onUpdateInfo(VideoCbType.eRet, m_videoRet);	//结果信息	
		}
		catch (Exception e)
		{
		}
	}
}
