package OO_8_Taxiplus;

import java.util.LinkedList;
import java.util.Scanner;


public class InputHandler extends Thread{
	private LinkedList<Taxi> taxis;
	public InputHandler(LinkedList<Taxi>taxis)
	{
		this.taxis=taxis;
	}
	//OverView:inputhandler类是输入处理类，处理输入的查询请求
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(taxis==null)
			return false;
		return true;
	}
	public void run()
	{
		try{
			System.out.println("请输入查询的出租车号和服务路径号,如12,1或者12,check");
			Scanner input=new Scanner(System.in);
			int n=1;
			String s=input.nextLine();
			while((!s.equals("end"))&n<101)
			{
				try{Thread.sleep(50);}
				catch(InterruptedException e){}
				this.parse(s,n);
				s=input.nextLine();
				n++;
			}
			if(n>=101)
			{
				System.out.println("查询出租车数量超过上限");
			}
			input.close();
		}
		catch(Throwable e){
			System.out.println("输入错误");
			System.exit(0);
		}
	}
	public void parse(String s,int n)
	{
		//REQUIRES：读入的String非空，n>0
		//MODIFIES: 字符串s
		//EFFECTS:  将读入的字符串进行处理，返回查询结果；
		String str[]=new String[4];
		int taxino;
		int serveno;
		s=s.replaceAll(" ", "");
		if(s.matches("^\\d{1,2},\\d{1,2}$"))
		{
			str = s.split(",");
			taxino=Integer.parseInt(str[0]);
			serveno=Integer.parseInt(str[1]);
			Taxi t=taxis.get(taxino);
			if(t.isAdvance())
			{
				TaxiAdvance ta=(TaxiAdvance)taxis.get(taxino);
				TwoWayIterator iterator=ta.PathAtIndex(serveno);
				if(iterator!=null)
				{
				while(iterator.hasNext())
				{
					try {
						System.out.println(iterator.next());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}
			}
			else
				System.out.println("所查询的出租车不是超级出租车");
		}
		else if(s.matches("^\\d{1,2},check$"))
		{
			str = s.split(",");
			taxino=Integer.parseInt(str[0]);
			Taxi t=taxis.get(taxino);
			if(t.isAdvance())
			{
				TaxiAdvance ta=(TaxiAdvance)taxis.get(taxino);
				System.out.println(taxino+"号超级出租车的服务次数为："+ta.getServeCount());
			}
			else
				System.out.println("所查询的出租车不是超级出租车");
		}
		else
			System.out.println("输入格式不匹配");
	}

}
