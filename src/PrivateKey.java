import java.math.BigInteger;

public final class PrivateKey {
	private transient BigInteger n, d;

	public PrivateKey(BigInteger n, BigInteger d) {
		this.n = n;
		this.d = d;
	}

	public BigInteger getD() {
		return this.d;
	}

	public String getDs() { 
		return this.d.toString();
	}

	public BigInteger getN() {
		return this.n;
	}

	public String getNs() { 
		return this.n.toString();
	}

	public String toString() {
		return "[Private key " + n + ", " + d + "]";
	}
}
