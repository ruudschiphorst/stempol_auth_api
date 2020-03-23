package nl.politie.predev.stempol.auth.api.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import nl.politie.predev.stempol.auth.api.util.AppConstants;

@Component
public class JwtTokenProvider extends BasicAuthenticationFilter  {

    public JwtTokenProvider(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    private String jwtSecret="JWTSuperSecretKey";
    //private int jwtExpirationInMs=604800000;	//7 dagen
    private int jwtExpirationInMs=900000;		//15 minuten

    public String generateToken(Authentication authentication) {

    	LdapUserDetailsImpl userPrincipal = (LdapUserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                
                .addClaims(generateClaims(userPrincipal.getAuthorities()))
                
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    
    private Map<String, Object> generateClaims(Collection<GrantedAuthority> authorities) {
    	Map<String, Object> retval = new HashMap<String, Object>();
    	boolean hasRoles=false;
    	String auths = "";
    	
    	for(GrantedAuthority ga : authorities){
    		if(hasRoles == true){
    			auths+=";";
    		}
    		hasRoles = true;
    		auths += ga.getAuthority();
    	}
    	retval.put(AppConstants.CLAIM_KEY_ROLES, auths);
    	
    	return retval;
    	
    }
    
    public String getUsernameFromJWT(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
        
    }
    
    public boolean validateToken(String authToken) {
        try {
        	Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody();
            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty.");
        }
        return false;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ") || !validateToken(header.replace("Bearer ", ""))) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }
    
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    	
    	String user = getUsernameFromJWT(request.getHeader("Authorization").replace("Bearer ", ""));
        if (user != null) {
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        return null;

    }
}