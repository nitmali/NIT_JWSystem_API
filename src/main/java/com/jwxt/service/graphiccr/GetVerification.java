package com.jwxt.service.graphiccr;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author nitmali@126.com
 * @date 2018/6/5 16:13
 */
@Service
public class GetVerification {

    private String verificationPath = "F:/";

    public String getVerification(File verification) {
        return GraphicC2Translator.getInstance().translate(verification);
    }

    public static void savaImage(byte[] img, String filePath, String fileName) {

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        File dir = new File(filePath);
        try {
            // 判断文件目录是否存在
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdir();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(img);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
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

    public String getVerificationPath() {
        return verificationPath;
    }
}
