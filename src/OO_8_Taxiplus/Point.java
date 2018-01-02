package OO_8_Taxiplus;

public class Point {
	private int x;
	private int y;
	public Point(int x,int y)
	{
		//REQUIRES：x，y都大于等于0小于80
		//EFFECTS:  根据输入的x，y值初始化
		this.x=x;
		this.y=y;
	}
	//OverView:Point类是抽象表示的地图上的路口，存储了坐标信息
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(x>=80||x<0)
			return false;
		if(y>=80||y<0)
			return false;
		return true;
	}
	public String toString()
	{
		//EFFECTS:将该点坐标返回为字符串形式
		String s=new String();
		s=s+"("+this.x+","+this.y+")";
		return s;
	}
	public boolean equals(Point a)
	{
		//REQUIRES：a非空
		//EFFECTS:  如果两点相同，返回true，反之返回false
		if (a.getX()==this.x&&a.getY()==this.y)
			return true;
		else
			return false;
	}
	public int getX()
	{
		//EFFECTS:  返回该点x坐标
		return this.x;
	}
	public int getY()
	{
		//EFFECTS:  返回该点y坐标
		return this.y;
	}

}
