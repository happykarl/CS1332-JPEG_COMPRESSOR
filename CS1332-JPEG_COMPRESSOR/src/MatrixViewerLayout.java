import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MatrixViewerLayout extends GridLayout {
	JpegPanel panel;
	public MatrixViewerLayout(JpegPanel _panel, int _cols, int _rows){
		super(0, _cols / Compressor.MATRIX_SIZE);
		panel = _panel;
		for(int i=0; i<_cols / Compressor.MATRIX_SIZE; i++){
			for(int j=0; j<_rows / Compressor.MATRIX_SIZE; j++){
				JpegButton bt = new JpegButton("", i, j);
				//bt.setSize(new Dimension(1, 1));
				bt.addActionListener(new EightByEightMatrix(bt.getRow(), bt.getCol()));
				panel.add(bt);
			}
		}
	}
	
	private class EightByEightMatrix implements ActionListener{
		int row;
		int col;
		public EightByEightMatrix(int _row, int _col){
			row = _row;
			col = _col;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println(String.valueOf(row) + " | " + String.valueOf(col));
			JFrame jf = new JFrame("8x8 Matrix Viewer");
			jf.add(new JpegPanel(jf, MODE.EIGHTBYEIGHT, row, col));
			jf.setVisible(true);
			jf.setLocation(Compressor.width, 150);
			jf.pack();
		}
		
	}
}
