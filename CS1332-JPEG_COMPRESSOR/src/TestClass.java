import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class TestClass {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedImage img = null;
		int width;
		int height;
		int[][][][] rArray, gArray, bArray;
		try {
			img = ImageIO.read(new File("lena.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	}

}
