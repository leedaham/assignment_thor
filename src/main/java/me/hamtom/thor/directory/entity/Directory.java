package me.hamtom.thor.directory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Directory {

    @Id @GeneratedValue
    private Long id;
}
