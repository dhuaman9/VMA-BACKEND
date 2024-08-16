package pe.gob.sunass.vma.security.jwt;

import java.security.Key;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pe.gob.sunass.vma.model.Usuario;
import pe.gob.sunass.vma.security.dto.JwtTokenDTO;

@Component
public class JWTProvider {

	@Value("${security.jwt.expiration-minutes}")
    private long EXPIRATION_MINUTES;

    @Autowired
    JWTBean jwtBean;
    
    public String generateToken(Usuario user) {

        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date( issuedAt.getTime() + (EXPIRATION_MINUTES * 60 * 1000) );

        Map<String, Object> extraClaims = new HashMap<>();
        
        extraClaims = this.generateExtraClaims(user);
        
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUserName())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key generateKey(){
        byte[] secretAsBytes = Decoders.BASE64.decode(jwtBean.getSecretKey());
        return Keys.hmacShaKeyFor(secretAsBytes);
    }

    public String extractUsername(String jwt){
    	
    	Claims myClaims = extractAllClaims(jwt);
    	
    	return myClaims.getExpiration().getTime() - myClaims.getIssuedAt().getTime() == (EXPIRATION_MINUTES * 60 * 1000) ? myClaims.getSubject() : "";
    }

    public String extractUsernameFromExpired(String jwtExpired) throws ParseException{
    	
    	JWT jwt = JWTParser.parse(jwtExpired);
    	
    	JWTClaimsSet claims = jwt.getJWTClaimsSet();
        
    	return claims.getSubject();
    }
    
    private Claims extractAllClaims(String jwt){
    	
    	return Jwts.parserBuilder().setSigningKey(generateKey()).build()
                .parseClaimsJws(jwt).getBody();
                
    }
    
    public boolean isEnabledToRefresh(String token) throws ParseException{
    	
    	JWT jwt = JWTParser.parse(token);
    	JWTClaimsSet claims = jwt.getJWTClaimsSet();
    	
    	Date isNow          = new Date(System.currentTimeMillis());
    	
    	Date limitToRefresh = new Date(claims.getIssueTime().getTime() + (EXPIRATION_MINUTES * 2 * 60 * 1000) );
    	
    	return isNow.getTime() <= limitToRefresh.getTime() ? true : false;
    }
    
    public String refreshToken(JwtTokenDTO jwtTokenDto, Usuario usuarioRefresh) throws ParseException{
    	
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date( issuedAt.getTime() + (EXPIRATION_MINUTES * 60 * 1000) );

        try {
            Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(jwtTokenDto.getJwt()).getBody();
        }
        catch(io.jsonwebtoken.ExpiredJwtException e) {
        
        	JWT jwt = JWTParser.parse(jwtTokenDto.getJwt());
        	JWTClaimsSet claims = jwt.getJWTClaimsSet();
            String username = claims.getSubject();
            
            Map<String, Object> extraClaims = new HashMap<>();
            
            extraClaims = this.generateExtraClaims(usuarioRefresh);
        	
	        return Jwts.builder()
	                .setClaims(extraClaims)
	                .setSubject(username)
	                .setIssuedAt(issuedAt)
	                .setExpiration(expiration)
	                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
	                .signWith(generateKey(), SignatureAlgorithm.HS256)
	                .compact();
        }
        return null;
    }
    
    private Map<String, Object> generateExtraClaims(Usuario user) {

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("shortname", user.getFullName());
        extraClaims.put("role", user.getRole().getAuth());
        extraClaims.put("permissions", user.getAuthorities());
        extraClaims.put("username", user.getUsername());

        return extraClaims;
    }
    
    
}
