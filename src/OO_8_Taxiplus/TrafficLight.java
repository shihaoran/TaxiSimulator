package OO_8_Taxiplus;

public class TrafficLight extends Thread{
	private Point position;
	private int mode;
	private boolean enable;
	private static final int UP_DOWN=0;
	private static final int LEFT_RIGHT=1;
	public static final int UP=0;
	public static final int RIGHT=1;
	public static final int DOWN=2;
	public static final int LEFT=3; 
	public TrafficLight(int x,int y,boolean enable)
	{
		//REQUIRES：x,y大于等于0小于80
		//EFFECTS:  初始化红绿灯，将初始灯设置为南北绿灯
		Point p=new Point(x,y);
		this.position=p;
		this.mode=UP_DOWN;	
		this.enable=enable;
	}
	//OverView:TrafficLight类是红绿灯类，每隔指定时间变换一次颜色，并且承担了阻塞交通的作用
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(position==null)
			return false;
		if(!position.repOK())
			return false;
		if(mode>1||mode<0)
			return false;
		return true;
	}
	public void run()
	{
		while(true)
		{
			try{sleep(300);}
			catch(InterruptedException e){}
			if(this.enable==true)
			{
			if(this.mode==UP_DOWN)
				this.mode=LEFT_RIGHT;
			else
				this.mode=UP_DOWN;
			}
		}
	}
	public boolean checkInOut(int in,int out)
	{
		//REQUIRES：读入的in和out是0-3之间的整数
		//EFFECTS:  返回当前路径是否被红绿灯阻塞
		if (this.enable==false)
			return true;
		int result=Math.abs((in-out)%4);
		if(result==1)
			return true;
		else if(result==2||result==3||result==0)
		{
			if((in==UP||in==DOWN)&&this.mode==UP_DOWN)
				return true;
			else if((in==RIGHT||in==LEFT)&&this.mode==LEFT_RIGHT)
				return true;
			else
			{
				//System.out.println(this.position+"in out "+in+" "+out);
				return false;
			}
		}
		else 
		{
			System.out.println(this.position+"in out "+in+" "+out);
			return false;
		}
	}
	public boolean chechLight(Point former,Point now,Point next)
	{
		//REQUIRES：读入的当前位置now和前一步位置former还有下一步位置next在范围内，且former,now和next之间距离为1
		//且now位置与红绿灯位置相等
		//EFFECTS:  返回当前路径是否被红绿灯阻塞
		try {
		int x_0=former.getX();
		int x_1=now.getX();
		int x_2=next.getX();
		int y_0=former.getY();
		int y_1=now.getY();
		int y_2=next.getY();
		int in;
		int out;
		if(!now.equals(position))
		{
			System.out.println("出错");
			return false;
		}
		if(this.enable==false)
			return true;
		if(former.equals(now))
			return true;
		if((x_1==x_0+1)&&(y_1==y_0))
			in=UP;
		else if((x_1==x_0)&&(y_1==y_0-1))
			in=RIGHT;
		else if((x_1==x_0-1)&&(y_1==y_0))
			in=DOWN;
		else if((x_1==x_0)&&(y_1==y_0+1))
			in=RIGHT;
		else
		{
			System.out.println("出错");
			return false;
		}
			
		if((x_1==x_2+1)&&(y_1==y_2))
			out=UP;
		else if((x_1==x_2)&&(y_1==y_2-1))
			out=RIGHT;
		else if((x_1==x_2-1)&&(y_1==y_2))
			out=DOWN;
		else if((x_1==x_2)&&(y_1==y_2+1))
			out=RIGHT;
		else
		{
			System.out.println("出错");
			return false;
		}
		return checkInOut(in,out);
		}
		catch(Throwable e){
			System.out.println("溢出");
			System.exit(0);
			return false;
		}
	}
	public void setEnable(boolean enable)
	{
		//MODIFIES: 红绿灯开启使能enable
		//EFFECTS:  开关红绿灯
		this.enable=enable;
	}

}
