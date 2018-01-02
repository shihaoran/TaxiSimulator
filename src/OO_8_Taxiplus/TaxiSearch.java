package OO_8_Taxiplus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class TaxiSearch extends Thread{
	private static final int SERVING=0;
	private static final int PICKING=1;
	private static final int WAITING=2;
	private static final int RESTING=3;
	private PassengerQueue passengerList;
	private LinkedList<Taxi> taxis;
	public TaxiSearch(PassengerQueue List,LinkedList<Taxi>taxis)
	{
		//REQUIRES：乘客列表，出租车列表非空
		//EFFECTS:  初始化对象内保存的乘客和出租车列表
		this.passengerList=List;
		this.taxis=taxis;
	}
	//OverView:Taxisearch类是乘客搜索出租车了，模拟了抢单时间窗，并在时间窗结束后根据指导书规则选择出租车
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(passengerList==null)
			return false;
		if(!passengerList.repOK())
			return false;
		if(taxis==null)
			return false;
		if(taxis.size()!=100)
			return false;
		for(Taxi t:taxis)
		{
			if(!t.repOK())
				return false;
		}
		return true;
	}
	public void run()
	{
		while(true)
		{
			try{sleep(1);}
			catch(InterruptedException e){}
			int size=passengerList.getSize();
			//LinkedList<Integer> removekeys=new LinkedList<Integer>();
			for(int key=0;key<size;key++)
			{
				Passenger p=passengerList.get(key);
				int x=p.getCallPoint().getX();
				int y=p.getCallPoint().getY();
				for(Taxi t:taxis)
				{
					if((Math.abs(t.getPosition().getX()-x)<=2)&&
					(Math.abs(t.getPosition().getY()-y)<=2)&&
					(t.getMode()==WAITING))
					{
						p.addTaxi(t);
					}
				}
				if(System.currentTimeMillis()-p.getTime()>=3000)
				{
					passengerList.remove(key);
					size--;
					p.getTaxi();
				}
			}
			
		}
	}
}
