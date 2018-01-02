package OO_8_Taxiplus;

import java.util.LinkedList;
import java.util.Random;

public class TaxiAdvance extends Taxi{
	private int serve_count;
	private LinkedList<PathPoint> HistoryPath=new LinkedList<PathPoint>();
	//OverView:TaxiAdvance类是超级出租车类，保存了出租车的信息，将会根据不同的模式进行运动
	public TaxiAdvance(int number, Center center) {
		//REQUIRES：number大于等于0小于100
		//EFFECTS:  初始化出租车，并随机选择一个初始位置
		super(number, center);
		serve_count=0;
		// TODO Auto-generated constructor stub
	}
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(!super.repOK())
			return false;
		if(serve_count<0)
			return false;
		if(HistoryPath==null)
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
				Point temp=center.getShortNextAdvance(position,formerposition,end);
				if(temp!=null)
				{
					formerposition=position;
					position=temp;
					PathPoint newpp=new PathPoint(this.no,this.serve_count,formerposition);
					HistoryPath.add(newpp);
				}
				update();
					if(this.position.equals(end))
					{
						this.mode=RESTING;
						PathPoint newpp=new PathPoint(this.no,this.serve_count,position);
						HistoryPath.add(newpp);
						update();
						try{sleep(1000);}
						catch(InterruptedException e){}
						System.out.println(this.getNum()+"号超级出租车到达目的地"+this.end);
						this.mode=WAITING;
						this.credit+=3;
						update();
					}
			}
			else if(mode==PICKING)
			{
				Point temp=center.getShortNextAdvance(position,formerposition,start);
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
						serve_count++;
						update();
					}
					
			}
			else if(mode==WAITING)
			{
				Point temp=center.getNextAdvance(position,formerposition);
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
	public LinkedList<PathPoint> getHistoryPath()
	{
		//EFFECTS:  返回当前出租车服务路径
		return this.HistoryPath;
	}
	public int getServeCount()
	{
		//EFFECTS:  设置当前出租车服务次数
		return this.serve_count;
	}
	public TwoWayIterator PathAtIndex(int index)
	{
		//EFFECTS:  返回迭代器
		if(index>this.serve_count||index<1)
		{
			System.out.println("要求的服务次数超出范围");
			return null;
		}
		return new TaxiGen(this,index);
	}
	public boolean isAdvance()
	{
		//EFFECTS:  返回当前出租车是否是超级出租
		return true;
	}
	private static class TaxiGen implements TwoWayIterator
	{
		private TaxiAdvance t;
		private int n;
		private int num;
		private LinkedList<PathPoint> path;
		TaxiGen(TaxiAdvance it,int serveno)
		{
			//REQUIRES：读入的it非空，serveno在范围内
			//EFFECTS:  生成器；
			t=it;
			num=serveno;
			path=it.getHistoryPath();
			int size=path.size();
			PathPoint p;
			for(int i=0;i<size;i++)
			{
				p=path.get(i);
				if(p.getServeCount()==num)
				{
					n=i-1;
					break;
				}
			}
		}
		public boolean hasNext()
		{
			//EFFECTS:  判断是否有下一个节点
			PathPoint p;
			if(n+1>=path.size())
				return false;
			p=path.get(n+1);
			if(p.getServeCount()==num)
				return true;
			else 
				return false;
		}
		public boolean hasPrevious()
		{
			//EFFECTS:  判断是否有上一个节点
			PathPoint p;
			if(n<=0)
				return false;
			p=path.get(n-1);
			if(p.getServeCount()==num)
				return true;
			else 
				return false;
		}
		public Object next() throws Exception
		{
			//EFFECTS:  返回下一个节点
			if(hasNext())
			{
				return path.get(++n);
			}
			else
				throw new Exception("noNext");
		}
		public Object previous() throws Exception
		{
			//EFFECTS: 返回上一个节点
			if(hasPrevious())
			{
				return path.get(--n);
			}
			else
				throw new Exception("noPrevious");
		}
	}

}
