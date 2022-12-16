package com.lijukay.quotesAltDesign.item;

public class wisdomItem {
    private final String authorAll;
    private final String wisdomAll;
    private final String titleAll;

    public wisdomItem(String authorAll, String wisdomAll, String titleAll) {
        this.authorAll = authorAll;
        this.wisdomAll = wisdomAll;
        this.titleAll = titleAll;

    }

    public String getAuthorAll() {
        return authorAll;
    }

    public String getWisdomAll() {
        return wisdomAll;
    }

    public String getTitleAll(){return titleAll;}
}
