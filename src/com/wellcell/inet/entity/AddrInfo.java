package com.wellcell.inet.entity;

//import com.baidu.location.BDLocation;
import com.baidu.location.BDLocation;
import com.wellcell.inet.Common.CGlobal;

//地址信息
public class AddrInfo
{
	public String m_strProv = "";		//省份
	public String m_strCity = "";		//城市
	public String m_strDistrict = "";	//区/县	
	public String m_strStreet = "";		//街道
	public String m_strStreetNum = "";	//街道号
	
	//当前经纬度
	public double m_dLon = 0;
	public double m_dLat = 0;
	
	public AddrInfo()
	{
		
	}
	
	/**
	 * 功能: 提取详细信息
	 * 参数:
	 * 返回值:
	 * 说明:
	 */
	public void getAddrInfo(BDLocation dbLoc)
	{
		if(dbLoc != null)
		{
			if(dbLoc.getProvince() != null)
				m_strProv = dbLoc.getProvince();
			
			if(dbLoc.getCity() != null)
				m_strCity = dbLoc.getCity();
			
			if(dbLoc.getDistrict() != null)
				m_strDistrict = dbLoc.getDistrict();
			
			if(dbLoc.getStreet() != null)
				m_strStreet = dbLoc.getStreet();
			
			if(dbLoc.getStreetNumber() != null)
				m_strStreetNum = dbLoc.getStreetNumber();
			
			m_dLon = CGlobal.floatFormat(dbLoc.getLongitude(), 5);
			m_dLat = CGlobal.floatFormat(dbLoc.getLatitude(), 5);
		}
	}
	
	//判断经纬度是否有效
	public boolean IsValidLonLat()
	{
		if(m_dLon != 0 && m_dLat != 0)
			return true;
		else 
			return false;
	}
	
	//更新经纬度信息
	public void updateLonLat(double dLon,double dLat)
	{
		if(!IsValidLonLat())
		{
			m_dLon = dLon;
			m_dLat = dLat;
		}
	}
	
	//返回格式化经纬度信息
	public String getLonLatString()
	{
		return "(" + m_dLon + " , " + m_dLat + ")";
	}

	@Override
	public String toString()
	{	//返回详细地址信息
		return m_strProv + m_strCity + m_strDistrict 
				+ m_strStreet + m_strStreetNum;
	}
	
	/**
	 * 功能:计算两点之间的距离
	 * 参数:
	 * 返回值: km
	 * 说明:
	 */
	public static double getDistinct(double lon1, double lat1, double lon2, double lat2)
	{
		double dR = 6371.0;	//地球半径(km)
		double dDist = 0.0;
		double dLat = (lat2 - lat1) * Math.PI / 180;
		double dLon = (lon2 - lon1) * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) 
				+ Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		dDist = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * dR;
		return dDist;
	}
	
	/**
	 * 功能: 获取两点距离
	 * 参数:
	 * 返回值:	m
	 * 说明:
	 */
	public double getDist(double dLon, double dLat)
	{
		if(!IsValidLonLat() || dLon == 0 || dLat == 0)
			return -1;
		
		return getDistinct(m_dLon, m_dLat, dLon, dLat) * 1000;
	}
}
