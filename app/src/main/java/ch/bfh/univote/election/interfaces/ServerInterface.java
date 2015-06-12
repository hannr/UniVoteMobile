package ch.bfh.univote.election.interfaces;

import org.json.JSONObject;

import java.util.Map;

import ch.bfh.univote.election.model.ElectionContainer;
import ch.bfh.univote.election.model.VerificationContainer;

/**
 * Created by hannr on 07.06.15.
 */
public interface ServerInterface {
    // Integer = Election ID
    Map<Integer, ElectionContainer> pullCurrentElections();
    // return JSON String
    String pullElectionDataByElectionId(int electionId);
    VerificationContainer pushElection(JSONObject jsonObject);
}
