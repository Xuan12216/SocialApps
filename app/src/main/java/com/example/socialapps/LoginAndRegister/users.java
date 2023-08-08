package com.example.socialapps.LoginAndRegister;

public class users {
    String UserId,UserName,ProfilePic,UserEmail;

    public users(String userId, String userName, String profilePic, String userEmail) {
        UserId = userId;
        UserName = userName;
        ProfilePic = profilePic;
        UserEmail = userEmail;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public users(){};
}
