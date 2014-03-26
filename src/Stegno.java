import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.swing.JButton;
import javax.swing.JFrame;

import sun.awt.image.FileImageSource;

public class Stegno implements ImageConsumer{

	JButton doSteg = new JButton("doSteg");
	JButton unSteg = new JButton("unSteg");

	JFrame mainFrame = new JFrame();
	int[] rgbPalette ;
	int numRGBvalues;
	ImageProducer   imagePro;

	public Stegno(){
		mainFrame.setPreferredSize(new Dimension(450, 450));
		mainFrame.setLayout(new GridLayout());
		mainFrame.getContentPane().add(doSteg);
		mainFrame.getContentPane().add(unSteg);

		doSteg.addActionListener(dostegAction);
		unSteg.addActionListener(unstegAction);
	}

	ActionListener dostegAction = new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("steg Action");
			String inImageFileName = null;
			String inDataFileName = null;
			String outImageFileName = null;

			FileDialog fileDialog_ImageIn = new FileDialog( mainFrame ,"Select a GIF file", FileDialog.LOAD);
			fileDialog_ImageIn.setVisible(true);
			if (fileDialog_ImageIn.getFile() == null) 
				return;
			else
				inImageFileName = fileDialog_ImageIn.getDirectory() + fileDialog_ImageIn.getFile();

			FileDialog fileDialog_DataIn = new FileDialog( mainFrame ,"Select a Data file to hide", FileDialog.LOAD);
			fileDialog_DataIn.setVisible(true);
			if (fileDialog_DataIn.getFile() == null) 
				return;
			else
				inDataFileName = fileDialog_DataIn.getDirectory() + fileDialog_DataIn.getFile();

			FileDialog fileDialog_ImageOut = new FileDialog( mainFrame ,"Save GIF file As", FileDialog.SAVE);
			fileDialog_ImageOut.setVisible(true);
			if (fileDialog_ImageOut.getFile() == null) 
				return;
			else
				outImageFileName = fileDialog_ImageOut.getDirectory() + fileDialog_ImageOut.getFile();

			FileInputStream inDataStream = null;	
			FileOutputStream outImageStream = null;

