package OO_8_Taxiplus;

import java.util.LinkedList;

public class PassengerQueue {
	LinkedList<Passenger> passenger_list=new LinkedList<Passenger>();
	//OverView:passengequeue类是乘客队列类，新生成的乘客将会被加入队列
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(passenger_list==null)
			return false;
		for(Passenger p:passenger_list)
		{
			if(!p.repOK())
				return false;
		}
		return true;
	}
	public synchronized void add(Passenger p)
	{
		//REQUIRES：p非空
		//MODIFIES: 乘客列表passenger_list
		//EFFECTS:  向乘客列表中添加乘客
		this.passenger_list.add(p);
	}
	public synchronized int getSize()
	{
		//EFFECTS:  返回乘客列表的长度
		return this.passenger_list.size();
	}
	public synchronized Passenger get(int n)
	{
		//REQUIRES：乘客列表中有第n个成员
		//EFFECTS:  返回乘客列表中的第n个成员
		return this.passenger_list.get(n);
	}
	public synchronized Passenger remove(int n)
	{
		//REQUIRES：乘客列表中有第n个成员
		//MODIFIES: 乘客列表passenger_list
		//EFFECTS:  返回乘客列表中的第n个成员并删除之
		return this.passenger_list.remove(n);
	}
	public synchronized void clear()
	{
		//MODIFIES: 乘客列表passenger_list
		//EFFECTS:  清空乘客列表
		this.passenger_list.clear();
	}
	public synchronized boolean isEmpty()
	{
		//EFFECTS:  返回乘客列表是否为空
		return this.passenger_list.isEmpty();
	}

}
