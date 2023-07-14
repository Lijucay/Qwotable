package com.lijukay.quotesAltDesign.item;

public class QuoteItem {
    private final String author;
    private final String quote;
    private final String source;


    public QuoteItem(String author, String quote, String source) {
        this.author = author;
        this.quote = quote;
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public String getQuote() {
        return quote;
    }

    public String getSource() {
        return source;
    }

}
