package ch.bfh.univote.registration;

import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.bfh.univote.election.R;

/**
 * Activity of the registation process.
 * @author Raphael Haenni
 */
public class RegistrationActivity extends Activity {
	/*
	 * UniVoteMobile Parameters
	 */
	private static final String CERTIFICATE_REQUEST_URL = "https://urd.bfh.ch/unicert-authentication/certificate-request/";
	public static final String KEY_FILE_NAME = "univote-key";
	public static final String EMAIL_FILE_NAME = "user-email";
	public static final String IDP_FILE_NAME = "user-idp";

	private static final int KEY_SIZE = 1024;
	public static final String APPLICATION_ID = "UniVote";
	private static final int IDENTITY_FUNCTION_DEFAULT = 1;
	private static final String ROLE = "Voter";
	private static final BigInteger P = new BigInteger("161931481198080639220214033595931441094586304918402813506510547237223787775475425991443924977419330663170224569788019900180050114468430413908687329871251101280878786588515668012772798298511621634145464600626619548823238185390034868354933050128115662663653841842699535282987363300852550784188180264807606304297");
	private static final BigInteger Q = new BigInteger("65133683824381501983523684796057614145070427752690897588060462960319251776021");
	private static final BigInteger G = new BigInteger("109291242937709414881219423205417309207119127359359243049468707782004862682441897432780127734395596275377218236442035534825283725782836026439537687695084410797228793004739671835061419040912157583607422965551428749149162882960112513332411954585778903685207256083057895070357159920203407651236651002676481874709");
	private static final int MIN_PASSWORD_LENGTH = 8;

	private DLogSetupFactory discreteLogSetupFactory;
	private String jSessionId;
	private UserData userData;

	/*
	 * GUI Elements
	 */
	private Button buttonGenerateKey;
	private TextView textViewPrivateKey;
	private EditText editTextPassword1;
	private EditText editTextPassword2;
	private Spinner spinnerIdentityFunction;
	private Button buttonSaveKeyOnDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.jSessionId = extras.getString("JSESSIONID");
			this.userData = new UserData(extras.getString("idp"), extras.getString("mail"), extras.getString("id"), IDENTITY_FUNCTION_DEFAULT, APPLICATION_ID, ROLE);

			setContentView(R.layout.activity_registration_v2);
			declareGuiElements();

			// Generate Key Listener
			buttonGenerateKey.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					RegistrationActivity.this.discreteLogSetupFactory = new DLogSetupFactory(KEY_SIZE, P, Q, G);