			try {
				inDataStream	 	= new FileInputStream(new File(inDataFileName));
				outImageStream 	= new FileOutputStream(new File(outImageFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			StegoEncoder encodedImage = null;
			Image inputImage = mainFrame.createImage(new FileImageSource(inImageFileName));
			loadImage(inputImage);
			try {
				encodedImage = new StegoEncoder( inputImage, outImageStream, null, rgbPalette);
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			encodedImage.setFunction(StegoEncoder.STEG);

			KeyValues keys = null;

			try
			{ 
				InputStream fis=new FileInputStream("D:\\Documents and Settings\\Appu\\My Documents\\Testing\\Keys\\Keys2");
				ObjectInputStream ois =new ObjectInputStream(fis);
				keys=(KeyValues)ois.readObject();
			}catch(Exception rer){
				System.out.println("EERRRORRR! "+rer);
			}

			BigInteger modValue 	= keys.mod;//new BigInteger(kk.mod);
			BigInteger publicKey 	= keys.pubkey;//new BigInteger(kk.pubkey);
			BigInteger privateKey 	= keys.privkey;//new BigInteger(kk.privkey);


			Cryptography encryption =new Cryptography();
			PublicKey pubKey = new PublicKey(modValue, publicKey);
			PrivateKey priKey = new PrivateKey(modValue, privateKey);

			inDataStream = (FileInputStream) encryption.doencryptcomp(inDataStream, pubKey, priKey);
			
			Compression compresser = new Compression();
			inDataStream = (FileInputStream) compresser.docomp(inDataStream);

			encodedImage.setInputStream(inDataStream);

			try
			{
				System.out.println("BEFORE ENCODE");
				encodedImage.encode();
				System.out.println("AFTER ENCODE"); 
			}
			catch (Throwable e1)
			{
				return;
			}
			System.out.println("Complete Steg");
		}
	};

	ActionListener unstegAction = new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("unsteg Action");
			String inImageFileName = null;
			String outputfile = null;

			FileDialog fileDialog_ImageIn = new FileDialog( mainFrame ,"Select a GIF file", FileDialog.LOAD);
			fileDialog_ImageIn.setVisible(true);
			if (fileDialog_ImageIn.getFile() == null) 
				return;
			else
				inImageFileName = fileDialog_ImageIn.getDirectory() + fileDialog_ImageIn.getFile();

			FileDialog fileDialog_ImageOut = new FileDialog( mainFrame ,"Save Output file As", FileDialog.SAVE);
			fileDialog_ImageOut.setVisible(true);
			if (fileDialog_ImageOut.getFile() == null) 
				return;
			else
				outputfile = fileDialog_ImageOut.getDirectory() + fileDialog_ImageOut.getFile();

			OutputStream outImageStream = System.out;

			try {
				outImageStream 	= new FileOutputStream(new File(outputfile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			StegoEncoder encodedImage = null;
			Image inputImage = mainFrame.createImage(new FileImageSource(inImageFileName));
			loadImage(inputImage);
			try {
				encodedImage = new StegoEncoder( inputImage, outImageStream, null, rgbPalette);
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			encodedImage.setFunction(StegoEncoder.UNSTEG);

			try
			{
				encodedImage.encode();
			}
			catch (IOException e)
			{
				return;
			};

			System.out.println("END EZSTEGO unSteg()");



			JFrame resultFrame=new JFrame("RESULT TEXT");

			try
			{
				File destination_file = new File(outputfile);
				FileInputStream outStream =new FileInputStream(destination_file);

				Decompression decompresser = new Decompression();
				outStream = (FileInputStream) decompresser.dodecomp(outStream);

				KeyValues keys = null;

				try
				{ 
					InputStream fis = new FileInputStream("D:\\Documents and Settings\\Appu\\My Documents\\Testing\\Keys\\Keys2");
					ObjectInputStream ois = new ObjectInputStream(fis);
					keys=(KeyValues)ois.readObject();
				}catch(Exception rer){
					System.out.println("EERRRORRR! "+rer);
				}

				BigInteger modValue 	= keys.mod;
				BigInteger publicKey 	= keys.pubkey;
				BigInteger privateKey 	= keys.privkey;

				Cryptography decryption = new Cryptography();
				PublicKey pubKey = new PublicKey(modValue, publicKey);
				PrivateKey priKey = new PrivateKey(modValue, privateKey);
				outStream = (FileInputStream) decryption.dodecryptcomp(outStream, pubKey, priKey);
				
				FileOutputStream outImageStream2 	= new FileOutputStream(new File(outputfile));
				
				byte rtr[]=new byte[outStream.available()];
				outStream.read(rtr);
				outImageStream2.write(rtr);
				outImageStream2.close();
				TextArea tta=new TextArea("",4,20);
				tta.setText(new String(rtr));
				resultFrame.add(tta);
				resultFrame.setSize(200,200);
				resultFrame.setVisible(true);
			}catch(Exception e4rr){}

			System.out.println("END EZSTEGO unSteg()");

		}
	};

	public Image loadImage(Image inputImage)
	{
		System.out.println("CALLED EZSTEGO loadImage()");
		try
		{
			boolean resi = waitForImage(mainFrame, inputImage);
			if(!resi)
			{
				return null;
			}
		}
		catch (Throwable e)
		{
			return null;
		}

		getImageStats(inputImage);

		System.out.println("END EZSTEGO loadImage()");
		return inputImage;
	}

	public void getImageStats(Image theImage)
	{
		System.out.println("CALLED EZSTEGO getImageStats()");
		try
		{
			imagePro = theImage.getSource();
			imagePro.addConsumer(this);
			imagePro.startProduction(this);
		}
		catch (Throwable e)
		{
			return;
		}
		while (numRGBvalues == 0) {};
		System.out.println("END EZSTEGO getImageStats()");
	}

	boolean waitForImage(Component component, Image myImage)
	{
		System.out.println("CALLED EZSTEGO waitForImage()");
		MediaTracker tracker = new MediaTracker(component);
		boolean getError = false;
		try
		{
			tracker.addImage(myImage, 0);
			tracker.waitForID(0);
			getError = tracker.isErrorAny();
		} 
		catch (Throwable anyDamnThing)
		{
			return false;
		}
		if (getError) 
		{
			return false;
		}
		System.out.println("END EZSTEGO waitForImage()");
		return true;
	}

	public void imageComplete (int status) {
		System.out.println("CALLED EZSTEGO imageComplete()");
		imagePro.removeConsumer(this);
		System.out.println("END EZSTEGO imageComplete()");
	}

	public void setColorModel (ColorModel model) {
		System.out.println("CALLED EZSTEGO setColorModel()");
		if (model instanceof IndexColorModel) {
			// Print contents of IndexColorTable
			IndexColorModel im = (IndexColorModel) model;
			numRGBvalues = im.getMapSize();

			rgbPalette = new int[numRGBvalues];
			for (int ii = 0; ii < numRGBvalues; ii++) {
				rgbPalette[ii] = im.getRGB(ii);
			} 
		}

		System.out.println("END EZSTEGO setColorModel()");
	}

	public static void main(String args[]){
		Stegno stegObj = new Stegno();
		stegObj.mainFrame.pack();
		stegObj.mainFrame.setVisible(true);
	}


	public void setDimensions(int width, int height) {

	}

	public void setHints(int hintflags) {

	}

	public void setPixels(int x, int y, int w, int h, ColorModel model,
			byte[] pixels, int off, int scansize) {

	}

	public void setPixels(int x, int y, int w, int h, ColorModel model,
			int[] pixels, int off, int scansize) {

	}

	public void setProperties(Hashtable<?, ?> props) {

	}

	public byte[] decompressData(InputStream ins)
	throws IOException {
		InflaterInputStream in = new InflaterInputStream(ins);
		ByteArrayOutputStream bout =
			new ByteArrayOutputStream(512);
		int b;
		while ((b = in.read()) != -1) {
			bout.write(b);
		}
		in.close();
		bout.close();
		return bout.toByteArray();
	}


	public void compressData(byte[] data, OutputStream out)
	throws IOException {
		Deflater d = new Deflater();
		DeflaterOutputStream dout = new DeflaterOutputStream(out, d);
		dout.write(data);
		dout.close();
	}
}
