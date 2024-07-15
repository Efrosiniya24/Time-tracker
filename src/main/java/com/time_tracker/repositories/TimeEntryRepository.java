package com.time_tracker.repositories;

import com.time_tracker.model.Project;
import com.time_tracker.model.User;
import com.time_tracker.model.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    List<TimeEntry> findByProjectAndUserAndEndTimeIsNull(Project project, User user);

    List<TimeEntry> findByProject(Project project);

    List<TimeEntry> findByProjectAndUser(Project project, User user);
}
