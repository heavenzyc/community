<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.servlet.ServletContext"%>
<%@ page import="javax.servlet.http.HttpServletRequest"%>
<%@ page import="com.naryou.util.ConfigUtils"%>
<%@ page import="com.naryou.config.LocalConfigUtils"%>
<% 
	String buildingId = request.getParameter("buildingId");
    //仅做示例用，请自行修改
	String path = "upload/"+buildingId;
	String imgStr ="";
	//文件保存地址应该是
	String realpath = getPerTangRealPath()+"/"+path;
	List<File> files = getFiles(realpath,new ArrayList());
	for(File file :files ){
		String[] filevale = file.getPath().split("img");
		imgStr += LocalConfigUtils.IMAGE_SERVER_PATH+filevale[1]+"ue_separate_ue";
	}
	if(imgStr!=""){
        imgStr = imgStr.substring(0,imgStr.lastIndexOf("ue_separate_ue")).replace(File.separator, "/").trim();
    }
	out.print(imgStr);		
%>
<%!
public List getFiles(String realpath, List files) {
	File realFile = new File(realpath);
	if (realFile.isDirectory()) {
		File[] subfiles = realFile.listFiles();
		for(File file :subfiles ){
			if(file.isDirectory()){
				getFiles(file.getAbsolutePath(),files);
			}else{
				if(!getFileType(file.getName()).equals("")) {
					files.add(file);
				}
			}
		}
	}
	return files;
}

public String getRealPath(HttpServletRequest request,String path){
	ServletContext application = request.getSession().getServletContext();
	String str = application.getRealPath(request.getServletPath());
	return new File(str).getParent();
}
/**
PerTang的图片保存地址真实地址
*/
public String getPerTangRealPath(){
	String str = LocalConfigUtils.IMAGE_SERVER_FILEPATH;
	return new File(str).getPath();
}

public String getFileType(String fileName){
	String[] fileType = {".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"};
	Iterator<String> type = Arrays.asList(fileType).iterator();
	while(type.hasNext()){
		String t = type.next();
		if(fileName.toLowerCase().endsWith(t)){
			return t;
		}
	}
	return "";
}
%>