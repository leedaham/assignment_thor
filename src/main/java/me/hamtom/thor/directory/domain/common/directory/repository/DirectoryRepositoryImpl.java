package me.hamtom.thor.directory.domain.common.directory.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static me.hamtom.thor.directory.domain.common.directory.entity.QDirectory.directory;

@Repository
@RequiredArgsConstructor
public class DirectoryRepositoryImpl implements DirectoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Integer getAllUsedCapacity() {
        return queryFactory
                .select(directory.size.sum())
                .from(directory)
                .fetchOne();
    }

    @Override
    public List<String> getChildDirectoriesPathName(String pathName) {
        return queryFactory
                .select(directory.pathName)
                .from(directory)
                .where(directory.pathName.startsWith(pathName))
                .fetch();
    }

    @Override
    public long renameDirectory(String newPathName, String oldPathName) {
        return queryFactory
                .update(directory)
                .set(directory.pathName, newPathName)
                .where(directory.pathName.eq(oldPathName))
                .execute();
    }
}
