package me.hamtom.thor.directory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Enviroment {

    @Id
    private String id = "capacityInfo";
    private int totalCapacity;
    private int currentCapacity;

}
