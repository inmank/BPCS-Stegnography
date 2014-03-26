import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.swing.JOptionPane;

public class Cryptography
{
	byte[] compressedcypher = null;
	String srcFile="";
	String compFile="";

	public InputStream doencryptcomp(InputStream ins,PublicKey pubK,PrivateKey priK) 
	{
		PublicKey pubKey = pubK;
		FileInputStream fis = null;
		byte plainb[] = null;
		byte[] cypherText = null;
		try
		{
			fis=(FileInputStream)ins;
			int av=fis.available();
			plainb=new byte[av]; 
			fis.read(plainb);
			cypherText = RSA.encrypt(plainb,pubKey);
			FileOutputStream cfis=new FileOutputStream("encFile");
			cfis.write(cypherText);
			cfis.close();
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error in Encrypting Data.\nPlease select valid file and try again.",
					"Error:",
					JOptionPane.ERROR_MESSAGE);
		}

		InputStream myFileInputStream1 =  null;

		try
		{
			myFileInputStream1 =  new FileInputStream("encFile");
		}
		catch (Exception e)
		{
			return null;
		}

		return myFileInputStream1;
	}

	public InputStream dodecryptcomp(InputStream ins, PublicKey pubK, PrivateKey priK) 
	{
		PrivateKey priKey = priK;
		byte plainb[] = null;
		byte[] cypherText = null;
		try
		{
			FileInputStream fis = (FileInputStream)ins;
			int av = fis.available();
			plainb = new byte[av];  
			fis.read(plainb);
			fis.close();
			
			cypherText = RSA.decrypt(plainb,priKey);
			FileOutputStream cfis = new FileOutputStream("decFile");
			cfis.write(cypherText);
			cfis.close();
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error in Decrypting Data.\n Please select valid file and try again.",
					"Error:",
					JOptionPane.ERROR_MESSAGE);
		}

		InputStream myFileInputStream1 =  null;

		try
		{
			myFileInputStream1 =  new FileInputStream("decFile");
		}
		catch (Exception e)
		{
			return null;
		}

		return myFileInputStream1;
	}
}
