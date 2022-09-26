package com.beerlot.core.domain.member.repository;

import com.beerlot.core.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
