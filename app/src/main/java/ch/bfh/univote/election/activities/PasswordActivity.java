package ch.bfh.univote.election.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import ch.bfh.univote.election.R;
import ch.bfh.univote.election.controllers.ElectionDataController;
import ch.bfh.univote.registration.AES;
import ch.bfh.univote.registration.IOUtil;
import ch.bfh.univote.registration.RegistrationActivity;
import ch.bfh.univote.registration.StartRegistrationActivity;

public class PasswordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final EditText editTextPassword = (EditText) findViewById(R.id.edit_text_password);

        Button continueButton = (Button) findViewById(R.id.passwordContinueButton);
        Button passwordForgottenButton = (Button) findViewById(R.id.button_password_forgotten);
        String readEmail = "";
        String readIDP = "";

        try {
            readEmail = IOUtil.readFromFile(RegistrationActivity.EMAIL_FILE_NAME, getApplicationContext());
            readIDP = IOUtil.readFromFile(RegistrationActivity.IDP_FILE_NAME, getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView userTextView = (TextView) findViewById(R.id.textViewPasswordActivityUser);
        userTextView.setText(readEmail);
        TextView idpTextView = (TextView) findViewById(R.id.textViewPasswordActivityIDP);
        idpTextView.setText(readIDP);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String passwordInput = editTextPassword.getText().toString();

                try {
                    String readFile = IOUtil.readFromFile(RegistrationActivity.KEY_FILE_NAME, getApplicationContext());
                    String privateKeyFromJson = AES.decrypt(readFile, passwordInput);

                    if (!(ElectionDataController.getInstance(getApplicationContext()).decryptedVotingKeyStoredInSharedPrefs())) {
                        ElectionDataController.getInstance(getApplicationContext()).addDecryptedVotingKeyToSharedPrefs(privateKeyFromJson);
                    }

                    Intent intent = new Intent(getApplicationContext(), BallotActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            PasswordActivity.this);

                    alertDialogBuilder.setTitle(R.string.password_not_correct);

                    alertDialogBuilder
                            .setMessage(R.string.enter_password_again)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                }
            }
        });

        passwordForgottenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StartRegistrationActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