					// Base64 String representation of private key
					String privateKeyBase64 = Base64.encodeToString(RegistrationActivity.this.discreteLogSetupFactory.getPrivateKey().toByteArray(), Base64.DEFAULT);
					//editTextPrivateKey.setText(privateKeyBase64);
					textViewPrivateKey.setText(privateKeyBase64);
				}
			});

			// Identity Function Listener
			spinnerIdentityFunction.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					// spinner entries start with 0. if we add 1, the spinner entry id's are corresponding to "real" identity function id's.
					// see more at strings.xml
					RegistrationActivity.this.userData.setIdentityFunction((int)id + 1);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});

			// Save Key on Device Listener
			buttonSaveKeyOnDevice.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (validateForm()) {
						try {
							String privateKeyBase64 = Base64.encodeToString(RegistrationActivity.this.discreteLogSetupFactory.getPrivateKey().toByteArray(), Base64.DEFAULT);
							// background task for generating unicert certificate and storing key on device
							new BackgroundTask().execute(new PasswordBasedEncryptionParams(privateKeyBase64, editTextPassword1.getText().toString()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	/*
	 * Returns true if form is valid. Returns false otherwise.
	 */
	private boolean validateForm() {
		Resources res = getResources();
		if (this.textViewPrivateKey.getText().toString().equalsIgnoreCase("") ||
				this.editTextPassword1.getText().toString().equalsIgnoreCase("") ||
				this.editTextPassword2.getText().toString().equalsIgnoreCase("")) {
			Toast.makeText(this.getApplicationContext(), R.string.all_fields_required, Toast.LENGTH_SHORT).show();
			return false;
		} else if (!(this.editTextPassword1.getText().toString().equals(this.editTextPassword2.getText().toString()))) {
			Toast.makeText(this.getApplicationContext(), R.string.passwords_not_the_same, Toast.LENGTH_SHORT).show();
			return false;
		} else if (!(this.editTextPassword1.getText().length() >= MIN_PASSWORD_LENGTH)) {
			Toast.makeText(this.getApplicationContext(), R.string.password_too_short + MIN_PASSWORD_LENGTH + ".", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	private void declareGuiElements() {
		this.buttonGenerateKey = (Button) findViewById(R.id.button_generate_key);
		this.textViewPrivateKey = (TextView) findViewById(R.id.text_view_private_key);
		this.editTextPassword1 = (EditText) findViewById(R.id.edit_text_password1);
		this.editTextPassword2 = (EditText) findViewById(R.id.edit_text_password2);
		this.spinnerIdentityFunction = (Spinner) findViewById(R.id.spinner_identity_function);
		this.buttonSaveKeyOnDevice = (Button) findViewById(R.id.button_save_key_on_device);
	}

	/*
	 * AsyncTask for requesting certificate from unicert issuer and saving encrypted key to file.
	 * RegistrationCompleteActivity Intent gets started afterwards.
	 */
	private class BackgroundTask extends AsyncTask<PasswordBasedEncryptionParams,Void,BackgroundTaskResults> {

		@Override
		protected BackgroundTaskResults doInBackground(PasswordBasedEncryptionParams... params) {
			PasswordBasedEncryptionParams aesParams = params[0];
			URL url;
			String serverResponse;
			String encryptedPrivateKey = new String();
			BackgroundTaskResults results = null;

			try {
				// certificate request
				url = new URL(RegistrationActivity.CERTIFICATE_REQUEST_URL);

				CertificateRequester certificateRequester = new CertificateRequester(discreteLogSetupFactory.getDLogSetup(userData), userData, jSessionId, url);
				serverResponse = certificateRequester.doRequest();

				// AES key encryption
				String encryptedMessage = AES.encrypt(aesParams.getMessageToEncrypt(), aesParams.getPassword());
				// write encrypted key to file
				IOUtil.writeFile(encryptedMessage, KEY_FILE_NAME, getApplicationContext());
				IOUtil.writeFile(userData.getMail(), EMAIL_FILE_NAME, getApplicationContext());
				IOUtil.writeFile(userData.getIdentityProvider(), IDP_FILE_NAME, getApplicationContext());

				results = new BackgroundTaskResults(aesParams.messageToEncrypt, aesParams.getPassword(), encryptedPrivateKey, serverResponse);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return results;
		}

		@Override
		protected void onPostExecute(BackgroundTaskResults results) {
			super.onPostExecute(results);

			String decryptedPrivateKeyString;
			try {
				Intent intent = new Intent(RegistrationActivity.this, RegistrationCompleteActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class BackgroundTaskResults {
		String originalPrivateKey, password;
		String encryptedPrivateKey, serverResponse;

		private BackgroundTaskResults(String originalPrivateKey, String password, String encryptedPrivateKey, String serverResponse) {
			this.originalPrivateKey = originalPrivateKey;
			this.password = password;
			this.encryptedPrivateKey = encryptedPrivateKey;
			this.serverResponse = serverResponse;
		}
	}

	private class PasswordBasedEncryptionParams {
		private String messageToEncrypt, password;

		private PasswordBasedEncryptionParams(String messageToEncrypt, String password) {
			this.messageToEncrypt = messageToEncrypt;
			this.password = password;
		}

		private String getMessageToEncrypt() {
			return this.messageToEncrypt;
		}

		private String getPassword() {
			return this.password;
		}
	}
}