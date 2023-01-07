package com.lijukay.quotesAltDesign.item;

public class AllItem {
    private final String authorAll;
    private final String quoteAll;
    private final String foundIn;


    public AllItem(String authorAll, String quoteAll, String foundIn) {
        this.authorAll = authorAll;
        this.quoteAll = quoteAll;
        this.foundIn = foundIn;
    }

    public String getAuthorAll() {
        return authorAll;
    }

    public String getQuoteAll() {
        return quoteAll;
    }

    public String getFoundIn(){return foundIn;}

}
