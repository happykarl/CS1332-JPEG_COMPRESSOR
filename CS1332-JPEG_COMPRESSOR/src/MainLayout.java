import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class MainLayout extends GroupLayout {
	JLabel lbImportDir, lbExportDir, lbQuality;
	JTextField tfImportDir, tfExportDir, tfQuality;
	JButton btImportDir, btShowMatrix, btDCT;
	JpegCompressor cp;
	
	public MainLayout(Container host) {
		super(host);
		
		this.setAutoCreateGaps(true);
		this.setAutoCreateContainerGaps(true);
   
		// Element Intialize
		lbImportDir = new JLabel("Import Directory: ");
		lbExportDir = new JLabel("Export Directory: ");
		lbQuality = new JLabel("JPEG Quality: ");
		tfImportDir = new JTextField("Import Directory...", 50);
		tfExportDir = new JTextField("Export Directory...", 50);
		tfQuality = new JTextField("50", 50);
		
	    btImportDir = new JButton("File Select");
	    btShowMatrix = new JButton("Show Matrix");
	    btDCT = new JButton("Start Converting");

	    // Vertex Group
	    GroupLayout.SequentialGroup leftToRight = this.createSequentialGroup();

	    GroupLayout.ParallelGroup columnLeft = this.createParallelGroup();
		columnLeft.addComponent(lbImportDir);
		columnLeft.addComponent(lbExportDir);
		columnLeft.addComponent(lbQuality);
		columnLeft.addComponent(btDCT);
		leftToRight.addGroup(columnLeft);
	    
		GroupLayout.ParallelGroup columnCenter = this.createParallelGroup();
		columnCenter.addComponent(tfImportDir);
		columnCenter.addComponent(tfExportDir);
		columnCenter.addComponent(tfQuality);
		columnCenter.addComponent(btShowMatrix);
		
		leftToRight.addGroup(columnCenter);
		
		GroupLayout.ParallelGroup columnRight = this.createParallelGroup();
		columnRight.addComponent(btImportDir);
		leftToRight.addGroup(columnRight);
		
		// Horizontal Group
		GroupLayout.SequentialGroup topToBottom = this.createSequentialGroup();
		GroupLayout.ParallelGroup rowTop = this.createParallelGroup();
		rowTop.addComponent(lbImportDir);
		rowTop.addComponent(tfImportDir);
		rowTop.addComponent(btImportDir);
		topToBottom.addGroup(rowTop);
		
		GroupLayout.ParallelGroup rowMiddle = this.createParallelGroup();
		rowMiddle.addComponent(lbExportDir);
		rowMiddle.addComponent(tfExportDir);
		topToBottom.addGroup(rowMiddle);
		
		GroupLayout.ParallelGroup rowBottom = this.createParallelGroup();
		rowBottom.addComponent(lbQuality);
		rowBottom.addComponent(tfQuality);
		topToBottom.addGroup(rowBottom);
		
		GroupLayout.ParallelGroup rowBottom2 = this.createParallelGroup();
		rowBottom2.addComponent(btShowMatrix);
		rowBottom2.addComponent(btDCT);
		topToBottom.addGroup(rowBottom2);
		
		this.setHorizontalGroup(leftToRight);
		this.setVerticalGroup(topToBottom);
		
		btImportDir.addActionListener(new FileOpen());
	}
	
	private class FileOpen implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int rsVal = fileChooser.showOpenDialog(btImportDir);
			if (rsVal == JFileChooser.APPROVE_OPTION) {
				String fileDir = fileChooser.getCurrentDirectory().toString() + "\\" + fileChooser.getSelectedFile().getName();
				tfImportDir.setText(fileDir);
				
				String outFileDir = fileDir.substring(0, fileDir.lastIndexOf(".")) + ".jpg";
				File outFile = new File(outFileDir);
				int i = 1;
				while (outFile.exists()) {
					outFileDir = fileDir.substring(0, fileDir.lastIndexOf(".")) + (i++) + ".jpg";
					outFile = new File(outFileDir);
				}
						
				tfExportDir.setText(outFileDir);
				btDCT.addActionListener(new StartConvert(fileDir, outFileDir));
			}
		}
	}
	
	private class StartConvert implements ActionListener{
		private String fileDir, outFileDir;
		public StartConvert(String _fileDir, String _outFileDir){
			fileDir = _fileDir;
			outFileDir = _outFileDir;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			cp = new JpegCompressor( fileDir, outFileDir, Integer.parseInt(tfQuality.getText()) );
			btShowMatrix.addActionListener(cp);
		}
		
	}
}
