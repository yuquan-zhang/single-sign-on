package com.zhang.yong.sso.listener;

import com.zhang.yong.sso.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashSet;

public class LogoutListener implements HttpSessionListener {
    private static Logger log = LoggerFactory.getLogger(LogoutListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {}

    @Override
    @SuppressWarnings("unchecked")
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        //通过httpClient向所有注册系统发送注销请求
        HttpSession session = httpSessionEvent.getSession();
        String globalToken = (String)session.getAttribute("token");
        HashSet<String> subSystems = (HashSet<String>)session.getAttribute("subSystems");
        if(subSystems != null){
            for(String subSystem : subSystems) {
                String subSysUrl = "";
                try{
                    if(subSysUrl.contains("?")){
                        subSysUrl = subSystem + "&logout=true&token=" + globalToken;
                    }else{
                        subSysUrl = subSystem + "?logout=true&token=" + globalToken;
                    }
                    HttpUtils.get(subSysUrl);
                }catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        }
        // 移除已失效的session
        ServletContext context = session.getServletContext();
        HashSet<HttpSession> sessions = (HashSet<HttpSession>)context.getAttribute("sessionHashSet");
        sessions.remove(session);
        context.setAttribute("sessionHashSet",sessions);
    }
}
