package com.example.fileupdownloadjoin.repository;

import com.example.fileupdownloadjoin.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
