package ch.bfh.univote.election.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import ch.bfh.univote.election.R;
import ch.bfh.univote.election.adapters.ElectionViewAdapter;
import ch.bfh.univote.election.model.ElectionContainer;
import ch.bfh.univote.election.controllers.ElectionDataController;
import ch.bfh.univote.election.interfaces.ServerInterface;

public class ElectionOverviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        ListView electionListView = (ListView)findViewById(R.id.currentElections);

        ServerInterface serverController = ElectionDataController.getInstance(getApplicationContext()).getServerController();
        Map<Integer, ElectionContainer> electionsMap = serverController.pullCurrentElections();

        ArrayList<ElectionContainer> electionList = new ArrayList<>();
        for (Map.Entry<Integer, ElectionContainer> entry : electionsMap.entrySet()) {
            electionList.add(entry.getValue());
        }

        ElectionViewAdapter eva = new ElectionViewAdapter(this, electionList);
        electionListView.setAdapter(eva);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(this, EntryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
