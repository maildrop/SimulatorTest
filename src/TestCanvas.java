import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
	 
 */
public class TestCanvas extends javax.swing.JComponent {
	public static final int CANVAS_X_MAX = 600;
	public static final int CANVAS_Y_MAX = 400;

	public ArrayList<Point> points;
	public ArrayList<DrawObject> objects;
	/**
		preferredSize を指定して構築する
		@param width 幅
		@param height 高さ
	*/
	public TestCanvas(int width, int height) {
		super();
		// Canvasのサイズ設定と生成
		points = new ArrayList<Point>(10);
		objects = new ArrayList<DrawObject>();
		setPreferredSize(new Dimension(width, height));
	}

	/**
		 デフォルトコンストラクタ
		 CANVAS_X_MAX と CANVAS_Y_MAX で初期化する。
	 */
	public TestCanvas(){
		this(CANVAS_X_MAX, CANVAS_Y_MAX);
	}

	/**
		 
	 */
	@Override 
	protected void paintComponent( Graphics g ) {  // paintComponent の アクセス指定を勝手に public に変更しない。
		// Graphics オブジェクトは、メンバー変数として保持してはダメ
		// なぜならば、その下のレイヤーのウィンドウシステムが
		// グラフィックスコンテキストを保持することを許していないためという仕様上の制限に由来するから
		// なので、Graphics オブジェクトは、必ずローカル変数として使われる。
		assert Graphics.class.isInstance( g ) : "Graphics.class.isInstance( g )";
		if( Graphics.class.isInstance( g ) ){
			paintComponent( Graphics2D.class.cast( g ) );
		}
	}
	
	private void paintComponent( Graphics2D g2 ){
		final Color previousColor = g2.getColor(); // 後で、もとに戻すために保存

		clearBackground( g2 , Color.white);

		g2.setColor( previousColor ); // 一度前景色に戻す
		
		System.out.printf("paint\n");
		
		for( final DrawObject drawObj : objects ){ // 素直に 拡張 for 文を使う
			drawObjects( g2 , drawObj );
		}
		g2.setColor( previousColor );
	}


	/**
		 (x,y) に、色 color で 文字 label を描画する。
	 */
	private static final void drawLabel( Graphics2D g2 , String label , float x , float y , Color color){
		g2.setColor( color );
		drawLabel( g2 , label , x , y  );
	}

	/**
		 (x,y) に 文字 label を描画する。
	 */
	private static final void drawLabel( Graphics2D g2 , String label , float x , float y ){
		if( label == null ||  "".equals( label )  ){ // 空文字列は描画しない
			return;
		}
		// 文字列の画面上の幅を計算する。
		java.text.AttributedCharacterIterator aci =
			new java.text.AttributedString( label ).getIterator();
		java.awt.font.TextMeasurer tm =  new java.awt.font.TextMeasurer( aci, g2.getFontRenderContext() );
		java.awt.font.TextLayout layout = tm.getLayout( aci.getBeginIndex() , aci.getEndIndex() );
		//g2.fill( new java.awt.geom.Rectangle2D.Float( x ,  y ,  layout.getAdvance(), (layout.getAscent() +  layout.getBaseline() ) ) );
		g2.drawString(label, x - (layout.getAdvance() / 2.0f) , y  -( layout.getBaseline() + layout.getAscent()) );
	}

	/**
		 (x,y) に色 color で点を描画する。

	 */
	private static final void drawPoint( Graphics2D g2 , float x , float y , Color color ){
		g2.setColor( color );
		drawPoint( g2 , x , y  );
	}
	/**
		 (x,y) に点を描画する。

	 */
	private static final void drawPoint( Graphics2D g2 , float x , float y ){
		//g2.drawRect(x, y, 3, 3);
		g2.fill( new java.awt.geom.Rectangle2D.Float( x, y , 3f ,3f ) );
	}
	
	/**
		 コンポーネントが保持する描画オブジェクトを描画する
		 描画順は、 まずポリゴンを描画してから、点を描画して点のラベルを描画する。
	 */
	private void drawObjects( Graphics2D g2 , DrawObject drawObj ){
		final int nPoints = drawObj.points.size();
		System.out.println( nPoints );
		int[] x = new int[nPoints];
		int[] y = new int[nPoints];

		for( int j = 0; j < nPoints ; ++j ){
			final Point point = drawObj.points.get( j );
			x[j] = point.x;
			y[j] = point.y;
			System.out.printf("draw Point %d\n", j);
		}

		assert nPoints<=x.length : "nPoints<=x.length" ;
		assert nPoints<=y.length : "nPoints<=y.length" ;
		final Color color = drawObj.color;
		if( color != null ){
			g2.setColor( color );
		}

		g2.drawPolygon(x, y, nPoints );

		for( final Point point : drawObj.points ){
			if( point.color != null ){
				drawPoint(g2, (float)point.x , (float)point.y , point.color ); 
				drawLabel(g2, point.text,(float)point.x,(float)point.y, point.color); 
			}else{
				drawPoint(g2, (float)point.x , (float)point.y );
				drawLabel(g2, point.text,(float)point.x,(float)point.y );
			}
		}
		return;
	}
		
		
	/**
		 背景色で塗りつぶす 
		 このメソッドは、前景色を変更するので、呼び出し側が、それを保持しておかなければならない
		 という前提条件があるので、 private 宣言
		 @param g グラフィックスオブジェクト
	 */
	private void clearBackground( Graphics g ){
		clearBackground( g , getBackground() );
		return;
	}

	
	/**
		 背景を 指定色で塗りつぶす
		 このメソッドは、前景色を変更するので、呼び出し側が、それを保持しておかなければならない
		 という前提条件があるので、 private 宣言
		 @param g グラフィックスオブジェクト
		 @param color 塗りつぶす指定色
	 */
	private void clearBackground( Graphics g , Color color ){
		g.setColor( color );
		g.fillRect( 0, 0, getWidth() , getHeight() );
	}
	

	public void addDrawObject(DrawObject drawObject){
		this.objects.add(drawObject);
	}
	
	/* show は 基底クラスで使われているメソッドなので、このようなオーバーライドはダメです。
		 素直に、repaint() を直接使うようにします。
	 */
	//public void show(){
	//repaint();
	//}

	public void clear(){
		objects.clear();
		repaint();
	}
}
