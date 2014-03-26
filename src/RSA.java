import java.math.BigInteger;

public final class RSA {
	/**
	 * Computes e so that e and (p-1)(q-1) are relatively prime
	 * (no prime factors in common):
	 * 
	 * phi = (p-1)(q-1)
	 * 1 &lt; e &lt; phi and gcd(e, phi) = 1
	 * 
	 * Obviously, the first value of epot must be chosen in a smarter way...
	 *  
	 * @param phi
	 * @return
	 */
	public static BigInteger computeE(BigInteger phi) {
		BigInteger e = null;
		
		final BigInteger one = new BigInteger("1");
		
		BigInteger epot = null;
		while(true) {
			epot = phi.subtract(one);
			if (phi.gcd(epot).intValue() == 1) {
				e = epot;
				break;
			} else {
				epot = epot.subtract(one); //reduce one each iteration
			}
		}
		
		return e;
	}
	
	/**
	 * computeN
	 * @param p
	 * @param q
	 * @return
	 */
	public static BigInteger computeN(PrimeNumber p, PrimeNumber q) {
		return p.getPrime().multiply(q.getPrime());
	}
	
	/**
	 * phi = (p-1)(q-1)
	 * 
	 * @param p
	 * @param q
	 * @return
	 */
	public static BigInteger computePhi(PrimeNumber p, PrimeNumber q) {
		final BigInteger one = new BigInteger("1");
		return p.getPrime().subtract(one).multiply(q.getPrime().subtract(one));
	}
	
	/**
	 * Compute the secret exponent d, 1 < d < phi, such that ed ? 1 (mod phi)
	 * 
	 * @param e
	 * @param phi
	 * @return
	 */
	public static BigInteger computeD(BigInteger e, BigInteger phi) {
		return e.modInverse(phi);
	}
	
	/**
	 * encrypt
	 * ciphertext c = m^e mod n
	 * 
	 * 
	 * @param plainText (m)
	 * @param pubKey
	 * @return
	 */
	public static byte[] encrypt(byte[] plainText, PublicKey pubKey) {
		BigInteger m = new BigInteger(plainText);
		return m.modPow(pubKey.getE(), new BigInteger(pubKey.getNs())).toByteArray(); 
	}
	
	/**
	 * decrypt
	 * m = c^d mod n
	 * 
	 * @param cypherText (c)
	 * @param priKey
	 * @return
	 */
	public static byte[] decrypt(byte[] cypherText, PrivateKey priKey) {
		BigInteger c = new BigInteger(cypherText);
		return c.modPow(new BigInteger(priKey.getDs()), priKey.getN()).toByteArray(); 
	}
}
