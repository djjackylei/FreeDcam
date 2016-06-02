package com.troop.androiddng;

/**
 * Created by troop on 01.06.2016.
 */
public class DngProfile
{

    public final static int Mipi = 0;
    public final static int Qcom = 1;
    public final static int Plain = 2;
    public final static int Mipi16 = 3;
    public final static int Mipi12 = 4;

    public static String BGGR = "bggr";
    public static String RGGB = "rggb";
    public static final String GRBG = "grbg";
    public static final String GBRG =  "gbrg";

    public static final String RGBW =  "rgbw";

    public static final int ROWSIZE = 5264;
    public static final int HTCM8_rowSize = 3360;
    public static final int XperiaL_rowSize = 4376;

    public int blacklevel;
    public int widht;
    public int height;
    public int rawType;
    public String BayerPattern;
    public int rowsize;
    public CustomMatrix matrixes;

    public DngProfile(int blacklevel,int widht, int height, int rawType, String bayerPattern, int rowsize, CustomMatrix matrixes)
    {
        this.blacklevel = blacklevel;
        this.widht = widht;
        this.height = height;
        this.rawType = rawType;
        this.BayerPattern = bayerPattern;
        this.rowsize = rowsize;
        this.matrixes = matrixes;
    }

    public static DngProfile getProfile(int blacklevel, int widht, int height,int rawFormat, String bayerPattern, int rowsize, float[] matrix1, float[] matrix2, float[] neutral, float[] fmatrix1, float[] fmatrix2, float[] rmatrix1, float[] rmatrix2, float[] noise)
    {
        return new DngProfile(blacklevel,widht,height, rawFormat,bayerPattern, 0,new CustomMatrix(matrix1,matrix2,neutral,fmatrix1,fmatrix2,rmatrix1,rmatrix2,noise));
    }
}
