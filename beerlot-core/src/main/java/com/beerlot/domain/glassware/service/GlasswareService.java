package com.beerlot.domain.glassware.service;

import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.glassware.Glassware;
import com.beerlot.domain.glassware.dto.response.GlasswareResponse;
import com.beerlot.domain.glassware.repository.GlasswareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GlasswareService {

    private final GlasswareRepository glasswareRepository;

    public List<GlasswareResponse> findGlasswareByCategoryId(Long categoryId, LanguageType language) {
        List<Glassware> glasswares = glasswareRepository.findByCategoryId(categoryId);
        
        return glasswares.stream()
                .map(glassware -> GlasswareResponse.of(language, glassware))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
