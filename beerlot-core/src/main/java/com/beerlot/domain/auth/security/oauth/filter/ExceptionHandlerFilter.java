package com.beerlot.domain.auth.security.oauth.filter;

import com.beerlot.exception.ConflictException;
import com.beerlot.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e){
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (ConflictException e) {
            setErrorResponse(response, HttpStatus.CONFLICT, e.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) {
        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try{
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorMessage(status.value(), message)));
        }catch (IOException e2){
            e2.printStackTrace();
        }
    }

    @Getter
    @AllArgsConstructor
    static class ErrorMessage {
        int status;
        String message;
    }
}
