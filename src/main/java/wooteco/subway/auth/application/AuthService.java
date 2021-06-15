package wooteco.subway.auth.application;

import org.springframework.stereotype.Service;
import wooteco.subway.auth.dto.LoginMember;
import wooteco.subway.auth.dto.TokenRequest;
import wooteco.subway.auth.dto.TokenResponse;
import wooteco.subway.auth.exception.AuthorizationException;
import wooteco.subway.auth.infrastructure.JwtTokenProvider;
import wooteco.subway.member.dao.MemberDao;
import wooteco.subway.member.domain.Member;

@Service
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDao memberDao;

    public AuthService(JwtTokenProvider jwtTokenProvider, MemberDao memberDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberDao = memberDao;
    }

    public TokenResponse createToken(TokenRequest tokenRequest) {
        Member member = findMember(tokenRequest);
        if (member.isInvalidPassword(tokenRequest.getPassword())) {
            throw new AuthorizationException("로그인 실패입니다.");
        }
        String accessToken = jwtTokenProvider.createToken(String.valueOf(member.getId()));
        return new TokenResponse(accessToken);
    }

    private Member findMember(TokenRequest tokenRequest) {
        return memberDao.findByEmail(tokenRequest.getEmail())
                .orElseThrow(() -> new AuthorizationException("로그인 실패입니다."));
    }

    public LoginMember findLoginMemberByToken(String token) {
        Long id = Long.valueOf(jwtTokenProvider.getPayload(token));
        return new LoginMember(memberDao.findById(id)
                .orElseThrow(() ->
                        new AuthorizationException("존재하지 않는 회원입니다.")));
    }

    public void validateToken(String token) {
        if (!jwtTokenProvider.isValidToken(token)) {
            throw new AuthorizationException("유효하지 않은 토큰입니다.");
        }
    }
}
