package com.wellcell.inet.Task;

//所有统计指标的分段及操作
//默认为五个分段
public class IndicRank
{
	//四个间隔,由低到高
	private int Rank0 = -1;
	private int Rank1 = -1;
	private int Rank2 = -1;
	private int Rank3 = -1;
	
	public double m_dSum;	//有效指标总和
	public int m_nCount;	//记录数
	
	//五个分段
	public int m_nPart0 = 0;
	public int m_nPart1 = 0;
	public int m_nPart2 = 0;
	public int m_nPart3 = 0;
	public int m_nPart4 = 0;
	
	public IndicRank()
	{
		Init();
	}
	
	public IndicRank(int rank0,int rank1,int rank2,int rank3)
	{
		Init();
		
		Rank0 = rank0;
		Rank1 = rank1;
		Rank2 = rank2;
		Rank3 = rank3;
	}
	
	//初始化
	public void Init()
	{
		m_dSum = 0;
		m_nCount = 0;

		m_nPart0 = 0;
		m_nPart1 = 0;
		m_nPart2 = 0;
		m_nPart3 = 0;
		m_nPart4 = 0;		
	}
	
	//分段
	public void setRank(double dVal)
	{
		m_dSum += dVal;
		m_nCount++;
		
		if (dVal < Rank0)	//(-∞,Rank0)
			m_nPart0++;
		else if (dVal >= Rank0 && dVal < Rank1)	//[Rank0,Rank1)
			m_nPart1++;
		else if (dVal >= Rank1 && dVal < Rank2)	//[Rank1,Rank2)
			m_nPart2++;
		else if (dVal >= Rank2 && dVal < Rank3)	//[Rank2,Rank3)
			m_nPart3++;
		else if (dVal >= Rank3)	//[Rank3,+∞)
			m_nPart4++;
	}
}
