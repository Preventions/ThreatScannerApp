package com.alienvault.threatscanner.model;

/**
 * Created by hbaxamoosa on 9/27/16.
 */

public class OTXResults {

    private String ScanningHost;
    private String MalwareDomain;
    private String ThreatScore;

    public OTXResults() {
    }

    public String getScanningHost() {
        return ScanningHost;
    }

    public void setScanningHost(String s) {
        this.ScanningHost = s;
    }

    public String getMalwareDomain() {
        return MalwareDomain;
    }

    public void setMalwareDomain(String s) {
        this.MalwareDomain = s;
    }

    public String getThreatScore() {
        return ThreatScore;
    }

    public void setThreatScore(String s) {
        this.ThreatScore = s;
    }
}
