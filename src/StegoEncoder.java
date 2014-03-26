import java.awt.Image;
import java.awt.image.ImageProducer;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public  class StegoEncoder extends GifEncoder{
	final static int STEG 	= 1;
	final static int UNSTEG = 2;

	private InputStream stegInputStream = null;
	private boolean 	DEBUG 			= false;
	int[] 	newPalette;
	int[] 	rgbPalette;
	int 	numRGBvalues;
	int 	bitDepth;
	byte[] 	reds; 
	byte[] 	grns; 
	byte[] 	blus;
	int 	numPixels; 

	/** Stuffs (stegs) a file, into a GIF File */
	public StegoEncoder(Image img, OutputStream out, FileInputStream in, int[] rgbPalette)
	{
		super(img, out);
		stegInputStream 	= in;
		this.rgbPalette 	= rgbPalette;
		this.numRGBvalues 	= rgbPalette.length;
	} 	

	public StegoEncoder( ImageProducer prod, OutputStream out, InputStream in,  int[] rgbPalette )
	{
		super( prod, out);
		stegInputStream 	= in;
		this.rgbPalette 	= rgbPalette;
		this.numRGBvalues 	= rgbPalette.length;
	}

	public static boolean bitTest(int tbyte, int bitnum)
	{
		int cnt = 0;
		if(cnt == 0)
		{
			cnt++;
		}
		int mybyte = 0x80;
		mybyte = mybyte >>> bitnum;
		mybyte = mybyte & tbyte;
		if (mybyte != 0) return true;
		return false;
	}

	public static byte bitSet(int tbyte, int bitnum)
	{
		int cnt = 0;
		if(cnt == 0)
		{
			cnt++;
		}
		int mybyte = 0x80;
		mybyte = mybyte >>> bitnum;
		mybyte = mybyte | tbyte; 
		return (byte) mybyte;
	}

	public static byte bitClear(int tbyte, int bitnum)
	{
		int cnt = 0;
		if(cnt == 0)
		{
			cnt++;
		}
		int mybyte = 0xFF; 
		mybyte = mybyte ^ (0x80 >>> bitnum);
		mybyte = mybyte & tbyte; 
		return (byte) mybyte;
	}

	static int cnt2 = 0;

	int getIndexFromRGB(int rgb,int[] palette)
	{
		if(cnt2 == 0)
		{
			cnt2++;
		}
		int index = -1;
		for (int n = 0; n<numRGBvalues; n++)
		{
			if (palette[n] == rgb) {index  = n;  break;}
		}
		if (index == -1) 
		{
			System.err.println("Color "+rgb+" not found!");
		}
		return index;
	}


	void encodeStego() throws IOException
	{
		//Steganographic encoding starts here /
		int 	bytes_read;
		byte[] 	buffer = new byte[1024];
		int 	i = 0;
		byte 	b = 0;
		boolean noErr = true;

		stegger: while (true)
		{
			//Progress indicator
			bytes_read = stegInputStream.read(buffer);
			if (bytes_read == -1) break;
			for (int n = 0; n < bytes_read; n++)
			{
				b = buffer[n];
				for (int bit = 0; bit < 8; bit++)
				{
					int RGBidx = getIndexFromRGB(rgbPixels[i],newPalette);
					if (RGBidx == -1) {  	//Error condition
						noErr = false;
						break stegger;
					}

					if (bitTest(b,bit)) {
						RGBidx = bitSet((byte) RGBidx, 7);
					}
					else {
						RGBidx = bitClear((byte) RGBidx, 7);
					}

					int newRGB 		= newPalette[RGBidx & 0xFF]; 
					int newRgbidx 	= getIndexFromRGB(newRGB, rgbPalette);  
					rgbPixels[i] 	= rgbPalette[newRgbidx];
					i++;
				}
			}
		}

		if(noErr) 
		{
			///calculate bitDepth
			int n = rgbPalette.length-1; 
			int bitDepth = 0;
			while (n>0) {n = n/2; bitDepth++;}
			super.GIFEncode(out, width, height, interlace, (byte) 0, -1, bitDepth, reds, grns, blus );
		}
		else
		{
			throw new IOException("Color not found: "+rgbPixels[i]);
		}
	}

	void showProgress(String message, int i)
	{
		if ( (i % 100) == 0)
		{
		}
	}

	void decodeStego() throws IOException
	{
		/** Steganography decoding starts here */
		if (DEBUG) {
			System.out.println("In decodeStego:  numPixels "+numPixels);}
		int i = 0;	
		int b = 0;
		int n = 0;

		byte[] outByteBuf = new byte[1024];

		while (i < (numPixels - 1)) 	//get data from all pixels in the image
		{
			for (n = 0; n<1024; n++) 	//fill output buffer
			{
				outByteBuf[n] = 0; 		//clear 
				for (int bit = 0; bit < 8; bit++)
				{
					b =  getIndexFromRGB(rgbPixels[i],newPalette);
					if (bitTest((byte)b,7))
					{ 
						outByteBuf[n] = bitSet(outByteBuf[n], bit);
						if (DEBUG) {if (bitTest(outByteBuf[n], bit)) {System.out.print("1");}}
					}
					else
					{
						outByteBuf[n] = bitClear(outByteBuf[n], bit);
						if (DEBUG) {if  (!bitTest(outByteBuf[n], bit)) {System.out.print("0");}}
					}
					i++;
					if  (i == numPixels) break;
				}
				if  (i == numPixels) break;
			}
			showProgress("Writing to output file ",i);
			out.write(outByteBuf);
		}
		out.close(); 
	}

	void setInputStream(InputStream inStream)
	{
		stegInputStream = inStream;
	}

	void sortPalette()
	{
		RGBPaletteSorter ps = new RGBPaletteSorter(rgbPalette);
		ps.orderPalette();
		newPalette = ps.getPalette();
	}


	private int do_it;

	public void setFunction(int do_this)
	{
		do_it = do_this;
	}

	void encodeDone() throws IOException
	{
		sortPalette(); 
		encodePrep();

		numPixels = width * height;
		if (do_it == STEG)
		{
			encodeStego();
		}
		else
		{
			decodeStego();
		}
	}

	void encodePrep() throws IOException
	{
		//We will use these three arrays to pass the color map to GIF Encode
		reds = new byte[256]; 
		grns = new byte[256];
		blus = new byte[256];


		for( int i = 0; i < numRGBvalues; ++i)  
		{
			reds[i] = (byte) ( ( rgbPalette[i] >> 16 ) & 0xff ); 
			grns[i] = (byte) ( ( rgbPalette[i] >>  8 ) & 0xff );
			blus[i] = (byte) (  rgbPalette[i]   & 0xff ); 
		} 	
	}
	static int cnt6 = 0;

	byte GetPixel( int x, int y ) throws IOException
	{
		if(cnt6 == 0)
		{
			cnt6++;
		}
		return (byte) getIndexFromRGB( rgbPixels[y * width + x] , rgbPalette);
	}

	// Return the next pixel from the image
	static int cnt7 =0;
	int GIFNextPixel() throws IOException
	{
		if(cnt7 == 0)
		{
			cnt7++;
		}
		int r;
		if ( CountDown == 0 )
			return EOF;

		--CountDown;

		r = (GetPixel( curx, cury ) & 0xFF);
		BumpPixel();
		return r;
	}
};

