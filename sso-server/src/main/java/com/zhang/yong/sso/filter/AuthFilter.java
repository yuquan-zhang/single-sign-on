package com.zhang.yong.sso.filter;

import com.zhang.yong.sso.util.AuthUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

public class AuthFilter implements Filter {
    private String exclude;
    private Pattern[] excludePattern;//不过滤的url正则表达式数组

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.exclude = filterConfig.getInitParameter("exclude");
        if (this.exclude != null && this.exclude.length() > 0) {
            String[] arr = exclude.split(",");
            excludePattern = new Pattern[arr.length];
            for (int i = 0; i < arr.length; i++) {
                excludePattern[i] = Pattern.compile(arr[i]);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpSession session = request.getSession();
        if (AuthUtils.checkExclude(request.getRequestURI(), excludePattern)) {
            filterChain.doFilter(request, response);
            return;
        }
        //如果是token令牌认证请求，则遍历所有注册会话，验证是否有该令牌存在
        if(request.getRequestURI().contains("/token")){
            filterChain.doFilter(request, response);
            return;
        }
        String globalToken = (String)session.getAttribute("token");
        String subSystemUrl = request.getParameter("subSystemUrl");
        String queryString = "subSystemUrl=" + subSystemUrl;
        //如果是login登陆请求，则处理登陆逻辑
        if(request.getRequestURI().contains("/login")){
            Object isLogin = session.getAttribute("isLogin");
            if(isLogin != null && (Boolean)isLogin){ //如果是已登陆，则跳转子系统，附带返回token令牌
                if(subSystemUrl.contains("?")){
                    subSystemUrl += "&token="+globalToken;
                }else{
                    subSystemUrl += "?token="+globalToken;
                }
                response.sendRedirect(subSystemUrl);
            }else{
                //如果是未登陆,，则直接过滤
                filterChain.doFilter(request, response);
                return;
            }
        }
        //如果是logout登出请求，则处理登出逻辑
        if(request.getRequestURI().contains("/logout")){
            String token = request.getParameter("token");
            //验证token令牌有效性
            if(token != null && token.equals(globalToken)){
                //若token有效，则销毁全局会话，并且通过会话监听器注销所有子系统局部会话
                session.invalidate();
                //跳转登陆页面
                response.sendRedirect(request.getContextPath() + "/login?" +  queryString);
            }else{
                response.getWriter().write("{\"error\":\"token is invalid\"}");
            }
            return;
        }
        //其他请求一律视为非法请求
        response.getWriter().write("{\"error\":\"invalid request\"}");
    }

    @Override
    public void destroy() {

    }
}
