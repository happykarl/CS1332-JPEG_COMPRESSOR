import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class MainLayout extends GroupLayout {
	JLabel lbImportDir, lbExportDir;
	JTextField tfImportDir, tfExportDir;
	JButton btImportDir, btProcess;
	
	public MainLayout(Container host) {
		super(host);
		
		this.setAutoCreateGaps(true);
		this.setAutoCreateContainerGaps(true);
   
		// Element Intialize
		lbImportDir = new JLabel("Import Directory: ");
		lbExportDir = new JLabel("Export Directory: ");
		tfImportDir = new JTextField("Import Directory...", 50);
		tfExportDir = new JTextField("Export Directory...", 50);
		
	    btImportDir = new JButton("File Select");
	    btProcess = new JButton("Start Compression");

	    // Vertex Group
	    GroupLayout.SequentialGroup leftToRight = this.createSequentialGroup();

	    GroupLayout.ParallelGroup columnLeft = this.createParallelGroup();
		columnLeft.addComponent(lbImportDir);
		columnLeft.addComponent(lbExportDir);
		leftToRight.addGroup(columnLeft);
	    
		GroupLayout.ParallelGroup columnCenter = this.createParallelGroup();
		columnCenter.addComponent(tfImportDir);
		columnCenter.addComponent(tfExportDir);
		columnCenter.addComponent(btProcess);
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
		rowBottom.addComponent(btProcess);
		topToBottom.addGroup(rowBottom);
		
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
				btProcess.addActionListener(new Compressor(fileDir));
			}
			
		}
	}
}
