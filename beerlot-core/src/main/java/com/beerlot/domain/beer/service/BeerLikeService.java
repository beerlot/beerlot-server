package com.beerlot.domain.beer.service;

import com.beerlot.domain.beer.Beer;
import com.beerlot.domain.beer.BeerLike;
import com.beerlot.domain.beer.repository.BeerLikeRepository;
import com.beerlot.domain.beer.repository.BeerRepository;
import com.beerlot.domain.member.Member;
import com.beerlot.domain.member.repository.MemberRepository;
import com.beerlot.domain.member.service.MemberService;
import com.beerlot.exception.ConflictException;
import com.beerlot.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BeerLikeService {

    private final MemberService memberService;

    @Autowired
    private BeerLikeRepository beerLikeRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private BeerService beerService;

    @Autowired
    private MemberRepository memberRepository;

    public void likeBeer(String oauthId, Long beerId) {
        Beer beer = beerService.findBeerById(beerId);
        Member member = memberService.findMemberByOauthId(oauthId);
        checkBeerLikeExist(beerId, member.getId(), true);
        BeerLike beerLike = new BeerLike(beer, member);
        beerLikeRepository.save(beerLike);
    }

    public void unlikeBeer(String oauthId, Long beerId) {
        Beer beer = beerService.findBeerById(beerId);
        Member member = memberService.findMemberByOauthId(oauthId);
        checkBeerLikeExist(beerId, member.getId(), false);
        beerLikeRepository.deleteByBeer_IdAndMember_Id(beerId, member.getId());
        beer.unlikeBeer();
    }

    private void checkBeerExist(Long beerId) {
        if (!beerRepository.existsById(beerId)) {
            throw new NoSuchElementException(ErrorMessage.BEER__NOT_EXIST.getMessage());
        }
    }

    private void checkBeerLikeExist(Long beerId, Long memberId, boolean isPositive) {
        if (isPositive && beerLikeRepository.existsByBeer_IdAndMember_Id(beerId, memberId)) {
            throw new ConflictException(ErrorMessage.BEER_LIKE__CONFLICT.getMessage());
        } else if (!isPositive && !beerLikeRepository.existsByBeer_IdAndMember_Id(beerId, memberId)) {
            throw new NoSuchElementException(ErrorMessage.BEER_LIKE__NOT_FOUND.getMessage());
        }
    }

    public List<Beer> getLikedBeers(Long memberId) {
        return beerLikeRepository.findByMember_Id(memberId).stream().map(BeerLike::getBeer).collect(Collectors.toList());
    }
}
