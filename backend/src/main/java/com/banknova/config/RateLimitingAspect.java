package com.banknova.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.banknova.exception.RateLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RateLimitingAspect {

    private final ConcurrentHashMap<String, UserRateLimit> rateLimits = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String principal = getRequestPrincipal(request);

        String key = principal + ":" + joinPoint.getSignature().getName();
        UserRateLimit limit = rateLimits.computeIfAbsent(key, k -> new UserRateLimit());

        if (limit.isExceeded(rateLimited.requests(), rateLimited.timeWindowSeconds())) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }

        limit.increment();
        return joinPoint.proceed();
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getRequestPrincipal(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getName() != null && !"anonymousUser".equals(authentication.getName())) {
            return "user:" + authentication.getName();
        }

        return "ip:" + getClientIP(request);
    }

    private static class UserRateLimit {
        private final AtomicInteger requests = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        public synchronized boolean isExceeded(int maxRequests, int timeWindowSeconds) {
            long currentTime = System.currentTimeMillis();
            long windowDuration = timeWindowSeconds * 1000L;

            if (currentTime - windowStart > windowDuration) {
                requests.set(0);
                windowStart = currentTime;
            }

            return requests.get() >= maxRequests;
        }

        public void increment() {
            requests.incrementAndGet();
        }
    }
}
