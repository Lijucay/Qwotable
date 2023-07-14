package com.lijukay.quotesAltDesign.item;

public class InformationItem {
    private final String title;
    private final String message;
    private final String date;

    public InformationItem(String title, String message, String date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
