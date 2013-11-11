import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;


public class EightByEightLayout extends GridLayout {
	private JpegPanel panel;
	private JLabel[][] labels;
	int col, row, cols, rows;
	public EightByEightLayout(JpegPanel _panel, int _col, int _row, int _cols, int _rows){
		super(0, _cols);
		col = _col;
		row = _row;
		cols = _cols;
		rows = _rows;
		panel = _panel;
		labels = new JLabel[cols][rows];
		
		for(int i=0; i<cols; i++){
			for(int j=0; j<rows; j++){
				labels[i][j] = new JLabel();
				labels[i][j].setSize(new Dimension(10, 10));
				String str = String.valueOf(Compressor.rCArray[col][row][j][i]) + " ";
				labels[i][j].setText(str);
				panel.add(labels[i][j]);
			}
		}
	}
	
	// TODO: Update matrix values every frame when the values are changed..
}
