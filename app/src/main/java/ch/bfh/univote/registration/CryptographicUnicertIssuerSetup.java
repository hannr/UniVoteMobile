package ch.bfh.univote.registration;

/**
 * Extended Cryptographic Setup needed for unicert certificate creation.
 * @author Raphael Haenni
 */
public interface CryptographicUnicertIssuerSetup extends CryptographicSetup {
	
	public String getBody(UserData userData);
	
}
