package com.jwxt.service.Verification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;

/**
 * @author me@nitmali.com
 * @date 2018/6/5 16:13
 */
@Service
public class GetVerification {

    @Resource
    private GraphicC2Translator graphicC2Translator;

    public String getVerification(File verification) {
        return graphicC2Translator.translate(verification);
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
// TODO Auto-generated catch block
            System.err.println("文件无法找到 ："+filePath+fileName);
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
// TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {

// TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
