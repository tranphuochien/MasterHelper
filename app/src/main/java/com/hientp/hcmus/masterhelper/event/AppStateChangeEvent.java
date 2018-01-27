package com.hientp.hcmus.masterhelper.event;

/**
 * Event use for eventbus
 * State : background | foreground
 */
public class AppStateChangeEvent {
    public boolean isForeground;

    public AppStateChangeEvent(boolean isForeground) {
        this.isForeground = isForeground;
    }
}
