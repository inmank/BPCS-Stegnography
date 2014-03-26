import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.Deflater;

import javax.swing.JOptionPane;

public class Compression 
{
	byte[] compressedByte 	= null; 		//stores compressed data 
	String srcFile 			= "";			//store input file to compress
	String compFile			= "";			//store file to save compressed data

	InputStream compInputStream = null;

	public InputStream docomp(InputStream inStream)
	{
		compInputStream = inStream;
		srcFile			= ""; 
		compFile		= "";
		try
		{
			byte plainData[] = null; 
			FileInputStream inputFileStream = (FileInputStream)compInputStream; 
			int availData = inputFileStream.available();
			plainData = new byte[availData];
			inputFileStream.read(plainData);
			inputFileStream.close();

			byte[] input = plainData;
			Deflater compressor = new Deflater();
			compressor.setLevel(Deflater.BEST_COMPRESSION);
			compressor.setInput(input);
			compressor.finish();
			ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
			byte[] buf = new byte[1024];
			while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				bos.write(buf, 0, count);
			}

			bos.close();

			compressedByte = bos.toByteArray();

			FileOutputStream cfis = new FileOutputStream("compFile");
			cfis.write(compressedByte);
			cfis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error in Compressing Data./n Please select valid file and try again.",
					"Error:",
					JOptionPane.ERROR_MESSAGE);
		}

		InputStream compDataStream =  null;

		try
		{
			compDataStream =  new FileInputStream("compFile");
		}
		catch (Exception e)
		{
			return null;
		}

		return compDataStream;
	}

	//set Input stream
	void setInputStream(InputStream f)
	{ 
		compInputStream = f;
	}

	//return compress data in byte array
	public byte[] getCompressed()
	{
		return compressedByte;
	}

	//return compressed file path
	public String getcompfile()
	{
		return compFile;
	}

	//return source file
	public String getsrcfile()
	{
		return srcFile;
	}
}
