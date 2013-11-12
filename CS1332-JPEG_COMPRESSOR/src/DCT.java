
public class DCT {
	public int[][] Q, Q50;
	public float[][] T;
	
	public DCT(int _quality){
		Q50 = new int[][]{
			 {16, 11, 10, 16, 24, 40, 51, 61}
			,{12, 12, 14, 19, 26, 58, 60, 55}
			,{14, 13, 16, 24, 40, 57, 69, 56}
			,{14, 17, 22, 29, 51, 87, 80, 62}
			,{18, 22, 37, 56, 68, 109, 103, 77}
			,{24, 35, 55, 64, 81, 104, 113, 92}
			,{49, 64, 78, 87, 103, 121, 120, 101}
			,{72, 92, 95, 98, 112, 100, 103, 99}
		};
		T = getT();
		Q = Q50;
	}
	
	public float[][] getDCT(float input[][]){
		//float M[][] = getM(input);
		//float TM[][] = getTM(T, M);
		//float D[][] = getD(TM, T);
		
		float D[][] = getD(input);
		
		return D;
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
	
	public float[][] getD(float[][] TM, float[][] T){
		float output[][] = new float[JpegCompressor.N][JpegCompressor.N];
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
	
	
	
	
	
	public float[][] getD(float input[][]) {
		float output[][] = new float[JpegCompressor.N][JpegCompressor.N];
		float tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7;
		float tmp10, tmp11, tmp12, tmp13;
		float z1, z2, z3, z4, z5, z11, z13;
		int i;
		int j;

		// Subtracts 128 from the input values
		for (i = 0; i < 8; i++) {
			for (j = 0; j < 8; j++) {
				output[i][j] = (input[i][j] - 128f);
				// input[i][j] -= 128;

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
			
			z1 = (tmp12 + tmp13) * 0.707106781f;
			output[i][2] = tmp13 + z1;
			output[i][6] = tmp13 - z1;
			
			tmp10 = tmp4 + tmp5;
			tmp11 = tmp5 + tmp6;
			tmp12 = tmp6 + tmp7;
			
			z5 = (tmp10 - tmp12) * 0.382683433f;
			z2 = 0.541196100f * tmp10 + z5;
			z4 = 1.306562965f * tmp12 + z5;
			z3 = tmp11 * 0.707106781f;
			
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
			
			z1 = (tmp12 + tmp13) * 0.707106781f;
			output[2][i] = tmp13 + z1;
			output[6][i] = tmp13 - z1;
			
			tmp10 = tmp4 + tmp5;
			tmp11 = tmp5 + tmp6;
			tmp12 = tmp6 + tmp7;
			
			z5 = (tmp10 - tmp12) * 0.382683433f;
			z2 = 0.541196100f * tmp10 + z5;
			z4 = 1.306562965f * tmp12 + z5;
			z3 = tmp11 * 0.707106781f;
			
			z11 = tmp7 + z3;
			z13 = tmp7 - z3;
			
			output[5][i] = z13 + z2;
			output[3][i] = z13 - z2;
			output[1][i] = z11 + z4;
			output[7][i] = z11 - z4;
		}
		return output;
	}
	
	public int[] quantize(float[][] D, int[][] Q){
		int output[] = new int[JpegCompressor.N * JpegCompressor.N];
		int C[][] = getC(D, Q);
		int index = 0;
		for(int h=0; h<JpegCompressor.N; h++){
			for(int w=0; w<JpegCompressor.N; w++){
				output[index] = C[h][w];
				index++;
			}
		}
		return output;
	}
	
	public int[][] getC(float[][] D, int[][] Q){
		int output[][] = new int[JpegCompressor.N][JpegCompressor.N];
		for(int h=0; h<JpegCompressor.N; h++){
			for(int w=0; w<JpegCompressor.N; w++){
				output[h][w] = (int) Math.round(D[h][w] / Q[h][w]);
				//System.out.print(output[h][w] + " ");
			}
			//System.out.println("");
		}
		//System.out.println("");
		return output;
	}
	
	public int[] getQ(){
		int[] output = new int[JpegCompressor.N*JpegCompressor.N];
		int index = 0;
		for(int h=0; h<JpegCompressor.N; h++){
			for(int w=0; w<JpegCompressor.N; w++){
				output[index] = Q[h][w];
				index++;
			}
		}
		return output;
		
	}
}
