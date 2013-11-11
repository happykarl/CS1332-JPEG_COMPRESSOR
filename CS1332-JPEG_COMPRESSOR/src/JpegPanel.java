import java.awt.ComponentOrientation;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class JpegPanel extends JPanel {
	MainLayout mainLayout;
	MatrixViewerLayout matrixLayout;
	EightByEightLayout eightByEightLayout;
	
	public JpegPanel(JFrame frame, MODE mode, Object... params){
		switch(mode){
		case MAIN:
			mainLayout = new MainLayout(this);
			this.setLayout(mainLayout);
			break;
		case MATRIX:
			//if(params.length == 2 && params[0] instanceof Integer){
			if(params.length == 2){
				this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				matrixLayout = new MatrixViewerLayout(this, (int) params[0], (int) params[1]);
				this.setLayout(matrixLayout);
			}
			break;
		case EIGHTBYEIGHT:
			if(params.length == 2){
				this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
				eightByEightLayout = new EightByEightLayout(this, (int) params[0], (int) params[1], Compressor.MATRIX_SIZE, Compressor.MATRIX_SIZE);
				this.setLayout(eightByEightLayout);
			}
			break;
		default:
			break;
		}
	}
}
