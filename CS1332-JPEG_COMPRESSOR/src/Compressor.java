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
	public static int[][][][] rArray, gArray, bArray;
	public static float[][][][] rTArray, gTArray, bTArray;
	public static float[][][][] rDArray, gDArray, bDArray;
	public static int[][][][] rCArray, gCArray, bCArray;
	public static int[][][] rZigZagArray, gZigZagArray, bZigZagArray;
	public static int[] rDCPMArray, gDCPMArray, bDCPMArray;
	public static int[][][][] rACArray, gACArray, bACArray;
	
	public static int[][] qArray = {
		 {16, 11, 10, 16, 24, 40, 51, 61}
		,{12, 12, 14, 19, 26, 58, 60, 55}
		,{14, 13, 16, 24, 40, 57, 69, 56}
		,{14, 17, 22, 29, 51, 87, 80, 62}
		,{18, 22, 37, 56, 68, 109, 103, 77}
		,{24, 35, 55, 64, 81, 104, 113, 92}
		,{49, 64, 78, 87, 103, 121, 120, 101}
		,{72, 92, 95, 98, 112, 100, 103, 99}
	};
	
	public Compressor(String _fileDir) {
		fileDir = _fileDir;
		try {
			img = ImageIO.read(new File(fileDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		width = img.getWidth();
		height = img.getHeight();
		rArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		gArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		bArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				Color c = new Color(img.getRGB(i, j));
				//int r = c.getRed();
				//int g = c.getGreen();
				//int b = c.getBlue();
				rArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] = c.getRed();
				gArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] = c.getGreen();
				bArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] = c.getBlue();
			}
		}
		
		rTArray = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		gTArray = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		bTArray = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		
		rDArray = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		gDArray = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		bDArray = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		
		rCArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		gCArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		bCArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		
		rZigZagArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE * MATRIX_SIZE];
		gZigZagArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE * MATRIX_SIZE];
		bZigZagArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE * MATRIX_SIZE];
		
		rDCPMArray = new int[(width/MATRIX_SIZE) * (height/MATRIX_SIZE)];
		gDCPMArray = new int[(width/MATRIX_SIZE) * (height/MATRIX_SIZE)];
		bDCPMArray = new int[(width/MATRIX_SIZE) * (height/MATRIX_SIZE)];
		
		rACArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE * MATRIX_SIZE][2];
		gACArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE * MATRIX_SIZE][2];
		bACArray = new int[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE * MATRIX_SIZE][2];
		
	}
	
	public void getTMatrix(){
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				if(j%MATRIX_SIZE==0){
					rTArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] 
							= (float) (1 / Math.sqrt((double) MATRIX_SIZE));
					gTArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] 
							= (float) (1 / Math.sqrt((double) MATRIX_SIZE));
					bTArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] 
							= (float) (1 / Math.sqrt((double) MATRIX_SIZE));
				}else{
					rTArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] 
							= (float) (  Math.sqrt( 2 / (double) MATRIX_SIZE )
									* (float) Math.cos( (2*(i%MATRIX_SIZE)+1)*(j%MATRIX_SIZE)*Math.PI/(2*MATRIX_SIZE) ) );
				}
			}
		}
	}
	
	public void getMMatrix(){
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				rArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] -= 128;
				gArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] -= 128;
				bArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] -= 128;
			}
		}
	}
	
	public void getDMatrix(){
		float[][][][] rTemp = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		float[][][][] gTemp = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		float[][][][] bTemp = new float[width/MATRIX_SIZE][height/MATRIX_SIZE][MATRIX_SIZE][MATRIX_SIZE];
		
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				for(int x=0; x<MATRIX_SIZE; x++){
					rTemp[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE]
							+= rTArray[i/MATRIX_SIZE][j/MATRIX_SIZE][x][j%MATRIX_SIZE]
									* rArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][x];
				}
			}
		}
		
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				rDArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] = 0;
				for(int x=0; x<MATRIX_SIZE; x++){
					rDArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE]
							+= rTemp[i/MATRIX_SIZE][j/MATRIX_SIZE][x][j%MATRIX_SIZE]
									* rTArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][x]; 
				}
			}
		}
	}
	
	public void getCMatrix(){
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				rCArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE] = (int) Math.round(
						rDArray[i/MATRIX_SIZE][j/MATRIX_SIZE][i%MATRIX_SIZE][j%MATRIX_SIZE]
								/ qArray[i%MATRIX_SIZE][j%MATRIX_SIZE] );
			}
		}
	}
	
	public void zigzagScan(){
		// Vectoring - ZigZag Scan
		for(int i=0; i<img.getWidth(); i++){
			for(int j=0; j<img.getHeight(); j++){
				int index = 0;
				int x, y, s;
				for(int k=0; k<2*(MATRIX_SIZE-1); k++){
					if(k%2 == 0){
						if(k < MATRIX_SIZE){
							x = 0;
							y = k;
							s = 0;
						}else{
							x = k - (MATRIX_SIZE - 1);
							y = MATRIX_SIZE - 1;
							s = k - (MATRIX_SIZE - 1);
						}
						
						while(y >= s){
							rZigZagArray[i/MATRIX_SIZE][j/MATRIX_SIZE][index]
									= rCArray[i/MATRIX_SIZE][j/MATRIX_SIZE][x][y];
							index++;
							x++;
							y--;
						}
					}else{
						if(k < MATRIX_SIZE){
							x = k;
							y = 0;
							s = 0;
						}else{
							x = MATRIX_SIZE - 1;
							y = k - (MATRIX_SIZE - 1);
							s = k - (MATRIX_SIZE - 1);
						}
						
						while(x >= s){
							rZigZagArray[i/MATRIX_SIZE][j/MATRIX_SIZE][index]
									= rCArray[i/MATRIX_SIZE][j/MATRIX_SIZE][x][y];
							index++;
							x--;
							y++;
						}
					}
				}
			}
		}
		
		for(int t=0; t<rZigZagArray[0][0].length; t++){
			System.out.print(String.valueOf(rZigZagArray[0][0][t]) + " ");
		}
		System.out.println("");
		
	}
	
	public void getDCPMDifference(){
		int k = 0;
		int rPrev = rZigZagArray[0][0][0];
		
		for(int j=0; j<height/MATRIX_SIZE; j++){
			for(int i=0; i<width/MATRIX_SIZE; i++){
				if(i==0 && j==0 && k==0){
					//rDCPMArray[k] = 0;
					rDCPMArray[k] = rZigZagArray[i][j][0];
				}else{
					rDCPMArray[k] = rZigZagArray[i][j][0] - rPrev;
				}
				rPrev = rZigZagArray[i][j][0];
				k++;
			}
		}
		/*
		for(int t=0; t<rDCPMArray.length; t++){
			System.out.print(String.valueOf(rDCPMArray[t]) + " ");
		}
		System.out.println("");
		*/
	}
	
	public void RLCCoding(){
		
		for(int i=0; i<width/MATRIX_SIZE; i++){
			for(int j=0; j<height/MATRIX_SIZE; j++){
				int zeroNum = 0;
				int index = 0;
				for(int k=0; k<rZigZagArray[i][j].length; k++){
					if(rZigZagArray[i][j][k] == 0){
						zeroNum++;
					}else{
						rACArray[i][j][index][0] = zeroNum;
						rACArray[i][j][index][1] = rZigZagArray[i][j][k];
						//System.out.println("zeroNum: " + zeroNum + " | rZigZagArray[i][j][k]: " + rZigZagArray[i][j][k]);
						zeroNum = 0;
						index++;
					}
				}
			}
		}
		/*
		for(int t=0; t<rACArray[0][0].length; t++){
			System.out.print(String.valueOf(rACArray[0][0][t][0]) + " " + String.valueOf(rACArray[0][0][t][1]) + " | ");
		}
		System.out.println("");
		*/
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "Show Matrix":
			
			
			//System.out.println(rArray[63][63][7][7]);
			//System.out.println(new Color(img.getRGB(511, 511)).getRed());
			
			JFrame jf = new JFrame("Matrix Viewer");
			jf.add(new JpegPanel(jf, MODE.MATRIX, width, height));
			jf.setVisible(true);
			jf.setSize(new Dimension(width, height));
			jf.setLocation(0, 150);
			//jf.pack();
			break;
		case "DCT Process":
			getTMatrix();
			getMMatrix();
			getDMatrix();
			getCMatrix();
			zigzagScan();
			getDCPMDifference();
			RLCCoding();
			break;
		}
	}
	
}
