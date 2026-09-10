package com.kk.Filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.kk.utils.JwtUtil;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 跨域预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //获取请求路径
        String path = request.getRequestURI();
        //放行登录接口
        if ("/login".equals(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //放行上传后的静态资源（浏览器 <img> 等原生请求无法携带 token 头）
        if (path.startsWith("/upload/")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //获取请求头Token
        String token = request.getHeader("token");

        //判断Token是否存在，不存在则响应401状态码
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //Token存在则校验令牌，正确则放行，错误则响应401状态码
        try {
            JwtUtil.parseToken(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //校验通过放行
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
