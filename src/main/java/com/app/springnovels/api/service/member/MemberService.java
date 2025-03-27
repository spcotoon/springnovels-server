package com.app.springnovels.api.service.member;


import com.app.springnovels.api.controller.member.requestDto.MemberCreateRequest;
import com.app.springnovels.api.exception.AlreadyExistsEmailException;
import com.app.springnovels.api.exception.AlreadyExistsNicknameException;
import com.app.springnovels.api.exception.InvalidSignInException;
import com.app.springnovels.api.exception.NotExistMemberException;
import com.app.springnovels.api.service.member.request.MemberCreateServiceRequest;
import com.app.springnovels.api.service.member.request.MemberLoginServiceRequest;
import com.app.springnovels.api.service.member.response.MemberResponse;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public MemberResponse join(MemberCreateServiceRequest request) {

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsEmailException();
        }

        if (memberRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new AlreadyExistsNicknameException();
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        if (member.getDeletedAt() == null) {
            member.addWelcomeCoin();
        }

        Member savedMember = memberRepository.save(member);

        return MemberResponse.from(savedMember);
    }

    public MemberResponse getMe(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(NotExistMemberException::new);
        return MemberResponse.from(member);
    }

    public Authentication authenticateJwtUser(MemberLoginServiceRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(auth);
            return SecurityContextHolder.getContext().getAuthentication();
        } catch (BadCredentialsException e) {
            throw new InvalidSignInException();
        }
    }

}
