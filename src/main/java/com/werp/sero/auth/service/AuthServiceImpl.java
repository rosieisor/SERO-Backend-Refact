package com.werp.sero.auth.service;

import com.werp.sero.auth.dto.LoginRequestDTO;
import com.werp.sero.auth.dto.LoginResponseDTO;
import com.werp.sero.auth.exception.LoginFailedException;
import com.werp.sero.employee.command.domain.aggregate.ClientEmployee;
import com.werp.sero.employee.command.domain.aggregate.Employee;
import com.werp.sero.employee.command.domain.repository.ClientEmployeeRepository;
import com.werp.sero.employee.command.domain.repository.EmployeeRepository;
import com.werp.sero.permission.command.domain.repository.EmployeePermissionRepository;
import com.werp.sero.security.authenticationToken.ClientEmployeeAuthenticationToken;
import com.werp.sero.security.authenticationToken.EmployeeAuthenticationToken;
import com.werp.sero.security.dto.JwtToken;
import com.werp.sero.security.enums.Type;
import com.werp.sero.security.jwt.JwtTokenProvider;
import com.werp.sero.security.jwt.exception.InvalidTokenException;
import com.werp.sero.security.userdetails.CustomUserDetails;
import com.werp.sero.security.userdetails.JwtUserDetails;
import com.werp.sero.util.CookieUtil;
import com.werp.sero.util.HeaderUtil;
import com.werp.sero.util.RedisUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String GRANT_TYPE = "Bearer";

    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeRepository employeeRepository;
    private final ClientEmployeeRepository clientEmployeeRepository;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @Override
    public LoginResponseDTO login(final LoginRequestDTO requestDTO, final HttpServletResponse response,
                                  final Type type) {
        try {
            Authentication authenticationToken = (type.equals(Type.EMPLOYEE))
                    ? new EmployeeAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword())
                    : new ClientEmployeeAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword());

            authenticationToken = authenticationManager.authenticate(authenticationToken);

            final JwtToken accessToken = jwtTokenProvider.generateAccessToken(authenticationToken);

            final JwtToken refreshToken = jwtTokenProvider.generateRefreshToken(authenticationToken);

            redisUtil.setData(REFRESH_TOKEN_PREFIX + requestDTO.getEmail(), refreshToken.getToken(),
                    refreshToken.getExpirationTime(), TimeUnit.MILLISECONDS);

            cookieUtil.generateRefreshTokenCookie(response, refreshToken);

            return new LoginResponseDTO(accessToken.getToken(), GRANT_TYPE, accessToken.getAuthorities());
        } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
            throw new LoginFailedException();
        }
    }

    @Transactional
    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            cookieUtil.deleteRefreshTokenCookie(response);

            final String accessToken = HeaderUtil.extractAccessTokenFromHeader(request);

            jwtTokenProvider.validateToken(accessToken);

            final String email = jwtTokenProvider.extractEmail(accessToken);

            redisUtil.deleteData(REFRESH_TOKEN_PREFIX + email);
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public LoginResponseDTO reissue(final String cookieRefreshToken, final HttpServletResponse response,
                                    final Type type) {
        jwtTokenProvider.validateToken(cookieRefreshToken);

        final String email = jwtTokenProvider.extractEmail(cookieRefreshToken);

        final String storedRefreshToken = redisUtil.getData(REFRESH_TOKEN_PREFIX + email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(cookieRefreshToken)) {
            throw new InvalidTokenException();
        }

        final CustomUserDetails userDetails = getUserDetailsForReissue(email, type);

        final Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        final JwtToken accessToken = jwtTokenProvider.generateAccessToken(authentication);

        final JwtToken refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        redisUtil.setData(REFRESH_TOKEN_PREFIX + email, refreshToken.getToken(),
                refreshToken.getExpirationTime(), TimeUnit.MILLISECONDS);

        cookieUtil.generateRefreshTokenCookie(response, refreshToken);

        return new LoginResponseDTO(accessToken.getToken(), GRANT_TYPE, accessToken.getAuthorities());
    }

    private CustomUserDetails getUserDetailsForReissue(final String email, final Type type) {
        if (type == Type.EMPLOYEE) {
            return buildFromEmployee(employeeRepository.findByEmailAndStatus(email, "ES_ACT")
                    .orElseThrow(InvalidTokenException::new));
        }

        return buildFromClientEmployee(clientEmployeeRepository.findByEmail(email)
                .orElseThrow(InvalidTokenException::new));
    }

    private CustomUserDetails buildFromEmployee(final Employee employee) {
        final List<String> permissions = employeePermissionRepository.findPermissionCodeByEmployee(employee);

        return new JwtUserDetails(employee.getId(), employee.getEmail(), null, Type.EMPLOYEE, permissions);
    }

    private CustomUserDetails buildFromClientEmployee(final ClientEmployee clientEmployee) {
        return new JwtUserDetails(clientEmployee.getId(), clientEmployee.getEmail(), clientEmployee.getClient().getId(),
                Type.CLIENT_EMPLOYEE, List.of("AC_CLI"));
    }
}