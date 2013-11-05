import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;


public class EightByEightLayout extends GridLayout {
	JpegPanel panel;
	public EightByEightLayout(JpegPanel _panel, int _cols, int _rows){
		super(0, _cols);
		panel = _panel;
		for(int i=0; i<_cols; i++){
			for(int j=0; j<_rows; j++){
				JLabel label = new JLabel(String.valueOf(i));
				label.setSize(new Dimension(10, 10));
				panel.add(label);
			}
		}
	}
	// TODO: Update matrix values every frame when the values are changed..
}
