package ch.bfh.univote.election.model;

import ch.bfh.univote.election.interfaces.Option;

public class ElectionList implements Option {

    private int id;
    private String type, number, title, partyAcronym;

    public ElectionList(int id, String type, String number, String title, String partyAcronym) {
        this.id = id;
        this.type = type;
        this.number = number;
        this.title = title;
        this.partyAcronym = partyAcronym;
    }

    public String getPartyAcronym() {
        return partyAcronym;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getNumber() {
        return this.number;
    }

    @Override
    public String getCommonName() {
        return this.title;
    }
}
