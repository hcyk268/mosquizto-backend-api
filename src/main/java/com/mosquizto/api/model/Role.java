package com.mosquizto.api.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_role")
@Entity
public class Role {

    @Id
    @Column(name = "id")
    private Integer id;

    private String name;
}
