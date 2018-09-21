package com.zhang.yong.sso.filter;

import com.zhang.yong.sso.util.AuthUtils;
import com.zhang.yong.sso.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

public class AuthFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private String exclude;
    private Pattern[] excludePattern;//不过滤的url正则表达式数组
    private String authServer;
    private String loginPath = "/login";
    private String tokenPath = "/token";
    private String logoutPath = "/logout";
    private String homepage;
    private HashSet<HttpSession> sessionSet = new HashSet<>();
    private String contentType = "application/x-www-form-urlencoded";

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
        this.authServer = filterConfig.getInitParameter("authServer");
        this.loginPath = this.authServer + this.loginPath;
        this.tokenPath = this.authServer + this.tokenPath;
        this.logoutPath = this.authServer + this.logoutPath;
        this.homepage = filterConfig.getInitParameter("homepage");
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
        Object isLogin = session.getAttribute("isLogin");
        String logoutFlag = request.getParameter("logout");
        String token = (String)session.getAttribute("token");
        String globalToken = request.getParameter("token");
        //如果是已登陆，且
        if(isLogin != null && (Boolean)isLogin){
            if(logoutFlag == null){ //不是注销请求，则直接过滤
                filterChain.doFilter(request,response);
            }else{ //是注销请求，且：

                if(globalToken == null){ // 参数中无token令牌， 则请求注销全局会话
                    String logoutQuery = this.logoutPath + "?token=" + token
                            + "&logout=true&subSystemUrl=" + this.homepage;
                    response.sendRedirect(logoutQuery);
                }else{ // 参数中有token令牌，则是由全局发起的注销请求，应注销子系统局部会话
                    HashSet<HttpSession> sessions = (HashSet<HttpSession>)sessionSet.clone();
                    String flag = "false";
                    for(HttpSession s : sessions) { // 只注销拥有和全局相同token令牌的会话
                        if(globalToken.equals(s.getAttribute("token"))){
                            s.invalidate();
                            sessionSet.remove(s);
                            flag = "true";
                        }
                    }
                    response.getWriter().write(flag);
                }
            }
        }else{
            String subSystemUrl = request.getScheme()+"://"+request.getServerName()
                    +":"+request.getServerPort()+request.getRequestURI();
            if(request.getQueryString() != null) subSystemUrl += "?" + request.getQueryString();
            String queryString = "subSystemUrl=" + subSystemUrl;
            //如果是未登陆,
            if(globalToken == null) {//且token令牌为空，则跳转登陆界面
                response.sendRedirect(this.loginPath + "?" +  queryString);
            }else{ //且token令牌不为空，则验证token令牌
                Map<String,String> params = new HashMap<>();
                params.put("token",globalToken);
                params.put("subSystemUrl",subSystemUrl);
                String success = "false";
                try{
                    success = HttpUtils.post(this.tokenPath,params,this.contentType);
                }catch (Exception e){
                    logger.info(e.getMessage());
                }
                if("true".equals(success)) { //若验证通过，则创建局部会话，然后过滤
                    session.setAttribute("isLogin",true);
                    session.setAttribute("token",globalToken);
                    sessionSet.add(session);
                    filterChain.doFilter(request,response);
                }else{ // 若验证不通过，则返回token令牌失效提示
                    response.getWriter().write("token is invalid!");
                }
            }
        }
    }

    @Override
    public void destroy() {

    }
}
