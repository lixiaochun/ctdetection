package com.wellcell.inet.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

public class QExListView extends ExpandableListView
{
	private boolean m_bCanExpand = true;	//是否可以展开
	private boolean m_bCanCollaspe = true;	//是否可以折叠
	private ViewGroup m_vgGroup;
	public int m_nGroupIndex = -1;

	public QExListView(Context context)
	{
		super(context);
	}

	public QExListView(Context context, AttributeSet attr)
	{
		super(context, attr);
	}

	public QExListView(Context context, AttributeSet attr, int defStyle)
	{
		super(context, attr, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		handlerGroupTop();
	}

	public void expandGroups()
	{
		for (int i = 0; i < getExpandableListAdapter().getGroupCount(); i++)
		{
			expandGroup(i);
		}
	}

	public void collapseGroups()
	{
		for (int i = 0; i < getExpandableListAdapter().getGroupCount(); i++)
		{
			collapseGroup(i);
		}
	}

	public void setCanExpand(boolean canExpand)
	{
		this.m_bCanExpand = canExpand;
	}

	public void setCanCollaspe(boolean canCollaspe)
	{
		this.m_bCanCollaspe = canCollaspe;
	}

	@Override
	public boolean expandGroup(int groupPos)
	{
		if (m_bCanExpand)
			return super.expandGroup(groupPos);
		else
			return false;
	}

	@Override
	public boolean collapseGroup(int groupPos)
	{
		if (m_bCanCollaspe)
			return super.collapseGroup(groupPos);
		else
			return false;
	}

	protected void handlerGroupTop()
	{
		int ptp = this.pointToPosition(0, 0);
		if (ptp != AdapterView.INVALID_POSITION)
		{
			long pos = getExpandableListPosition(ptp);
			int groupPos = ExpandableListView.getPackedPositionGroup(pos);
			int childPos = ExpandableListView.getPackedPositionChild(pos);

			if (childPos < 0)
				groupPos = -1;
			
			if (groupPos < m_nGroupIndex)
			{
				m_nGroupIndex = groupPos;

				if (m_vgGroup != null)
				{
					m_vgGroup.removeAllViews();
					m_vgGroup.setVisibility(GONE);// 这里设置Gone 为了不让它遮挡后面header
				}
			}
			else if (groupPos > m_nGroupIndex)
			{
				m_nGroupIndex = groupPos;
				invalidateGroupTop();
			}
		}
	}

	public void invalidateGroupTop()
	{
		if (m_nGroupIndex != -1)
		{
			final RelativeLayout fl = (RelativeLayout) getParent();
			if (m_vgGroup != null)
				fl.removeView(m_vgGroup);

			m_vgGroup = (ViewGroup) getExpandableListAdapter().getGroupView(m_nGroupIndex, true, null, null);
			m_vgGroup.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					collapseGroup(m_nGroupIndex);

					fl.removeView(m_vgGroup);
					fl.addView(m_vgGroup);
				}
			});

			fl.addView(m_vgGroup, fl.getChildCount());
		}
	}
}
