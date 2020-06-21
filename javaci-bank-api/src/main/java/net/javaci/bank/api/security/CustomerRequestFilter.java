package net.javaci.bank.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

public class CustomerRequestFilter extends OncePerRequestFilter {

	private JwtTokenUtil jwtTokenUtil;
	private UserDetailsService userDetailsService;
	
	public CustomerRequestFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
		super();
		this.jwtTokenUtil = jwtTokenUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			String jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				logger.warn("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				logger.warn("JWT Token has expired");
			}
			//Once we get the token validate it.
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				// if token is valid configure Spring Security to manually set authentication
				if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					//usernamePasswordAuthenticationToken
					//		.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					// After setting the Authentication in the context, we specify
					// that the current user is authenticated. So it passes the Spring Security Configurations successfully.
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String" + request.getRequestURI());
		}


		chain.doFilter(request, response);
	}

}
