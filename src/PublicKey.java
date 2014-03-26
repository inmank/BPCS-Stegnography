import java.math.BigInteger;

public final class PublicKey {
	private transient BigInteger n, e;

	public PublicKey(BigInteger n, BigInteger e) {
		this.n = n;
		this.e = e;
	}

	public BigInteger getE() {
		return this.e;
	}

	public String getEs() {
		return this.e.toString();
	}

	public BigInteger getN() {
		return this.n;
	}

	public String getNs() { 
		return this.n.toString();  
	}

	public String toString() {
		return "[Public key " + n + ", " + e + "]";
	}
}
