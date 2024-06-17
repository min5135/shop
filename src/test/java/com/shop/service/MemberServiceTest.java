package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*; //정적 임포트
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")

class MemberServiceTest {
    @Autowired
    MemberService memberService; //Autowired 붙으려면 MemberService 클래스에 @Service 때문
    @Autowired // Config에 @Bean때문에 사용가능
    PasswordEncoder passwordEncoder;
    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto(); //일반 자바처럼 만들어야겠다
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("인천시 부평구 경원대로 1366");
        memberFormDto.setPassword("1234");
        memberFormDto.setTel("01012345678");
        return Member.createMember(memberFormDto,passwordEncoder);
    }
    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(),savedMember.getEmail());
        assertEquals(member.getName(),savedMember.getName());
        assertEquals(member.getAddress(),savedMember.getAddress());
        assertEquals(member.getPassword(),savedMember.getPassword());
        assertEquals(member.getRole(),savedMember.getRole());
        assertEquals(member.getTel(),savedMember.getTel());
    }

    //중복확인
    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class,()->{
            memberService.saveMember(member2);
        });

        assertEquals("이미 가입된 회원입니다.",e.getMessage());
    }
}