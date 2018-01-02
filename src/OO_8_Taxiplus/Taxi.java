package OO_8_Taxiplus;

import java.util.LinkedList;
import java.util.Random;

public class Taxi extends Thread{
	protected Point position;
	protected Point formerposition;
	protected Point start;
	protected Point end;
	protected int no;
	protected int mode;
	protected int credit;
	protected Center center;
	protected static final int SIZE=80;
	protected static final int SERVING=0;
	protected static final int PICKING=1;
	protected static final int WAITING=2;
	protected static final int RESTING=3;
	protected int waiting_count;
	public Taxi (int number,Center center)
	{
		//REQUIRES：number大于等于0小于100
		//EFFECTS:  初始化出租车，并随机选择一个初始位置
		this.no=number;
		this.mode=WAITING;
		this.credit=0;
		this.center=center;
		waiting_count=0;
		Random rand=new Random ();
		position=new Point(rand.nextInt(SIZE),rand.nextInt(SIZE));
		formerposition=position;
	}
	//OverView:Taxi类是出租车类，保存了出租车的信息，将会根据不同的模式进行运动
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(position==null)
			return false;
		if(!position.repOK())
			return false;
		if(formerposition==null)
			return false;
		if(!formerposition.repOK())
			return false;
		if(!start.repOK())
			return false;
		if(!end.repOK())
			return false;
		if(no<0||no>=100)
			return false;
		if(mode<0||mode>3)
			return false;
		if(credit<0)
			return false;
		if(!center.repOK())
			return false;
		if(waiting_count<0||waiting_count>20)
			return false;
		return true;
		
	}
	public void run()
	{
		while(true)
		{
			try{sleep(100);}
			catch(InterruptedException e){}
			if(mode==SERVING)
			{
				Point temp=center.getShortNext(position,formerposition,end);
				if(temp!=null)
				{
					formerposition=position;
					position=temp;
				}
				update();
					if(this.position.equals(end))
					{
						this.mode=RESTING;
						update();
						try{sleep(1000);}
						catch(InterruptedException e){}
						System.out.println(this.getNum()+"号出租车到达目的地"+this.end);
						this.mode=WAITING;
						this.credit+=3;
						update();
					}
			}
			else if(mode==PICKING)
			{
				Point temp=center.getShortNext(position,formerposition,start);
				if(temp!=null)
				{
					formerposition=position;
					position=temp;
				}
				update();
					if(this.position.equals(start))
					{
						this.mode=RESTING;
						update();
						try{sleep(1000);}
						catch(InterruptedException e){}
						this.mode=SERVING;
						update();
					}
					
			}
			else if(mode==WAITING)
			{
				Point temp=center.getNext(position,formerposition);
				if(temp!=null)
				{
					formerposition=position;
					position=temp;
				}
				update();
				waiting_count++;
				if(waiting_count==20)
				{
					try{
						this.mode=RESTING;
						update();
						sleep(1000);
						waiting_count=0;
						this.mode=WAITING;
						update();
					}
					catch(InterruptedException e){}
				}
			}
		}
	}
	public void update()
	{
		//REQUIRES：当前出租车号大于等于0小于100
		//MODIFIES: 信用统计taxi_credit,状态统计taxi_mode,位置taxi_position
		//EFFECTS:  更新center中储存的出租车信息
		center.updateCredit(no, credit);
		center.updateMode(no, mode);
		center.updatePosition(no, position);
	}
	public Point getPosition()
	{
		//EFFECTS: 返回出租车当前位置
		return this.position;
	}
	public int getNum()
	{
		//EFFECTS: 返回出租车编号
		return this.no;
	}
	public int getCredit()
	{
		//EFFECTS: 返回出租车信用
		return this.credit;
	}
	public int getMode()
	{
		//EFFECTS: 返回出租车运行模式
		return this.mode;
	}
	public void setMode(int mode)
	{
		//REQUIRES：模式是0-3之间的整数
		//MODIFIES: 出租车模式mode
		//EFFECTS:  设置当前出租车运行模式
		this.mode=mode;
	}
	public void setStart(Point p)
	{
		//REQUIRES：p在地图范围内
		//MODIFIES: 乘客位置start
		//EFFECTS:  设置当前出租车接载乘客位置
		this.start=p;
	}
	public void setEnd(Point p)
	{
		//REQUIRES：p在地图范围内
		//MODIFIES: 乘客目的地end
		//EFFECTS:  设置当前出租车目的地
		this.end=p;
	}
	public void addCredit(int c)
	{
		//REQUIRES：c为1或3
		//MODIFIES: 出租车信用credit
		//EFFECTS:  设置当前出租车信用
		this.credit+=c;
	}
	public boolean isAdvance()
	{
		//EFFECTS:  返回当前出租车是否是超级出租
		return false;
	}
}
