package com.time_tracker.service;

import com.time_tracker.model.Project;
import com.time_tracker.model.TimeEntry;
import com.time_tracker.model.User;
import com.time_tracker.repositories.ProjectRepository;
import com.time_tracker.repositories.TimeEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final ProjectRepository projectRepository;

    public TimeEntryService(TimeEntryRepository timeEntryRepository, ProjectRepository projectRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.projectRepository = projectRepository;
    }

    // Метод для фиксации начала выполнения проекта
    public TimeEntry startProjectTime(Project project, User user) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setProject(project);
        timeEntry.setUser(user);
        timeEntry.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return timeEntryRepository.save(timeEntry);
    }

    // Метод для фиксации окончания выполнения проекта
    public TimeEntry stopProjectTime(Project project, User user) {
        List<TimeEntry> activeEntries = timeEntryRepository.findByProjectAndUserAndEndTimeIsNull(project, user);
        if (activeEntries != null && !activeEntries.isEmpty()) {
            for (TimeEntry timeEntry : activeEntries) {
                timeEntry.setEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                String duration = calculateDuration(timeEntry.getStartTime(), timeEntry.getEndTime());
                timeEntry.setDuration(duration);
                timeEntryRepository.save(timeEntry);
            }

            String totalTime = calculateTotalTimeForProject(project);
            project.setTime(totalTime);
            projectRepository.save(project);
            return activeEntries.get(activeEntries.size() - 1);
        }
        return null;
    }

    // Метод для получения общего времени по проекту
    public String getTotalTimeForProject(Project project) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByProject(project);
        long totalSeconds = calculateTotalSeconds(timeEntries);
        return formatTime(totalSeconds);
    }

    // Метод для получения времени пользователя по выполнению проекта
    public String getUserTimeOnProject(Project project, User user) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByProjectAndUser(project, user);
        long totalSeconds = calculateTotalSeconds(timeEntries);
        return formatTime(totalSeconds);
    }

    // Метод для расчета общего времени в секундах
    private long calculateTotalSeconds(List<TimeEntry> timeEntries) {
        long totalSeconds = 0;
        for (TimeEntry entry : timeEntries)
            if (entry.getStartTime() != null && entry.getEndTime() != null) {
                totalSeconds += ChronoUnit.SECONDS.between(entry.getStartTime(), entry.getEndTime());
            }
        return totalSeconds;
    }

    // Метод для расчета общего времени в формате HH:mm:ss
    private String calculateTotalTimeForProject(Project project) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByProject(project);
        long totalSeconds = calculateTotalSeconds(timeEntries);
        return formatTime(totalSeconds);
    }

    // Метод для расчета времени между началом выполнения проекта и его окончанием
    private String calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    // Метод для форматирования времени из секунд в строку HH:mm:ss
    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
