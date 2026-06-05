package com.mosquizto.api.service;


public interface NotificationService {
    public void sendInvitationToSpecificUser(String targetUsername, String message) ;
    public void sendReportToSpecificUser(String targetUsername, String message) ;
}
