package pe.gob.sunass.vma.security.jwt;

public class JWTBean {
	
	private String secretKey;

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public JWTBean(String secretKey) {
		super();
		this.secretKey = secretKey;
	}
}
