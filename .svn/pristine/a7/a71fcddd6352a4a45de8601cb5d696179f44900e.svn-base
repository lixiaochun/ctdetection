package com.wellcell.MainFrag;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.wellcell.ctdetection.R;
import com.wellcell.inet.Database.LocalCache;

//设置主界面
public class SettingFragment extends Fragment implements OnCheckedChangeListener, OnClickListener
{
	private RadioGroup m_rgUpLoadType; //上传模式
	private EditText m_etThreadCount; //线程数
	private EditText m_etSizeLimit; //测速流量限制
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.activity_setting, container, false);
		//上传模式
		m_rgUpLoadType = (RadioGroup) rootView.findViewById(R.id.uploadtype);
		m_rgUpLoadType.setOnCheckedChangeListener(this);

		//线程数
		m_etThreadCount = (EditText) rootView.findViewById(R.id.et_threadcount);
		m_etSizeLimit = (EditText) rootView.findViewById(R.id.et_sizelimit);

		rootView.findViewById(R.id.btn_clear).setOnClickListener(this);
		rootView.findViewById(R.id.btn_save).setOnClickListener(this);
		LoadPar();
		return rootView;
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1)
	{
		switch (arg0.getCheckedRadioButtonId())
		{
		case R.id.schedule:
			rootView.findViewById(R.id.pick_time).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.byhand).setVisibility(View.GONE);
			break;
		case R.id.single:
			rootView.findViewById(R.id.pick_time).setVisibility(View.GONE);
			rootView.findViewById(R.id.byhand).setVisibility(View.GONE);
			break;
		case R.id.handle:
			rootView.findViewById(R.id.byhand).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.pick_time).setVisibility(View.GONE);
			break;
		}
	}

	//加载配置数据
	private boolean LoadPar()
	{
		SharedPreferences sp = getActivity().getSharedPreferences(LocalCache.SP_Setting, Context.MODE_PRIVATE);
		String strJson = LocalCache.getValueFromSharePreference(sp, SettingPar.class.getSimpleName());

		if (strJson.length() > 0)
		{
			SettingPar settingPar = new SettingPar(strJson);
			m_etThreadCount.setText(settingPar.m_nThreadCount + "");
			m_etSizeLimit.setText(settingPar.m_nSizeLimit + "");
			return true;
		}

		return false;
	}

	//保存数据
	private boolean savePar()
	{
		SettingPar settingPar = new SettingPar();
		try
		{
			String strVal = m_etThreadCount.getText().toString();
			if (strVal.length() > 0)
				settingPar.m_nThreadCount = Integer.parseInt(strVal);

			strVal = m_etSizeLimit.getText().toString();
			if (strVal.length() > 0)
				settingPar.m_nSizeLimit = Integer.parseInt(strVal);

			SharedPreferences sp = getActivity().getSharedPreferences(LocalCache.SP_Setting, Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = sp.edit();
			LocalCache.saveValueToSharePreference(editor, SettingPar.class.getSimpleName(), LocalCache.WEEK, settingPar.getJson());
			editor.commit();
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_clear: //清理缓存
			for (int i = 0; i < LocalCache.cacheSpNames.length; i++)
			{
				LocalCache.clear(getActivity(), LocalCache.cacheSpNames[i]);
			}
			Toast.makeText(getActivity(), "清除缓存成功!", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_save: //保存配置
			String strMsg = "";
			if (savePar())
				strMsg = "保存配置成功!";
			else
				strMsg = "保存配置出错！请重新配置保存。";

			Toast.makeText(getActivity(), strMsg, Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	//配置项
	public static class SettingPar
	{
		public int m_nUploadType = 0; //上传类型
		public int m_nThreadCount = 7; //线程数
		public int m_nSizeLimit = 10; //流量限制

		public SettingPar()
		{

		}

		public SettingPar(String strJson)
		{
			try
			{
				JSONArray jsonObj = new JSONArray(strJson);

				int nIndex = 0;
				m_nUploadType = jsonObj.getInt(nIndex++);
				m_nThreadCount = jsonObj.getInt(nIndex++);
				m_nSizeLimit = jsonObj.getInt(nIndex++);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		public String getJson()
		{
			JSONArray jsonObj = new JSONArray();
			try
			{
				jsonObj.put(m_nUploadType);
				jsonObj.put(m_nThreadCount);
				jsonObj.put(m_nSizeLimit);
			}
			catch (Exception e)
			{
			}
			return jsonObj.toString();
		}
	}
}
