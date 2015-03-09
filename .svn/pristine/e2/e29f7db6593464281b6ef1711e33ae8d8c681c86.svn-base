package com.wellcell.inet.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.util.Base64;

//数据压缩
public class GzipHelper
{
	/**
	 * 功能: 压缩字符串
	 * 参数: strSrc: 压缩字符串
	 * 返回值:
	 * 说明:
	 */
	public static String getGzipCompress(String strSrc)
	{
		byte[] buffer = strSrc.getBytes();

		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(os);
			gzipOutputStream.write(buffer);
			gzipOutputStream.flush();
			gzipOutputStream.close();

			return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 功能: 解压缩字符串
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public static String getGzipDecompress(String strSrc)
	{
		byte[] data = Base64.decode(strSrc, Base64.DEFAULT);
		return getGzipDecompress(data);
	}

	public static String getGzipDecompress(byte[] data)
	{
		try
		{
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			GZIPInputStream gzipInputStream = new GZIPInputStream(is);

			byte[] buffer = new byte[1024];
			int length = 0;

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			while ((length = gzipInputStream.read(buffer, 0, buffer.length)) > 0)
			{
				os.write(buffer, 0, length);
			}

			return os.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
