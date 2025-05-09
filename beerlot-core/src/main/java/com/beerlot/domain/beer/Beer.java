package com.beerlot.domain.beer;

import com.beerlot.domain.brewery.Brewery;
import com.beerlot.domain.category.Category;
import com.beerlot.domain.common.entity.CreateAndUpdateDateTime;
import com.beerlot.domain.review.BuyFromConverter;
import com.beerlot.domain.review.Review;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "beer")
public class Beer extends CreateAndUpdateDateTime {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "volume", nullable = false)
    private Float volume;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brewery_id")
    private Brewery brewery;

    @Column(name = "calorie")
    private Integer calorie;

    @Column(name = "calorie_unit", nullable = false)
    private Integer calorieUnit;

    @Convert(converter = BuyFromConverter.class)
    @Column(name = "buy_from")
    private Set<String> buyFrom = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "beer")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "beer", cascade = CascadeType.PERSIST)
    private List<BeerInternational> beerInternationals = new ArrayList<>();

    @OneToMany(mappedBy = "beer")
    private List<BeerLike> beerLikes = new ArrayList<>();

    @Column(name = "like_count", columnDefinition = "int default 0")
    private long likeCount = 0L;

    @Column(name = "review_count", columnDefinition = "int default 0")
    private long reviewCount = 0L;

    @Column(name = "rate", columnDefinition = "float default 0.0")
    private float rate = 0F;

    public void likeBeer() {
        this.likeCount += 1;
    }

    public void unlikeBeer() {
        this.likeCount -= 1;
    }

    public void addReview() {
        this.reviewCount += 1;
    }

    public void removeReview() {
        this.reviewCount -= 1;
    }

    public void calculateRate(float rate) {
        this.rate = rate > 0 ? (this.rate * (reviewCount - 1) + rate) / reviewCount : (this.rate * (reviewCount + 1) + rate) / reviewCount;
    }

    public void addBuyFrom(String buyFrom) {
        this.buyFrom.add(buyFrom);
    }

    public void removeBuyFrom(String buyFrom) {
        this.buyFrom.remove(buyFrom);
    }

    @Builder
    public Beer(Long id, Float volume, Category category, String imageUrl, Set<String> buyFrom,
                OffsetDateTime createdAt, OffsetDateTime updatedAt, List<BeerInternational> beerInternationals,
                Integer calorie, Integer calorieUnit, Brewery brewery) {
        this.id = id;
        this.volume = volume;
        this.category = category;
        this.imageUrl = imageUrl;
        this.buyFrom = buyFrom;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.beerInternationals = beerInternationals;
        this.calorie = calorie;
        this.calorieUnit = calorieUnit;
        this.brewery = brewery;
    }
}

