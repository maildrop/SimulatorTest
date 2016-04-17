public class Ruler{

	/**
	* 2�_�Ԃ̋��������߂�
	*/
	public static double getDistance(Point p1, Point p2){
		double x = p1.x - p2.x;
		double y = p1.y - p2.y;

		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	/**
	* 3�_����p�x�����߂� p1�̊p�x���Ԃ�
	*/
	public static double getAngle(Point p1, Point p2, Point p3){
		double vec12[] ={p1.x - p2.x,  p1.y - p2.y };
		double vec23[] ={p3.x - p2.x,  p3.y - p2.y };

		double vec1223 = vec12[0]*vec23[0]+vec12[1]*vec23[1];
		double absvec12 = Math.sqrt(Math.pow(vec12[0], 2) + Math.pow(vec12[1], 2));
		double absvec23 = Math.sqrt(Math.pow(vec23[0], 2) + Math.pow(vec23[1], 2));

		return Math.toDegrees(Math.acos(vec1223 / (absvec12 * absvec23)));
	}
}