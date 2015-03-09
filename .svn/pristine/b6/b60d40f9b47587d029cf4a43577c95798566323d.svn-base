package com.wellcell.MainFrag;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.wellcell.ctdetection.R;
import com.wellcell.inet.CQT.TaskRetActivity;
import com.wellcell.inet.Common.CGlobal.ModuleIndex;
import com.wellcell.inet.Common.ExtraInfo;
import com.wellcell.inet.Common.InetDlg;
import com.wellcell.inet.TaskList.TaskInfo;
import com.wellcell.inet.TaskList.TaskUtil;

public class TaskListFragment extends Fragment implements OnClickListener
{
	private List<TaskInfo> m_listTaskInfo = new ArrayList<TaskInfo>(); //业务信息
	private TaskListAdapter m_adpTaskList; //任务列表适配器
	private List<View> m_listTaskView = new ArrayList<View>(); //测试过程所有view

	//private ModuleIndex m_modulePre = null;	//上一级的module

	private Button m_btnStart; //开始按钮
	protected ProgressDialog m_pdLoading = null;
	boolean m_bTxAuth = false; // 是否进行了腾讯认证

	private View rootView;

	class ViewHolder
	{
		public CheckBox m_cbName;
		public ProgressBar m_pbLoading;
		public TextView m_tvRet;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.tasklist, container, false);

		//logThread thread = new logThread(this, "进入CQT测试");
		//new Thread(thread).start();

		//任务列表
		ListView lvTask = (ListView) rootView.findViewById(android.R.id.list);
		m_adpTaskList = new TaskListAdapter();
		lvTask.setAdapter(m_adpTaskList);
		lvTask.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				v.requestFocus();
				return false;
			}
		});

		m_btnStart = (Button) rootView.findViewById(R.id.btn_starttest); //开始按钮
		m_btnStart.setOnClickListener(this);

		new Thread(getTaskList).start(); //获取任务列表

		return rootView;
	}

	@SuppressLint("HandlerLeak")
	private final Handler m_hHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1: // 跳转到测试过程窗口
				Intent intent = new Intent(getActivity().getApplicationContext(), TaskRetActivity.class);
				intent.putExtra(ExtraInfo.Extra_TaskInfo, msg.obj.toString()); //业务列表
				startActivity(intent);
				break;
			case 2:
				if (msg.obj != null)
					Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();

				break;
			case 3: //获取百度热点ID处理
				break;
			case 4: //获取任务列表处理
				View v = rootView.findViewById(R.id.v_loading);
				v.setVisibility(View.GONE);

				v = rootView.findViewById(R.id.layout_listview);
				v.setVisibility(View.VISIBLE);

				m_adpTaskList.InitViews();
				m_adpTaskList.notifyDataSetChanged(); //更新list
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onDestroyView()
	{
		InetDlg.DismissDlg(m_pdLoading);
		try
		{
			//if (m_bTxAuth)
			//	AuthHelper.unregister(getApplicationContext());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onDestroyView();
	}

	// 获取CQT任务列表
	private Runnable getTaskList = new Runnable()
	{
		@Override
		public void run()
		{
			m_listTaskInfo = TaskUtil.getTaskList(getActivity(), ModuleIndex.eCQT);
			m_hHandler.sendMessage(Message.obtain(m_hHandler, 4));
		}
	};
	//添加已选业务到测试列表
	private Runnable addTasks = new Runnable()
	{
		@Override
		public void run()
		{
			JSONArray arr = new JSONArray();

			// 设置任务
			ViewHolder holder;
			for (int i = 0; i < m_listTaskInfo.size(); i++)
			{
				holder = (ViewHolder) m_listTaskView.get(i).getTag();
				if (holder.m_cbName.isChecked())
					arr.put(m_listTaskInfo.get(i).toJsonObj());
			}

			if (arr.length() > 0)
			{
				m_hHandler.sendMessage(Message.obtain(m_hHandler, 1, arr.toString())); //调转到测试界面
			}
			else
			//没选中测试任务
			{
				getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(getActivity().getApplicationContext(), "没有选择任务，不能开始测试", Toast.LENGTH_SHORT).show();
						onResume();
					}
				});
			}

		}
	};

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_starttest: //开始按钮
			new Thread(addTasks).start();
			break;
		case R.id.btn_back: //返回按钮
			getActivity().finish();
			break;
		case R.id.btn_left: //刷新按钮
			break;
		default:
			break;
		}
	}

	//-----------------------------------------------------------------------------------
	//任务列表适配器
	class TaskListAdapter extends BaseAdapter
	{
		public void InitViews()
		{
			m_listTaskView.clear(); //清空

			if (m_listTaskInfo.size() <= 0)
			{
				Toast.makeText(getActivity().getApplicationContext(), "获取任务列表失败!请重试...", Toast.LENGTH_SHORT).show();
				return;
			}

			for (int i = 0; i < m_listTaskInfo.size(); i++)
			{
				ViewHolder holder = new ViewHolder();
				View convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listitem_task, null);
				holder.m_cbName = (CheckBox) convertView.findViewById(R.id.cb_task);
				holder.m_pbLoading = (ProgressBar) convertView.findViewById(R.id.pb_task);
				holder.m_tvRet = (TextView) convertView.findViewById(R.id.tv_result);
				convertView.setTag(holder);

				holder.m_cbName.setChecked(m_listTaskInfo.get(i).m_bChecked);
				holder.m_cbName.setText(getItem(i).toString());
				holder.m_cbName.setTag(i);
				holder.m_cbName.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						if (m_listTaskInfo != null)
						{
							try
							{
								int i = (Integer) buttonView.getTag();
								m_listTaskInfo.get(i).m_bChecked = isChecked;
								TaskUtil.saveTaskList(getActivity().getApplicationContext(), m_listTaskInfo, ModuleIndex.eCQT);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}

					}
				});

				m_listTaskView.add(i, convertView);
			}
		}

		@Override
		public int getCount()
		{
			return m_listTaskInfo.size();
		}

		@Override
		public Object getItem(int position)
		{
			return m_listTaskInfo.get(position).m_strTaskName;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			return m_listTaskView.get(position);
		}
	}
}