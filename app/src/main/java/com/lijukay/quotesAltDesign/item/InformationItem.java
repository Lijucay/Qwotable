package com.lijukay.quotesAltDesign.item;

public class InformationItem {
    private final String TITLE;
    private final String MESSAGE;
    private final String DATE;

    public InformationItem(String title, String message, String date){
        TITLE = title;
        MESSAGE = message;
        DATE = date;
    }

    public String getTITLE(){
        return TITLE;
    }
    public String getMessage(){
        return MESSAGE;
    }
    public String getDate(){
        return DATE;
    }
}
