package com.lijukay.quotes.item;

public class WisdomItem {
    private final String author;
    private final String wisdom;
    private final String source;
    private final String title;

    public WisdomItem(String author, String wisdom, String source, String title) {
        this.author = author;
        this.wisdom = wisdom;
        this.source = source;
        this.title = title;


    }

    public String getAuthor() {
        return author;
    }

    public String getWisdom() {
        return wisdom;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }
}
