package ch.bfh.univote.election.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ch.bfh.univote.election.activities.OptionListFragment;
import ch.bfh.univote.election.controllers.ElectionDataController;

public class OptionListFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    Context context;

    public OptionListFragmentStatePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return ElectionDataController.getInstance(context).getLists().size();
    }

    @Override
    public Fragment getItem(int position) {
        // position+1 because tabposition starts at 0 and lists always at 1.
        OptionListFragment clf = OptionListFragment.getInstance(position + 1);
        return clf;
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }
}