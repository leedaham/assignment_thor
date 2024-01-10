package me.hamtom.thor.directory.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static me.hamtom.thor.directory.entity.QDirectory.*;


@Repository
@RequiredArgsConstructor
public class DirectoryRepositoryImpl implements DirectoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Integer getAllUsedCapacity() {
        return queryFactory.select(directory.size.sum())
                .from(directory)
                .fetchOne();
    }
}
