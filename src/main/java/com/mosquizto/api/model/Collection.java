package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_collection")
@Entity
@Builder
@Data
public class Collection extends AbstractEntity<Integer> {

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description ;
    private Boolean visibility ;

    @ManyToOne(targetEntity = User.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user ;

}
