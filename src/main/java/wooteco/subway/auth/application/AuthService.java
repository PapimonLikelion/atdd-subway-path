package wooteco.subway.auth.application;

import org.springframework.stereotype.Service;
import wooteco.subway.auth.dto.TokenRequest;
import wooteco.subway.auth.dto.TokenResponse;
import wooteco.subway.auth.infrastructure.JwtTokenProvider;
import wooteco.subway.auth.ui.AuthorizationException;
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
        validateLogin(tokenRequest);
        final Member member = memberDao.findByEmail(tokenRequest.getEmail()).get();
        final String memberId = String.valueOf(member.getId());
        final String accessToken = jwtTokenProvider.createToken(memberId);
        return new TokenResponse(accessToken);
    }

    private void validateLogin(TokenRequest tokenRequest) {
        final String email = tokenRequest.getEmail();
        final String password = tokenRequest.getPassword();
        final Member member = memberDao.findByEmail(email)
                .orElseThrow(NoSuchMemberException::new);
        if (!member.isSamePassword(password)) {
            throw new AuthorizationException();
        }
    }
}
