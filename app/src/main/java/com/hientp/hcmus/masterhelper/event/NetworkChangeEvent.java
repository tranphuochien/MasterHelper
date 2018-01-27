package com.hientp.hcmus.masterhelper.event;


public class NetworkChangeEvent {
    public final boolean isOnline;

    public NetworkChangeEvent(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
