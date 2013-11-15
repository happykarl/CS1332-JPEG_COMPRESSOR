import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;



public class JpegCompressor implements ActionListener{
	public static final int N = 8;
	
	
	private String fileDir, outFileDir;
	private File outFile;
	private int quality;
	private BufferedImage image = null;
	private BufferedOutputStream outStream;
	private FileOutputStream fileStream;
	private int imageWidth, imageHeight;
	JpegInfo JpegObj;
	private DCT dct;
	private Huffman Huf;
	
	
	
	public JpegCompressor(String _fileDir, String _outFileDir, int _quality){
		fileDir = _fileDir;
		try {
			image = ImageIO.read(new File(fileDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		quality = _quality;
		outFileDir = _outFileDir;
		outFile = new File(outFileDir);
		try {
			fileStream = new FileOutputStream(outFile);
			outStream = new BufferedOutputStream(fileStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		JpegObj = new JpegInfo(image);
		imageHeight = JpegObj.imageHeight;
		imageWidth = JpegObj.imageWidth;
		
		dct = new DCT(quality);
		Huf = new Huffman(imageWidth, imageHeight);
		compress();
	}
	
	public void compress(){
		WriteHeaders(outStream);
		WriteCompressedData(outStream);
		WriteEOI(outStream);
		try {
			outStream.flush();
		}catch (IOException e){
			System.out.println("IO Error: " + e.getMessage());
		}
		
		try {
			fileStream.close();
			} catch (IOException e) {
				
			}
	}
	
	
	public void WriteCompressedData(BufferedOutputStream outStream) {
		int i, j, r, c, a, b;
	    int comp, xpos, ypos, xblockoffset, yblockoffset;
	    float inputArray[][];
	    float dctArray1[][] = new float[8][8];
	    double dctArray2[][] = new double[8][8];
	    int dctArray3[] = new int[8 * 8];

	    /*
	     * This method controls the compression of the image. Starting at the upper
	     * left of the image, it compresses 8x8 blocks of data until the entire
	     * image has been compressed.
	     */

	    int lastDCvalue[] = new int[JpegObj.NumberOfComponents];
	    int MinBlockWidth, MinBlockHeight;
	    // This initial setting of MinBlockWidth and MinBlockHeight is done to
	    // ensure they start with values larger than will actually be the case.
	    MinBlockWidth = ((imageWidth % 8 != 0) ? (int) (Math.floor(imageWidth / 8.0) + 1) * 8 : imageWidth);
	    MinBlockHeight = ((imageHeight % 8 != 0) ? (int) (Math.floor(imageHeight / 8.0) + 1) * 8 : imageHeight);
	    for (comp = 0; comp < JpegObj.NumberOfComponents; comp++) {
	    	MinBlockWidth = Math.min(MinBlockWidth, JpegObj.BlockWidth[comp]);
	    	MinBlockHeight = Math.min(MinBlockHeight, JpegObj.BlockHeight[comp]);
    	}
	    xpos = 0;
	    for (r = 0; r < MinBlockHeight; r++) {
	    	for (c = 0; c < MinBlockWidth; c++) {
	    		xpos = c * 8;
	    		ypos = r * 8;
	    		for (comp = 0; comp < JpegObj.NumberOfComponents; comp++) {
	    			// Width = JpegObj.BlockWidth[comp];
	    			// Height = JpegObj.BlockHeight[comp];
	    			inputArray = (float[][]) JpegObj.Components[comp];

	    			for (i = 0; i < JpegObj.VsampFactor[comp]; i++) {
	    				for (j = 0; j < JpegObj.HsampFactor[comp]; j++) {
	    					xblockoffset = j * 8;
	    					yblockoffset = i * 8;
	    					for (a = 0; a < 8; a++) {
	    						for (b = 0; b < 8; b++) {
	    							dctArray1[a][b] = inputArray[ypos + yblockoffset + a][xpos + xblockoffset + b];
    							}
    						}
	    					dctArray2 = dct.forwardDCT2(dctArray1);
	    					dctArray3 = dct.quantizeBlock(dctArray2, JpegObj.QtableNumber[comp]);
	    					Huf.HuffmanBlockEncoder(outStream, dctArray3, lastDCvalue[comp], JpegObj.DCtableNumber[comp], JpegObj.ACtableNumber[comp]);
	    					lastDCvalue[comp] = dctArray3[0];
    					}
    				}
	    		}
	    	}
	    }
	    Huf.flushBuffer(outStream);
	}

	public void WriteEOI(BufferedOutputStream out){
		byte[] EOI = { (byte) 0xFF, (byte) 0xD9 };
		WriteMarker(EOI, out);
	}
	
	public void WriteMarker(byte[] data, BufferedOutputStream out){
		try{
			out.write(data, 0, 2);
		}catch(IOException e){
			System.out.println("IO Error: " + e.getMessage());
		}
	}
	
	public void WriteArray(byte[] data, BufferedOutputStream out){
		int length;
		try{
			length = ((data[2] & 0xFF) << 8) + (data[3] & 0xFF) + 2;
			out.write(data, 0, length);
		}catch(IOException e){
			System.out.println("IO Error: " + e.getMessage());
		}
	}
	
	public void WriteHeaders(BufferedOutputStream out) {
		int i, j, index, offset, length;
		int tempArray[];

		// the SOI marker (Start of Image) First byte is 255 and second byte is 216
		byte[] SOI = { (byte) 0xFF, (byte) 0xD8 };
		WriteMarker(SOI, out);

		// The order of the following headers is quiet inconsequential.
		// the JFIF header
		byte JFIF[] = new byte[18];
		JFIF[0] = (byte) 0xff;
		JFIF[1] = (byte) 0xe0;
		JFIF[2] = (byte) 0x00;
		JFIF[3] = (byte) 0x10;
		JFIF[4] = (byte) 0x4a;
		JFIF[5] = (byte) 0x46;
		JFIF[6] = (byte) 0x49;
		JFIF[7] = (byte) 0x46;
		JFIF[8] = (byte) 0x00;
		JFIF[9] = (byte) 0x01;
		JFIF[10] = (byte) 0x00;
		JFIF[11] = (byte) 0x00;
		JFIF[12] = (byte) 0x00;
		JFIF[13] = (byte) 0x01;
		JFIF[14] = (byte) 0x00;
		JFIF[15] = (byte) 0x01;
		JFIF[16] = (byte) 0x00;
		JFIF[17] = (byte) 0x00;
		WriteArray(JFIF, out);

		/*
		// Comment Header
		String comment = "";
		comment = JpegObj.getComment();
		length = comment.length();
		byte COM[] = new byte[length + 4];
		COM[0] = (byte) 0xFF;
		COM[1] = (byte) 0xFE;
		COM[2] = (byte) ((length >> 8) & 0xFF);
		COM[3] = (byte) (length & 0xFF);
		java.lang.System.arraycopy(JpegObj.Comment.getBytes(), 0, COM, 4, JpegObj.Comment.length());
		WriteArray(COM, out);
		*/
		
		// The DQT header (Define Quantization Table) - 255 / 219
		// 0 is the luminance index and 1 is the chrominance index
		byte DQT[] = new byte[134];
		DQT[0] = (byte) 0xFF;
		DQT[1] = (byte) 0xDB;
		DQT[2] = (byte) 0x00;
		DQT[3] = (byte) 0x84;
		offset = 4;
		for (i = 0; i < 2; i++) {
			DQT[offset++] = (byte) ((0 << 4) + i);
			tempArray = (int[]) dct.quantum[i];
			for (j = 0; j < 64; j++) {
				//DQT[offset++] = (byte) tempArray[Huffman.jpegNaturalOrder[j]];	// more smoothing
				DQT[offset++] = (byte) tempArray[j];
			}
		}
		WriteArray(DQT, out);

		// SOF (Start of Frame Header)
		byte SOF[] = new byte[19];
		SOF[0] = (byte) 0xFF;
		SOF[1] = (byte) 0xC0;
		SOF[2] = (byte) 0x00;
		SOF[3] = (byte) 17;
		SOF[4] = (byte) JpegObj.Precision;
		SOF[5] = (byte) ((JpegObj.imageHeight >> 8) & 0xFF);
		SOF[6] = (byte) ((JpegObj.imageHeight) & 0xFF);
		SOF[7] = (byte) ((JpegObj.imageWidth >> 8) & 0xFF);
		SOF[8] = (byte) ((JpegObj.imageWidth) & 0xFF);
		SOF[9] = (byte) JpegObj.NumberOfComponents;
		index = 10;
		for (i = 0; i < SOF[9]; i++) {
			SOF[index++] = (byte) JpegObj.CompID[i];
			SOF[index++] = (byte) ((JpegObj.HsampFactor[i] << 4) + JpegObj.VsampFactor[i]);
			SOF[index++] = (byte) JpegObj.QtableNumber[i];
		}
		WriteArray(SOF, out);

		// The DHT Header (Define Huffman Table)	// 255, 196
		byte DHT1[], DHT2[], DHT3[], DHT4[];
		int bytes, temp, oldindex, intermediateindex;
		length = 2;
		index = 4;
		oldindex = 4;
		DHT1 = new byte[17];
		DHT4 = new byte[4];
		DHT4[0] = (byte) 0xFF;
		DHT4[1] = (byte) 0xC4;
		for (i = 0; i < 2; i++) {	// 0: DC Luminance 1: AC Luminance
			bytes = 0;
			DHT1[index++ - oldindex] = (byte) ((int[]) Huf.bits.elementAt(i))[0];
			for (j = 1; j < 17; j++) {
				temp = ((int[]) Huf.bits.elementAt(i))[j];
				DHT1[index++ - oldindex] = (byte) temp;
				bytes += temp;
			}
			intermediateindex = index;
			DHT2 = new byte[bytes];
			for (j = 0; j < bytes; j++) {
				DHT2[index++ - intermediateindex] = (byte) ((int[]) Huf.val.elementAt(i))[j];
			}
			DHT3 = new byte[index];
			java.lang.System.arraycopy(DHT4, 0, DHT3, 0, oldindex);
			java.lang.System.arraycopy(DHT1, 0, DHT3, oldindex, 17);
			java.lang.System.arraycopy(DHT2, 0, DHT3, oldindex + 17, bytes);
			DHT4 = DHT3;
			oldindex = index;
		}
		DHT4[2] = (byte) (((index - 2) >> 8) & 0xFF);
		DHT4[3] = (byte) ((index - 2) & 0xFF);
		WriteArray(DHT4, out);

		// Start of Scan Header (SOS) 255 -> 218 length.
		byte SOS[] = new byte[14];
		SOS[0] = (byte) 0xFF;
		SOS[1] = (byte) 0xDA;
		SOS[2] = (byte) 0x00;
		SOS[3] = (byte) 12;	//6 + 2 * the number of components (3)
		SOS[4] = (byte) JpegObj.NumberOfComponents;	//Then comes a byte stating the number of components (1-4)
	    index = 5;
	    for (i = 0; i < SOS[4]; i++) {
	    	SOS[index++] = (byte) JpegObj.CompID[i];	//the first is the component identifier (defined in the frame segment)
	    	SOS[index++] = (byte) ((JpegObj.DCtableNumber[i] << 4) + JpegObj.ACtableNumber[i]);	//and the second is divided up in two parts, the first stating the destination selector of the DC Huffman table and the second the destination selector of the AC Huffman table
		}
	    //The segment closes with three bytes which in our case (sequential DCT) are 0, 63 and 0 (the last divided in two half bytes)
		SOS[index++] = (byte) JpegObj.Ss;
		SOS[index++] = (byte) JpegObj.Se;	//63
		SOS[index++] = (byte) ((JpegObj.Ah << 4) + JpegObj.Al);
		WriteArray(SOS, out);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "Show Matrix":
			break;
		case "DCT Process":
			compress();
			break;
		}
	}
}