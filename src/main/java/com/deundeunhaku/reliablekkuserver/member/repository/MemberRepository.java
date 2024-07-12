package com.deundeunhaku.reliablekkuserver.member.repository;

import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByPhoneNumber(String phoneNumber);


}
