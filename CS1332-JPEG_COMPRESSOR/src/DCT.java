
public class DCT {
	public static final int N = 8;
	public int QUALITY = 50;
	public Object quantum[] = new Object[2];
	public Object Divisors[] = new Object[2];
	
	// Quantitization Matrix for luminace.
	public int quantum_luminance[] = new int[]{
			  16, 11, 10, 16,  24,  40,  51,  61
			, 12, 12, 14, 19,  26,  58,  60,  55
			, 14, 13, 16, 24,  40,  57,  69,  56
			, 14, 17, 22, 29,  51,  87,  80,  62
			, 18, 22, 37, 56,  68, 109, 103,  77
			, 24, 35, 55, 64,  81, 104, 113,  92
			, 49, 64, 78, 87, 103, 121, 120, 101
			, 72, 92, 95, 98, 112, 100, 103,  99
		};
	public double DivisorsLuminance[] = new double[]{
			  17, 18, 24, 47, 99, 99, 99, 99
			, 18, 21, 26, 66, 99, 99, 99, 99
			, 24, 26, 56, 99, 99, 99, 99, 99
			, 47, 66, 99, 99, 99, 99, 99, 99
			, 99, 99, 99, 99, 99, 99, 99, 99
			, 99, 99, 99, 99, 99, 99, 99, 99
			, 99, 99, 99, 99, 99, 99, 99, 99
			, 99, 99, 99, 99, 99, 99, 99, 99
	};
	
	// Quantitization Matrix for chrominance.
	public int quantum_chrominance[] = new int[N * N];
	public double DivisorsChrominance[] = new double[N * N];

	public DCT(int QUALITY) {
		initMatrix(QUALITY);
	}
	
