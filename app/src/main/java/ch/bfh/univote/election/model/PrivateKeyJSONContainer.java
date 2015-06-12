package ch.bfh.univote.election.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PrivateKeyJSONContainer implements Parcelable {
    private String privateKey;
    private long padding;

    public PrivateKeyJSONContainer(String privateKey, long padding) {
        this.privateKey = privateKey;
        this.padding = padding;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public long getPadding() {
        return padding;
    }

    protected PrivateKeyJSONContainer(Parcel in) {
        privateKey = in.readString();
        padding = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(privateKey);
        dest.writeLong(padding);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PrivateKeyJSONContainer> CREATOR = new Parcelable.Creator<PrivateKeyJSONContainer>() {
        @Override
        public PrivateKeyJSONContainer createFromParcel(Parcel in) {
            return new PrivateKeyJSONContainer(in);
        }

        @Override
        public PrivateKeyJSONContainer[] newArray(int size) {
            return new PrivateKeyJSONContainer[size];
        }
    };
}