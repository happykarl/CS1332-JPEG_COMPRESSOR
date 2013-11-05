import javax.swing.JFrame;

public class JpegMain {
	private static final int  WINDOW_W = 500;
	private static final int  WINDOW_H = 500;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame jf = new JFrame("JPEG Compressor");
		jf.add(new JpegPanel(jf, MODE.MAIN));
		jf.setSize(WINDOW_W, WINDOW_H);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.pack();
	}
}