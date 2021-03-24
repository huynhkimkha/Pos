package com.antdigital.agency.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class EmailHelper {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void send(Map<String,Object> mail) throws MessagingException, IOException {
        if(mail.get("template") == null || mail.get("subject") == null
                || mail.get("from") == null || mail.get("to") == null) {
            return;
        }

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

//        helper.addAttachment("logo.png", new ClassPathResource("memorynotfound-logo.png"));

        String html = "";
        if(mail.get("data") != null && mail.get("template") != null) {
            Context context = new Context();
            context.setVariables((Map<String, Object>) mail.get("data"));
            html = templateEngine.process((String) mail.get("template"), context);
        }

        helper.setSubject((String) mail.get("subject"));
        helper.setFrom((String) mail.get("from"));
        List<String> to = (List<String>)mail.get("to");
        String[] toArr = new String[to.size()];
        helper.setTo(to.toArray(toArr));
        helper.setText(html, true);

        if(mail.get("cc") != null) {
            List<String> cc = (List<String>)mail.get("cc");
            String[] ccArr = new String[cc.size()];
            helper.setCc(cc.toArray(ccArr));
        }

        if(mail.get("bcc") != null) {
            List<String> bcc = (List<String>)mail.get("bcc");
            String[] bccArr = new String[bcc.size()];
            helper.setBcc(bcc.toArray(bccArr));
        }
//        helper.addInline("logo.png", new ClassPathResource("memorynotfound-logo.png"));

        emailSender.send(message);
    }
}

