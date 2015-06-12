package ch.bfh.univote.election.controllers;

import android.content.Context;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ch.bfh.univote.election.interfaces.ServerInterface;
import ch.bfh.univote.election.model.ElectionContainer;
import ch.bfh.univote.election.model.VerificationContainer;

public class DummyServerController implements ServerInterface {

    HashMap<Integer, ElectionContainer> currentElections;
    JSONObject pulledElectionObject;
    Context context;

    public DummyServerController(Context context) {
        this.context = context;
        this.currentElections = new HashMap<>();
        this.currentElections.put(1, new ElectionContainer
                (1, "Universität Bern: Wahlen des SUB-StudentInnenrates",
                        "Université de Berne: Élection du conseil des étudiant-e-s SUB",
                        "University of Berne: SUB Elections",
                        "Der Wahlzettel kann einer Liste zugeordnet werden und maximal 40 Kandidierende aus verschiedenen Listen enthalten. Einzelne Kandidierende können bis zu dreimal aufgeführt werden. Enthält ein Wahlzettel weniger als 40 Kandidierende, so zählen die fehlenden Einträge als Zusatzstimmen für die ausgewählte Liste. Wenn keine Liste angegeben ist, verfallen diese Stimmen.",
                        "Le bulletin de vote peut comporter au maximum 40 candidats. Il peut comporter des candidats de listes différentes. Chaque électeur peut cumuler jusqu’à trois suffrages sur un candidat. Le bulletin de vote peut-être attribué à une liste. Si un bulletin de vote contient un nombre de candidats inférieurs à 40, les lignes laissées en blanc sont considérées comme autant de suffrages complémentaires attribués à la liste choisie. Si aucune liste n’est indiquée, ces suffrages  sont considérés comme périmés.",
                        "Your ballot paper can include up to 40 candidates from all lists. It can include candidates from different lists. You may vote for each candidate up to three times. You can assign a list to your ballot. If you vote for less than 40 candidates, the missing entries will count as list votes for the chosen list. If you do not assign a list, they will expire and count as empty votes.",
                        "2015-03-08T23:00:00.000Z",
                        "2015-03-26T11:00:00.000Z"));
    }

    @Override
    public Map<Integer, ElectionContainer> pullCurrentElections() {
        return this.currentElections;
    }

    @Override
    public String pullElectionDataByElectionId(int electionId) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("listElectionDataExample.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public VerificationContainer pushElection(JSONObject jsonObject) {
        return new VerificationContainer(null, null, null);
    }
}
