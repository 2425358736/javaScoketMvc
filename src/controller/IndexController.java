package controller;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import Util.ClassUtil;

public class IndexController {
    public void index(Socket client,Map<String,Object> map){
    	//输出
    	Map<String,Object> user = new HashMap<String,Object>();
    	user.put("name", "刘志强");
    	user.put("age", 12);
    	map.put("user", user);
    	ClassUtil.print(client,map,"view/index");
    }
    public void indexTwo(Socket client,Map<String,Object> map){
    	//输出
    	ClassUtil.printJson(client,map);
    }
}
