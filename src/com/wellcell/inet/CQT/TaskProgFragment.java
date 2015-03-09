package com.wellcell.inet.CQT;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wellcell.ctdetection.R;
import com.wellcell.inet.CQT.TaskRetActivity.TaskProgListener;
import com.wellcell.inet.Common.CGlobal.ModuleIndex;
import com.wellcell.inet.Common.CGlobal.TestState;
import com.wellcell.inet.TaskList.TaskInfo;
import com.wellcell.inet.view.QExListView;

//CQT/客户经理 测试业务详细进度
public class TaskProgFragment extends Fragment implements TaskProgListener
{
	private List<TaskInfo> m_listTtaskinfo = new ArrayList<TaskInfo>();

	private ModuleIndex m_moduleTest;	//测试模块
	private TaskListAdapter adapter;
	private QExListView m_lvTask;

	private TaskInfoProvider m_provTaskInfo;
	private View m_vMsg;

	// 任务状态接口
	public interface TaskInfoProvider
	{
		public int getTaskCount();
		public String getTaskLog(int index);
		public TestState getTaskState(int index);
		public int getCurPorgress();
	}

	class ViewHolder
	{
		public TextView tvName;
		public ProgressBar pbLoading;
		public TextView tvResult;
	}
	
	public TaskProgFragment(ModuleIndex moduleIndex)
	{
		m_moduleTest = moduleIndex;
	}

	public static TaskProgFragment newInsance(TaskInfoProvider taskInfoProvider,ModuleIndex moduleIndex)
	{
		TaskProgFragment fragment = new TaskProgFragment(moduleIndex);
		fragment.m_provTaskInfo = taskInfoProvider;
		return fragment;
	}

	public void addTaskProgress(TaskInfo taskInfo)
	{
		m_listTtaskinfo.add(taskInfo);
		notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		switch (m_moduleTest)
		{
		case eCQT:
			m_vMsg = inflater.inflate(R.layout.cqt_taskprogress, null);
			m_lvTask = (QExListView) m_vMsg.findViewById(R.id.lv_task);
			adapter = new TaskListAdapter();
			m_lvTask.setAdapter(adapter);
			break;
		case eCust:
			m_vMsg = new TextView(getActivity());
			((TextView)m_vMsg).setText("   正在获取配置任务...");
			((TextView)m_vMsg).setTextSize(25);
			((TextView)m_vMsg).setTextColor(Color.BLACK);
			break;
		default:
			break;
		}
		
		return m_vMsg;
	}
	
	//显示信息
	public void showMsg(String strMsg)
	{
		if(m_vMsg != null)
			((TextView)m_vMsg).setText(strMsg);
	}

	class TaskListAdapter extends BaseExpandableListAdapter
	{
		@Override
		public Object getChild(int groupPosition, int childPosition)
		{
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
			return 0;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
		{
			switch (childPosition)
			{
			case 0:
				// 返回一个textview显示数据
				TextView tv = new TextView(getActivity());
				tv.setBackgroundColor(Color.parseColor("#E4EFF9"));
				tv.setTextColor(Color.parseColor("#1D486C"));
				tv.setPadding(10, 0, 0, 0);

				// 对任务结果赋值
				if (m_provTaskInfo.getTaskCount() <= groupPosition)
					tv.setText("没有相关记录");
				else
					tv.setText(m_provTaskInfo.getTaskLog(groupPosition));
				return tv;
			default:
				return null;
			}
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition)
		{
			return m_listTtaskinfo.get(groupPosition).m_strTaskName;
		}

		@Override
		public int getGroupCount()
		{
			return m_listTtaskinfo.size();
			// return taskInfoProvider.getCurPorgress();
		}

		@Override
		public long getGroupId(int groupPosition)
		{
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
		{
			ViewHolder holder = new ViewHolder();
			if (convertView == null)
			{
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_task_result, null);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_task);
				holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pb_task);
				holder.tvResult = (TextView) convertView.findViewById(R.id.tv_result);
				convertView.setTag(holder);
			}
			else
				holder = (ViewHolder) convertView.getTag();

			holder.tvName.setText(getGroup(groupPosition).toString());

			if (m_provTaskInfo.getTaskCount() > groupPosition)
			{
				if (m_provTaskInfo.getTaskState(groupPosition) == TestState.eTesting)
					holder.pbLoading.setVisibility(View.VISIBLE);
				else
					holder.pbLoading.setVisibility(View.GONE);
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds()
		{
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return false;
		}
	}

	@Override
	public void expandGroup(int index)
	{
		if (m_lvTask != null && index < adapter.getGroupCount())
			m_lvTask.expandGroup(index);
	}

	@Override
	public void collapseGroup(int index)
	{
		if (m_lvTask != null && index < adapter.getGroupCount())
			m_lvTask.collapseGroup(index);
	}

	@Override
	public void notifyDataSetChanged()
	{
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	public void invalidateGroupTop()
	{
		if (m_lvTask != null)
			m_lvTask.invalidateGroupTop();
	}

	@Override
	public void setTranscriptMode(int mode)
	{
		if (m_lvTask != null)
			m_lvTask.setTranscriptMode(mode);
	}
}
