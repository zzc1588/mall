package com.atguigu.auth;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Random;

@SpringBootTest
class GulimallAuthServerApplicationTests {
    String yzm;
    @Test
    void contextLoads() {
        yzm = random1();
        try {
            //HtmlEmail方式
            //sendEmail();
            //javax.mail方式
            sendMail("786614275@qq.com", "ggigmnvxkxzqbaic", "786614275@qq.com", "测试", "<html><h1>我的验证码："+yzm+"</h1></html>");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void sendEmail() {
        HtmlEmail send = new HtmlEmail();//创建一个HtmlEmail实例对象
        // 获取随机验证码
        String resultCode = yzm;
        try {
            send.setHostName("smtp.qq.com");
            send.setAuthentication("786614275@qq.com", "ggigmnvxkxzqbaic"); //第一个参数是发送者的QQEamil邮箱   第二个参数是刚刚获取的授权码

            send.setFrom("786614275@qq.com", "orison有限公司");//发送人的邮箱为自己的，用户名可以随便填  记得是自己的邮箱不是qq
//			send.setSmtpPort(465); 	//端口号 可以不开
            send.setSSLOnConnect(true); //开启SSL加密
            send.setCharset("utf-8");
            send.addTo("786614275@qq.com");  //设置收件人    email为你要发送给谁的邮箱账户
            send.setSubject("测试测试"); //邮箱标题
            send.setMsg("HelloWorld!<font color='red'>您的验证码:</font>   " + resultCode + " ，五分钟后失效"); //Eamil发送的内容
            send.send();  //发送
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }


    /**方式2：发送QQ邮件
     * @param sender 发送方的邮箱
     * @param auth qq邮箱中申请的16位授权码
     * @param to 接收人邮箱
     * @param title 邮件标题
     * @param content 邮件内容
     * */
    public static void sendMail(String sender,String auth,String to,String title,String content) throws MessagingException, GeneralSecurityException, javax.mail.MessagingException {
        //创建一个配置文件并保存
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");


        properties.setProperty("mail.host","smtp.qq.com");
        properties.setProperty("mail.transport.protocol","smtp");
        properties.setProperty("mail.smtp.auth","true");
        //QQ存在一个特性设置SSL加密
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);

        //创建一个session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender,auth);
            }
        });
        //开启debug模式
        session.setDebug(true);
        //获取连接对象
        Transport transport = session.getTransport();
        //连接服务器
        transport.connect("smtp.qq.com",sender,auth);
        //创建邮件对象
        MimeMessage mimeMessage = new MimeMessage(session);
        //邮件发送人
        mimeMessage.setFrom(new InternetAddress(sender));
        //邮件接收人
        mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
        //邮件标题
        mimeMessage.setSubject(title);
        //邮件内容
        mimeMessage.setContent(content,"text/html;charset=UTF-8");
        //发送邮件
        transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
        //关闭连接
        transport.close();
    }

    //生成6位数  验证码
    public static String random1(){
        String code = "";
        Random rd=new Random();
        for (int i = 0; i < 6; i++) {
            int r = rd.nextInt(10); //每次随机出一个数字（0-9）
            code = code + r;  //把每次随机出的数字拼在一起
        }
        System.out.println(code);
        return code;
    }

}
