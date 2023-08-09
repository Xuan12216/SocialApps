package com.example.socialapps.LoginAndRegister;

public class users {
    String UserId,UserName,ProfilePic,ProfilePic1,UserEmail;

    public users(String userId, String userName, String profilePic, String profilePic1,String userEmail) {
        UserId = userId;
        UserName = userName;
        ProfilePic = profilePic;
        ProfilePic1 = profilePic1;
        UserEmail = userEmail;
    }

    public String getProfilePic1() {
        return ProfilePic1;
    }

    public void setProfilePic1(String profilePic1) {
        ProfilePic1 = profilePic1;
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
