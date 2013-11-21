
public class DCT {
	private int quality = 50;
	
	// Quantinization Matrix for luminace.
	private int[] quantumLuminance = new int[]{
		  16, 11, 10, 16,  24,  40,  51,  61
		, 12, 12, 14, 19,  26,  58,  60,  55
		, 14, 13, 16, 24,  40,  57,  69,  56
		, 14, 17, 22, 29,  51,  87,  80,  62
		, 18, 22, 37, 56,  68, 109, 103,  77
		, 24, 35, 55, 64,  81, 104, 113,  92
		, 49, 64, 78, 87, 103, 121, 120, 101
		, 72, 92, 95, 98, 112, 100, 103,  99
	};
	
	// Divisor Matrix for luminance.
	private float[] divisorLuminance = new float[]{
		  17, 18, 24, 47, 99, 99, 99, 99
		, 18, 21, 26, 66, 99, 99, 99, 99
		, 24, 26, 56, 99, 99, 99, 99, 99
		, 47, 66, 99, 99, 99, 99, 99, 99
		, 99, 99, 99, 99, 99, 99, 99, 99
		, 99, 99, 99, 99, 99, 99, 99, 99
		, 99, 99, 99, 99, 99, 99, 99, 99
		, 99, 99, 99, 99, 99, 99, 99, 99
	};
	
	
	private float[] AANscaleFactor = {
		1.0f, 1.387039845f, 1.306562965f, 1.175875602f, 1.0f, 0.785694958f, 0.541196100f, 0.275899379f
	};

	public DCT(int _quality) {
		quality = _quality;
		
		
		if (quality <= 0)
			quality = 1;
		if (quality > 100)
			quality = 100;
		if (quality < 50)
			quality = 5000 / quality;
		else
			quality = 200 - quality * 2;
		
		// Luminance Calibrating
		for (int i = 0; i < 64; i++) {
			int temp = (quantumLuminance[i] * quality + 50) / 100;
			if (temp <= 0)
				temp = 1;
			if (temp > 255)
				temp = 255;
			quantumLuminance[i] = temp;
		}
		
		int index = 0;
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				divisorLuminance[index] = (1.0f / (quantumLuminance[index] * AANscaleFactor[i] * AANscaleFactor[j] * 8.0f));
				index++;
			}
		}
	}
	
	public int[] quantize(float[][] inputData) {
		int outputData[] = new int[Compressor.N * Compressor.N];
		int index = 0;
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				outputData[index] = (int) Math.round(inputData[h][w] / quantumLuminance[index]);
				index++;
			}
		}
		return outputData;
	}
	
	public int[] getQuantum(){
		return quantumLuminance;
	}
	/*
	public float[] getDivisor(){
		return divisorLuminance;
	}
	*/
	
	public float[][] forwardDCT(float input[][]) {
		float[][] M = getM(input);
		float[][] T = getT();
		float[][] TTran = getTran(T);
		float[][] TM = getTM(T, M);
		float[][] D = getD(TM, TTran);
		
		
		return D;
		
	}
	
	public float[][] getM(float input[][]){
		// level off (subtract 128 from original values)
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				output[h][w] = input[h][w] - 128f;
			}
		}
		return output;
	}
	
	public float[][] getT(){
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				if(h==0){
					output[h][w] = (float) (1 / Math.sqrt((float) Compressor.N));
				}else{
					output[h][w] = (float) (  Math.sqrt( 2 / (float) Compressor.N )
							* (float) Math.cos( (2*w+1)*h*Math.PI/(2*Compressor.N) ) );
				}
				//System.out.print(output[h][w]);
			}
			//System.out.println("");
		}
		return output;
	}
	public float[][] getTran(float[][] m){
		float [][] sol = new float[Compressor.N][Compressor.N];
		for(int i =0;i<Compressor.N;i++){
			for(int j =0;j<Compressor.N;j++){
				sol[j][i] = m[i][j];
			}
		}
		return sol;
	}
	
	public float[][] getTM(float[][] T, float[][] M){
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				for(int x=0; x<Compressor.N; x++){
					output[h][w] += T[h][x] * M[x][w];
				}
				//System.out.print(output[h][w]);
			}
			//System.out.println("");
		}
		return output;
	}
	
	public float[][] getD(float[][] TM, float[][] T){
		float output[][] = new float[Compressor.N][Compressor.N];
		for(int h=0; h<Compressor.N; h++){
			for(int w=0; w<Compressor.N; w++){
				for(int x=0; x<Compressor.N; x++){
					output[h][w] += TM[h][x] * T[x][w];
				}
				//System.out.print(output[h][w]);
			}
			//System.out.println("");
		}
		return output;
	}

}
