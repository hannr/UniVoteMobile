package ch.bfh.univote.election.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import java.util.TreeMap;
import ch.bfh.univote.election.R;
import ch.bfh.univote.election.adapters.OptionListFragmentStatePagerAdapter;
import ch.bfh.univote.election.controllers.ElectionDataController;
import ch.bfh.univote.election.model.ElectionList;

public class OptionSelectionActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static String ACTION_BAR_COLOR = "#871818";
    OptionListFragmentStatePagerAdapter pagerAdapter;
    ViewPager viewPager;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TreeMap<Integer, ElectionList> lists = (TreeMap<Integer, ElectionList>) ElectionDataController.getInstance(this).getLists();
        pagerAdapter = new OptionListFragmentStatePagerAdapter(getApplicationContext(), getSupportFragmentManager());
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        for (Integer elem : lists.keySet()) {
            actionBar.addTab(actionBar.newTab().setText("List " + lists.get(elem).getNumber()).setTabListener(this));
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(ACTION_BAR_COLOR)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor(ACTION_BAR_COLOR)));

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onPause() {
        super.onPause();

        if (ElectionDataController.getInstance(this).getLoginRequiredFlag()) {
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ElectionDataController.getInstance(this).setLoginRequiredFlag(true);

    }

    @Override
    public void onBackPressed() {
        ElectionDataController.getInstance(this).setLoginRequiredFlag(false);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                ElectionDataController.getInstance(this).setLoginRequiredFlag(false);
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
