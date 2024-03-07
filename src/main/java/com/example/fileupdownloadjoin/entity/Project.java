package com.example.fileupdownloadjoin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectId;
    private String projectName;

    @OneToMany(mappedBy = "project")
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ProjectAccess> projectAccesses = new ArrayList<>();
}


