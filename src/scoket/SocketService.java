package scoket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.ClassUtil;

public class SocketService {
	// 搭建服务器端
	public static void main(String[] args) throws IOException {
		SocketService socketService = new SocketService();
		// 1、a)创建一个服务器端Socket，即SocketService
		socketService.oneServer();
	}

	public void oneServer() {
		try {
			ServerSocket server = null;
			try {
				server = new ServerSocket(5209);
				// b)指定绑定的端口，并监听此端口。
				System.out.println("服务器启动成功");
				// 创建一个ServerSocket在端口5209监听客户请求
			} catch (Exception e) {
				System.out.println("没有启动监听：" + e);
				// 出错，打印出错信息
			}
			Socket socket = null;
			while (true) {
				try {
					socket = server.accept();
					// 2、调用accept()方法开始监听，等待客户端的连接
					// 使用accept()阻塞等待客户请求，有客户
					// 请求到来则产生一个Socket对象，并继续执行
				} catch (Exception e) {
					System.out.println("Error." + e);
					// 出错，打印出错信息
				}
				//获取客户端传过来的信息
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));		
				String str = in.readLine();
				//信息解析
				if (str != null && !str.equals("")){
				str=new String(str.getBytes("8859_1"),"GB2312"); 
				System.out.println("请求" + str);	
				String resource = str.substring(str.indexOf('/') + 1,
						str.lastIndexOf('/') - 5);
				System.out.println(resource);
				// 将字符串转成map
				Map<String, Object> map = StrMap(resource);
				// mvc
				transferFileHandle(socket, map);
				}
				//关闭scoket
				closeSocket(socket);
				continue;
			}
		} catch (Exception e) {// 出错，打印出错信息
			System.out.println("Error." + e);
		}

	}

	private void transferFileHandle(Socket client, Map<String, Object> map)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		// 获取controller下的所有类
		List<Class<?>> classes = ClassUtil.getClasses("controller");
		String methodName = map.get("path").toString();
		int no = 1;
		for (int i = 0; i < classes.size(); i++) {
			Object obj = classes.get(i).newInstance();
			// 获取类的所有方法
			Method[] methods = classes.get(i).getMethods();
			for (Method method : methods) {
				String methodNameTwo = method.getName();
				if (methodNameTwo.equals(methodName)) {
					no = 2;
					// 执行 
					method.invoke(obj, client, map);
				}
			}
		}
		if (no != 2) {
			FourHundredAndFour(client);
		}
	}

	public void index(Socket client, Map<String, Object> map) {
		try {
			PrintStream writer = new PrintStream(client.getOutputStream());
			writer.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
			writer.println("Connection:keep-alive");
			writer.println("Content-Type:application/json;charset=UTF-8");
			writer.println("Date:Sat, 26 Aug 2017 01:07:14 GMT");
			writer.println("Server:tomcat");
			writer.println("Transfer-Encoding:chunked");
			writer.println("Content-Length:" + map.toString().length());// 返回内容字节数
			writer.println();// 根据 HTTP 协议, 空行将结束头信息

			// FileInputStream fis = new FileInputStream(fileToSend);
			byte[] buf = map.toString().getBytes();
			// fis.read(buf);
			writer.write(buf);
			writer.close();
			// fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println(socket + "离开了HTTP服务器");
	}

	public Map<String, Object> StrMap(String str) {
		Map<String, Object> map = new HashMap<String, Object>();
		String strArr[] = str.split("[?]");
		map.put("path", strArr[0]);
		if (strArr.length > 1) {
			Map<String, Object> mapParameter = new HashMap<String, Object>();
			String Parameter = strArr[1];
			for (int i = 0; i < Parameter.split("&").length; i++) {
				mapParameter.put(Parameter.split("&")[i].split("=")[0],
						Parameter.split("&")[i].split("=")[1]);
			}
			map.put("Parameter", mapParameter);
		}
		return map;
	}

	public void FourHundredAndFour(Socket client) {
		try {
			PrintStream writer = new PrintStream(client.getOutputStream());
			writer.println("HTTP/1.0 404 OK");// 返回应答消息,并结束应答
			writer.close();
			// fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
