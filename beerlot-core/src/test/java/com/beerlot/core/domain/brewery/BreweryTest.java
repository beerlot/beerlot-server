package com.beerlot.core.domain.brewery;

import com.beerlot.domain.brewery.Brewery;
import com.beerlot.domain.brewery.BreweryInternational;
import com.beerlot.domain.brewery.BreweryInternationalId;
import com.beerlot.domain.common.entity.LanguageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BreweryTest {

    @Test
    @DisplayName("Brewery의 getName은 International 에서 찾아 반환한다.")
    void getValidBreweryName() {
        // given
        BreweryInternational breweryFirst =
                new BreweryInternational(new BreweryInternationalId(1L, LanguageType.KR), null, "FIRST", "");
        BreweryInternational brewerySecond =
                new BreweryInternational(new BreweryInternationalId(2L, LanguageType.EN), null, "SECOND", "");

        Brewery brewery = new Brewery(1L, List.of(breweryFirst, brewerySecond), null, null);

        // when
        String breweryName = brewery.getName(LanguageType.EN);

        // then
        assertThat(breweryName).isEqualTo(brewerySecond.getName());
    }

    @Test
    @DisplayName("Brewery International이 없을 경우 빈 이름을 반환한다.")
    void getEmptyBreweryNameWhenInternationalIsNotExist() {
        // given
        BreweryInternational breweryFirst =
                new BreweryInternational(new BreweryInternationalId(1L, LanguageType.KR), null, "FIRST", "");

        Brewery brewery = new Brewery(1L, List.of(breweryFirst), null, null);

        // when
        String breweryName = brewery.getName(LanguageType.EN);

        // then
        assertThat(breweryName).isEmpty();
    }

}
