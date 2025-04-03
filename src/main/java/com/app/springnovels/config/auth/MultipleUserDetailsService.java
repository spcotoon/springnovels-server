package com.app.springnovels.config.auth;


import com.app.springnovels.api.exception.InvalidSignInException;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MultipleUserDetailsService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    private final AuthorRepository authorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (isAuthor(username)) {
            return loadAuthor(username);
        } else {
            return loadMember(username);
        }
    }

    private boolean isAuthor(String username) {
        return username.endsWith("@spring.novels.author");
    }

    private boolean isMember(String username) {
        return !username.endsWith("@spring.novels.author");
    }



    private UserDetails loadAuthor(String email) {
        Author author = authorRepository.findByEmail(email).orElseThrow(InvalidSignInException::new);
        return new CustomUser(author.getId(), author.getEmail(), author.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_ARTIST")), author.getPenName());
    }

    private UserDetails loadMember(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(InvalidSignInException::new);
        return new CustomUser(member.getId(), member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")),  member.getNickname());
    }

}
