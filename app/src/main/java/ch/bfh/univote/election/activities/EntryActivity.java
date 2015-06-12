package ch.bfh.univote.election.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import ch.bfh.univote.election.R;
import ch.bfh.univote.election.controllers.ElectionDataController;
import ch.bfh.univote.registration.RegistrationActivity;
import ch.bfh.univote.registration.StartRegistrationActivity;

public class EntryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
    }

    @Override
    public void onResume() {
        super.onResume();

        // always clear sharedpreferences first
        ElectionDataController.getInstance(this).clearSharedPrefs();

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        File file = getFileStreamPath(RegistrationActivity.KEY_FILE_NAME);
        Intent intent;
        if(file.exists()) {
            intent = new Intent(this, ElectionOverviewActivity.class);
        } else {
            intent = new Intent(this, StartRegistrationActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
