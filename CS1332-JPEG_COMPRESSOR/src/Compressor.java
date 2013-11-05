import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


public class Compressor implements ActionListener{
	public static final int MATRIX_SIZE = 8;
	public static int width, height;
	String fileDir;
	BufferedImage img = null;
	int[][][][] rArray, gArray, bArray;
	
	public Compressor(String _fileDir) {
		fileDir = _fileDir;
		try {
			img = ImageIO.read(new File(fileDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		width = img.getWidth();
		height = img.getHeight();
		rArray = new int[width/8][height/8][8][8];
		gArray = new int[width/8][height/8][8][8];
		bArray = new int[width/8][height/8][8][8];
		
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				Color c = new Color(img.getRGB(i, j));
				//int r = c.getRed();
				//int g = c.getGreen();
				//int b = c.getBlue();
				rArray[i/8][j/8][i%8][j%8] = c.getRed();
				gArray[i/8][j/8][i%8][j%8] = c.getGreen();
				bArray[i/8][j/8][i%8][j%8] = c.getBlue();
			}
		}
		
		System.out.println(rArray[63][63][7][7]);
		System.out.println(new Color(img.getRGB(511, 511)).getRed());
		
		JFrame jf = new JFrame("Matrix Viewer");
		jf.add(new JpegPanel(jf, MODE.MATRIX, width, height));
		jf.setVisible(true);
		jf.setSize(new Dimension(width, height));
		jf.setLocation(0, 150);
		//jf.pack();
	}

}
