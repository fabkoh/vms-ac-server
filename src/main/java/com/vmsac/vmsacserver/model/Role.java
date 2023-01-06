package com.vmsac.vmsacserver.model;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ERole roleName;


    public Role() {
    }

    public Role(ERole roleName) {
        this.roleName = roleName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getRoleName() {
        return roleName;
    }

    public void setRoleName(ERole roleName) {
        this.roleName = roleName;
    }
}
