package com.wellcell.SubFrag.TestRec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.wellcell.ctdetection.R;

public class RecordActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		// display log file content
		if (getIntent().getCharSequenceExtra(BrowseFragment.RECORD) != null)
		{
			String fileName = String.valueOf(getIntent().getCharSequenceExtra(BrowseFragment.RECORD));
			TextView record = (TextView) findViewById(R.id.record);
			// retrieve file
			File tmp = new File(getDir(FileManager.DIRECTORY, Context.MODE_PRIVATE).getAbsolutePath() + "/" + fileName);
			if (!tmp.exists())
				return;
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
					record.append("\n" + line);
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
		}
	}
}