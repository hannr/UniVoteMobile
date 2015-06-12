package ch.bfh.univote.election.activities;

import ch.bfh.univote.election.interfaces.Option;

public class OptionItem {
    private Option option;
    private int count;

    public OptionItem(Option option, int count) {
        this.option = option;
        this.count = count;
    }

    public String getName() {
        return this.option.getCommonName();
    }

    public int getId() {
        return this.option.getId();
    }

    public String getType() {
        return this.option.getType();
    }

    public String getNumber() {
        return this.option.getNumber();
    }

    public String getCommonName() {
        return this.option.getCommonName();
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}