package ch.bfh.univote.election.interfaces;

import android.content.Context;

/**
 * Created by hannr on 28.05.15.
 */
public interface Rule {
    boolean isAllowed(Context context, int optionId);
    boolean optionIdsContainsId(int id);
}
