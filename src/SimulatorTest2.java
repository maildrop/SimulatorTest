
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
								Random rnd = new Random();
								int_pair src[] = {
									new int_pair( rnd.nextInt(canvas.getWidth()), rnd.nextInt(canvas.getHeight() ) ),
									new int_pair( rnd.nextInt(canvas.getWidth()), rnd.nextInt(canvas.getHeight() ) )
								};
								if( src[0].y > src[1].y ){
									int_pair.swap( src[0], src[1] );
								}
								canvas.addDrawObject( buildDrawObject( src ) );
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
								Test( new Pen( canvas ) );
								return;
							}
						});
					buttonPanel.add( button );
				}

				if( false ){ // 多角形(n>3) になると、ねじれが起きるので、どうするのか考えるところ
					final JButton button = new JButton("四角形");
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								Random rnd = new Random();
								int_pair src[] = {
									new int_pair( rnd.nextInt(canvas.getWidth()), rnd.nextInt(canvas.getHeight() ) ),
									new int_pair( rnd.nextInt(canvas.getWidth()), rnd.nextInt(canvas.getHeight() ) ),
									new int_pair( rnd.nextInt(canvas.getWidth()), rnd.nextInt(canvas.getHeight() ) ),
									new int_pair( rnd.nextInt(canvas.getWidth()), rnd.nextInt(canvas.getHeight() ) ),
								};

								canvas.addDrawObject( buildDrawObject( src ) );
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
			return t; // t*90.0f が degree である。 Windowの座標系が上下逆なことに注意
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
	
	public static void Test(Pen pen){
		Random rnd = new Random();

		// y 軸 の頂点に最も近い点を、基準点にもってきて、
		// 左回りに点を整列させる。
		int_pair src[] = {
			new int_pair( rnd.nextInt(pen.canvas.getWidth()), rnd.nextInt(pen.canvas.getHeight() ) ),
			new int_pair( rnd.nextInt(pen.canvas.getWidth()), rnd.nextInt(pen.canvas.getHeight() ) ),
			new int_pair( rnd.nextInt(pen.canvas.getWidth()), rnd.nextInt(pen.canvas.getHeight() ) )
		};
		// 基準点を見つけるために、最小のy を持つ int_pair を int_pair[0] に持ってくる
		if( src[0].y > src[1].y ){
			int_pair.swap( src[0] , src[1] );
		}
		if( src[0].y > src[2].y ){
			int_pair.swap( src[0] , src[2] ); 
		}

		if( int_pair.theta( src[0] , src[1] ) < int_pair.theta( src[0], src[2] ) ){
			int_pair.swap( src[1] , src[2] );
		}
		
		DrawObject sankaku = buildDrawObject( src );
		// sankaku.setText(); // 無意味
		pen.Draw(sankaku);
		
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
