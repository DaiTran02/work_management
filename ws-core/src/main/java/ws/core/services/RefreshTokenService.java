package ws.core.services;

import ws.core.model.RefreshToken;

public interface RefreshTokenService {
	public RefreshToken findRefreshTokenByRefreshToken(String refreshToken);
	
	public boolean deleteRefreshToken(RefreshToken refreshToken);
	
	public RefreshToken saveRefreshToken(RefreshToken refreshToken);
	
	public RefreshToken createRefreshToken(String userId);
	
	public RefreshToken verifyExpiration(RefreshToken refreshToken);
}
