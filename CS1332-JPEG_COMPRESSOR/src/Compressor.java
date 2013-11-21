import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Compressor {
	public static final int N = 8;
	public int numOfChannel = 1;
	
	private String fileDir, outFileDir;
	private int quality;
	private BufferedImage image = null;
	
	private ImageWriter writer;
	private ImageInfo imgInfo;
	private DCT dct;
	private Huffman hufman;

	public void setFileDir(String _fileDir) {
		fileDir = _fileDir;
	}

	public void setOutFileDir(String _outFileDir) {
		outFileDir = _outFileDir;
	}

	public void setQuality(int _quality) {
		quality = _quality;
	}
	
	public void setMode(boolean _mode) {
		if(_mode)	numOfChannel = 3;
		else	numOfChannel = 1;
	}
	
	public void execute() {
		try {
			image = ImageIO.read(new File(fileDir));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
		
		imgInfo = new ImageInfo(image);
		dct = new DCT(quality);
		hufman = new Huffman();
		writer = new ImageWriter(outFileDir, quality, dct, numOfChannel, imgInfo, hufman);
		hufman.setWriter(writer);
		
		writer.writeHeader();
		compress();
		writer.writeEnd();
		
		try {
			writer.outStream.flush();
		}catch (IOException e){
			System.out.println("IO Error: " + e.getMessage());
		}
		
		try {
			writer.fileStream.close();
		} catch (IOException e) {
			
		}
	}
	
	private void compress(){
		float inputArray[][];
		float dctArray1[][] = new float[N][N];
		float dctArray2[][] = new float[N][N];
		int dctArray3[] = new int[N * N];
		
		/*
		 * This method controls the compression of the image. Starting at the upper
		 * left of the image, it compresses 8x8 blocks of data until the entire
		 * image has been compressed.
		 */
	    int lastDCvalue[] = new int[numOfChannel];
	    int MinBlockWidth, MinBlockHeight;
	    
	    // This initial setting of MinBlockWidth and MinBlockHeight is done to
		// ensure they start with values larger than will actually be the case.
		MinBlockWidth = ((imgInfo.getWidth() % 8 != 0) ? (int) (Math.floor(imgInfo.getWidth() / 8.0) + 1) * 8 : imgInfo.getWidth());
		MinBlockHeight = ((imgInfo.getHeight() % 8 != 0) ? (int) (Math.floor(imgInfo.getHeight() / 8.0) + 1) * 8 : imgInfo.getHeight());
		for (int comp = 0; comp < numOfChannel; comp++) {
			MinBlockWidth = Math.min(MinBlockWidth, imgInfo.getBlockWidth());
			MinBlockHeight = Math.min(MinBlockHeight, imgInfo.getBlockHeight());
		}
		int xpos = 0;
		int ypos = 0;
		for (int r = 0; r < MinBlockHeight; r++) {
			for (int c = 0; c < MinBlockWidth; c++) {
				xpos = c * 8;
				ypos = r * 8;
				for (int comp = 0; comp < numOfChannel; comp++) {
					// Width = JpegObj.BlockWidth[comp];
					// Height = JpegObj.BlockHeight[comp];
					inputArray = imgInfo.getComp()[comp];
		
					for (int i = 0; i < ImageWriter.numOfSample; i++) {
						for (int j = 0; j < ImageWriter.numOfSample; j++) {
							int xblockoffset = j * 8;
							int yblockoffset = i * 8;
							for (int a = 0; a < 8; a++) {
								for (int b = 0; b < 8; b++) {
									dctArray1[a][b] = inputArray[ypos + yblockoffset + a][xpos + xblockoffset + b];
								}
							}
							dctArray2 = dct.forwardDCT(dctArray1);
							dctArray3 = dct.quantize(dctArray2);
							hufman.encode(dctArray3, lastDCvalue[comp]);
							lastDCvalue[comp] = dctArray3[0];
						}
					}
				}
			}
		}
		writer.flushBuffer();
	}
}
