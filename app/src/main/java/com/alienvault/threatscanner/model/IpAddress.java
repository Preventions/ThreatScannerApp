package com.alienvault.threatscanner.model;

/**
 * Created by hbaxamoosa on 9/23/16.
 */

public class IpAddress {

    /**
     * Builders: When you have an object that requires more than ~3 constructor parameters, use a
     * builder to construct the object. It might be a little more verbose to write but it scales
     * well and it’s very readable. If you are creating a value class, consider AutoValue.
     */
    private String ipAddress;

    public IpAddress() {
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
