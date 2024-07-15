package com.time_tracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.time_tracker.model.Project;
import com.time_tracker.model.TimeEntry;
import com.time_tracker.repositories.ProjectRepository;
import com.time_tracker.service.TimeEntryService;
import com.time_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.time_tracker.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("time-tracker/user")
@Slf4j
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    public final ProjectRepository projectRepository;
    public final ObjectMapper objectMapper;
    private final UserService userService;
    private final TimeEntryService timeEntryService;

    @GetMapping("/allProjects")
    public String allProjects() throws JsonProcessingException {
        List<String> allProjects = projectRepository.findAll()
                .stream()
                .map(Project::getName)
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(allProjects);
    }

    @PostMapping("/startProject/{projectId}")
    public ResponseEntity<String> startProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = userService.getCurrentUser();
        timeEntryService.startProjectTime(project, currentUser);

        return ResponseEntity.ok("Time tracking started for project: " + project.getName());
    }

    @PostMapping("/stopProject/{projectId}")
    public ResponseEntity<String> stopProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = userService.getCurrentUser();
        TimeEntry timeEntry = timeEntryService.stopProjectTime(project, currentUser);
        if (timeEntry != null) {
            String duration = timeEntry.getDurationString();
            return ResponseEntity.ok(String.format("Stopped project time tracking. Duration: %s", duration));
        } else {
            return ResponseEntity.badRequest().body("No active time entry found for the project");
        }
    }

    @GetMapping("/totalTime/{projectId}")
    public ResponseEntity<String> getTotalTime(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        String totalTime = timeEntryService.getTotalTimeForProject(project);
        return ResponseEntity.ok("Total time worked on project " + project.getName() + ": " + totalTime);
    }

    @GetMapping("/userTimeOnProject/{projectId}")
    public ResponseEntity<String> getUserTimeOnProject(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = userService.getCurrentUser();
        String totalTime = timeEntryService.getUserTimeOnProject(project, currentUser);
        return ResponseEntity.ok("Total time worked on project " + project.getName() + " by the current user: " + totalTime);
    }

}
