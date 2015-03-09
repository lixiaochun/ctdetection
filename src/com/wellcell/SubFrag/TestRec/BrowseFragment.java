package com.wellcell.SubFrag.TestRec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wellcell.ctdetection.R;

public class BrowseFragment extends Fragment implements OnItemClickListener
{
	public static final String RECORD = "record_file_name";
	private ArrayList<Record> records;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		records = new ArrayList<Record>();
		for (String tmp : FileManager.fetch(getActivity()))
			records.add(new Record(Record.Network.WIFI, Calendar.getInstance().getTime(), Record.Operation.HTTP, tmp));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// return super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_browse, null);
		ListView list = (ListView) view.findViewById(R.id.records);
		list.setAdapter(new RecordsAdapter(getActivity(), R.layout.list_item_browse, records));
		list.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		startActivity(new Intent(getActivity(), RecordActivity.class).putExtra(RECORD, records.get(arg2).fileName));
	}
}

class Record
{
	public static enum Network
	{
		WIFI, MOBILE
	}

	public static enum Operation
	{
		HTTP, VIDEO
	}

	Network networkType;
	Date testTime;
	Operation operationType;
	String fileName;

	public Record(Network network, Date time, Operation oper, String file)
	{
		networkType = network;
		testTime = time;
		operationType = oper;
		fileName = file;
	}
}

class RecordsAdapter extends ArrayAdapter<Record>
{
	public RecordsAdapter(Context context, int textViewResourceId, ArrayList<Record> objects)
	{
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		records = objects;
		this.context = context;
	}

	private ArrayList<Record> records;
	private Context context;

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_browse, parent, false);
			holder = new ViewHolder();
			holder.networkType = (TextView) convertView.findViewById(R.id.network_type);
			holder.testTime = (TextView) convertView.findViewById(R.id.test_time);
			holder.operationType = (TextView) convertView.findViewById(R.id.operation_type);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		switch (records.get(position).networkType)
		{
		case WIFI:
			holder.networkType.setText(R.string.wifi);
			break;
		case MOBILE:
			holder.networkType.setText(R.string.mobile_data);
			break;
		}
		// test file name
		switch (records.get(position).operationType)
		{
		case HTTP:
			holder.operationType.setText(R.string.http);
			break;
		case VIDEO:
			holder.operationType.setText(R.string.video);
			break;
		}
		holder.testTime.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(records.get(position).testTime));
		return convertView;
	}

	static class ViewHolder
	{
		TextView networkType, testTime, operationType;
	}
}

class FileManager
{
	// 将文件放在log文件夹中
	public static final String DIRECTORY = "log";

	/**
	 * 
	 * @param context
	 * @return 返回所有文件名，放在一个数组中
	 */
	public static String[] fetch(Context context)
	{
		return context.getDir(DIRECTORY, Context.MODE_PRIVATE).list();
	}

	// test
	//	public static void inital(Context context) throws IOException
	//	{
	//		for (int i = 0; i < 4; i++)
	//			write(context, "text" + i, "hello world" + i);
	//	}

	/**
	 * 
	 * @param context
	 * @param file The file name of file you want to write into.
	 * @param content The content you want to write into the file.
	 */
	public static void write(Context context, String file, String content)
	{
		// retrieve file
		File tmp = new File(context.getDir(DIRECTORY, Context.MODE_PRIVATE).getAbsolutePath() + "/" + file);
		if (!tmp.exists())
			try
			{
				tmp.createNewFile();
				// write string to file
				FileWriter writer = null;
				writer = new FileWriter(tmp);
				writer.write(content);
				if (writer != null)
					writer.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	/**
	 * 
	 * @param context
	 * @param file The file you want to append into.
	 * @param content The content you want to append.
	 */
	public static void append(Context context, String file, String content)
	{
		// retrieve file
		File tmp = new File(context.getDir(DIRECTORY, Context.MODE_PRIVATE).getAbsolutePath() + "/" + file);
		if (!tmp.exists())
			try
			{
				tmp.createNewFile();

				// write string to file
				FileWriter writer = null;
				writer = new FileWriter(tmp);
				writer.append(content);
				writer.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	/**
	 * 
	 * @param context
	 * @param file The log file you want to read from.
	 * @return The whole content of the file as a String.
	 */
	public static String read(Context context, String file)
	{
		String result = "";
		// retrieve file
		File tmp = new File(context.getDir(FileManager.DIRECTORY, Context.MODE_PRIVATE).getAbsolutePath() + "/" + file);
		if (!tmp.exists())
			return null;
		// write string to file
		FileReader reader = null;
		try
		{
			reader = new FileReader(tmp);
			BufferedReader buffreader = new BufferedReader(reader);

			String line;

			// read every line of the file into the line-variable, on line at the time
			while ((line = buffreader.readLine()) != null)
			{
				// do something with the settings from the file
				result = result + "\n" + line;
			}
			reader.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}