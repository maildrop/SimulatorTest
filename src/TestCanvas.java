import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**

	 
 */
public class TestCanvas extends javax.swing.JComponent {
	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TestCanvas.class.getName());

	public static final int CANVAS_X_MAX = 600; // 幅の最大値
	public static final int CANVAS_Y_MAX = 400; // 高さの最大値

	// public ArrayList<Point> points; // 使っていない模様なので一度退避
	private ArrayList<DrawObject> objects;
	private ArrayList<DrawableShape> shapes;
	/**
		コンストラクタ preferredSize を指定して構築する
		@param width 幅
		@param height 高さ
	*/
	public TestCanvas(int width, int height) {
		super();
		// Canvasのサイズ設定と生成
		// points = new ArrayList<Point>(10);
		objects = new ArrayList<DrawObject>();
		shapes = new ArrayList<DrawableShape>();
		
		setPreferredSize(new Dimension(width, height));
	}

	/**
		 描画オブジェクトの内部表現 精度はすべて float 
	 */
	private static class DrawableShape{
		private static final class ControlPoint2D {
			float x;
			float y;
			String text;
			Color color;
			ControlPoint2D( float x , float y, String text , Color color){
				assert( color != null );
				assert( text != null );
				this.x = x;
				this.y = y;
				this.text = text;
				this.color = color;
			}
			public synchronized Color getColor(){
				return this.color;
			}
		};
		
		private ArrayList<ControlPoint2D> control;
		private String shapeLabel;
		private Color color;
		private float stroke_width;

		DrawableShape(String shapeLabel , Color color){
			this.control = new ArrayList<ControlPoint2D>();
			this.shapeLabel = shapeLabel;
			this.color = color;
			this.stroke_width = 0.5f;
		}
		/**
			 描画オブジェクトの使用する色
		 */
		public synchronized Color getColor(){
			return color;
		}
		/** 
			 コントロールポイントのラベルをおまかせで生成する
			 @return 新しく生成したコントロールポイントのラベル
		 */
		private synchronized String createControlpointLabel(){
			return tryCreateControlpointLabel( 0 );
		}

		/**
			 コントロールポイントのラベルをおまかせで生成する。
			 @return 新しく生成したコントロールポイントのラベル
		 */
		private synchronized String tryCreateControlpointLabel(int offset){
			final String controlLabel;

			if( offset < ( ('Z' - 'A' + 1) - control.size()) ) {
				controlLabel = ( shapeLabel == null) ?
					String.format( "点%c" , ( 'A' + control.size() + offset ) ) :
					String.format( "%s:点%c" , shapeLabel,( 'A' + control.size() + offset ) );
			}else{
				controlLabel = ( shapeLabel == null) ?
					String.format( "点%d" , control.size() + offset ) :
					String.format( "%s:点%d" , control.size() + offset );
			}
			
			for( ControlPoint2D point : control ){
				// 既に同じ名前のラベルが存在した場合は offset を進めて作り直す
				if( controlLabel.equals ( point.text ) ){ 
					return tryCreateControlpointLabel( offset + 1 );
				}
			}
			return controlLabel;
		}
		
		public synchronized void addControlPoint( float x , float y , String text , Color pointColor ){
			control.add( new ControlPoint2D( x , y , text, pointColor ) );
		}
		
		public synchronized void addControlPoint( float x , float y , String text ){
			this.addControlPoint( x , y , text , getColor() );
		}
		public synchronized void addControlPoint( float x , float y , Color pointColor ){
			this.addControlPoint( x , y , createControlpointLabel() , pointColor );
		}
		public synchronized void addControlPoint( float x , float y ){
			this.addControlPoint( x , y , createControlpointLabel(), getColor() );
		}

		/**
			 コントロールポイントを、void Graphics#drawPolygon( int[] , int[] , int ) で使用するための
			 int 配列にパッキングするためのメソッド

			 @param x x軸方向
			 @param y y軸方向
			 @return コントロールポイントの数
			 @see drawPolygon(Graphics2D,TestCanvas) 
			 <p>
			 配列を参照渡ししたいのであるが、Java の仕様上プリミティブな配列の参照渡しはできないので、
			 配列の配列を渡して、参照渡しの代わりにする。
			 </p>

			 疑似コードとしては、
			 int x[][] = {null};
			 int y[][] = {null};
			 int nPoint = packingPolygon( x , y );
			 graphics.drawPolygon( x[0] , y[0] , nPoint );
		*/

		private synchronized int packingPolygon( int x[][] , int y[][]){
			assert x != null :"x is null";
			assert y != null :"y is null";
			assert x.length > 0 : "x.length must be larger than 1" ;
			assert y.length > 0 : "y.length must be larger than 1";
			final int nPoint = control.size();
			x[0] = new int[nPoint];
			y[0] = new int[nPoint];
			for( int i = 0; i < nPoint ; ++i ){
				final ControlPoint2D point = control.get(i);
				x[0][i] = (int)point.x;
				y[0][i] = (int)point.y;
			}
			return nPoint;
		}

		private static final void drawControlPoint( Graphics2D g2 , TestCanvas canvas ,ControlPoint2D point){
			g2.fill( new java.awt.geom.Ellipse2D.Float( point.x-1f , point.y - 1f , 3f ,3f ) );
		}
		
		private static final void drawControlLabel( Graphics2D g2 , TestCanvas canvas ,ControlPoint2D point){
			synchronized( point ){
				final String text = point.text;
				if( text != null ){
					java.text.AttributedCharacterIterator aci = new java.text.AttributedString( text ).getIterator();
					java.awt.font.TextLayout layout = 
						new java.awt.font.TextMeasurer( aci , g2.getFontRenderContext() ).getLayout( aci.getBeginIndex() , aci.getEndIndex() ) ;
					final float label_x = Math.min(Math.max( 0 , point.x - (layout.getAdvance() / 2.0f) ),
																				 ( (float) canvas.getWidth() - (layout.getAdvance() ) ) );       
					final float label_y = Math.max( layout.getAscent() ,
																					point.y - ( layout.getBaseline() + layout.getDescent() ) ); 
					g2.drawString( text, label_x , label_y  );
				}
			}
		}

		public synchronized void draw(Graphics2D g2 , TestCanvas canvas ){
			final int x[][] = {null};
			final int y[][] = {null};
			final int nPoint = packingPolygon( x, y );
			final Color contextColor = g2.getColor();
			{
				final Color shapeColor = getColor();
				final java.awt.Stroke stroke = g2.getStroke();
				if( shapeColor != null ){
					g2.setColor( shapeColor );
				}
				g2.setStroke( new java.awt.BasicStroke( stroke_width ) );
				g2.drawPolygon( x[0] , y[0] , nPoint );
				g2.setStroke( stroke );
			}
			if( shapeLabel != null && ! "".equals( shapeLabel ) ){
				// コントロールポイントの重心に、ラベルを表示する
				int xs = 0; 
				int ys = 0;
				for( int v : x[0] ) xs += v;
				for( int v : y[0] ) ys += v;
				xs /= nPoint;
				ys /= nPoint;

				java.text.AttributedCharacterIterator aci = new java.text.AttributedString( shapeLabel ).getIterator();
				java.awt.font.TextLayout layout =
					new java.awt.font.TextMeasurer( aci , g2.getFontRenderContext() ).getLayout( aci.getBeginIndex() , aci.getEndIndex());
				final float label_x = Math.min( Math.max( 0 , xs - ( layout.getAdvance() / 2.0f ) ),
																				( (float)canvas.getWidth() - ( layout.getAdvance() )));
				final float label_y = Math.min( Math.max( layout.getAscent() , ys + ( layout.getAscent() / 2.0f) ) ,
																				( (float)canvas.getHeight() - (layout.getBaseline() )) );

				g2.drawString( shapeLabel , label_x , label_y );
			}
			for( ControlPoint2D point : control ){
				final Color requestColor = point.getColor();
				if( requestColor != null ){
					g2.setColor( requestColor );
				}
				drawControlPoint( g2 , canvas , point );
				drawControlLabel( g2 , canvas , point );
			}
			g2.setColor( contextColor );
		}
	}

	/**
		 デフォルトコンストラクタ
		 CANVAS_X_MAX と CANVAS_Y_MAX で初期化する。
	 */
	public TestCanvas(){
		this(CANVAS_X_MAX, CANVAS_Y_MAX);
	}

	/**
		 UI 委譲のイベントメソッド
		 このメソッドは、void paint( Graphics ) から呼ばれ 
		 paintComponentImplement( Graphics ) へディスパッチする。
	 */
	@Override 
	protected final void paintComponent( Graphics g ) {  // paintComponent(Graphics) のアクセス指定を、勝手に public に変更しない。
		// Graphics オブジェクトは、メンバー変数として保持してはダメ
		// なぜならば、その下のレイヤーのウィンドウシステムが
		// グラフィックスコンテキストを保持することを許していないためという仕様上の制限に由来するから
		// なので、Graphics オブジェクトは、必ずローカル変数として使われる。
		paintComponentImplement( g );
	}

	/**
		 UI 委譲のイベントメソッド 
		 
	 */
	private void paintComponentImplement( Graphics g ){
		assert Graphics.class.isInstance( g ) : "Graphics.class.isInstance( g )";
		if( Graphics.class.isInstance( g ) ){
			paintComponent( Graphics2D.class.cast( g ) );
		}
	}
	
	private void paintComponent( Graphics2D g2 ){
		final Color previousColor = g2.getColor(); // 後で、もとに戻すために保存

		// 描画で使うアンチエイリアスを有効にする
		g2.setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING ,
												 java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
		
		clearBackground( g2 , Color.white);

		g2.setColor( previousColor ); // 一度前景色に戻す

		if( false ){
			logger.info( "paint" );
			
			for( final DrawObject drawObj : objects ){ // 素直に 拡張 for 文を使う
				drawObjects( g2 , drawObj );
			}
			
		}else{
			for( final DrawableShape shape : shapes ){
				shape.draw( g2 , this );
			}
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

		// 描画位置を示すためのテスト
		//g2.fill( new java.awt.geom.Rectangle2D.Float( x ,  y ,  layout.getAdvance(), (layout.getAscent() +  layout.getBaseline() ) ) );

		// 画面外へ描画されることを抑制
		final float label_y = Math.max( layout.getAscent() ,
																		y -( layout.getBaseline() + layout.getAscent()) ); 
		final float label_x = Math.max( 0 , x - (layout.getAdvance() / 2.0f) ); // TODO 右側は、飛び出す
		g2.drawString(label, label_x , label_y  );
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
		g2.fill( new java.awt.geom.Rectangle2D.Float( x - 1f, y -1f , 3f ,3f ) );
	}
	
	/**
		 コンポーネントが保持する描画オブジェクトを描画する
		 描画順は、 まずポリゴンを描画してから、点を描画して点のラベルを描画する。
		 このメソッドは、前景色を変更するので、呼び出し側が、それを保持しておかなければならない
		 という前提条件があるので、 private 宣言
		 @param g2 Graphics オブジェクト
		 @param drawObj 描画する対象オブジェクト
	 */
	private void drawObjects( Graphics2D g2 , DrawObject drawObj ){
		final java.awt.Stroke stroke = g2.getStroke(); // 線幅の調整
		g2.setStroke( new java.awt.BasicStroke( 0.5f ) );
		{
			final int nPoints = drawObj.points.size();
			
			int[] x = new int[nPoints];
			int[] y = new int[nPoints];
			
			for( int j = 0; j < nPoints ; ++j ){
				final Point point = drawObj.points.get( j );
				x[j] = point.x;
				y[j] = point.y;
				// System.out.printf("draw Point %d\n", j);
			}
			
			final Color color = drawObj.color;
			if( color != null ){
				g2.setColor( color );
			}
			
			assert nPoints<=x.length : "nPoints<=x.length" ;
			assert nPoints<=y.length : "nPoints<=y.length" ;
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
		}
		g2.setStroke( stroke ); // 線幅を戻す
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
	
	/**
		 描画オブジェクトを追加する。
	 */
	public synchronized void addDrawObject(DrawObject drawObject ){
		addDrawObject( drawObject , false );
	}

	/**
		 描画オブジェクトを追加する。
	 */
	public synchronized void addDrawObject(DrawObject drawObject, boolean enableLabel){
		//objects.add(drawObject);
		final Color color = Color.black;
		final DrawableShape shape;
		if( enableLabel ){
			switch( drawObject.points.size() ){
			case 1:
				shape = new DrawableShape(null , color );
				break;
			case 2:
				shape = new DrawableShape("直線" , color );
				break;
			case 3:
				shape = new DrawableShape("三角形" , color );
				break;
			case 4:
				shape = new DrawableShape("四角形" , color );
				break;
			default:
				shape = new DrawableShape("多角形" , color );
				break;
			}
		}else{
			shape = new DrawableShape( null , color );
		}
		
		for( int index = 0;  index < drawObject.points.size() ; ++index ){
			final Point point = drawObject.points.get( index );
			final Color pointColor = point.color;
			final String pointLabel = 
				( drawObject.texts == null) ? null : (( index < drawObject.texts.size() ) ? drawObject.texts.get( index ) : null);
			if( pointLabel == null ){
				shape.addControlPoint( point.x , point.y , pointColor );
			}else{
				shape.addControlPoint( point.x , point.y , pointLabel , pointColor );
			}
		}
		shapes.add( shape );
		
		repaint(); // 再描画を指示
	}
	
	/* show は 基底クラスで使われているメソッドなので、このようなオーバーライドはダメです。
		 素直に、repaint() を直接使うようにします。
	 */
	//public void show(){
	//repaint();
	//}

	public void clear(){
		objects.clear();
		shapes.clear();
		repaint();
	}
}
