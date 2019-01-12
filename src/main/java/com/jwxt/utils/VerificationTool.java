/**
 * Copyright 2016 By_syk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jwxt.utils;

import com.jwxt.config.VerificationConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 第2类图形验证码识别
 * <br />针对截至 2016-11-22 为止成都医学院、四川理工学院教务管理系统登录用的验证码
 * <br />图形尺寸为 72*27
 * 
 * @author By_syk
 */

@Service
@Slf4j
public class VerificationTool {
    
    private BufferedImage trainImg = null;
    
    /**
     * 元字符宽度
     */
    private static final int UNIT_W = 13;
    
    /**
     * 元字符高度
     */
    private static final int UNIT_H = 22;
    
    /**
     * 训练元字符数
     */
    private static final int TRAIN_NUM = 32;
    
    /**
     * 所有元字符
     */
    private static final char[] TRAIN_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',/* '9',*/
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            /*'o', */'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y'/*, 'z'*/};
    
    /**
     * 有效像素颜色值
     */
    private static final int TARGET_COLOR = Color.BLACK.getRGB();
    
    /**
     * 无效像素颜色值
     */
    private static final int USELESS_COLOR = Color.WHITE.getRGB();

    @Resource
    private VerificationConfig verificationConfig;

    private VerificationTool() {}

    /**
     * 去噪
     * 
     * @param picFile 图形验证码文件
     * @return
     * @throws Exception
     */
    private BufferedImage deNoise(File picFile) throws IOException {
        BufferedImage img = ImageIO.read(picFile);
        return doDeNoise(img);
    }

    /**
     * 去噪
     *
     * @param img 图形验证码文件流
     * @return BufferedImage
     * @throws Exception
     */
    private BufferedImage deNoise(BufferedImage img) {
        return doDeNoise(img);
    }

    private BufferedImage doDeNoise(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        final int TARGET = 0xff000099;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (img.getRGB(x, y) == TARGET) {
                    img.setRGB(x, y, TARGET_COLOR);
                } else {
                    img.setRGB(x, y, USELESS_COLOR);
                }
            }
        }
        return img;
    }

    /**
     * 分割元字符
     * 
     * @param img
     * @return
     * @throws Exception
     */
    private List<BufferedImage> split(BufferedImage img) {
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        subImgs.add(img.getSubimage(4, 0, UNIT_W, UNIT_H));
        subImgs.add(img.getSubimage(16, 0, UNIT_W, UNIT_H));
        subImgs.add(img.getSubimage(28, 0, UNIT_W, UNIT_H));
        subImgs.add(img.getSubimage(40, 0, UNIT_W, UNIT_H));
        return subImgs;
    }

    /**
     * 取出训练数据
     * 
     * @return
     * @throws Exception
     */

    private BufferedImage loadTrainData() throws Exception {
        if (trainImg == null) {
            Connection.Response txtSecretCodeResponse = Jsoup
                    .connect("http://127.0.0.1:10000/verification/train.png")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();

            byte[] png = txtSecretCodeResponse.bodyAsBytes();

            ByteArrayInputStream in = new ByteArrayInputStream(png);

            trainImg = ImageIO.read(in);
        }
        
        return trainImg;
    }

    /**
     * 将训练元字符装在一起
     * 
     * @param trainImg
     * @param smallImg
     * @param ch
     * @return
     */
    private void addTrainImg(BufferedImage trainImg, BufferedImage smallImg, char ch) {
        if (recognize(smallImg, trainImg) == ch) {
            return;
        }

        int which = Arrays.binarySearch(TRAIN_CHARS, ch);
        int x = -1;
        int y = -1;
        for (int i = 0; i < TRAIN_NUM; ++i) {
            if (trainImg.getRGB(i * UNIT_W, which * (UNIT_H + 1) + UNIT_H) != TARGET_COLOR) {
                x = i * UNIT_W;
                y = which * (UNIT_H + 1);
                break;
            }
        }
        
        if (x == -1 || y == -1) {
            return;
        }
        
        for (int i = 0; i < UNIT_W; ++i) {
            for (int j = 0; j < UNIT_H; ++j) {
                trainImg.setRGB(x + i, y + j, smallImg.getRGB(i, j));
            }
        }
        trainImg.setRGB(x, y + UNIT_H, TARGET_COLOR);
    }

    /**
     * 单元识别
     * 
     * @param img
     * @param trainImg
     * @return
     */
    private char recognize(BufferedImage img, BufferedImage trainImg) {
        char result = ' ';
        int width = img.getWidth();
        int height = img.getHeight();
        // 最小差异像素数
        int min = width * height;
        for (int i = 0; i < TRAIN_NUM; ++i) {
            for (int j = 0; j < TRAIN_CHARS.length; ++j) {
                int startX = UNIT_W * i;
                int startY = (UNIT_H + 1) * j;
                if (trainImg.getRGB(startX, startY + UNIT_H) != TARGET_COLOR) {
                    continue;
                }
                int count = 0; // 差异像素数
                for (int x = 0; x < UNIT_W; ++x) {
                    for (int y = 0; y < UNIT_H; ++y) {
                        count += (img.getRGB(x, y) != trainImg.getRGB(startX + x, startY + y) ? 1 : 0);
                        if (count >= min) {
                            break;
                        }
                    }
                }
                if (count < min) {
                    min = count;
                    result = TRAIN_CHARS[j];
                }
            }
        }
        
        return result;
    }

    /**
     * 识别
     * 
     * @param picFile 图形验证码文件
     * @return
     */
    public String translate(File picFile) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedImage img = deNoise(picFile);
            List<BufferedImage> listImg = split(img);
            BufferedImage trainImg = loadTrainData();
            for (BufferedImage bi : listImg) {
                result.append(recognize(bi, trainImg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 识别
     *
     * @param img 图形验证码流
     * @return
     */
    public String translate(BufferedImage img) {
        StringBuilder result = new StringBuilder();
        try {
            List<BufferedImage> listImg = split(deNoise(img));
            BufferedImage trainImg = loadTrainData();
            for (BufferedImage bi : listImg) {
                result.append(recognize(bi, trainImg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 训练
     */
    public boolean train() {
        File targetTrainFile = new File(verificationConfig.getTargetTrainFilePath());
        File rawDir = new File(verificationConfig.getTargetPath());
        try {
            BufferedImage trainImg = ImageIO.read(targetTrainFile);
            for (File file : Objects.requireNonNull(rawDir.listFiles())) {
                BufferedImage img = deNoise(file);
                List<BufferedImage> listImg = split(img);
                String[] parts = file.getName().split("\\.");
                char[] chars = parts[0].toCharArray();
                char[] addFlags;
                if (parts.length > 2) {
                    addFlags = parts[1].toCharArray();
                } else {
                    addFlags = new char[]{'1', '1', '1', '1'};
                }
                for (int i = 0, len = listImg.size(); i < len; ++i) {
                    if (addFlags[i] == '1') {
                        addTrainImg(trainImg, listImg.get(i), chars[i]);
                    }
                }
            }
            return ImageIO.write(trainImg, "PNG", targetTrainFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}