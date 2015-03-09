package com.wellcell.inet.view;

import com.wellcell.ctdetection.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

//速率仪表盘
public class SpeedPanel extends SurfaceView implements Callback
{
	private SurfaceHolder m_sufaceHolder;
	private Bitmap m_bmpPanel, m_bmpPointer;	//仪表盘及指针
	
	private Paint m_paint;
	private Canvas m_canvas;

	private int m_nScreenW;	//屏幕宽度
	private int m_nPanelX, m_nPanelY;	//面板左上角坐标
	private int m_nPointerX, m_nPointerY;	//指针左上角坐标
	
	private Rect m_bgRect;
	public int m_nDegrees = -90;	//角度

	public SpeedPanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		m_sufaceHolder = getHolder();
		m_sufaceHolder.addCallback(this);
		//setZOrderOnTop(true);	//放置顶层,防止拖动出现黑影
		//m_sufaceHolder.setFormat(PixelFormat.TRANSPARENT);	//设置透明
		
		m_paint = new Paint();
		m_paint.setAntiAlias(true);
		m_paint.setColor(Color.rgb(215,235,255));
		m_paint.setTextSize(22);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		m_bmpPanel = BitmapFactory.decodeResource(getResources(), R.drawable.panlte);
		m_bmpPointer = BitmapFactory.decodeResource(getResources(), R.drawable.needle);
		
		int nY = getHeight();
		m_nScreenW = getWidth();
		m_bgRect = new Rect(0, 0, m_bmpPanel.getWidth(), m_bmpPanel.getHeight());
		if(m_nScreenW - m_bmpPanel.getWidth() > 0)
			m_nPanelX = (m_nScreenW - m_bmpPanel.getWidth()) / 2;
		else
			m_nPanelX = 0;
		m_nPanelY = 0;
		m_nPointerX = m_nPanelX + m_bmpPanel.getWidth() / 2 - m_bmpPointer.getWidth() / 2;
		//m_nPointerY = m_bmpPanel.getHeight() - m_bmpPointer.getHeight()/2 - 33;
		m_nPointerY = (int)((m_bmpPanel.getHeight() - m_bmpPointer.getHeight()/2)/1.8);

		onDrawed(0);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
	}

	//画图
	public void onDrawed(int nIncreDegs)
	{
		try
		{
			m_canvas = m_sufaceHolder.lockCanvas(m_bgRect);
			//m_canvas.drawRect(new Rect(0, 0, m_bmpPanel.getWidth(), m_bmpPanel.getHeight()), m_paint);
			m_canvas.drawColor(Color.rgb(215,235,255));
			
			//重绘指针
			m_canvas.drawBitmap(m_bmpPanel, m_nPanelX, m_nPanelY, m_paint);
			m_canvas.save();
			m_canvas.rotate(m_nDegrees + nIncreDegs, m_nPointerX + m_bmpPointer.getWidth() / 2, m_nPointerY + m_bmpPointer.getHeight() / 2);
			m_canvas.drawBitmap(m_bmpPointer, m_nPointerX, m_nPointerY, m_paint);
			m_canvas.restore();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			m_sufaceHolder.unlockCanvasAndPost(m_canvas);
		}
		
		//m_nDegrees++;	//角度自增
	}
}
