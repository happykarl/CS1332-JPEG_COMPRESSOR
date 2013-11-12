import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;


public class JpegInfo {
	public int NumberOfComponents = 3;
	public int Precision = 8;
	public int[] CompID = { 1, 2, 3 };
	public int[] HsampFactor = { 1, 1, 1 };
	public int[] VsampFactor = { 1, 1, 1 };
	public int[] QtableNumber = { 0, 1, 1 };
	public int[] DCtableNumber = { 0, 1, 1 };
	public int[] ACtableNumber = { 0, 1, 1 };
	public boolean[] lastColumnIsDummy = { false, false, false };
	public boolean[] lastRowIsDummy = { false, false, false };
	public int Ss = 0;
	public int Se = 63;
	public int Ah = 0;
	public int Al = 0;
  
	public Image imageobj;
	public int imageHeight;
	public int imageWidth;
	public Object Components[];
	public int compWidth[], compHeight[], BlockWidth[], BlockHeight[];
	
	public JpegInfo(Image image){
		imageobj = image;
		imageWidth = image.getWidth(null);
		imageHeight = image.getHeight(null);
		Components = new Object[NumberOfComponents];
		compWidth = new int[NumberOfComponents];
	    compHeight = new int[NumberOfComponents];
	    BlockWidth = new int[NumberOfComponents];
	    BlockHeight = new int[NumberOfComponents];
	    getYCCArray();
	}
	
	private void getYCCArray(){
		int pixels[] = new int[imageWidth * imageHeight];
		PixelGrabber pg = new PixelGrabber(imageobj, 0, 0, imageWidth, imageHeight, 
				pixels, 0, imageWidth);
		try{
			pg.grabPixels();
		}catch(InterruptedException e){
			System.err.println("interrupted waiting for pixels!");
			return;
		}
		if( (pg.getStatus() & ImageObserver.ABORT) != 0 ){
			System.err.println("image fetch aborted or errored");
			return;
		}
		
		
		for (int i=0; i<NumberOfComponents; i++){
			compWidth[i] = (imageWidth % 8 != 0) ? ((int) Math.ceil(imageWidth / 8.0)) * 8 : imageWidth;
			compHeight[i] = (imageWidth % 8 != 0) ? ((int) Math.ceil(imageWidth / 8.0)) * 8 : imageWidth;
			BlockWidth[i] = (int) Math.ceil(compWidth[i] / 8.0);
			BlockHeight[i] = (int) Math.ceil(compHeight[i] / 8.0);
			//System.out.println("BlockWidth[i]: " + BlockWidth[i] + " | BlockHeight[i]: " + BlockHeight[i]);
		}
		float Y[][] = new float[compHeight[0]][compWidth[0]];
		float Cr1[][] = new float[compHeight[0]][compWidth[0]];
		float Cb1[][] = new float[compHeight[0]][compWidth[0]];
		
		int index = 0;
		for (int y = 0; y < imageHeight; ++y) {
			for (int x = 0; x < imageWidth; ++x) {
				int r = ((pixels[index] >> 16) & 0xff);
				int g = ((pixels[index] >> 8) & 0xff);
				int b = (pixels[index] & 0xff);
				//System.out.println("index: " + index + " | values[index]: " + pixels[index]);
				//System.out.println("r: " + r + " | g: " + g + " | b: " + b);
				Y[y][x] = (float) ((0.299 * r + 0.587 * g + 0.114 * b));
				Cb1[y][x] = 128 + (float) ((-0.16874 * r - 0.33126 * g + 0.5 * b));
				Cr1[y][x] = 128 + (float) ((0.5 * r - 0.41869 * g - 0.08131 * b));
				index++;
			}
		}
		Components[0] = Y;
		Components[1] = Cb1;
		Components[2] = Cr1;
	}
}
