package OO_8_Taxiplus;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Test extends Thread{
	private static final int SLEEP_INTERVAL=500;
	private static final int SIZE=80;
	private static final int PassengerAmount=100;
	private PassengerQueue passengerList;
	private Center center;
	public Test(PassengerQueue passengerList,Center center)
	{
		//REQUIRES：乘客队列非空，center存在
		//EFFECTS:  初始化乘客队列
		this.passengerList=passengerList;
		this.center=center;
	}
	//OverView:Test类是生成乘客类，模拟随机生成乘客
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(passengerList==null)
			return false;
		if(!passengerList.repOK())
			return false;
		if(center==null)
			return false;
		if(!center.repOK())
			return false;
		return true;
	}
	public void run()
	{
		int passenger_count=0;
		while(true)
		{
			Random rand = new Random();
			try {
				TimeUnit.MILLISECONDS.sleep(rand.nextInt(SLEEP_INTERVAL));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Point c=new Point(rand.nextInt(SIZE),rand.nextInt(SIZE));
			Point d=new Point(rand.nextInt(SIZE),rand.nextInt(SIZE));
			Passenger p=new Passenger(passenger_count,c,d,center);
			this.passengerList.add(p);
			passenger_count++;
			if(passenger_count==PassengerAmount)
				break;
		}
		
	}

}
