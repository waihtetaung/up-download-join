package com.example.fileupdownloadjoin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Table(name = "documents")
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 525, nullable = false, unique = true)
    private String name;
    private long size;
    @Column(name = "upload_time")
    private Date uploadTime;
    @Column(length = 255, nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "projectId")
    private Project project;

    public Document(Long id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }
}
