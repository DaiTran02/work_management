package ws.core.service.impl;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.advice.TokenRefreshException;
import ws.core.model.RefreshToken;
import ws.core.respository.RefreshTokenRepository;
import ws.core.security.JwtTokenProvider;
import ws.core.services.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Override
	public RefreshToken findRefreshTokenByRefreshToken(String refreshToken) {
		Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
		if(findRefreshToken.isPresent()) {
			return findRefreshToken.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy refreshToken ["+refreshToken+"]");
	}

	@Override
	public boolean deleteRefreshToken(RefreshToken refreshToken) {
		refreshTokenRepository.delete(refreshToken);
		return true;
	}

	@Override
	public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
		return refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken createRefreshToken(String userId) {
		RefreshToken refreshToken=new RefreshToken();
		refreshToken.setUserId(userId);
		refreshToken.setExpiryTime(new Date(System.currentTimeMillis() + jwtTokenProvider.getExpiryTime()));
		refreshToken.setRefreshToken(UUID.randomUUID().toString());
		return saveRefreshToken(refreshToken);
	}

	@Override
	public RefreshToken verifyExpiration(RefreshToken refreshToken) {
		if(refreshToken.getExpiryTimeLong()<System.currentTimeMillis()) {
			deleteRefreshToken(refreshToken);
			throw new TokenRefreshException("RefreshToken ["+refreshToken.getRefreshToken()+"] hết hạn");
		}
		return refreshToken;
	}
}
