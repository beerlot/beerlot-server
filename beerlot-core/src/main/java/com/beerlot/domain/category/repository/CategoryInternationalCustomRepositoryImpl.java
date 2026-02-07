package com.beerlot.domain.category.repository;

import com.beerlot.domain.category.CategoryInternational;
import com.beerlot.domain.common.entity.LanguageType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.beerlot.domain.category.QCategoryInternational.categoryInternational;

@Repository
@RequiredArgsConstructor
public class CategoryInternationalCustomRepositoryImpl implements CategoryInternationalCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryInternational> findByNameAndLanguageType(String name, LanguageType languageType) {
        JPAQuery<CategoryInternational> query = queryFactory.select(categoryInternational)
                .from(categoryInternational)
                .where(
                        matchLanguage(languageType),
                        containsName(name)
                );

        return query.fetch();
    }

    private BooleanExpression matchLanguage(LanguageType languageType) {
        if (languageType == null) {
            return null;
        }

        return categoryInternational.id.language.eq(languageType);
    }

    private BooleanExpression containsName(String name) {
        if (name == null) {
            return null;
        }

        return categoryInternational.name.contains(name);
    }
}
