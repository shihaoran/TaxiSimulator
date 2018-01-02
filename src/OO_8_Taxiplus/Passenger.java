package OO_8_Taxiplus;

import java.util.HashMap;
import java.util.LinkedList;

public class Passenger {
	private static final int SERVING=0;
	private static final int PICKING=1;
	private static final int WAITING=2;
	private static final int RESTING=3;
	private Point call_point;
	private Point des_point;
	private Center center;
	private int no;
	private long time;
	private HashMap<Integer,Taxi> availabletaxi=new HashMap<Integer,Taxi>();
	
	public Passenger(int no,Point c,Point d,Center center)
	{
		//REQUIRES：c,d在地图范围内
		//EFFECTS:  根据给出的位置初始化乘客
		this.no=no;
		this.call_point=c;
		this.des_point=d;
		this.center=center;
		this.time=System.currentTimeMillis();
	}
	//OverView:passenger类是乘客类，负责通过构造器传入的坐标生成乘客，同时记录当前系统时间，同时本类中也保存了抢到该乘客单的出租车。
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(call_point==null)
			return false;
		if(!call_point.repOK())
			return false;
		if(des_point==null)
			return false;
		if(!des_point.repOK())
			return false;
		if(center==null)
			return false;
		if(!center.repOK())
			return false;
		if(no<0)
			return false;
		if(time<0)
			return false;
		if(availabletaxi==null)
			return false;
		return true;
	}
	public void addTaxi(Taxi t)
	{
		//REQUIRES：t非空
		//MODIFIES: 抢单出租车列表availabletaxi，出租车信用credit
		//EFFECTS:  向乘客对象中保存抢单成功的出租车，并将抢单成功的出租车信用加1
		if(!availabletaxi.containsKey(t.getNum()))
		{
			availabletaxi.put(t.getNum(), t);
			t.addCredit(1);
		}
	}
	public Taxi getTaxi()
	{
		//MODIFIES: 抢单出租车列表availabletaxi
		//EFFECTS:  从抢单成功的出租车中按照指导书要求选择最终接载乘客的出租车
		int maxCredit=0;
		int minDistance=-1;
		int distance=0;
		LinkedList<Taxi> creditList=new LinkedList<Taxi>();
		Taxi choosed=null;
		for(int no:availabletaxi.keySet())
		{
			Taxi t=availabletaxi.get(no);
			if(t.getMode()==WAITING)
			{
				if(t.getCredit()>maxCredit)
				{
					maxCredit=t.getCredit();
					creditList.clear();
					creditList.add(t);
				}
				else if(t.getCredit()==maxCredit)
				{
					creditList.add(t);
				}
			}
		}
		if(creditList.isEmpty())
		{
			System.out.println("乘客"+call_point+"无车可坐");
			return null;
		}
		for(Taxi t:creditList)
		{
			distance=Math.abs(t.getPosition().getX()-call_point.getX())+
							Math.abs(t.getPosition().getY()-call_point.getY());
			if(minDistance==-1)
			{
				minDistance=distance;
				choosed=t;
			}
			if(distance<minDistance)
			{
				minDistance=distance;
				choosed=t;
			}
		}
		choosed.setMode(PICKING);
		choosed.setStart(call_point);
		choosed.setEnd(des_point);
		System.out.println("乘客"+call_point+"被"+choosed.getNum()+"号出租接载");
		return choosed;
	}

	public Point getCallPoint()
	{
		//EFFECTS:  返回乘客的叫车位置
		return this.call_point;
	}
	public Point getDesPoint()
	{
		//EFFECTS:  返回乘客的目的位置
		return this.des_point;
	}
	public long getTime()
	{
		//EFFECTS:  返回乘客的叫车时间
		return this.time;
	}
}
