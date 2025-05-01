package com.rahul.gateway.filter;

import com.rahul.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            
            // Check if path is in excluded paths
            if (config.getExcludedPaths() != null) {
                for (String excludedPath : config.getExcludedPaths()) {
                    if (path.contains(excludedPath)) {
                        return chain.filter(exchange);
                    }
                }
            }

            if (isAuthMissing(exchange)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = getAuthHeader(exchange).replace("Bearer ", "");

            if (jwtUtil.isTokenExpired(token)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            exchange.getRequest().mutate()
                    .header("X-Auth-User-ID", jwtUtil.extractUserId(token))
                    .build();

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    private String getAuthHeader(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    private boolean isAuthMissing(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    public static class Config {
        private String[] excludedPaths;

        public String[] getExcludedPaths() {
            return excludedPaths;
        }

        public void setExcludedPaths(String[] excludedPaths) {
            this.excludedPaths = excludedPaths;
        }
    }
}
