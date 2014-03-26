import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.OutputStream;

public abstract class ImageEncoder implements ImageConsumer
{
	protected OutputStream out;
	private ImageProducer prod;
	private int width = -1;
	private int height = -1;
	private int hintflags = 0;
	private boolean started = false;
	private boolean encoding;
	private IOException iox;
	private static ColorModel rgbModel = ColorModel.getRGBdefault();

	public ImageEncoder( Image img, OutputStream out )
	{
		this(img.getSource(), out );
	}

	public ImageEncoder( ImageProducer prod, OutputStream out )
	{
		this.prod = prod;
		this.out = out;
	}

	public void setDimensions( int width, int height )
	{
		this.width = width;
		this.height = height;
	}

	public void setHints( int hintflags )
	{
		this.hintflags = hintflags;
	}

	// Called to get things going.
	public synchronized void encode() throws IOException
	{
		encoding = true;
		iox = null;
		prod.startProduction( this );
		while ( encoding )  
			try
		{
				wait();
		}
		catch ( InterruptedException e ) {}
		if ( iox != null )
			throw iox;
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels,
			int off, int scansize )
	{
		int[] rgbPixels = new int[w];
		for ( int row = 0; row < h; ++row )
		{
			int rowOff = off + row * scansize;
			for ( int col = 0; col < w; ++col )
			{
				int i = rowOff + col;
				rgbPixels[col] = model.getRGB( pixels[i] & 0xff );
			}
			try
			{
				encodeRgbWrapper( x, y + row, w, 1, rgbPixels, 0, w );
			}
			catch ( IOException e )
			{
				iox = e;
				done();
				return;
			}
		}
	}

	public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels,
			int off, int scansize )
	{
		if ( model == rgbModel )
			try
		{
				encodeRgbWrapper( x, y, w, h, pixels, off, scansize );
		}
		catch ( IOException e )
		{
			iox = e;
			done();
			return;
		}
		else
		{
			int[] rgbPixels = new int[w];
			for ( int row = 0; row < h; ++row )
			{
				int rowOff = off + row * scansize;
				for ( int col = 0; col < w; ++col )
				{
					int i = rowOff + col;
					rgbPixels[col] = model.getRGB( pixels[i] );
				}
				try
				{
					encodeRgbWrapper( x, y + row, w, 1, rgbPixels, 0, w );
				}
				catch ( IOException e )
				{
					iox = e;
					done();
					return;
				}
			}
		}
	}

	private void encodeRgbWrapper(int x, int y, int w, int h, int[] rgbPixels, int off, int scansize)
	throws IOException
	{
		if ( ! started )
		{
			started = true;
			encodeStart( width, height );
			if ( ( hintflags & TOPDOWNLEFTRIGHT ) == 0 )
			{
				prod.requestTopDownLeftRightResend( this );
				return;
			}
		}
		encodeRgb( x, y, w, h, rgbPixels, off, scansize );
	}

	public void imageComplete( int status )
	{
		prod.removeConsumer( this );
		if ( ( status & ImageConsumer.IMAGEABORTED ) != 0 )
			iox = new IOException( "image aborted" );
		else
		{
			try
			{
				encodeDone();
			}
			catch ( IOException e )
			{
				iox = e;
			}
		}
		done();
	}

	private synchronized void done()
	{
		encoding = false;
		notifyAll();
	}

	// Here are the methods that subclasses implement:
	// Subclasses implement this to initialize an encoding.
	abstract void encodeStart( int w, int h ) throws IOException;

	// Subclasses implement this to actually write out some bits.  They
	// are guaranteed to be delivered in top-bottom-left-right order.
	abstract void encodeRgb(int x, int y, int w, int h, int[] rgbPixels, int off, int scansize)
	throws IOException;

	// Subclasses implement this to finish an encoding.
	abstract void encodeDone() throws IOException;

}
