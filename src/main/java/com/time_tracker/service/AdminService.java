package com.time_tracker.service;

import com.time_tracker.model.Project;
import com.time_tracker.model.TimeEntry;
import com.time_tracker.model.User;
import com.time_tracker.repositories.TimeEntryRepository;
import com.time_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;

    //Метод для получения общего времени, которое пользователи потратили на проект
    public List<Map.Entry<User, String>> getUsersTotalTimeForProject(Long projectId) {
        Project project = new Project();
        project.setId(projectId);

        List<Map.Entry<User, String>> usersTotalTime = new ArrayList<>();

        //Вычисление общего времени, потраченного на проект, для каждого пользователя
        List<User> users = userRepository.findAll();
        for (User user : users) {
            String totalTime = getTotalTimeForProjectAndUser(project, user);
            usersTotalTime.add(new HashMap.SimpleEntry<>(user, totalTime));
        }

        return usersTotalTime;
    }

    //Метод для непосредственного вычисления общего времени для каждого пользователя
    private String getTotalTimeForProjectAndUser(Project project, User user) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByProjectAndUser(project, user);
        long totalSeconds = 0;
        for (TimeEntry entry : timeEntries) {
            if (entry.getStartTime() != null && entry.getEndTime() != null)
                totalSeconds += ChronoUnit.SECONDS.between(entry.getStartTime(), entry.getEndTime());
        }
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
