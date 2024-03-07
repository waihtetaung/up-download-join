package com.example.fileupdownloadjoin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;
    @Column(name = "role_name")
    private String roleName;


    @OneToMany(mappedBy = "role")
    private List<ProjectAccess> projectAccesses = new ArrayList<>();
}