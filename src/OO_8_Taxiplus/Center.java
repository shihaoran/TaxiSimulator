package OO_8_Taxiplus;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Center {
	private static boolean map[][][]=new boolean [80][80][4];
	private static boolean originalmap[][][]=new boolean [80][80][4];
	private static int taxiflow[][][]=new int [80][80][4];
	private static String[][] tempmap=new String [80][80];
	private static String[][] templight=new String [80][80];
	private static TrafficLight[][] light=new TrafficLight [80][80];
	public static final int UP=0;
	public static final int RIGHT=1;
	public static final int DOWN=2;
	public static final int LEFT=3; 
	public static final int MAX_TAXI=100;
	private static final int SERVING=0;
	private static final int PICKING=1;
	private static final int WAITING=2;
	private static final int RESTING=3;
	private static int taxi_credit[]=new int [MAX_TAXI];
	private static Point taxi_position[]=new Point[MAX_TAXI];
	private static int taxi_mode[]=new int [MAX_TAXI];
	
	
	public Center()
	{
		//EFFECTS:  初始化center中保存的数据
		for(int i=0;i<80;i++)
		{
			for(int j=0;j<80;j++)
			{
				map[i][j][0]=false;
				map[i][j][1]=false;
				map[i][j][2]=false;
				map[i][j][3]=false;
				tempmap[i][j]=null;
				templight[i][j]=null;
			}
		}
		for(int i=0;i<MAX_TAXI;i++)
		{
			taxi_credit[i]=0;
			taxi_position[i]=null;
			taxi_mode[i]=WAITING;
		}
	}
	//OverView:center类是整个工程的主类，负责在程序的最外层进行读入map文件和light文件，以及启动taxi（出租车）、taxisearch(乘客搜索出租车)、test（测试）、trafficlight（交通信号）、inputhandler(路径查询)线程的操作。
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		for(int i=0;i<80;i++)
		{
			for(int j=0;j<80;j++)
			{
				for(int k=0;k<4;k++)
				{
					if(taxiflow[i][j][k]<0)
						return false;
				}
				if(!(tempmap[i][j].equals("0")||tempmap[i][j].equals("1")||tempmap[i][j].equals("2")||tempmap[i][j].equals("3")))
					return false;
				if(!(templight[i][j].equals("0")||templight[i][j].equals("1")))
					return false;
				if(light[i][j]==null)
					return false;
				if(!light[i][j].repOK())
					return false;
			}
		}
		for(int i=0;i<100;i++)
		{
			if(taxi_credit[i]<0)
				return false;
			if(taxi_position[i]==null)
				return false;
			if(!taxi_position[i].repOK())
				return false;
			if(taxi_mode[i]>3||taxi_mode[i]<0)
				return false;
		}
		return true;
	}
	public static void main(String[] args) 
	{
		try {
			Center center=new Center();
			File file = new File("C:\\input\\map.txt");
			if(!file.exists())
			{
				System.out.println("地图文件不存在，程序已退出");
				System.exit(0);
			}	
			BufferedReader reader = null;
			try { reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line=0;
            while ((tempString = reader.readLine()) != null) 
            {
            	tempmap[line] = tempString.split("\\s");
            	line++;
            }
            reader.close();
            
            File file1 = new File("C:\\input\\light.txt");
			if(!file1.exists())
			{
				System.out.println("红绿灯文件不存在，程序已退出");
				System.exit(0);
			}	
			BufferedReader reader1 = null;
			reader1 = new BufferedReader(new FileReader(file1));
            String tempString1 = null;
            line=0;
            while ((tempString1 = reader1.readLine()) != null) 
            {
            	templight[line] = tempString1.split("\\s");
            	line++;
            }
            reader1.close();
            }catch(Throwable e){
    		System.out.println("溢出");
    			System.exit(0);}
			
            center.setMap();
            center.setLight();
           Taxi taxi[]=new Taxi[MAX_TAXI];
            LinkedList<Taxi> taxiqueue=new LinkedList<Taxi>();
            int j=0;
            for(int i=0;i<MAX_TAXI-30;i++)
            {
            	taxi[i]=new Taxi(i,center);
            	taxi[i].start();
            	taxiqueue.add(taxi[i]);
            	try{Thread.sleep(1);}
    			catch(InterruptedException e){}
            	j=i;
            }
            for(int i=j+1;i<MAX_TAXI;i++)
            {
            	taxi[i]=new TaxiAdvance(i,center);
            	taxi[i].start();
            	taxiqueue.add(taxi[i]);
            	try{Thread.sleep(1);}
    			catch(InterruptedException e){}
            }
            PassengerQueue passengerList=new PassengerQueue();
            Test test=new Test(passengerList,center);
            test.start();
            TaxiSearch taxisearch=new TaxiSearch(passengerList,taxiqueue);
            taxisearch.start();
            InputHandler input=new InputHandler(taxiqueue);
            input.start();
            try{Thread.sleep(5000);}
			catch(InterruptedException e){}
            /*请在这里添加道路开关信息
             * 例
             * Point p1=new Point(10,11);
             * Point p2=new Point(10,12);
             * center.closeRoad(p1,p2);
             * try{Thread.sleep(1000);}
			 *catch(InterruptedException e){}
             * 关闭道路同理，每次开关完道路后请睡一段时间
             * 请保证图的连通性
             */
        }
		catch(Throwable e){
			System.out.println("溢出");
			System.exit(0);
		}
        
	}
	public void setLight()
	{
		//REQUIRES：读入的templight文件仅由0,1,2,3组成
		//MODIFIES: 红绿灯数组light
		//EFFECTS:  将读入的templight进行处理，初始化红绿灯数组；
		for(int i=0;i<80;i++)
		{
			for(int j=0;j<80;j++)
			{
				if(templight[i][j].equals("1"))
				{
					int count=0;
					for(int k=0;k<4;k++)
					{
						if(map[i][j][k]==true)
							count++;
					}
					if(count>=3)
					{
						light[i][j]=new TrafficLight(i,j,true);
						light[i][j].start();
					}
					else
					{
						light[i][j]=new TrafficLight(i,j,false);
						light[i][j].start();
					}
				}
				else if(templight[i][j].equals("0"))
				{
					light[i][j]=new TrafficLight(i,j,false);
					light[i][j].start();
				}
				else
				{
					System.out.println("红绿灯文件存在错误");
					System.exit(0);
				}
			}
		}
	}
	public void setMap()
	{
		//REQUIRES：读入的tempmap文件联通，且仅由0,1,2,3组成
		//MODIFIES: 地图文件map
		//EFFECTS:  将读入的tempmap进行处理，变为各条边的联通状态，联通为true否则为false；
		for(int i=0;i<79;i++)
		{
			for(int j=0;j<80;j++)
			{
				if(j!=79)
				{
					if(tempmap[i][j].equals("0"))
					{
						map[i][j+1][LEFT]=false;
						map[i+1][j][UP]=false;
						map[i][j][RIGHT]=false;
						map[i][j][DOWN]=false;
					}
					else if(tempmap[i][j].equals("1"))
					{
						map[i][j+1][LEFT]=true;
						map[i+1][j][UP]=false;
						map[i][j][RIGHT]=true;
						map[i][j][DOWN]=false;
					}
					else if(tempmap[i][j].equals("2"))
					{
						map[i][j+1][LEFT]=false;
						map[i+1][j][UP]=true;
						map[i][j][RIGHT]=false;
						map[i][j][DOWN]=true;
					}
					else if(tempmap[i][j].equals("3"))
					{
						map[i][j+1][LEFT]=true;
						map[i+1][j][UP]=true;
						map[i][j][RIGHT]=true;
						map[i][j][DOWN]=true;
					}
					else
						System.out.println("地图文件存在错误");
				}
				else
				{
					if(tempmap[i][j].equals("0"))
					{
						map[i+1][j][UP]=false;	
						map[i][j][DOWN]=false;
					}
					else if(tempmap[i][j].equals("2"))
					{
						map[i+1][j][UP]=true;
						map[i][j][DOWN]=true;
					}
					else
						System.out.println("地图文件存在错误");
				}
			}
		}
		for(int j=0;j<79;j++)
		{
			if(tempmap[79][j].equals("0"))
			{
				map[79][j+1][LEFT]=false;	
				map[79][j][RIGHT]=false;
			}
			else if(tempmap[79][j].equals("1"))
			{
				map[79][j+1][LEFT]=true;	
				map[79][j][RIGHT]=true;
			}
			else
				System.out.println("地图文件存在错误");
		}
		for(int i=0;i<80;i++)
		{
			for(int j=0;j<80;j++)
			{
				for(int k=0;k<4;k++)
					originalmap[i][j][k]=map[i][j][k];
			}
		}
	}

	public int getLen(Point start,Point end)
	{
		//REQUIRES：读入的起点和终点坐标在0-79范围内
		//EFFECTS:  计算起点和终点之间的最短路径距离，如果不存在返回99999；
		if(start.equals(end))
			return 0;
		else
		{
		int count=0;
		LinkedList<Point> queue=new LinkedList<Point>();
		ArrayList<Point> VisitedPoint=new ArrayList<Point>();
		HashMap<Point,Point> map=new HashMap<>();
		boolean flag=false;
		VisitedPoint.add(start);
		queue.add(start);
		while(!queue.isEmpty())
		{
			Point father=queue.poll();
			ArrayList<Point> toBeVisitedPoint=getChildren(father);
			for(Point children:toBeVisitedPoint)
			{
			    if(children.equals(end))
			    {
			    	map.put(children, father);
			    	queue.clear();
			    	end=children;
			    	flag=true;
			    	break;
			    }
				else if(!checkContains(VisitedPoint,children))
				{
					VisitedPoint.add(children);
					map.put(children, father);
					queue.add(children);
				}
			}
		}
		if(flag)
		{
			Point father=map.get(end);
			count++;
			while(!father.equals(start))
			{
				count++;
				father=map.get(father);
			}
			return count;
		}
		else
			return 99999999;
		}
	}
	public ArrayList<Point> getChildren(Point p)
	{
		//REQUIRES：读入的Point p坐标在范围内
		//EFFECTS: 获取读入周边四个节点中与之联通的节点，并返回一个节点的ArrayList
		int x=p.getX();
		int y=p.getY();
		ArrayList<Point> list = new ArrayList<Point>();
		if(map[x][y][UP])
		{
			Point p1=new Point(x-1,y);
			list.add(p1);
		}
		if(map[x][y][RIGHT])
		{
			Point p2=new Point(x,y+1);
			list.add(p2);
		}
		if(map[x][y][DOWN])
		{
			Point p3=new Point(x+1,y);
			list.add(p3);
		}
		if(map[x][y][LEFT])
		{
			Point p4=new Point(x,y-1);
			list.add(p4);
		}
		return list;
	}
	public Point getNext(Point p,Point fp)
	{
		//REQUIRES：读入的当前位置p和前一步位置fp在范围内，且p和fp之间距离为1
		//MODIFIES: 车流统计taxiflow
		//EFFECTS:  当出租车处于waiting状态时，利用该方法获取下一步位置，满足指导书要求中的选择车流最小的边行驶
		int x=p.getX();
		int y=p.getY();
		int minflow=99999;
		ArrayList<Point> list = new ArrayList<Point>();
		for(int i=0;i<4;i++)
		{
			if(map[x][y][i])
			{
				if(taxiflow[x][y][i]<minflow)
				{
					minflow=taxiflow[x][y][i];
					list.clear();
					if(i==UP)
					{
						Point p1=new Point(x-1,y);
						list.add(p1);
					}
					else if(i==RIGHT)
					{
						Point p2=new Point(x,y+1);
						list.add(p2);
					}
					else if(i==DOWN)
					{
						Point p3=new Point(x+1,y);
						list.add(p3);
					}
					else if(i==LEFT)
					{
						Point p4=new Point(x,y-1);
						list.add(p4);
					}
					
				}
				else if(taxiflow[x][y][i]==minflow)
				{
					if(i==UP)
					{
						Point p1=new Point(x-1,y);
						list.add(p1);
					}
					else if(i==RIGHT)
					{
						Point p2=new Point(x,y+1);
						list.add(p2);
					}
					else if(i==DOWN)
					{
						Point p3=new Point(x+1,y);
						list.add(p3);
					}
					else if(i==LEFT)
					{
						Point p4=new Point(x,y-1);
						list.add(p4);
					}
				}
			}
		}
		Random rand=new Random ();
		int random=rand.nextInt(list.size());
		if(list.get(random)!=null)
		{
			Point choosed=list.get(random);
			if(!light[p.getX()][p.getY()].chechLight(fp, p, choosed))
			{
				return null;
			}
			addFlow(p,choosed);
			if(!p.equals(fp))
				removeFlow(fp,p);
			return choosed;
		}
		else 
		{
			System.out.println("寻找下一位置错误");
			return null;
		}
	}
	public Point getShortNext(Point p,Point fp,Point end)
	{
		//REQUIRES：读入的当前位置p和前一步位置fp还有最终位置end在范围内，且p和fp之间距离为1
		//MODIFIES: 车流统计taxiflow
		//EFFECTS:  当出租车处于serving和picking状态时，利用该方法获取下一步位置，满足指导书要求中的选择
		//路径最短中车流最小的边行驶的要求
		int x=p.getX();
		int y=p.getY();
		int minflow=99999;
		int minlen=99999;
		int len=99999;
		int flow=99999;
		ArrayList<Point> list = new ArrayList<Point>();
		ArrayList<Point> choosedlist = new ArrayList<Point>();
		Point temp = null;
		for(int i=0;i<4;i++)
		{
			if(map[x][y][i])
			{
				if(i==UP)
				{
					Point p1=new Point(x-1,y);
					temp=p1;
					len=getLen(p1,end);
				}
				else if(i==RIGHT)
				{
					Point p2=new Point(x,y+1);
					temp=p2;
					len=getLen(p2,end);
				}
				else if(i==DOWN)
				{
					Point p3=new Point(x+1,y);
					temp=p3;
					len=getLen(p3,end);
				}
				else if(i==LEFT)
				{
					Point p4=new Point(x,y-1);
					temp=p4;
					len=getLen(p4,end);
				}
				if(len<minlen)
				{
					minlen=len;
					list.clear();
					list.add(temp);
				}
				else if(len==minlen)
				{
					list.add(temp);
				}
			}
		}
		for(Point key:list)
		{
			flow=getFlow(p,key);
			if(flow<minflow)
			{
				choosedlist.clear();
				choosedlist.add(key);
			}
			else if(flow==minflow)
			{
				choosedlist.add(key);
			}
		}	
		Random rand=new Random ();
		int random=rand.nextInt(choosedlist.size());
		if(choosedlist.get(random)!=null)
		{
			Point choosed=choosedlist.get(random);
			if(!light[p.getX()][p.getY()].chechLight(fp, p, choosed))//红绿灯
			{
				return null;
			}
			addFlow(p,choosed);
			if(!p.equals(fp))
				removeFlow(fp,p);
			return choosed;
		}
		else 
		{
			System.out.println("寻找下一位置错误");
			return null;
		}
	}
	//超级出租车部分
	public int getLenAdvance(Point start,Point end)
	{
		//REQUIRES：读入的起点和终点坐标在0-79范围内
		//EFFECTS:  计算起点和终点之间的最短路径距离，如果不存在返回99999；
		if(start.equals(end))
			return 0;
		else
		{
		int count=0;
		LinkedList<Point> queue=new LinkedList<Point>();
		ArrayList<Point> VisitedPoint=new ArrayList<Point>();
		HashMap<Point,Point> map=new HashMap<>();
		boolean flag=false;
		VisitedPoint.add(start);
		queue.add(start);
		while(!queue.isEmpty())
		{
			Point father=queue.poll();
			ArrayList<Point> toBeVisitedPoint=getChildrenAdvance(father);
			for(Point children:toBeVisitedPoint)
			{
			    if(children.equals(end))
			    {
			    	map.put(children, father);
			    	queue.clear();
			    	end=children;
			    	flag=true;
			    	break;
			    }
				else if(!checkContains(VisitedPoint,children))
				{
					VisitedPoint.add(children);
					map.put(children, father);
					queue.add(children);
				}
			}
		}
		if(flag)
		{
			Point father=map.get(end);
			count++;
			while(!father.equals(start))
			{
				count++;
				father=map.get(father);
			}
			return count;
		}
		else
			return 99999999;
		}
	}
	public ArrayList<Point> getChildrenAdvance(Point p)
	{
		//REQUIRES：读入的Point p坐标在范围内
		//EFFECTS: 获取读入周边四个节点中与之联通的节点，并返回一个节点的ArrayList
		int x=p.getX();
		int y=p.getY();
		ArrayList<Point> list = new ArrayList<Point>();
		if(originalmap[x][y][UP])
		{
			Point p1=new Point(x-1,y);
			list.add(p1);
		}
		if(originalmap[x][y][RIGHT])
		{
			Point p2=new Point(x,y+1);
			list.add(p2);
		}
		if(originalmap[x][y][DOWN])
		{
			Point p3=new Point(x+1,y);
			list.add(p3);
		}
		if(originalmap[x][y][LEFT])
		{
			Point p4=new Point(x,y-1);
			list.add(p4);
		}
		return list;
	}
	public Point getNextAdvance(Point p,Point fp)
	{
		//REQUIRES：读入的当前位置p和前一步位置fp在范围内，且p和fp之间距离为1
		//MODIFIES: 车流统计taxiflow
		//EFFECTS:  当出租车处于waiting状态时，利用该方法获取下一步位置，满足指导书要求中的选择车流最小的边行驶
		int x=p.getX();
		int y=p.getY();
		int minflow=99999;
		ArrayList<Point> list = new ArrayList<Point>();
		for(int i=0;i<4;i++)
		{
			if(originalmap[x][y][i])
			{
				if(taxiflow[x][y][i]<minflow)
				{
					minflow=taxiflow[x][y][i];
					list.clear();
					if(i==UP)
					{
						Point p1=new Point(x-1,y);
						list.add(p1);
					}
					else if(i==RIGHT)
					{
						Point p2=new Point(x,y+1);
						list.add(p2);
					}
					else if(i==DOWN)
					{
						Point p3=new Point(x+1,y);
						list.add(p3);
					}
					else if(i==LEFT)
					{
						Point p4=new Point(x,y-1);
						list.add(p4);
					}
					
				}
				else if(taxiflow[x][y][i]==minflow)
				{
					if(i==UP)
					{
						Point p1=new Point(x-1,y);
						list.add(p1);
					}
					else if(i==RIGHT)
					{
						Point p2=new Point(x,y+1);
						list.add(p2);
					}
					else if(i==DOWN)
					{
						Point p3=new Point(x+1,y);
						list.add(p3);
					}
					else if(i==LEFT)
					{
						Point p4=new Point(x,y-1);
						list.add(p4);
					}
				}
			}
		}
		Random rand=new Random ();
		int random=rand.nextInt(list.size());
		if(list.get(random)!=null)
		{
			Point choosed=list.get(random);
			if(!light[p.getX()][p.getY()].chechLight(fp, p, choosed))
			{
				return null;
			}
			addFlow(p,choosed);
			if(!p.equals(fp))
				removeFlow(fp,p);
			return choosed;
		}
		else 
		{
			System.out.println("寻找下一位置错误");
			return null;
		}
	}
	public Point getShortNextAdvance(Point p,Point fp,Point end)
	{
		//REQUIRES：读入的当前位置p和前一步位置fp还有最终位置end在范围内，且p和fp之间距离为1
		//MODIFIES: 车流统计taxiflow
		//EFFECTS:  当出租车处于serving和picking状态时，利用该方法获取下一步位置，满足指导书要求中的选择
		//路径最短中车流最小的边行驶的要求
		int x=p.getX();
		int y=p.getY();
		int minflow=99999;
		int minlen=99999;
		int len=99999;
		int flow=99999;
		ArrayList<Point> list = new ArrayList<Point>();
		ArrayList<Point> choosedlist = new ArrayList<Point>();
		Point temp = null;
		for(int i=0;i<4;i++)
		{
			if(originalmap[x][y][i])
			{
				if(i==UP)
				{
					Point p1=new Point(x-1,y);
					temp=p1;
					len=getLenAdvance(p1,end);
				}
				else if(i==RIGHT)
				{
					Point p2=new Point(x,y+1);
					temp=p2;
					len=getLenAdvance(p2,end);
				}
				else if(i==DOWN)
				{
					Point p3=new Point(x+1,y);
					temp=p3;
					len=getLenAdvance(p3,end);
				}
				else if(i==LEFT)
				{
					Point p4=new Point(x,y-1);
					temp=p4;
					len=getLenAdvance(p4,end);
				}
				if(len<minlen)
				{
					minlen=len;
					list.clear();
					list.add(temp);
				}
				else if(len==minlen)
				{
					list.add(temp);
				}
			}
		}
		for(Point key:list)
		{
			flow=getFlow(p,key);
			if(flow<minflow)
			{
				choosedlist.clear();
				choosedlist.add(key);
			}
			else if(flow==minflow)
			{
				choosedlist.add(key);
			}
		}	
		Random rand=new Random ();
		int random=rand.nextInt(choosedlist.size());
		if(choosedlist.get(random)!=null)
		{
			Point choosed=choosedlist.get(random);
			if(!light[p.getX()][p.getY()].chechLight(fp, p, choosed))//红绿灯
			{
				return null;
			}
			addFlow(p,choosed);
			if(!p.equals(fp))
				removeFlow(fp,p);
			return choosed;
		}
		else 
		{
			System.out.println("寻找下一位置错误");
			return null;
		}
	}
	public synchronized void addFlow(Point f,Point l)
	{
		//REQUIRES：读入的起始位置f和目标位置l在范围内,且两点之间距离为1
		//MODIFIES: 车流统计taxiflow
		//EFFECTS:  将起始位置和目标位置之间的边车流统计+1
		int f_x=f.getX();
		int f_y=f.getY();
		int l_x=l.getX();
		int l_y=l.getY();
		if(f_x==l_x&&f_y==l_y-1)
		{
			if(map[f_x][f_y][RIGHT]==true)
				taxiflow[f_x][f_y][RIGHT]++;
			taxiflow[l_x][l_y][LEFT]=taxiflow[f_x][f_y][RIGHT];
		}
		else if(f_x==l_x&&f_y==l_y+1)
		{
			if(map[f_x][f_y][LEFT]==true)
				taxiflow[f_x][f_y][LEFT]++;
			taxiflow[l_x][l_y][RIGHT]=taxiflow[f_x][f_y][LEFT];
		}
		else if(f_x==l_x-1&&f_y==l_y)
		{
			if(map[f_x][f_y][DOWN]==true)
				taxiflow[f_x][f_y][DOWN]++;
			taxiflow[l_x][l_y][UP]=taxiflow[f_x][f_y][DOWN];
		}
		else if(f_x==l_x+1&&f_y==l_y)
		{
			if(map[f_x][f_y][UP]==true)
				taxiflow[f_x][f_y][UP]++;
			taxiflow[l_x][l_y][DOWN]=taxiflow[f_x][f_y][UP];
		}
		else
			System.out.println("车流错误");
	}
	public synchronized void removeFlow(Point f,Point l)
	{
		//REQUIRES：读入的起始位置f和目标边位置l在范围内,且两点之间距离为1
		//MODIFIES: 车流统计taxiflow
		//EFFECTS:  将起始位置和目标位置之间的边车流统计-1
		int f_x=f.getX();
		int f_y=f.getY();
		int l_x=l.getX();
		int l_y=l.getY();
		if(f_x==l_x&&f_y==l_y-1)
		{
			if(taxiflow[f_x][f_y][RIGHT]>0&&map[f_x][f_y][RIGHT]==true)
				taxiflow[f_x][f_y][RIGHT]--;
			taxiflow[l_x][l_y][LEFT]=taxiflow[f_x][f_y][RIGHT];
		}
		else if(f_x==l_x&&f_y==l_y+1)
		{
			if(taxiflow[f_x][f_y][LEFT]>0&&map[f_x][f_y][LEFT]==true)	
				taxiflow[f_x][f_y][LEFT]--;
			taxiflow[l_x][l_y][RIGHT]=taxiflow[f_x][f_y][LEFT];
		}
		else if(f_x==l_x-1&&f_y==l_y)
		{
			if(taxiflow[f_x][f_y][DOWN]>0&&map[f_x][f_y][DOWN]==true)
				taxiflow[f_x][f_y][DOWN]--;
			taxiflow[l_x][l_y][UP]=taxiflow[f_x][f_y][DOWN];
		}
		else if(f_x==l_x+1&&f_y==l_y)
		{
			if(taxiflow[f_x][f_y][UP]>0&&map[f_x][f_y][UP]==true)
				taxiflow[f_x][f_y][UP]--;
			taxiflow[l_x][l_y][DOWN]=taxiflow[f_x][f_y][UP];
		}
		else
			System.out.println("车流错误");
	}
	public int getFlow(Point f,Point l)
	{
		//REQUIRES：读入的起始位置f和目标边位置l在范围内,且两点之间距离为1
		//EFFECTS:  获取起始位置和目标位置之间的边车流统计
		int f_x=f.getX();
		int f_y=f.getY();
		int l_x=l.getX();
		int l_y=l.getY();
		if(f_x==l_x&&f_y==l_y-1)
		{
			return taxiflow[f_x][f_y][RIGHT];
		}
		else if(f_x==l_x&&f_y==l_y+1)
		{
			return taxiflow[f_x][f_y][LEFT];
		}
		else if(f_x==l_x-1&&f_y==l_y)
		{
			return taxiflow[f_x][f_y][DOWN];
		}
		else if(f_x==l_x+1&&f_y==l_y)
		{
			return taxiflow[f_x][f_y][UP];
		}
		else
		{
			System.out.println("车流错误");
			return 99999;
		}
	}
	public void updatePosition(int no,Point p)
	{
		//REQUIRES：读入的位置p在范围内,且100>no>=0
		//MODIFIES: 车辆位置统计taxi_position
		//EFFECTS:  更新center中存储的车辆位置信息
		taxi_position[no]=p;
	}
	public void updateCredit(int no,int credit)
	{
		//REQUIRES：读入的位置p在范围内,且100>no>=0
		//MODIFIES: 车辆位置统计taxi_credit
		//EFFECTS:  更新center中存储的车辆信用信息
		taxi_credit[no]=credit;
	}
	public void updateMode(int no,int mode)
	{
		//REQUIRES：读入的位置p在范围内,且100>no>=0
		//MODIFIES: 车辆状态统计taxi_mode
		//EFFECTS:  更新center中存储的车辆状态信息
		taxi_mode[no]=mode;
	}
	public boolean checkContains(ArrayList<Point> list,Point p)
	{
		//REQUIRES：p非空
		//EFFECTS:  查找list中是否含有p
		for(Point key:list)
		{
			if(key.equals(p))
				return true;
		}
		return false;
	}
	public int getCredit(int no)
	{
		//REQUIRES：100>no>=0
		//EFFECTS:  获取指定车辆的信用信息
		return taxi_credit[no];
	}
	public int getMode(int no)
	{
		//REQUIRES：100>no>=0
		//EFFECTS:  获取指定车辆的状态信息
		return taxi_mode[no];
	}
	public Point getPosition(int no)
	{
		//REQUIRES：100>no>=0
		//EFFECTS:  获取指定车辆的位置信息
		return taxi_position[no];
	}
	public void closeRoad(Point f,Point l)
	{
		//REQUIRES：道路的起始位置f和结束位置l在范围内,且两点之间距离为1
		//MODIFIES: 地图数据map,红绿灯数据light
		//EFFECTS:  关闭两点之间的道路，重新设置红绿灯
		int f_x=f.getX();
		int f_y=f.getY();
		int l_x=l.getX();
		int l_y=l.getY();
		int count_f=0;
		int count_l=0;
		if(f_x==l_x&&f_y==l_y-1)
		{
			map[f_x][f_y][RIGHT]=false;
			map[l_x][l_y][LEFT]=false;
		}
		else if(f_x==l_x&&f_y==l_y+1)
		{
			map[f_x][f_y][LEFT]=false;
			map[l_x][l_y][RIGHT]=false;
		}
		else if(f_x==l_x-1&&f_y==l_y)
		{
			map[f_x][f_y][DOWN]=false;
			map[l_x][l_y][UP]=false;
		}
		else if(f_x==l_x+1&&f_y==l_y)
		{
			map[f_x][f_y][UP]=false;
			map[l_x][l_y][DOWN]=false;
		}
		else
		{
			System.out.println("关闭道路错误");
		}
		for(int i=0;i<4;i++)
		{
			if(map[f_x][f_y][i]==true)
				count_f++;
			else if(map[l_x][l_y][i]==true)
				count_l++;
		}
		if(count_f<=2&&templight[f_x][f_y].equals("1"))
			light[f_x][f_y].setEnable(false);
		if(count_l<=2&&templight[l_x][l_y].equals("1"))
			light[l_x][l_y].setEnable(false);
	}
	public void openRoad(Point f,Point l)
	{
		//REQUIRES：道路的起始位置f和结束位置l在范围内,且两点之间距离为1
		//MODIFIES: 地图数据map,红绿灯数据light
		//EFFECTS:  打开两点之间的道路,重新设置红绿灯
		int f_x=f.getX();
		int f_y=f.getY();
		int l_x=l.getX();
		int l_y=l.getY();
		int count_f=0;
		int count_l=0;
		boolean flag=false;
		if(f_x==l_x&&f_y==l_y-1)
		{
			if(originalmap[f_x][f_y][RIGHT]==true)
			{
				map[f_x][f_y][RIGHT]=true;
				map[l_x][l_y][LEFT]=true;
				flag=true;
			}
			else
				System.out.println("不能打开原图中不存在的道路");
		}
		else if(f_x==l_x&&f_y==l_y+1)
		{
			if(originalmap[f_x][f_y][LEFT]==true)
			{
				map[f_x][f_y][LEFT]=true;
				map[l_x][l_y][RIGHT]=true;
				flag=true;
			}
			else
				System.out.println("不能打开原图中不存在的道路");
		}
		else if(f_x==l_x-1&&f_y==l_y)
		{
			if(originalmap[f_x][f_y][DOWN]==true)
			{
				map[f_x][f_y][DOWN]=true;
				map[l_x][l_y][UP]=true;
				flag=true;
			}
			else
				System.out.println("不能打开原图中不存在的道路");
		}
		else if(f_x==l_x+1&&f_y==l_y)
		{
			if(originalmap[f_x][f_y][UP]=true)
			{
				map[f_x][f_y][UP]=true;
				map[l_x][l_y][DOWN]=true;
				flag=true;
			}
			else
				System.out.println("不能打开原图中不存在的道路");
		}
		else
		{
			System.out.println("打开道路错误");
		}
		for(int i=0;i<4;i++)
		{
			if(map[f_x][f_y][i]==true)
				count_f++;
			else if(map[l_x][l_y][i]==true)
				count_l++;
		}
		if(count_f>=3&&templight[f_x][f_y].equals("1"))
			light[f_x][f_y].setEnable(true);
		if(count_l>=3&&templight[l_x][l_y].equals("1"))
			light[l_x][l_y].setEnable(true);
		if(flag)
			System.out.println("打开道路"+f+"-"+l+"成功");
	}
}
