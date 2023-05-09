package com.lijukay.quotesAltDesign.item;

public class wisdomItem {
    private final String AUTHOR;
    private final String WISDOM;
    private final String SOURCE;
    private final String TITLE;

    public wisdomItem(String author, String wisdom, String source, String title) {
        AUTHOR = author;
        WISDOM = wisdom;
        SOURCE = source;
        TITLE = title;


    }

    public String getAuthor() {
        return AUTHOR;
    }

    public String getWisdom() {
        return WISDOM;
    }

    public String getSource(){return SOURCE;}

    public String getTitle(){return TITLE;}
}
