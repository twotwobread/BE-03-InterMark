package com.prgrms.be.intermark.domain.schedule;

import com.prgrms.be.intermark.domain.performance.Performance;
import com.prgrms.be.intermark.domain.performance_stadium.PerformanceStadium;
import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_stadium_id")
    private PerformanceStadium performanceStadium;

    @OneToMany(mappedBy = "schedule")
    private List<ScheduleSeat> scheduleSeats = new ArrayList<>();

    @Builder
    public Schedule(LocalDateTime startTime, PerformanceStadium performanceStadium, List<ScheduleSeat> scheduleSeats) {
        this.startTime = startTime;
        this.performanceStadium = performanceStadium;
        this.scheduleSeats = scheduleSeats;
    }

    public void setPerformanceStadium(PerformanceStadium performanceStadium) {
        if (Objects.nonNull(this.performanceStadium)) {
            this.performanceStadium.getSchedules().remove(this);
        }

        this.performanceStadium = performanceStadium;
        performanceStadium.getSchedules().add(this);
    }
}