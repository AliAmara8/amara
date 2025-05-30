package com.ali.amara.user;

public class UserDTO {
    private Long id;
    private String lastName;
    private String firstName;
    private String email;

    // Constructeur par défaut
    public UserDTO() {}

    // Constructeur avec paramètres
    public UserDTO(Long id, String lastname, String name, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastname;
        this.email = email;
    }

    public UserDTO(UserEntity user) {
        if(user != null) {
            this.id = user.getId();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
           // this.avatarUrl = user.getAvatarUrl();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastname) {
        this.lastName = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return firstName;
    }

    public void setName(String firstName) {
        this.firstName = firstName;
    }
}
