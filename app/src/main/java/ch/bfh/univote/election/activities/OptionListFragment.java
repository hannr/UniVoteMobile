package ch.bfh.univote.election.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import ch.bfh.univote.election.R;
import ch.bfh.univote.election.adapters.OptionViewAdapter;
import ch.bfh.univote.election.controllers.ElectionDataController;
import ch.bfh.univote.election.interfaces.Option;

public class OptionListFragment extends ListFragment {
    int mNum;
    OptionViewAdapter cva;

    public static OptionListFragment getInstance(int num) {
        OptionListFragment f = new OptionListFragment();

        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

        ArrayList<OptionItem> myArrayList = ElectionDataController.getInstance(getActivity()).getOptionItemsFromSharedPrefsByListNumber(mNum);
        cva = new OptionViewAdapter(this, getActivity(), myArrayList);
        setListAdapter(cva);
    }

    public void addOption(View v) {
        final OptionItem itemToAdd = (OptionItem)v.getTag();

        if (itemToAdd.getType().equals("list")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            // set title
            alertDialogBuilder.setTitle(R.string.add_list);

            // set dialog message
            alertDialogBuilder
                    .setMessage(R.string.question_only_list_or_including_candidates)
                    .setCancelable(false)
                    .setPositiveButton(R.string.including_candidates, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ElectionDataController.getInstance(getActivity()).addListWithAllCandidates(itemToAdd.getId());
                            OptionSelectionActivity cpsa = (OptionSelectionActivity) getActivity();
                            cpsa.pagerAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.only_list, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            itemToAdd.setCount(ElectionDataController.getInstance(getActivity()).addVote(itemToAdd.getId()));
                            cva.notifyDataSetChanged();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else {
            itemToAdd.setCount(ElectionDataController.getInstance(getActivity()).addVote(itemToAdd.getId()));
            cva.notifyDataSetChanged();
        }
    }

    public void removeOption(View v) {
        OptionItem itemToRemove = (OptionItem)v.getTag();
        itemToRemove.setCount(ElectionDataController.getInstance(getActivity()).removeFromBallot(itemToRemove.getId()));
        cva.notifyDataSetChanged();
    }
}
