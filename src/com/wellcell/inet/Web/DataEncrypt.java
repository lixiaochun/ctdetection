package com.wellcell.inet.Web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class DataEncrypt
{
	public static final String[] KEYS = {"YJKLData","InetData","InetBTS","InetAnte","InetTask","InetPara","RankPara"};
	/**
	 * 功能:	压缩并加密数据
	 * 参数:	strInput: 源数据
	 * 		strKey: 密钥
	 * 返回值:
	 * 说明: 先对数据压缩,再进行加密
	 */
	public static String getGzipEncrypt_DES(String strInput, String strKey)
	{
		String strRetCom = getGzipCompress(strInput);	//压缩
		return encryptDES(strRetCom, strKey);	//加密
	}

	/**
	 * 功能: 解压并解密数据
	 * 参数:	strInput: 源数据
	 * 		strKey: 密钥
	 * 返回值:
	 * 说明: 先对数据解压缩,再进行解密
	 */
	public static String getUngzDecrypt_DES(String strInput, String strKey)
	{
		String strRetDecry = decryptDES(strInput, strKey);//解密
		return getGzipDecompress(strRetDecry);	//解压
		//return URLDecoder.decode(strRet);	
	}
	
	//========================================================================
	/**
	 * 功能: DES加密
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	private static String encryptDES(String strInput, String strKey)
	{
		try
		{
			byte[] keys = strKey.getBytes();
			SecretKeySpec keySpec = new SecretKeySpec(keys, "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(keys));
			byte[] encryptedData = cipher.doFinal(strInput.getBytes());

			return Base64.encodeToString(encryptedData, Base64.DEFAULT);
		}
		catch (Exception e)
		{
			return "";
		}
	}
	/**
	 * 功能: DES解密
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	private static String decryptDES(String strInput, String strKey)
	{
		try
		{
			byte[] keys = strKey.getBytes();
			byte[] byteMi = Base64.decode(strInput, Base64.DEFAULT);
			SecretKeySpec key = new SecretKeySpec(keys, "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(keys));
			byte decryptedData[] = cipher.doFinal(byteMi);

			return new String(decryptedData);
		}
		catch (Exception e) 
		{
			return "";
		}
	}


	/**
	 * 功能: 压缩字符
	 * 参数:
	 * 返回:
	 * 说明:
	 */
	private static String getGzipCompress(String input)
	{
		byte[] buffer = input.getBytes();

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
	 * 返回:
	 * 说明:
	 */
	private static String getGzipDecompress(String input)
	{
		byte[] data = Base64.decode(input, Base64.DEFAULT);
		return getGzipDecompress(data);
	}

	private static String getGzipDecompress(byte[] data)
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

			return os.toString("UTF8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
