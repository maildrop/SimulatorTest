
import java.lang.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
	 AWT(とSwing)のコンポーネントは、JDK 1.1 までは、イベントハンドリングのために、
	 継承して使うのが基本であったが、継承は内部構造に熟知する必要があったので、
	 （オーバーライドしてよいメソッドとしてはいけないメソッドがあったのだが、
	 ユーザ（ここではプログラムを書く人のこと）はそんなこと考えずにガンガン継承して
	 不具合を出す、バージョンアップで動作しなくなる等がおきた。）
	 
	 イベントモデルの整備後は、特定のコンポーネント（Canvas/JComponent 等）以外は、
	 継承せずに、手続き的に使うことが推奨されているのであるが、
	 （例えば、 How to Make Frames(Main Windows) 
	 http://docs.oracle.com/javase/tutorial/uiswing/components/frame.html )
	 1.0 時代からの伝統的な書籍類が、継承スタイルで書くのでこの問題はずっと残っている。
	 残念 

	 なら、final つけたり、protected classs にすればいいじゃないと思われるかもしれないが
	 これは破壊的な変更なので、歴史的経緯ってやつで無理なんだ すまない。
 */

/**


 */
public final class SimulatorTest2 {

	/**
		 GUI を作成して表示する
	 */
	private final static void createAndShowGUI(){
		JFrame mainFrame = new JFrame("SimulatorTest");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout( new java.awt.BorderLayout() );

		{
			final TestCanvas canvas = new TestCanvas();
			panel.add( canvas , BorderLayout.CENTER );
			
			
			{ // 南側に配置するパネルを作成
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout( new java.awt.FlowLayout() );
				
				{
					final JButton button = new JButton( "直線" );
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								canvas.addDrawObject( createRundomizePolygonSample( 2 , canvas ) );
								canvas.repaint();
								return;
							}
						});
					buttonPanel.add( button );
				}

