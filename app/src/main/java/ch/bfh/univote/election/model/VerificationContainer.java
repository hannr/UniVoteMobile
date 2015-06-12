package ch.bfh.univote.election.model;

public class VerificationContainer {
    private String R;
    private String Enc;
    private String Sig;

    public VerificationContainer(String enc, String r, String sig) {
        Enc = enc;
        R = r;
        Sig = sig;
    }

    public String getEnc() {
        return Enc;
    }

    public String getR() {
        return R;
    }

    public String getSig() {
        return Sig;
    }
}
