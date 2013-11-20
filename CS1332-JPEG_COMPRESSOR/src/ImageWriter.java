import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageWriter {
	private final int Ss = 0;
	private final int Se = 63;
	private final int Ah = 0;
	private final int Al = 0;
	public static final int numOfSample = 1;
	public static final int numTable = 0;
	
	private File outFile;
	public BufferedOutputStream outStream;
	public FileOutputStream fileStream;
	private DCT dct;
	private int numOfChannel;
	private ImageInfo imgInfo;
	private Huffman hufman;
	
	private int bufferPutBits, bufferPutBuffer;
	
	public ImageWriter(String _outFileDir, int _quality, DCT _dct, int _numOfChannel, ImageInfo _imgInfo, Huffman _hufman) {
		outFile = new File(_outFileDir);
		dct = _dct;
		numOfChannel = _numOfChannel;
		imgInfo = _imgInfo;
		hufman = _hufman;
		
		try {
			fileStream = new FileOutputStream(outFile);
			outStream = new BufferedOutputStream(fileStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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

	public void writeHeader() {
		SOI();
		JFIF();
		DQT();
		SOF();
		DHT();
		SOS();
		
	}
	
	private void SOI(){
		// the SOI marker (Start of Image) First byte is 255 and second byte is 216
		byte[] SOI = { (byte) 0xFF, (byte) 0xD8 };
		WriteMarker(SOI, outStream);
	}
	
	private void JFIF(){
		// The order of the following headers is quiet inconsequential.
		// the JFIF header
		byte JFIF[] = new byte[18];
		JFIF[0] = (byte) 0xff;	// APP0 marker 2 bytes
		JFIF[1] = (byte) 0xe0;	// Always equals 0xFFE0
		JFIF[2] = (byte) 0x00;	// Length
		JFIF[3] = (byte) 0x10;	// Length of segment excluding APP0 marker
		JFIF[4] = (byte) 0x4a;	// Identifier (size 5)
		JFIF[5] = (byte) 0x46;	// Always equals "JFIF" (with zero following) (0x4A46494600)
		JFIF[6] = (byte) 0x49;
		JFIF[7] = (byte) 0x46;
		JFIF[8] = (byte) 0x00;	// End of Identifier
		JFIF[9] = (byte) 0x01;	// Version (2bytes)
		JFIF[10] = (byte) 0x02;	// : First byte is major version (currently 0x01), Second byte is minor version (currently 0x02)
		JFIF[11] = (byte) 0x00; // Density units: Units for pixel density fields / 0 - No units, aspect ratio only specified  / 1 - Pixels per inch / 2 - Pixels per centimetre
		JFIF[12] = (byte) 0x00;	//  (2 bytes) Horizontal pixel density
		JFIF[13] = (byte) 0x01;
		JFIF[14] = (byte) 0x00;	//  (2 bytes) Vertical pixel density
		JFIF[15] = (byte) 0x01;	
		JFIF[16] = (byte) 0x00;	// (1 byte) Thumbnail horizontal pixel count
		JFIF[17] = (byte) 0x00; // (1 byte) Thumbnail vertical pixel count
		WriteArray(JFIF, outStream);
	}
	
	private void DQT(){
		// The DQT header (Define Quantization Table) - 255 / 219
		byte DQT[] = new byte[69];
		DQT[0] = (byte) 0xFF;	//255
		DQT[1] = (byte) 0xDB;	// 192 ( meaning the baseline sequential DCT mode)
		DQT[2] = (byte) 0x00;
		DQT[3] = (byte) 0x43;	// length (0~67)	// 64 byte + 1 byte (table number)
		int offset = 4;
		int[] tempArray = dct.getQuantum();
		DQT[offset++] = (byte) ((0 << 4) + 0);
		for (int j = 0; j < 64; j++) {
			DQT[offset++] = (byte) tempArray[Huffman.jpegNaturalOrder[j]];	// more smoothing
			//DQT[offset++] = (byte) tempArray[j];
		}
		WriteArray(DQT, outStream);
	}
	
	private void SOF(){
		// SOF (Start of Frame Header)
		byte SOF[] = new byte[19];
		SOF[0] = (byte) 0xFF;	//255
		SOF[1] = (byte) 0xC0;	// 192 ( meaning the baseline sequential DCT mode)
		SOF[2] = (byte) 0x00;
		SOF[3] = (byte) (byte) (8 + 3 * numOfChannel); // (8 + 3 * the number of components)	// 17;
		SOF[4] = (byte) Compressor.N;
		SOF[5] = (byte) ((imgInfo.getHeight() >> 8) & 0xFF);
		SOF[6] = (byte) ((imgInfo.getHeight()) & 0xFF);
		SOF[7] = (byte) ((imgInfo.getWidth() >> 8) & 0xFF);
		SOF[8] = (byte) ((imgInfo.getWidth()) & 0xFF);
		SOF[9] = (byte) numOfChannel;
		int index = 10;
		for (int i = 0; i < SOF[9]; i++) {
			SOF[index++] = (byte) (i+1);
			SOF[index++] = (byte) ((numOfSample << 4) + numOfSample);
			SOF[index++] = (byte) numTable;
		}
		WriteArray(SOF, outStream);
	}
	
	private void DHT(){
		// The DHT Header (Define Huffman Table)	// 255, 196
		byte DHT1[], DHT2[], DHT3[], DHT4[];
		int bytes, temp, oldindex, intermediateindex;
		int index = 4;
		oldindex = 4;
		DHT1 = new byte[17];
		DHT4 = new byte[4];
		DHT4[0] = (byte) 0xFF;	// 255
		DHT4[1] = (byte) 0xC4;	// 196
		for (int i = 0; i < 2; i++) {	// 0: DC Luminance 1: AC Luminance
			bytes = 0;
			DHT1[index++ - oldindex] = (byte) ((int) hufman.bits.get(i)[0]);
			for (int j = 1; j < 17; j++) {
				temp = hufman.bits.get(i)[j];
				DHT1[index++ - oldindex] = (byte) temp;
				bytes += temp;
			}
			intermediateindex = index;
			DHT2 = new byte[bytes];
			for (int j = 0; j < bytes; j++) {
				DHT2[index++ - intermediateindex] = (byte) ((int) hufman.val.get(i)[j]);
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
		WriteArray(DHT4, outStream);
	}
	
	private void SOS(){
		// Start of Scan Header (SOS) 255 -> 218 .
		byte SOS[] = new byte[14];
		SOS[0] = (byte) 0xFF;	// 255
		SOS[1] = (byte) 0xDA;	// 218
		SOS[2] = (byte) 0x00;	// 2 byte length info which is (0, 6 + 2 * the number of components)
		SOS[3] = (byte) (6 + 2 * numOfChannel); //12;	//6 + 2 * the number of components (3)
		SOS[4] = (byte) numOfChannel;	//Then comes a byte stating the number of components (1-4)
	    int index = 5;
	    for (int i = 0; i < SOS[4]; i++) {
	    	SOS[index++] = (byte) (i+1);	//the first is the component identifier (defined in the frame segment)
	    	SOS[index++] = (byte) ((numTable << 4) + numTable);	//and the second is divided up in two parts, the first stating the destination selector of the DC Huffman table and the second the destination selector of the AC Huffman table
		}
	    //The segment closes with three bytes which in our case (sequential DCT) are 0, 63 and 0 (the last divided in two half bytes)
		SOS[index++] = (byte) Ss;	// 0
		SOS[index++] = (byte) Se;	//63
		SOS[index++] = (byte) ((Ah << 4) + Al);	// 0
		WriteArray(SOS, outStream);
	}
	
	private void EOI(){
		byte[] EOI = { (byte) 0xFF, (byte) 0xD9 };
		WriteMarker(EOI, outStream);
	}
	
	public void bufferIt(int code, int size) {
		int PutBuffer = code;
		int PutBits = bufferPutBits;

		PutBuffer &= (1 << size) - 1;
		PutBits += size;
		PutBuffer <<= 24 - PutBits;
		PutBuffer |= bufferPutBuffer;

		while (PutBits >= 8) {
			int c = ((PutBuffer >> 16) & 0xFF);
			try {
				outStream.write(c);
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
			if (c == 0xFF) {
				try {
					outStream.write(0);
				} catch (IOException e) {
					System.out.println("IO Error: " + e.getMessage());
				}
			}
			PutBuffer <<= 8;
			PutBits -= 8;
		}
		bufferPutBuffer = PutBuffer;
		bufferPutBits = PutBits;
	}
	
	void flushBuffer() {
		int PutBuffer = bufferPutBuffer;
		int PutBits = bufferPutBits;
		while (PutBits >= 8) {
			int c = ((PutBuffer >> 16) & 0xFF);
			try {
				outStream.write(c);
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
			if (c == 0xFF) {
				try {
					outStream.write(0);
				} catch (IOException e) {
					System.out.println("IO Error: " + e.getMessage());
				}
			}
			PutBuffer <<= 8;
			PutBits -= 8;
		}
		if (PutBits > 0) {
			int c = ((PutBuffer >> 16) & 0xFF);
			try {
				outStream.write(c);
			} catch (IOException e) {
				System.out.println("IO Error: " + e.getMessage());
			}
		}
	}

	public void writeEnd() {
		EOI();
	}
	
}
