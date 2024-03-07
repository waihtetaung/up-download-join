package com.example.fileupdownloadjoin.controller;

import com.example.fileupdownloadjoin.entity.*;
import com.example.fileupdownloadjoin.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AppController {
    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProjectAccessRepository accessRepository;

    @GetMapping("/")
    public String viewHomePage(Model model){
        List<Document> documentList = documentRepository.findAllBy();
        model.addAttribute("documentList", documentList);
        return "home";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile,
                             @RequestParam("projectId") Integer projectId,
                             RedirectAttributes attributes) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        String uploadDir = "/MemberName/ProjectName/";
        String filePath = uploadDir + fileName;
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        }
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()){
                Project project = optionalProject.get();
                Document document = new Document();
                document.setName(fileName);
                document.setSize(multipartFile.getSize());
                document.setUploadTime(new Date());
                document.setFilePath(filePath);
                document.setProject(project);

                documentRepository.save(document);
                attributes.addFlashAttribute("message", "The file has been uploaded successfully.");
        }else {
            attributes.addFlashAttribute("error", "Project not found with ID: " + projectId);
        }
        return "redirect:/";
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam("id") Long id,
                             @RequestParam("projectId") Integer projectId,
                             @RequestParam("userId") Integer userId,
                             @RequestParam("roleId") Integer roleId,
                             HttpServletResponse response) throws Exception{
        Optional<Document> result = documentRepository.findById(id);
        if (result.isEmpty()) {
            throw new Exception("Could not find document with ID: " + id);
        }

        Document document = result.get();
        String filePath = document.getFilePath();
        File file = new File(filePath);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new Exception("Project not found with ID: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with ID: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new Exception("Role not found with ID: " + roleId));

        ProjectAccess projectAccess = new ProjectAccess();
        projectAccess.setProject(project);
        projectAccess.setUser(user);
        projectAccess.setRole(role);
        accessRepository.save(projectAccess);

        accessRepository.save(projectAccess);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + document.getName());

        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @GetMapping("/documents")
    public String downloadHistory(@RequestParam("userId") Integer userId,
                                                      @RequestParam("projectId") Integer projectId,
                                                      Model model) {
        List<Document> documents = documentRepository.findDocumentsForUserByRoleAndProject(userId, projectId);
        model.addAttribute("documents", documents);
        return "downloadhistory";
    }

}
