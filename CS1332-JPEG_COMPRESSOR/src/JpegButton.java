import javax.swing.JButton;


public class JpegButton extends JButton {
	int col;
	int row;
	public JpegButton(String _str, int _col, int _row){
		super(_str);
		col = _col;
		row = _row;
	}
	public int getCol(){
		return col;
	}
	public int getRow(){
		return row;
	}

}
