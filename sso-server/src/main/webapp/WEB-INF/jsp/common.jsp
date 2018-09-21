<%@ taglib prefix="c" uri="/WEB-INF/tld/c-rt.tld"%>
<%@ taglib prefix="fmt" uri="/WEB-INF/tld/fmt-rt.tld"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>
<c:set var="basePath" value="<%=basePath %>"/>
<script>
    var PATH = {
        VERSION : "1.0",
        BASEPATH : "<%=request.getContextPath()%>",
        SESSIONID: "<%=request.getSession().getId()%>"
    };
</script>
