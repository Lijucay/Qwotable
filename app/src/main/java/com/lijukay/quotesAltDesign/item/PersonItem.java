package com.lijukay.quotesAltDesign.item;

public class PersonItem {
    private final String authorPQ;
    private final String quotePQ;

    public PersonItem(String authorPQ, String quotePQ) {
        this.authorPQ = authorPQ;
        this.quotePQ = quotePQ;
    }

    public String getAuthorPQ() {
        return authorPQ;
    }

    public String getQuotePQ() {
        return quotePQ;
    }
}
