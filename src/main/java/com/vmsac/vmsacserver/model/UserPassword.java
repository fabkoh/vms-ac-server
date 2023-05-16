package com.vmsac.vmsacserver.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserPassword {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String oldpassword;

    @NotBlank
    @Size(min = 6, max = 40)
    @Size(max = 120)
    private String newpassword;

    public String getOldpassword() {
        return oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }
}
