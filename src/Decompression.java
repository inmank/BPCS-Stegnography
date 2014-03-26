import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.Inflater;

import javax.swing.JOptionPane;

public class Decompression  
{
	byte[] decompressedByte = null; 		//stores compressed data
	String compFile 	= "";					//store input file to compress
	String decompFile 	= "";               //store file to save compressed data

	InputStream compInputStream = null;

	public InputStream dodecomp(InputStream inStream)
	{
		compInputStream = inStream;

		byte[] compressedData ;
		byte plainb[] = null; 
		try
		{
			FileInputStream fis = (FileInputStream)compInputStream;
			int av = fis.available();
			plainb = new byte[av];
			fis.read(plainb);
			fis.close();

			compressedData=plainb;
			Inflater decompressor = new Inflater();
			decompressor.setInput(compressedData);
			ByteArrayOutputStream bos2 = new ByteArrayOutputStream(compressedData.length);
			byte[] buf2 = new byte[1024];
			while (!decompressor.finished()) {
				int count = decompressor.inflate(buf2);
				bos2.write(buf2, 0, count);
			}
			bos2.close();
			byte[] decompressedData2 = bos2.toByteArray();
			FileOutputStream cfis = new FileOutputStream("decompFile");
			cfis.write(decompressedData2);
			cfis.close();
			System.out.println("Compressed finished");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error in DeCompressing Data.\n Please select valid file and try again.",
					"Error:",
					JOptionPane.ERROR_MESSAGE);
		}

		InputStream decompDataStream =  null;

		try
		{
			decompDataStream =  new FileInputStream("decompFile");
		}
		catch (Exception e)
		{
			return null;
		}

		return decompDataStream;
	}

	//return decompress data in byte array
	public byte[] getdeCompressed()			
	{
		return decompressedByte;
	}

	//return decompressed file path
	public String getdecompfile()			
	{
		return decompFile;
	}

	//return compressed file path
	public String getcompfile()
	{
		return compFile;
	} 
}
