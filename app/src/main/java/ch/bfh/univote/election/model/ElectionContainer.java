package ch.bfh.univote.election.model;

public class ElectionContainer {

    private int electionId;
    private String electionTitleDe;
    private String electionTitleFr;
    private String electionTitleEn;

    private String electionDescriptionDe;
    private String electionDescriptionFr;
    private String electionDescriptionEn;

    private String votingPeriodBegin;
    private String votingPeriodEnd;

    public ElectionContainer(int electionId, String electionTitleDe, String electionTitleEn, String electionTitleFr, String electionDescriptionDe, String electionDescriptionEn, String electionDescriptionFr, String votingPeriodBegin, String votingPeriodEnd) {
        this.electionId = electionId;
        this.electionTitleDe = electionTitleDe;
        this.electionTitleEn = electionTitleEn;
        this.electionTitleFr = electionTitleFr;
        this.electionDescriptionDe = electionDescriptionDe;
        this.electionDescriptionEn = electionDescriptionEn;
        this.electionDescriptionFr = electionDescriptionFr;
        this.votingPeriodBegin = votingPeriodBegin;
        this.votingPeriodEnd = votingPeriodEnd;
    }

    public String getElectionDescriptionDe() {
        return electionDescriptionDe;
    }

    public String getElectionDescriptionEn() {
        return electionDescriptionEn;
    }

    public String getElectionDescriptionFr() {
        return electionDescriptionFr;
    }

    public int getElectionId() {
        return electionId;
    }

    public String getElectionTitleDe() {
        return electionTitleDe;
    }

    public String getElectionTitleEn() {
        return electionTitleEn;
    }

    public String getElectionTitleFr() {
        return electionTitleFr;
    }

    public String getVotingPeriodBegin() {
        return votingPeriodBegin;
    }

    public String getVotingPeriodEnd() {
        return votingPeriodEnd;
    }
}