				{ 
					final JButton button = new JButton( "三角形" );
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								canvas.addDrawObject( createRundomizePolygonSample( 3 , canvas ) );
								canvas.repaint();
								return;
							}
						});
					buttonPanel.add( button );
				}
				
				{
					final JButton button = new JButton("四角形");
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								canvas.addDrawObject( createRundomizePolygonSample( 4 , canvas ) );
								canvas.repaint();
								return;
							}
						});
					buttonPanel.add( button );
				}

				{
					final JButton button = new JButton("test1");
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								canvas.addDrawObject( createRundomizePolygonSample( 256 , canvas ) );
								canvas.repaint();
								return;
							}
						});
					buttonPanel.add( button );
				}
				
				{ // CLEAR ボタン
					final JButton button = new JButton( "CLEAR" );
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								canvas.clear();
								return;
							}
						});
					buttonPanel.add( button );
				}
				
				panel.add( buttonPanel, BorderLayout.SOUTH );
			}
		}

		mainFrame.getContentPane().add( panel , BorderLayout.CENTER );
		mainFrame.pack(); // 再配置
		mainFrame.setVisible(true);
		return;
	}

	/**
		canvas の 幅さと高さに納まるように、頂点をランダムに選択してnNum角形のDrawObject を 
		構築する。 頂点は、y軸が最も 0に近い点を最初に選び 以後左回りで SimpleClosePath を構築する。
		createRundomizePolygonSample( int ,int ,int ) のコンビニエンスメソッド
		@param pNum 作成する多角形の頂点の数
		@param canvas 描画先のオブジェクト。オブジェクトの高さと幅を使用する。
		@return 作成されたDrawObject
	*/
	private static final DrawObject createRundomizePolygonSample( int pNum , java.awt.Component canvas){
		assert canvas != null : "canvas is not allowed null" ;
		return createRundomizePolygonSample( pNum , canvas.getWidth() , canvas.getHeight() );
	}

	/**
		 (0,0)-(width,height) に納まるように頂点をランダムに選択して、pNum 角形の DrawObject を構築する 
		 頂点は、y軸が最も 0 に近い ( GUI 上では、もっとも上に位置する点）を最初に選び、以後左回りでSimpleClosePath を構築する。
		 @param pNum 作成する多角形の頂点の数
		 @param width 生成される頂点のx軸方向の最大値
		 @param height 生成される頂点のy軸方向の最大値 
		 @return 作成されたDrawObject
	 */
	private static final DrawObject createRundomizePolygonSample( int pNum , int width , int height ){
		// 引数チェック 
		assert pNum > 0 : "pNum must be natural number" ;
		assert width > 0 ;
		assert height > 0;
		if( 0 < pNum ){
			throw new IllegalArgumentException("pNum must be natural number");
		}
		
		Random rnd = new Random();
		int_pair src[] = new int_pair[pNum];
		for( int i = 0; i < src.length ; ++i ){
			src[i] = new int_pair( rnd.nextInt( width ), rnd.nextInt(height) );
		}
		
		for(int i = 1 ; i< src.length ; ++i ){
			if( src[0].y > src[i].y ){
				int_pair.swap( src[0] , src[i] );
			}
		}
		
		final int_pair base = src[0];
		java.util.Arrays.sort( src , 1, src.length ,new java.util.Comparator<int_pair>(){
				@Override
				public int compare( int_pair l , int_pair r ){
					float lv = base == l ? 0f : int_pair.theta( base , l );
					float rv = base == r ? 0f : int_pair.theta( base , r );
					if( lv == rv ){
						return 0;
					}else{
						return ( lv < rv ) ? 1 : -1;
					}
				}
			});
		return buildDrawObject( src );
	}
	
	private static class int_pair{
		public int x;
		public int y;
		private int_pair( int x , int y ){
			this.x = x;
			this.y = y;
		}
		private int_pair( int_pair r ){
			this.x = r.x;
			this.y = r.y;
		}
		private static final void swap( int_pair l , int_pair r ){
			final int_pair tmp = new int_pair( l );
			l.x = r.x ;
			l.y = r.y ;
			r.x = tmp.x;
			r.y = tmp.y;
		}

		// atan-1 の順番に並べるための式
		private static final float theta( int_pair p1 , int_pair p2 ){
			final int dx = p2.x - p1.x;
			final int dy = p2.y - p1.y;
			final int ax = Math.abs( dx );
			final int ay = Math.abs( dy );
			float t = ( ax + ay ) == 0 ? 0 : (float)(dy)/((float)ax + (float)ay );
			if( dx < 0 ){
				t = 2f -t ;
			}else if( dy < 0 ){
				t = t + 4f;
			}
			return t; // t*90.0f が degree である。 Windowの座標系は、Y軸は下方向へ伸びることに注意
		}
		
	};

	/**
		 int_pair の配列から ArrayList&lt;Point&gt; を作成する
	 */
	private static final ArrayList<Point> buildPointArrayList( int_pair src[] ){
		final java.util.concurrent.Callable<String> labelGen = new java.util.concurrent.Callable<String>(){
				int i = 0;
				public synchronized final String call(){
					return String.format( "点%c" , 'A' + (i++) );
				}
		};
		ArrayList<Point> points = new ArrayList<Point>();
		for( int_pair p : src ){
			try{
				points.add( new Point( p.x , p.y , labelGen.call() ) );
			}catch( Exception e ){
				points.add( new Point( p.x , p.y , e.toString() ) );
			}
		}
		if( false ){
			switch( points.size() ){
			case 3:
				System.out.println(String.format( "%.1f度",Ruler.getAngle(points.get(0) , points.get(1) , points.get(2) ) ));
				break;
			default:
				;
			}
		}
		return points;
	}
	
	/**
		 DrawObject を構築する。
	 */
	private static final DrawObject buildDrawObject( int_pair src[] ){
		return new DrawObject( buildPointArrayList( src ) );
	}

	/**
		 エントリーポイント
	 */
	public static final void main(String args[]){
		// main() の中から直接実行すればよいと思われるかもしれないが、
		// ClassLoaderが、他のクラスの初期化を終えるまで、
		// GUI の表示を待つ必要がある時がある。
		// ( これを、スレッドセーフのためと Oracle は説明している。）
		// このために、実際の実行を後回しにするサポートメソッド必要
		javax.swing.SwingUtilities.invokeLater( new Runnable(){
				@Override
				public synchronized void run(){
					createAndShowGUI();
				}
			});
	}
}
