package com.wellcell.ctdetection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.wellcell.MainFrag.AboutActivity;
import com.wellcell.MainFrag.AidInfoActivity;
import com.wellcell.MainFrag.DiagnosisActivity;
import com.wellcell.MainFrag.SettingActivity;
import com.wellcell.MainFrag.TaskListActivity;
import com.wellcell.MainFrag.TaskTestActivity;
import com.wellcell.MainFrag.TestRecActivity;

public class CommonActivity extends ActionBarActivity implements SidebarFragment.SideBarSelCallbacks
{

	private SidebarFragment m_sidebarFrag; // 侧边栏
	protected MainModule moduleMain;

	// 侧边栏菜单项
	public static enum MainModule
	{
		eTaskTest, // 业务测试
		eDiagnosis, // 辅助诊断
		eAidInfo, // 辅助信息
		eOneKey, // 一键测试
		eTestRec, // 测试记录
		eSetting, // 设置
		eAbout, // 关于
		eQuit
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common);

		// side bar
		m_sidebarFrag = (SidebarFragment) getSupportFragmentManager().findFragmentById(R.id.sidebardrawer);
		// Set up the drawer.
		m_sidebarFrag.setUp(R.id.sidebardrawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	protected void setModule(MainModule module)
	{
		moduleMain = module;
	}

	// 侧边栏选择
	@Override
	public void onSideBarItemSelected(int position)
	{
		// update the main content by replacing fragments
		MainModule moduleOther = MainModule.values()[position];
		if (moduleOther == null)
			return;

		if (moduleOther == moduleMain)
			return;

		Intent intent = null;
		switch (moduleOther)
		{
		case eSetting:
			intent = new Intent(this, SettingActivity.class);
			break;
		case eOneKey:
			intent = new Intent(this, TaskListActivity.class);
			break;
		case eAbout:
			intent = new Intent(this, AboutActivity.class);
			break;
		case eTestRec:
			intent = new Intent(this, TestRecActivity.class);
			break;
		case eTaskTest:
			intent = new Intent(this, TaskTestActivity.class);
			break;
		case eAidInfo:
			intent = new Intent(this, AidInfoActivity.class);
			break;
		case eDiagnosis:
			intent = new Intent(this, DiagnosisActivity.class);
			break;
		case eQuit:
			intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.putExtra(EnvelopActivity.MESSAGE_QUIT, true);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		default:
			return;
		}
		if (intent != null)
		{
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.global, menu);
		// custom menus
		if (SidebarFragment.DISPLAY)
			menu.findItem(R.id.action_settings).setVisible(false);
		return super.onCreateOptionsMenu(menu);
		// android.R.drawable.ic_menu_preferences
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		switch (item.getItemId())
		{
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}