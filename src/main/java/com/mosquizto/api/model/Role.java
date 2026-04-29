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

    @Column(name = "name", length = 50)
    private String name;
}
