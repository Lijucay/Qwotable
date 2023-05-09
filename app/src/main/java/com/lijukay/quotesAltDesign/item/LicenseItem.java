package com.lijukay.quotesAltDesign.item;

public class LicenseItem {
    private final String TITLE;
    private final String LICENSE;
    private final String LICENSE_LINK;

    public LicenseItem(String title, String license, String link) {
        TITLE = title;
        LICENSE = license;
        LICENSE_LINK = link;
    }

    public String getLicense() {
        return LICENSE;
    }

    public String getLicenseTitle() {
        return TITLE;
    }

    public String getLicenseLink() {
        return LICENSE_LINK;
    }
}
