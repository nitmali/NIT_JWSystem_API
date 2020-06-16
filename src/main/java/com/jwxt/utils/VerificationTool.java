/**
 * Copyright 2016 By_syk
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jwxt.utils;

import com.jwxt.config.VerificationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
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

    public VerificationTool() {
    }

    /**
     * 分割元字符
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
     */
    private BufferedImage loadTrainData() throws Exception {
        if (trainImg == null) {
            trainImg = ImageIO.read(this.getClass().getResourceAsStream("/static/verification/train.png"));
        }

        return trainImg;
    }

    /**
     * 将训练元字符装在一起
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
     * @param file 图形验证码文件
     * @return String
     */
    public String translate(File file) {
        try {
            return translate(ImageIO.read(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            List<BufferedImage> listImg = split(denoising(img));
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
                BufferedImage img = denoising(ImageIO.read(file));
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


    /**
     * 图像去噪
     */
    public static BufferedImage denoising(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        int[][] gray = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = image.getRGB(x, y);
                // 图像加亮（调整亮度识别率非常高）
                int r = MathUtil.min((int) (((argb >> 16) & 0xFF) * 1.1 + 30), 255);
                int g = MathUtil.min((int) (((argb >> 8) & 0xFF) * 1.1 + 30), 255);
                int b = MathUtil.min((int) (((argb) & 0xFF) * 1.1 + 30), 255);
                gray[x][y] = (int) MathUtil.pow((MathUtil.pow(r, 2.2) * 0.2973 + MathUtil.pow(g, 2.2) * 0.6274 + MathUtil.pow(b, 2.2) * 0.0753), 1 / 2.2);
            }
        }

        // 二值化
        int threshold = binarization(gray, width, height);
        BufferedImage binaryBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (gray[x][y] > threshold) {
                    gray[x][y] |= 0x00FFFF;
                } else {
                    gray[x][y] &= 0xFF0000;
                }
                binaryBufferedImage.setRGB(x, y, gray[x][y]);
            }
        }
        return binaryBufferedImage;
    }

    /**
     * 二值化
     */
    public static int binarization(int[][] gray, int w, int h) {
        int[] histData = new int[w * h];
        // Calculate histogram
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int red = 0xFF & gray[x][y];
                histData[red]++;
            }
        }

        // Total number of pixels
        int total = w * h;

        float sum = 0;
        for (int t = 0; t < 256; t++) {
            sum += t * histData[t];
        }

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histData[t]; // Weight Background
            if (wB == 0)
                continue;

            wF = total - wB; // Weight Foreground
            if (wF == 0) {
                break;
            }

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }

    /**
     * 图片灰度，黑白
     */
    public static BufferedImage gray(BufferedImage image) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        image = op.filter(image, null);
        return image;
    }
}