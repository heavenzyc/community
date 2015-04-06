<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.naryou.config.Uploader" %>
<%
String buildingId = request.getParameter("buildingId");
request.setCharacterEncoding("utf-8");
response.setCharacterEncoding("utf-8");
Uploader up = new Uploader(request);
//savePath上面带有商户id
up.setSavePath("upload/"+buildingId);
String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
up.setAllowFiles(fileType);
up.setMaxSize(20480); //单位KB
up.upload();
response.getWriter().print("{'original':'"+up.getOriginalName()+"','url':'"+up.getUrl()+"','title':'"+up.getTitle()+"','state':'"+up.getState()+"'}");
%>