package com.example.connectue.model;

public class User2 {

    /**
     * Class tag for logs.
     */
    private static final String TAG = "User model";

    /**
     * Name of users collection in the database.
     */
    public static final String USER_COLLECTION_NAME = "users";

    protected String userId;

    protected String firstName;

    protected String lastName;

    protected boolean isVerified;

    protected String password;

    protected String email;

    protected String phoneNumber;

    protected String profilePicUrl;

    protected String program;

    protected int role;

    public User2(String userId, String firstName, String lastName, boolean isVerified,
                 String email, String phoneNumber, String profilePicUrl,
                 String program, int role) {
        setId(userId);
        setFirstName(firstName);
        setLastName(lastName);
        setIsVerified(isVerified);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setProfilePicUrl(profilePicUrl);
        setProgram(program);
        setRole(role);
    }





    // Getters and setters for user ID.

    public String getId() { return userId; }

    public void setId(String userId) { this.userId = userId; }

    // Getters and setters for the first name.

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    // Getters and setters for the last name.

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    //Getter for the full name, which is first name + last name.

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and setters for is verified.

    public boolean isVerified() { return isVerified; }

    public void setIsVerified(boolean isVerified) { this.isVerified = isVerified; }

    // Getters and setters for password.

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    //Getters and setters for email.

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and setters for phone number.

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // Getters and setters for profile picture url.

    public String getProfilePicUrl() { return profilePicUrl; }

    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }

    // Getters and setters for program.

    public String getProgram() { return program; }

    public void setProgram(String program) {
        this.program = program;
    }

    // Getters and setters for role.

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
