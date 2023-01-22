package com.lijukay.quotesAltDesign.item;

public class wisdomItem {
    private final String authorAll;
    private final String wisdomAll;
    private final String foundIn;
    private final String titleAll;

    public wisdomItem(String authorAll, String wisdomAll, String foundIn, String titleAll) {
        this.authorAll = authorAll;
        this.wisdomAll = wisdomAll;
        this.foundIn = foundIn;
        this.titleAll = titleAll;


    }

    public String getAuthor() {
        return authorAll;
    }

    public String getWisdom() {
        return wisdomAll;
    }

    public String getFoundIn(){return foundIn;}

    public String getTitle(){return titleAll;}
}
