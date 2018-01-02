package OO_8_Taxiplus;

public class PathPoint {
	private Point p;
	private int CarNo;
	private int ServeCount;
	//OverView:PathPoint类是封装了一个point点和一个int数，代表服务次数
	public PathPoint(int no,int count,Point p)
	{
		//REQUIRES：no大于等于0小于100，count大于0，p非空
		//EFFECTS:  根据输入的值初始化
		this.CarNo=no;
		this.ServeCount=count;
		this.p=p;
	}
	public boolean repOK()
	{
		//Effects: returns true if the rep variant holds for this, otherwise returns false
		if(p==null)
			return false;
		if(CarNo<0||CarNo>=100)
			return false;
		if(ServeCount<0)
			return false;
		return true;
	}
	public int getServeCount()
	{
		//EFFECTS:  返回当前节点存储的服务编号
		return this.ServeCount;
	}
	public Point getPoint()
	{
		//EFFECTS:  返回当前节点存储的路径节点
		return this.p;
	}
	public String toString()
	{
		//EFFECTS:  返回当前节点的字符串表示
		String s=new String();
		s+=p;
		return s;
	}
}
