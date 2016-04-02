package com.rhwayfun.sso.client;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接收服务端发送的通知
 */
@WebServlet("/notice/*")
public class ServerNoticeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // notice后路径为notice类型，如/notice/timeout，则当前通知为timeout类型
        String uri = request.getRequestURI();
        String cmd = uri.substring(uri.lastIndexOf("/") + 1);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");

        switch (cmd) {
        case "timeout": {
            String vt = request.getParameter("vt");
            int tokenTimeout = Integer.parseInt(request.getParameter("tokenTimeout"));
            Date expries = TokenManager.timeout(vt, tokenTimeout);
            response.getWriter().write(expries == null ? "" : String.valueOf(expries.getTime()));
            break;
        }
        case "logout": {
            String vt = request.getParameter("vt");
            TokenManager.invalidate(vt);
            response.getWriter().write("true");
            break;
        }
        case "shutdown":
            TokenManager.destroy();
            response.getWriter().write("true");
            break;
        }
    }

}
