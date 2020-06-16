package com.jwxt.service.imbl;

import com.jwxt.service.IVerificationService;
import com.jwxt.utils.VerificationTool;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

import static com.jwxt.service.imbl.ILoginServiceImpl.GET_VERIFICATION_URL;
import static com.jwxt.service.imbl.ILoginServiceImpl.HOST;

/**
 * @author me@nitmali.com
 * @date 2018/12/14 15:47
 */
@Component
@Slf4j
@Scope("prototype")
public class VerificationServiceImpl implements IVerificationService {

    @Resource
    private VerificationTool verificationTool;

    public static byte[] getGif;

    @Override
    public String getVerificationCode(Map<String, String> loginPageCookies) throws IOException {

        Connection.Response verificationCodeUrl = Jsoup
                .connect(HOST + GET_VERIFICATION_URL)
                .method(Connection.Method.GET)
                .data("FunMode", "GETYZM")
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        Connection.Response txtSecretCodeResponse = Jsoup
                .connect(HOST + verificationCodeUrl.body())
                .method(Connection.Method.GET)
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        byte[] gif = txtSecretCodeResponse.bodyAsBytes();

        getGif = gif;

        ByteArrayInputStream in = new ByteArrayInputStream(gif);

        BufferedImage image = ImageIO.read(in);

        return verificationTool.translate(image);
    }

    public static void saveImage(byte[] img, String filePath, String fileName) {

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        try {
            file = new File(filePath + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(img);
        } catch (FileNotFoundException e) {
            log.error("文件无法找到 ：" + filePath + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
