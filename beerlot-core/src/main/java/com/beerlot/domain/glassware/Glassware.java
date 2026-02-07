package com.beerlot.domain.glassware;

import com.beerlot.domain.category.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "glassware")
public class Glassware {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "glassware")
    private List<GlasswareInternational> glasswareInternationals = new ArrayList<>();

    @ManyToMany(mappedBy = "glasswares")
    private Set<Category> categories = new HashSet<>();

}
