import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class TestCanvas extends JPanel {
	Graphics g;
	Graphics2D g2;

	public static final int CANVAS_X_MAX = 600;
	public static final int CANVAS_Y_MAX = 400;

	public ArrayList<Point> points = new ArrayList<Point>(10);
	public ArrayList<DrawObject> objects = new ArrayList<DrawObject>();

	public TestCanvas(int width, int height) {
		// �p�l���̐����T�C�Y��ݒ�Apack()����Ƃ��ɕK�v
		setPreferredSize(new Dimension(width, height));
	}

	public TestCanvas(){
		this(CANVAS_X_MAX, CANVAS_Y_MAX);
	}

	public void paintComponent(Graphics g) {
		this.g2 = (Graphics2D)g;

		g.setColor(Color.white);
		g.fillRect(0, 0, CANVAS_X_MAX, CANVAS_Y_MAX);
		System.out.printf("paint\n");

		for(int i = 0; i < objects.size(); i++ ){
			DrawObject drawObj = objects.get(i);
			int[] x = new int[drawObj.points.size()];
			int[] y = new int[drawObj.points.size()];

			for(int j = 0; j < drawObj.points.size(); j++){
				Point point = drawObj.points.get(j);
				//this.points.add(point);
				x[j] = point.x;
				y[j] = point.y;
				g2.setColor(point.color);
				g2.drawRect(point.x, point.y, 3, 3);
				g2.drawString(point.text, point.x - 10, point.y - 10);
				System.out.printf("draw Point %d\n", j);
			}
			g2.setColor(drawObj.color);
			g2.drawPolygon(x, y, drawObj.points.size());
		}

	}

	public void addDrawObject(DrawObject drawObject){
		this.objects.add(drawObject);
	}

	public void show(){
		repaint();
	}

	public void clear(){
		objects.clear();
		show();
	}
}