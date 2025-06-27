package com.beerlot.domain.review;

import com.beerlot.domain.common.entity.SortType;
import com.beerlot.domain.common.util.SortTypeHelper;
import com.querydsl.core.types.OrderSpecifier;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

@AllArgsConstructor
public enum ReviewSortType implements SortType {
    RECENTLY_UPDATED("updatedAt", Direction.DESC),
    RECENTLY_CREATED("createdAt", Direction.DESC),
    MOST_LIKES("likeCount", Direction.DESC),
    HIGH_RATE("rate", Direction.DESC),
    LOW_RATE("rate", Direction.ASC)
    ;

    private String property;
    private Direction direction;

    @Override
    public String getProperty() {
        return this.property;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public <T> OrderSpecifier[] orderBy(Class clazz, String name) {
        return SortTypeHelper.orderBy(clazz, name, this);
    }
}
