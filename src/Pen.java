import java.awt.Color;

public class Pen{
	public TestCanvas canvas;
	Color color;

	public Pen(TestCanvas canvas){
		this.canvas = canvas;
	}

	public void Draw(DrawObject drawObject){
		this.canvas.addDrawObject(drawObject);
		canvas.show();
	}


	/*
	public Pen(TestCanvas canvas){
		this.canvas = canvas;
	}


	public void DrawLine(int x1, int y1, int x2, int y2){
		canvas.addLine(new Line(x1, y1, x2, y2));
		canvas.show();
	}

	public void DrawLine(Point p1, Point p2){
		canvas.addPoint(p1);
		canvas.addPoint(p2);
		DrawLine(p1.x, p1.y, p2.x, p2.y);
	}

	public void DrawLine(Point p, int x, int y){
		canvas.addPoint(p);
		DrawLine(p.x, p.y, x, y);
	}

	public void DrawLine(int x, int y, Point p){
		canvas.addPoint(p);
		DrawLine(x, y, p.x, p.y);
	}

	public void DrawLine(Point p){
		canvas.addPoint(p);
		DrawLine(p.x, p.y, 0, 0);
	}

	public void DrawLine(int x, int y){
		DrawLine(x, y, 0, 0);
	}

	public void DrawRandomLine(int x_max, int y_max){
		Random rnd = new Random();
		DrawLine(new Point(rnd.nextInt(x_max), rnd.nextInt(y_max)),
				new Point(rnd.nextInt(x_max), rnd.nextInt(y_max)));
	}

	public void DrawPolygon(Polygon polygon){
		canvas.addPolygon(polygon);
		canvas.show();
	}

	public void DrawTriangle(Point p1, Point p2, Point p3){
		DrawLine(p1, p2);
		DrawLine(p2, p3);
		DrawLine(p3, p1);
	}

	public void DrawTriangle(Point p1, Point p2){
		DrawTriangle(new Point(), p1, p2);
	}

	public void DrawRandomTriangle(int x_max, int y_max){
		Random rnd = new Random();
		DrawTriangle(new Point(rnd.nextInt(x_max), rnd.nextInt(y_max)),
				new Point(rnd.nextInt(x_max), rnd.nextInt(y_max)),
				new Point(rnd.nextInt(x_max), rnd.nextInt(y_max)));
	}
	*/
}