package com.mosquizto.api.util;

public class NotificationWriter {
    public static String inviteToCollection(String inviter , String role , String collectionName )
    {
        return  String.format("%s invites you to be %s of %s collection. Please check your collection library!", inviter , role, collectionName ) ;
    }
    public static String reportCollection(String collectionName, String reporter)
    {
        return String.format("Your %s collection has been report by %s. Please check your notifications! ", collectionName ,reporter) ;
    }

    public static String hasFollower(String followerName) {
        return String.format("%s started following you", followerName);
    }

    public static String userReported() {
        return "Your account has been reported. Please review your profile and make updates if needed.";
    }
}
