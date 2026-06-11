package com.restaurant.hnks24cntt1it211tranhuunhatproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badminton_clusters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadmintonCluster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(name = "hot_line", nullable = false, length = 20)
    private String hotLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;
}