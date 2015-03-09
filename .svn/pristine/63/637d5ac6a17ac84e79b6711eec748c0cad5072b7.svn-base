package com.wellcell.inet.TaskList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.wellcell.ctdetection.DetectionApp;
import com.wellcell.ctdetection.R;
import com.wellcell.inet.CQT.TaskRetActivity;
import com.wellcell.inet.Common.CGlobal.ModuleIndex;
import com.wellcell.inet.Common.ExtraInfo;
import com.wellcell.inet.Common.InetDlg;

//测试任务列表窗口
public class TaskListActivity extends Activity implements OnClickListener
{
	private DetectionApp m_inetApp;
	private boolean m_bRefresh = false;	//是否正在刷新获取任务列表
	private List<TaskInfo> m_listTaskInfo = new ArrayList<TaskInfo>();	//业务信息
	private TaskListAdapter m_adpTaskList;	//任务列表适配器
	private List<View> m_listTaskView = new ArrayList<View>();	//测试过程所有view
	
	//private ModuleIndex m_modulePre = null;	//上一级的module

	private Button m_btnStart;		//开始按钮
	protected ProgressDialog m_pdLoading = null;
	boolean m_bTxAuth = false;	// 是否进行了腾讯认证
	
	//百度热点经纬度
	private boolean m_bHasHotID = false; //是否有热点ID
	private String m_strHotName;
	private String m_strHotID;
	private double m_dHotLon;
	private double m_dHotLat;

	class ViewHolder
	{
		public CheckBox m_cbName;
		public ProgressBar m_pbLoading;
		public TextView m_tvRet;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist);


		m_inetApp = (DetectionApp)getApplicationContext();
		
		//logThread thread = new logThread(this, "进入CQT测试");
		//new Thread(thread).start();

		//任务列表
		ListView lvTask = (ListView) findViewById(android.R.id.list);
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
		
		//--------------提取传递过来的参数---------------------------------------
		/*Bundle bundle = getIntent().getExtras();
		m_modulePre = ModuleIndex.valueOf(bundle.getString(ExtraInfo.Extra_Module));//模块
		if (m_modulePre == ModuleIndex.eCQT)
		{
			if (bundle.containsKey(HotPointInfo.Extra_HotID)) //如翼热点
			{
				m_strHotName = bundle.getString(HotPointInfo.Extra_HotName);
				m_strHotID = bundle.getString(HotPointInfo.Extra_HotID);
				m_bHasHotID = false;
			}
			else	//百度热点
			{
				m_strHotName = bundle.getString(HotPointInfo.Extra_HotName);
				m_dHotLon = bundle.getDouble(HotPointInfo.Extra_Lon);
				m_dHotLat = bundle.getDouble(HotPointInfo.Extra_Lat);

				m_strHotID = null;
				m_bHasHotID = true;
			}
		}*/

		//标题
		//((TextView) findViewById(R.id.tv_title)).setText(m_strHotName);
		//((TextView)findViewById(R.id.header)).setText("选择测试项目");

		m_btnStart = (Button)findViewById(R.id.btn_starttest); //开始按钮
		m_btnStart.setOnClickListener(this);	
		/*findViewById(R.id.btn_back).setOnClickListener(this);//返回按钮

		// 构建左侧按钮——刷新任务
		Button btnRefresh = (Button) findViewById(R.id.btn_left);
		btnRefresh.setBackgroundDrawable(getResources().getDrawable(R.drawable.sel_btn_refresh));
		//btnRefresh.setBackground(getResources().getDrawable(R.drawable.sel_btn_refresh));
		btnRefresh.setPadding(20, 5, 5, 5);
		btnRefresh.setVisibility(View.VISIBLE);
		btnRefresh.setOnClickListener(this);
		*/
		new Thread(getTaskList).start();	//获取任务列表
	}
	
	private final Handler m_hHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:	// 跳转到测试过程窗口
				Intent intent = new Intent(getApplicationContext(), TaskRetActivity.class);
				intent.putExtra(ExtraInfo.Extra_TaskInfo, msg.obj.toString());	//业务列表
				startActivity(intent);
				break;
			case 2:
				if (msg.obj != null)
					Toast.makeText(TaskListActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				
				break;
			case 3: //获取百度热点ID处理
				/*m_strHotID = (String) msg.obj;
				if (m_strHotID.length() <= 0)
					m_strHotID = null;

				if (m_strHotID != null)
					m_hHandler.postDelayed(authSinaWeibo, 500); //开始测试
				else
					Toast.makeText(getApplicationContext(), "获取百度热点ID失败,请重试", Toast.LENGTH_SHORT).show();*/
				break;
			case 4:	//获取任务列表处理
				View v = findViewById(R.id.v_loading);
				v.setVisibility(View.GONE);

				v = findViewById(R.id.layout_listview);
				v.setVisibility(View.VISIBLE);

				m_adpTaskList.InitViews();
				m_adpTaskList.notifyDataSetChanged();	//更新list
				m_bRefresh = false;	//更新完毕
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onResume()
	{
		//m_inetApp.m_inetLog.setStartTime(ActivityName.eTaskList);	
		
		//m_btnStart.setEnabled(true);
		//InetDlg.DismissDlg(m_pdLoading);
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
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
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		/*case ExtraInfo.REQUESTCODE_SEL_TASK:
			switch (resultCode)
			{
			case RESULT_OK:
				m_hHandler.post(addTasks);
				break;
			default:
				break;
			}
			break;*/
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 获取CQT任务列表
	private Runnable getTaskList = new Runnable()
	{
		@Override
		public void run()
		{	m_bRefresh = true;	//正在更新
			m_listTaskInfo = TaskUtil.getTaskList(TaskListActivity.this, ModuleIndex.eCQT);
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
				m_hHandler.sendMessage(Message.obtain(m_hHandler, 1, arr.toString()));	//调转到测试界面
			}
			else //没选中测试任务
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(getApplicationContext(), "没有选择任务，不能开始测试", Toast.LENGTH_SHORT).show();
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
		case R.id.btn_starttest:	//开始按钮
			new Thread(addTasks).start();
			break;
		case R.id.btn_back:	//返回按钮
			finish();
			break;
		case R.id.btn_left:	//刷新按钮
			/*if(m_bRefresh)	//正在刷新
				return;
			
			TaskRankPar.clear(this);
			TaskUtil.clearTaskList(this);
			m_listTaskInfo.clear();	//情况列表
			m_adpTaskList.notifyDataSetChanged();
			new Thread(getTaskList).start();
			*/break;
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
			
			if(m_listTaskInfo.size() <= 0)
			{
				Toast.makeText(getApplicationContext(), "获取任务列表失败!请重试...", Toast.LENGTH_SHORT).show();
				return;
			}

			for (int i = 0; i < m_listTaskInfo.size(); i++)
			{
				ViewHolder holder = new ViewHolder();
				View convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listitem_task, null);
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
								TaskUtil.saveTaskList(getApplicationContext(), m_listTaskInfo, ModuleIndex.eCQT);
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
