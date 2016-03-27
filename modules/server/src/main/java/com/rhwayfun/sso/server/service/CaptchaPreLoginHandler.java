package com.rhwayfun.sso.server.service;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by rhwayfun on 16-3-27.
 * 图形验证码处理程序，用于生成图形验证码
 */
public class CaptchaPreLoginHandler implements  IPreLoginHandler {

    //验证码的值域
    private static final String CODES = "0123456789";
    //验证码的长度
    private static final int LEN = 4;

    /**
     * 登陆前验证码预处理程序
     * @param session
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> handle(HttpSession session, HttpServletResponse response) throws Exception {
        //返回的map
        Map<String,Object> res = new HashMap<>();
        generateImage(session,response);
        //把生成的png图片放入map中
        /*res.put("imgData", "data:image/JPEG;base64,"
                + Base64.getEncoder().encodeToString(generateImage(code,response)));*/
        return res;
    }

    /**
     * 生成png图片
     * @return
     */
    private void generateImage(HttpSession session,HttpServletResponse response) throws Exception {
        //生成的四位随机码
        String code = randomCode();
        session.setAttribute(SESSION_ATTR_NAME, code);
        //设置响应头无缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //验证码的宽度
        final int width = 75;
        final int height = 30;
        //使用图像缓冲区绘制图像
        BufferedImage bufferImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        //得到缓冲区的画笔
        Graphics2D g = bufferImage.createGraphics();
        //设置背景
        g.setColor(Color.WHITE);
        g.fillRect(0,0,width,height);
        //设置字体的颜色
        g.setColor(Color.gray);
        g.setFont(new Font("黑体",Font.BOLD,25));
        //绘制10条干扰线
        Random ran = new Random();
        for (int i = 0; i < 10; i++){
            int x1 = ran.nextInt(width);
            int y1 = ran.nextInt(height);
            int x2 = ran.nextInt(width);
            int y2 = ran.nextInt(height);
            g.drawLine(x1,y1,x2,y2);
        }
        //写四位的验证码
        for (int i = 0; i < LEN; i++){
            g.drawString(String.valueOf(code.charAt(i)),5 + 16 * i,25);
        }
        //释放画笔的句柄
        g.dispose();

        ServletOutputStream output;
        try {
            output = response.getOutputStream();
            ImageIO.write(bufferImage,"JPEG",output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成四位随机码
     * @return
     * @throws Exception
     */
    private String randomCode() throws Exception{
        //随机数计数器
        Random ran = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < LEN; i++){
            builder.append(CODES.charAt(ran.nextInt(CODES.length())));
        }
        return builder.toString();
    }
}
