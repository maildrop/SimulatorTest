import java.awt.Color;
import java.util.ArrayList;

public class DrawObject{
	ArrayList<Point> points;
	ArrayList<String> texts;
	Color color;

	public DrawObject(Point p){
		points.add(p);
	}

	public DrawObject(ArrayList<Point> p){
		this.points = p;
	}

	public DrawObject(int x, int y){
		this(new Point(x, y));
	}

	public DrawObject(int[] x, int[] y){
		for(int i = 0; i < x.length; i++){
			addPoint(x[i], y[i]);
		}
	}

	public DrawObject(Integer[] x, Integer[] y){
		for(int i = 0; i < x.length; i++){
			addPoint(x[i], y[i]);
		}
	}

	/*
	//TODO 例外処理
	//System.out.printf("DrawObjectの引数x座標とy座標の数が一致しません\n");
	public DrawObject(List<Integer> listx, List<Integer> listy){
		Integer[] x =(Integer[])listx.toArray(new Integer[0]);
		Integer[] y =(Integer[])listy.toArray(new Integer[0]);
		this(x, y);
	}
	*/

	public void addPoint(int x, int y){
		points.add(new Point(x, y));
	}

	public void setText(){
		for(char n = 'A'; n < (char)('A'+ points.size()); n++){
			System.out.print(n);
		}
	}

	public void setColor(Color color){
		this.color = color;
	}
}