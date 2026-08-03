package dev.marcos.uptime.monitor.exceptions;

public class MonitorNotFoundException extends RuntimeException {
    public MonitorNotFoundException(String message) {
        super(message);
    }
}
