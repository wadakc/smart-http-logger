package jp.springbootreference.smarthttplogger;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class SmartLoggerFilter extends OncePerRequestFilter {

    @Value("#{'${smartlog.header.secrets:null}'.split(',')}")
    private List<String> secretHeaders;


    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        LogCacheObject cache = new LogCacheObject();
        try {
            beforeRequest(cache,request, response);
            filterChain.doFilter(request, response);
        }
        finally {
            afterRequest(cache, request, response);
            response.copyBodyToResponse();
            LoggingPrinter.logging(cache, secretHeaders);
        }
    }

    protected void beforeRequest(LogCacheObject logCache, ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled()) {
            logRequestHeader(logCache, request);
        }
    }

    protected void afterRequest(LogCacheObject logCache, ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled()) {
            logRequestBody(logCache, request);
            logResponse(logCache, response);
        }
    }

    private static void logRequestHeader(LogCacheObject logCache, ContentCachingRequestWrapper request) {
        logCache.setMethod(request.getMethod());
        String queryString = request.getQueryString();
        if (queryString == null) {
            logCache.setRequestURl(request.getRequestURI());
        } else {
            logCache.setRequestURl(request.getRequestURI()+ "?"+queryString);
        }
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                Collections.list(request.getHeaders(headerName)).forEach(headerValue ->{
                            logCache.setRequestHeader(headerName,headerValue);
                })
        );
    }

    private static void logRequestBody(LogCacheObject logCache, ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            MediaType mediaType = MediaType.valueOf(request.getContentType());
            boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
            if (visible) {
                try {
                    String contentString = new String(content, request.getCharacterEncoding());
                    logCache.setRequestBody(contentString);
                } catch (UnsupportedEncodingException e) {
                }
            } else {
            }
        }
    }

    private static void logResponse(LogCacheObject logCache, ContentCachingResponseWrapper response) {
        logCache.setResponseStatus(response.getStatus());
        response.getHeaderNames().forEach(headerName ->
                response.getHeaders(headerName).forEach(headerValue -> {
                            logCache.setResponseHeader(headerName,headerValue);
                        })
        );

        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            MediaType mediaType = MediaType.valueOf(response.getContentType());
            boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
            if (visible) {
                try {
                    String contentString = new String(content, response.getCharacterEncoding());
                    logCache.setResponseBody(contentString);
                } catch (UnsupportedEncodingException e) {
                }
            }
        }
    }



    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}