
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
				
				{ // 第一要素 TEST ボタン
					final JButton button = new JButton( "TEST" );
					final Pen pen = new Pen( canvas );
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								Test( pen );
								return;
							}
						});
					buttonPanel.add( button );
				}
				
				{ // 第二要素 CLEAR ボタン
					final JButton button = new JButton( "CLEAR" );
					button.addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent event ){
								System.out.printf("clear");
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

	public static void Test(Pen pen){
		Random rnd = new Random();

		ArrayList<Point> points = new ArrayList<Point>();
		Point p1 = new Point(rnd.nextInt(pen.canvas.getWidth()), rnd.nextInt(pen.canvas.getHeight()));
		Point p2 = new Point(rnd.nextInt(pen.canvas.getWidth()), rnd.nextInt(pen.canvas.getHeight()));
		Point p3 = new Point(rnd.nextInt(pen.canvas.getWidth()), rnd.nextInt(pen.canvas.getHeight()));

		points.add(p1);
		points.add(p2);
		points.add(p3);

		DrawObject sankaku = new DrawObject(points);
		pen.Draw(sankaku);
		sankaku.setText();
		System.out.printf("%.1f度\n",Ruler.getAngle(p1, p2, p3));
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
