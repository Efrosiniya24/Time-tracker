package com.time_tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "time")
    private String time;

    @Column(name = "total_time")
    private String totalTime;

    @JsonIgnore
    @ManyToMany(mappedBy = "projects", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TimeEntry> timeEntries = new HashSet<>();


}
