package com.lijukay.quotes.item;

public class LicenseItem {
    private final String title;
    private final String license;
    private final String licenseLink;

    public LicenseItem(String title, String license, String link) {
        this.title = title;
        this.license = license;
        this.licenseLink = link;
    }

    public String getLicense() {
        return license;
    }

    public String getLicenseTitle() {
        return title;
    }

    public String getLicenseLink() {
        return licenseLink;
    }
}
