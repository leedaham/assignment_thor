package me.hamtom.thor.directory.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "directory")
public class Directory extends BaseTime{

    @Id @GeneratedValue
    @Column(name = "directory_id")
    private Long id;

    @Column(length = 4096, nullable = false)
    private String pathName;

    @Column(nullable = false)
    private String owner;

    @Column(name = "owner_group", nullable = false)
    private String group;

    @Column(length = 9, nullable = false)
    private String permissions;

    @Column(nullable = false)
    private int size;

    public static Directory createDirectory(String pathName, String owner, String group, String permissions, int size) {
        Directory directory = new Directory();
        directory.setPathName(pathName);
        directory.setOwner(owner);
        directory.setGroup(group);
        directory.setPermissions(permissions);
        directory.setSize(size);
        return directory;
    }

}
