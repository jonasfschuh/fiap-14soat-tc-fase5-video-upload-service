package br.com.fiap.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Reads X-Correlation-Id, X-Service-Order-Id and X-User-Id from incoming HTTP requests
 * and places them in the MDC so all log statements in this MS automatically
 * include these fields — enabling distributed tracing in New Relic.
 *
 * If X-Correlation-Id is absent (direct calls to this MS), a new UUID is generated.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpCorrelationLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(HttpCorrelationLoggingFilter.class);

    private static final String CORRELATION_ID_HEADER   = "X-Correlation-Id";
    private static final String SERVICE_ORDER_ID_HEADER = "X-Service-Order-Id";
    private static final String USER_ID_HEADER          = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId  = resolveCorrelationId(request);
        String serviceOrderId = request.getHeader(SERVICE_ORDER_ID_HEADER);
        String userId         = request.getHeader(USER_ID_HEADER);

        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        MDC.put("correlationId", correlationId);
        MDC.put("httpMethod", request.getMethod());
        MDC.put("requestPath", request.getRequestURI());
        if (serviceOrderId != null && !serviceOrderId.isBlank()) {
            MDC.put("serviceOrderId", serviceOrderId.trim());
        }
        if (userId != null && !userId.isBlank()) {
            MDC.put("userId", userId.trim());
        }

        long startMs = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startMs;
            log.info("[HTTP] method={} path={} status={} durationMs={} correlationId={}",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), durationMs, correlationId);
            MDC.remove("correlationId");
            MDC.remove("serviceOrderId");
            MDC.remove("userId");
            MDC.remove("httpMethod");
            MDC.remove("requestPath");
        }
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String id = request.getHeader(CORRELATION_ID_HEADER);
        return (id != null && !id.isBlank()) ? id.trim() : UUID.randomUUID().toString();
    }
}
