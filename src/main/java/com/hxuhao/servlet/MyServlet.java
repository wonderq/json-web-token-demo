package com.hxuhao.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hxuhao.model.User;
import com.hxuhao.utils.JWTUtil;

import io.jsonwebtoken.Claims;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private HashMap<Integer,User> users = new HashMap<>();
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		users.put(Integer.valueOf(1), new User(1,"test1","123","测试用户1"));
		users.put(Integer.valueOf(2), new User(2,"test2","123","测试用户2"));
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		if(request.getMethod().equals("POST")){
			doPost(request, response);
		}else{
			doGet(request, response);
		}
	}

	/**
	 * 查看信息
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		// 验证用户
		Cookie[] cookies =  request.getCookies();
		//User user=null;
		String username = null;
		if(cookies!=null){
			for(int i=0;i<cookies.length;i++){
				System.out.println(cookies[i].getName() + " : " + cookies[i].getValue());
				if(cookies[i].getName().equals("JWT")){
					Cookie cookie = cookies[i];
					try {
						// 检查token
						Claims  claims = JWTUtil.parseJWT(cookie.getValue());
						username = claims.getSubject();
						System.out.println("name : " + username);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if(username!=null){
			request.setAttribute("username", username);
			request.getRequestDispatcher("../info.jsp").forward(request, response);
		}else{
			//System.out.println("SendRedirect");
			response.sendRedirect("../login.jsp");
		}
	}

	/**
	 * 登录
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		System.out.println(account + " : " + password);
		String token = ""; 
		for(Entry<Integer, User> item : users.entrySet()){
			User u = item.getValue();
			if(u.getAccount().equals(account)
					&&u.getPassword().equals(password)){
				try {
					System.out.println(u.getName());
					token = JWTUtil.createJWT(String.valueOf(u.getId()), u.getName(), 1000*60*10);
					// 将token放进Cookie
					Cookie cookie = new Cookie("JWT", token);
					cookie.setPath("/");
					// 过期时间设为10min
					cookie.setMaxAge(60*10);
					response.addCookie(cookie);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		PrintWriter pw = response.getWriter();
		if(!token.equals("")){
			System.out.println(token);
			pw.print("login succeed : " + token);
		}
		else{
			pw.print("login failed : error account or password");
		}
		pw.flush();
		pw.close();
	}

}
