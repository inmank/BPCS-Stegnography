import java.math.BigInteger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Test {

	public KeyValues Keys(int bitlen)
	{
		if(bitlen<512) 
			bitlen=1024;

		KeyValues kv = new KeyValues();
		long t1, t2;

		System.out.println("Generating prime number p");
		t1 = System.currentTimeMillis();
		PrimeNumber p = new PrimeNumber(bitlen);
		t2 = System.currentTimeMillis() - t1;
		System.out.println("Generated p = " + p + " in " + t2 + " ms!");

		System.out.println("Generating prime number q");
		t1 = System.currentTimeMillis();
		PrimeNumber q = new PrimeNumber(bitlen);
		t2 = System.currentTimeMillis() - t1;
		System.out.println("Generated p = " + q + " in " + t2 + " ms!");

		System.out.println("Computing n = p.q");
		t1 = System.currentTimeMillis();
		BigInteger n = RSA.computeN(p, q);
		t2 = System.currentTimeMillis() - t1;
		System.out.println("Computed n = " + n + " in " + t2 + " ms!");

		System.out.println("Computing phi = (p-1)(q-1)");
		t1 = System.currentTimeMillis();
		BigInteger phi = RSA.computePhi(p, q);
		t2 = System.currentTimeMillis() - t1;
		System.out.println("Computed phi = " + phi + " in " + t2 + " ms!");

		System.out.println("Computing e");
		t1 = System.currentTimeMillis();
		BigInteger e = RSA.computeE(phi);
		t2 = System.currentTimeMillis() - t1;
		System.out.println("Computed e = " + e + " in " + t2 + " ms!");

		System.out.println("Computing d");
		t1 = System.currentTimeMillis();
		BigInteger d = RSA.computeD(e, phi);
		t2 = System.currentTimeMillis() - t1;
		System.out.println("Computed d = " + d + " in " + t2 + " ms!");

		PublicKey pubKey = new PublicKey(n, e);
		System.out.println(pubKey.toString());
		PrivateKey priKey = new PrivateKey(n, d);
		System.out.println(priKey.toString());

		kv.pubkey=pubKey.getE();
		kv.privkey=priKey.getD();

		if(pubKey.getNs().equals(priKey.getNs()))
		{
			kv.mod=pubKey.getN(); 
		}
		else
		{
			System.out.println("PUBKEY AND PRIV KEY DOESN'T MATCH");
			JOptionPane.showMessageDialog(new JFrame(),"PUBKEY AND PRIVKEY DOESN'T MATCH");
			return null;
		}  

		return kv;
	} 
}