	private void initMatrix(int quality) {
		double[] AANscaleFactor = { 1.0, 1.387039845, 1.306562965, 1.175875602, 1.0, 0.785694958
				, 0.541196100, 0.275899379 };
		int i;
		int j;
		int index;
		int Quality;
		int temp;
		
		// Quality Calibrating
		Quality = quality;
		if (Quality <= 0)
			Quality = 1;
		if (Quality > 100)
			Quality = 100;
		if (Quality < 50)
			Quality = 5000 / Quality;
		else
			Quality = 200 - Quality * 2;
		
		// Luminance Calibrating
		for (j = 0; j < 64; j++) {
			temp = (quantum_luminance[j] * Quality + 50) / 100;
			if (temp <= 0)
				temp = 1;
			if (temp > 255)
				temp = 255;
			quantum_luminance[j] = temp;
		}
		index = 0;
		for (i = 0; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				DivisorsLuminance[index] = (1.0 / (quantum_luminance[index] * AANscaleFactor[i] * AANscaleFactor[j] * 8.0));
				index++;
			}
		}
		// Chrominance Calibrating
		for (j = 0; j < 64; j++) {
			temp = (quantum_chrominance[j] * Quality + 50) / 100;
			if (temp <= 0)
				temp = 1;
			if (temp >= 255)
				temp = 255;
			quantum_chrominance[j] = temp;
		}
		index = 0;
		for (i = 0; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				DivisorsChrominance[index] = 1.0 / (quantum_chrominance[index] * AANscaleFactor[i] * AANscaleFactor[j] * 8.0);
				index++;
			}
		}
		// quantum and Divisors are objects used to hold the appropriate matices

		quantum[0] = quantum_luminance;
		Divisors[0] = DivisorsLuminance;
		quantum[1] = quantum_chrominance;
		Divisors[1] = DivisorsChrominance;
	}
	
	public double[][] forwardDCT2(float input[][]) {
		float[][] M = getM(input);
		float[][] T = getT();
		float[][] TM = getTM(T, M);
		double[][] D = getD(TM, T);
		
		
		return D;
		
	}
	
	public double[][] forwardDCT(float input[][]) {
		double output[][] = new double[N][N];
		double tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7;
		double tmp10, tmp11, tmp12, tmp13;
		double z1, z2, z3, z4, z5, z11, z13;
		int i;
		int j;
		
		// Level Off - Subtracts 128 from the input values
		for (i = 0; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				output[i][j] = (input[i][j] - 128.0);
			}
		}
		for (i = 0; i < 8; i++) {
			tmp0 = output[i][0] + output[i][7];
			tmp7 = output[i][0] - output[i][7];
			tmp1 = output[i][1] + output[i][6];
			tmp6 = output[i][1] - output[i][6];
			tmp2 = output[i][2] + output[i][5];
			tmp5 = output[i][2] - output[i][5];
			tmp3 = output[i][3] + output[i][4];
			tmp4 = output[i][3] - output[i][4];
			
			tmp10 = tmp0 + tmp3;
			tmp13 = tmp0 - tmp3;
			tmp11 = tmp1 + tmp2;
			tmp12 = tmp1 - tmp2;
			
			output[i][0] = tmp10 + tmp11;
			output[i][4] = tmp10 - tmp11;
			
			z1 = (tmp12 + tmp13) * 0.707106781;
			output[i][2] = tmp13 + z1;
			output[i][6] = tmp13 - z1;
			
			tmp10 = tmp4 + tmp5;
			tmp11 = tmp5 + tmp6;
			tmp12 = tmp6 + tmp7;
			
			z5 = (tmp10 - tmp12) * 0.382683433;
			z2 = 0.541196100 * tmp10 + z5;
			z4 = 1.306562965 * tmp12 + z5;
			z3 = tmp11 * 0.707106781;
			
			z11 = tmp7 + z3;
			z13 = tmp7 - z3;
			
			output[i][5] = z13 + z2;
			output[i][3] = z13 - z2;
			output[i][1] = z11 + z4;
			output[i][7] = z11 - z4;
		}
		for (i = 0; i < 8; i++) {
			tmp0 = output[0][i] + output[7][i];
			tmp7 = output[0][i] - output[7][i];
			tmp1 = output[1][i] + output[6][i];
			tmp6 = output[1][i] - output[6][i];
			tmp2 = output[2][i] + output[5][i];
			tmp5 = output[2][i] - output[5][i];
			tmp3 = output[3][i] + output[4][i];
			tmp4 = output[3][i] - output[4][i];
			
			tmp10 = tmp0 + tmp3;
			tmp13 = tmp0 - tmp3;
			tmp11 = tmp1 + tmp2;
			tmp12 = tmp1 - tmp2;
			
			output[0][i] = tmp10 + tmp11;
			output[4][i] = tmp10 - tmp11;
			
			z1 = (tmp12 + tmp13) * 0.707106781;
			output[2][i] = tmp13 + z1;
			output[6][i] = tmp13 - z1;
			
			tmp10 = tmp4 + tmp5;
			tmp11 = tmp5 + tmp6;
			tmp12 = tmp6 + tmp7;
			
			z5 = (tmp10 - tmp12) * 0.382683433;
			z2 = 0.541196100 * tmp10 + z5;
			z4 = 1.306562965 * tmp12 + z5;
			z3 = tmp11 * 0.707106781;
			
			z11 = tmp7 + z3;
			z13 = tmp7 - z3;
			
			output[5][i] = z13 + z2;
			output[3][i] = z13 - z2;
			output[1][i] = z11 + z4;
			output[7][i] = z11 - z4;
		}
		return output;
	}

	public int[] quantizeBlock(double inputData[][], int code) {
		int outputData[] = new int[N * N];
		int i, j;
		int index;
		index = 0;
		for (i = 0; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				// The second line results in significantly better compression.
				outputData[index] = (int) (Math.round(inputData[i][j] * (((double[]) (Divisors[code]))[index])));
				// outputData[index] = (int)(((inputData[i][j] * (((double[])
				// (Divisors[code]))[index])) + 16384.5) -16384);
				index++;
			}
		}
		return outputData;
	}
	
	
	
	
	
	public float[][] getM(float input[][]){
		// level off (subtract 128 from original values)
		float output[][] = new float[JpegCompressor.N][JpegCompressor.N];
		for(int h=0; h<JpegCompressor.N; h++){
			for(int w=0; w<JpegCompressor.N; w++){
				output[h][w] = input[h][w] - 128f;
			}
		}
		return output;
	}
	
	public float[][] getT(){
		float output[][] = new float[JpegCompressor.N][JpegCompressor.N];
		for(int h=0; h<JpegCompressor.N; h++){
			for(int w=0; w<JpegCompressor.N; w++){
				if(h==0){
					output[h][w] = (float) (1 / Math.sqrt((float) JpegCompressor.N));
				}else{
					output[h][w] = (float) (  Math.sqrt( 2 / (float) JpegCompressor.N )
							* (float) Math.cos( (2*(w%JpegCompressor.N)+1)*(h%JpegCompressor.N)*Math.PI/(2*JpegCompressor.N) ) );
				}
				//System.out.print(output[h][w]);
			}
			//System.out.println("");
		}
		return output;
	}
	
	public float[][] getTM(float[][] T, float[][] M){
		float output[][] = new float[JpegCompressor.N][JpegCompressor.N];
		for(int h=0; h<JpegCompressor.N; h++){
			for(int w=0; w<JpegCompressor.N; w++){
				for(int x=0; x<JpegCompressor.N; x++){
					output[h][w] += T[h][x] * M[x][w];
				}
				//System.out.print(output[h][w]);
			}
			//System.out.println("");
		}
		return output;
	}
	
	public double[][] getD(float[][] TM, float[][] T){
		double output[][] = new double[JpegCompressor.N][JpegCompressor.N];
		for(int h=0; h<JpegCompressor.N; h++){
			for(int w=0; w<JpegCompressor.N; w++){
				for(int x=0; x<JpegCompressor.N; x++){
					output[h][w] += TM[h][x] * T[x][w];
				}
				//System.out.print(output[h][w]);
			}
			//System.out.println("");
		}
		return output;
	}
	
	
}