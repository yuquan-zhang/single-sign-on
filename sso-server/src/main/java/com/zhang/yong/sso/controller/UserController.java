package com.zhang.yong.sso.controller;

import com.zhang.yong.sso.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class UserController {
    private static Logger log = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @RequestMapping("index")
    public void index(){

    }

    @RequestMapping(value = "login",method=RequestMethod.GET)
    public ModelAndView loginGet(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("login");
        String subSystemUrl = request.getParameter("subSystemUrl");
        String errorMsg = request.getParameter("errorMsg");
        modelAndView.addObject("subSystemUrl",subSystemUrl);
        if(errorMsg != null) {
            modelAndView.addObject("errorMsg",URLDecoder.decode(errorMsg,"UTF-8"));
        }
        return modelAndView;
    }

    @RequestMapping(value = "login",method=RequestMethod.POST)
    @SuppressWarnings("unchecked")
    public void loginPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String subSystemUrl = request.getParameter("subSystemUrl");
        String msg = userService.login(username,password);
        if("true".equals(msg)){
            HttpSession session = request.getSession();
            ServletContext context = session.getServletContext();
            session.setAttribute("username",username);
            session.setAttribute("isLogin",true);
            String token = UUID.randomUUID().toString();
            session.setAttribute("token",token);
            HashSet<HttpSession> sessions = (HashSet<HttpSession>)context.getAttribute("sessionHashSet");
            if(sessions == null){
                sessions = new HashSet<HttpSession>();
            }
            //一个用户只允许一个会话存在，所以需关闭同一用户之前的所有会话
            HashSet<HttpSession> sessionSet = (HashSet<HttpSession>)sessions.clone();
            for (HttpSession s : sessionSet) {
                if(username != null && username.equals(s.getAttribute("username"))){
                    s.invalidate();
                    sessions.remove(s);
                }
            }
            sessions.add(session);
            context.setAttribute("sessionHashSet",sessions);
            if(subSystemUrl.contains("?")){
                subSystemUrl += "&token="+token;
            }else{
                subSystemUrl += "?token="+token;
            }
            response.sendRedirect(subSystemUrl);
        }else{
            String queryString = "subSystemUrl=" + subSystemUrl + "&errorMsg=" + URLEncoder.encode(msg,"UTF-8");
            response.sendRedirect(request.getContextPath() + "/login?" +  queryString);
        }
    }

    @RequestMapping(value = "token",method=RequestMethod.POST)
    @ResponseBody
    @SuppressWarnings("unchecked")
    public String validateToken(HttpServletRequest request){
        // 遍历所有注册会话，验证是否有该令牌存在
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        HashSet<HttpSession> sessions = (HashSet<HttpSession>)context.getAttribute("sessionHashSet");
        String token = request.getParameter("token");
        if(token != null && sessions != null){
            HashSet<HttpSession> sessionSet = (HashSet<HttpSession>)sessions.clone();
            for (HttpSession s : sessionSet) {
                try{
                    if(token.equals(s.getAttribute("token"))){
                        session = s;
                    }
                }catch (Exception e){
                    log.info(e.getMessage());
                    sessions.remove(s);
                }
            }
            context.setAttribute("sessionHashSet",sessions);
        }
        String globalToken = (String)session.getAttribute("token");
        //验证token令牌，如果一致，则说明令牌有效，在全局注册该子系统
        if(token != null && token.equals(globalToken)){
            String subSystemUrl = request.getParameter("subSystemUrl");
            HashSet<String> subSystems = (HashSet<String>)session.getAttribute("subSystems");
            if(subSystems == null){
                subSystems = new HashSet<String>();
            }
            subSystems.add(subSystemUrl.split("\\?")[0]);
            session.setAttribute("subSystems",subSystems);
            return "true";
        }
        return "false";
    }
}
