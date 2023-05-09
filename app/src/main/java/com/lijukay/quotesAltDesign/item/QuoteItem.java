package com.lijukay.quotesAltDesign.item;

public class QuoteItem {
    private final String AUTHOR;
    private final String QUOTE;
    private final String SOURCE;


    public QuoteItem(String author, String quote, String source) {
        AUTHOR = author;
        QUOTE = quote;
        SOURCE = source;
    }

    public String getAuthor() {
        return AUTHOR;
    }

    public String getQuote() {
        return QUOTE;
    }

    public String getSource(){return SOURCE;}

}
