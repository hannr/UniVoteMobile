package ch.bfh.univote.election.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.bfh.univote.election.R;
import ch.bfh.univote.election.adapters.BallotViewAdapter;
import ch.bfh.univote.election.controllers.ElectionDataController;

public class VerificationActivity extends Activity {

    private ListView verificationListView;
    private BallotViewAdapter bva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verificationListView = (ListView) findViewById(R.id.verificationListView);
        List<OptionItem> optionItems = new ArrayList<>();
        TreeMap<Integer, OptionItem> sortedMap = (TreeMap<Integer, OptionItem>) ElectionDataController.getInstance(getApplicationContext()).getOptionItemsFromSharedPrefsWhereOptionCountBiggerZero();
        for (Map.Entry<Integer, OptionItem> entry : sortedMap.entrySet()) {
            optionItems.add(entry.getValue());
        }

        bva = new BallotViewAdapter(this, optionItems);
        verificationListView.setAdapter(bva);

        Button showVerification = (Button) findViewById(R.id.showVerificationButton);
        showVerification.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VerificationTabActivity.class);
                startActivity(intent);
            }
        });

        Button finishButton = (Button) findViewById(R.id.verificationFinishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // clear sharedpreferences here for security reasons
        ElectionDataController.getInstance(getApplicationContext()).clearSharedPrefs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verification, menu);
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
