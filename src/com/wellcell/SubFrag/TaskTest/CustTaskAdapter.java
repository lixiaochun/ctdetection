package com.wellcell.SubFrag.TaskTest;


import java.util.HashMap;
import java.util.Map;

import com.wellcell.ctdetection.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

//大众版测试--感知测试--业务列表及结果adapter
public class CustTaskAdapter extends BaseAdapter
{
	private Context m_context = null;
	private Map<Integer, CustTask> m_mapItem = new HashMap<Integer, CustTask>(); //数据源<索引,任务>
	private boolean m_bEnabled = true; //checkbox是否可选

	public CustTaskAdapter(Context context)
	{
		this.m_context = context;
	}

	@Override
	public int getCount()
	{
		return m_mapItem.size();
	}

	@Override
	public Object getItem(int position)
	{
		return m_mapItem.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{

		ViewGroup layout = null;
		if (convertView == null)
			layout = (ViewGroup) LayoutInflater.from(m_context).inflate(R.layout.custtasklist_item, parent, false);
		else
			layout = (ViewGroup) convertView;

		final CustTask custTask = m_mapItem.get(position); //提取Item

		//item内容
		final CheckBox cbCheck = (CheckBox) layout.findViewById(R.id.cbCheckBox);
		cbCheck.setText(custTask.getTaskName());
		((TextView) layout.findViewById(R.id.tvAvgSpeed)).setText(custTask.getAvgSpeed());
		((TextView) layout.findViewById(R.id.tvSize)).setText(custTask.getSize());
		((TextView) layout.findViewById(R.id.tvDelay)).setText(custTask.getDelay());

		//设置单选按钮的选中
		cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (m_bEnabled) //可选
				{
					custTask.setChecked(isChecked);
					m_mapItem.put(position, custTask); //将选择项加载到map里面寄存
				}
				else //不可选
					cbCheck.setChecked(!isChecked);
			}
		});

		cbCheck.setChecked(custTask.IsChecked());

		return layout;
	}

	//所有项是否可选
	@Override
	public boolean areAllItemsEnabled()
	{
		return m_bEnabled;
	}

	//新增一项
	public void add(CustTask custTask)
	{
		this.m_mapItem.put(m_mapItem.size(), custTask);
	}

	//更新
	public void update(Integer nIndex, CustTask custTask)
	{
		this.m_mapItem.put(nIndex, custTask);
	}
	
	//设置checkbox是否可选
	public void setSelectStatus(boolean bSelected)
	{
		m_bEnabled = bSelected;
	}

	//初始化列表数据
	public void Init()
	{
		Integer nIndex;
		CustTask custTask;
		for (Integer key : m_mapItem.keySet())
		{
			nIndex = key;
			custTask = m_mapItem.get(key);
			custTask.Init();
			update(nIndex, custTask);
		}
		notifyDataSetChanged();
	}

	//返回adapter数据
	public Map<Integer, CustTask> getDatas()
	{
		return this.m_mapItem;
	}
}
