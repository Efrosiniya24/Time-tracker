package com.time_tracker.controller;

import com.time_tracker.model.Project;
import com.time_tracker.repositories.ProjectRepository;
import com.time_tracker.service.AdminService;
import com.time_tracker.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import com.time_tracker.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("time-tracker/admin")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final AdminService adminService;


    //Добавление в базу нового проекта
    @PostMapping("/addProject")
    public void addProject(@RequestBody Project project) {
        log.info("New row: " + projectRepository.save(project));
    }

    //Получение из базы проекта
    @GetMapping("/project/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        if (project != null)
            return ResponseEntity.ok(project);
        else
            return ResponseEntity.notFound().build();
    }

    //Удаление проекта
    @DeleteMapping("/project/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectRepository.deleteById(id);
    }

    //Изменение название проекта
    @PutMapping("/project/{id}")
    @Transactional
    public ResponseEntity<Project> updateProjectName(@PathVariable Long id, @RequestBody Project projectUpdate) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        existingProject.setName(projectUpdate.getName());
        Project updatedProject = projectRepository.save(existingProject);

        return ResponseEntity.ok(updatedProject);
    }

    //Получение общего времени работы пользователя над проектом
    @GetMapping("/usersTotalTime/{projectId}")
    public ResponseEntity<List<Map.Entry<User, String>>> getUsersTotalTimeForProject(@PathVariable Long projectId) {
        List<Map.Entry<User, String>> usersTotalTime = adminService.getUsersTotalTimeForProject(projectId);
        return ResponseEntity.ok(usersTotalTime);
    }
}
