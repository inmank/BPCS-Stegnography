import java.math.BigInteger;
import java.security.SecureRandom;

public final class PrimeNumber {
	private final SecureRandom SECURE_RND = new SecureRandom();
	private BigInteger prime = null;
	
	public PrimeNumber(int bitLength) {
		this.prime = BigInteger.probablePrime(bitLength, SECURE_RND);
	}
	
	public BigInteger getPrime() {
		return this.prime;
	}
	
	public String toString() {
		return "[Prime Number " + this.prime.toString() + "]";
	}
}
 