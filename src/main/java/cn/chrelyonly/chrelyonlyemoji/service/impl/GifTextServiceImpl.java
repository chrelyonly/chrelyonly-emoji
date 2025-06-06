package cn.chrelyonly.chrelyonlyemoji.service.impl;

import cn.chrelyonly.chrelyonlyemoji.service.GifTextService;
import cn.hutool.core.img.gif.AnimatedGifEncoder;
import cn.hutool.core.img.gif.GifDecoder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 实现 GifTextService 接口，实现将圆形头像叠加到 gif 每一帧的指定位置
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GifTextServiceImpl implements GifTextService {

    // Spring 注入的资源加载器
    private final ResourceLoader resourceLoader;

    // 当前线程的 HTTP 响应对象
    private final HttpServletResponse response;

    /**
     * 将头像叠加到 gif 的每一帧中
     * @param gifBytes 原始 gif 图片的字节数组
     * @param avatarBytes 头像图片的字节数组
     * @return 合成后的 gif 字节数组
     * @throws IOException 图片读取失败时抛出
     */
    @Override
    public byte[] replaceGifFace(byte[] gifBytes, byte[] avatarBytes) throws IOException {
        // 解码 GIF 图片
        GifDecoder decoder = new GifDecoder();
        decoder.read(new ByteArrayInputStream(gifBytes));


        // 初始化 gif 编码器
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.start(outputStream);
        encoder.setRepeat(decoder.getLoopCount());

        // 每一帧对应的头像坐标数组
        int[][] avatarPositions = {
                {36, 69, 60}, {33, 74, 60}, {30, 67, 60}, {28, 58, 60}, {19, 5, 60},
                {20, 7, 60}, {21, 6, 60}, {21, 9, 60}, {20, 10, 60}, {26, 17, 60},
                {59, 47, 60}, {63, 56, 60}, {74, 42, 60}, {70, 30, 60}, {70, 30, 60},
                {70, 30, 60}, {79, 34, 60}, {82, 35, 60}, {86, 34, 60}, {83, 49, 60},
                {59, 25, 60}, {33, 9, 60}, {37, 14, 60}, {40, 15, 60}, {40, 14, 60},
                {43, 51, 50}, {39, 55, 51}, {27, 54, 54}, {29, 54, 55}, {41, 48, 55},
                {42, 42, 58}, {44, 41, 61}, {44, 42, 60}, {44, 40, 60}, {39, 39, 67},
                {33, 37, 74}, {28, 34, 77}, {17, 28, 85}, {6, 32, 90}, {-7, 38, 103},
                {11, 39, 100}, {20, 23, 100}, {23, 19, 95}, {24, 19, 95}, {22, 20, 95},
                {25, 23, 95}, {25, 18, 95}, {24, 17, 95}, {22, 20, 95}, {24, 18, 95},
                {23, 17, 95}, {24, 18, 95}, {26, 22, 90}, {30, 26, 85}
        };

        // 遍历每一帧，叠加头像并添加到新的 gif 中
        for (int i = 0; i < decoder.getFrameCount(); i++) {

            // 读取头像图片，并裁剪为圆形大小为 155x155
            BufferedImage avatar = ImageIO.read(new ByteArrayInputStream(avatarBytes));
            avatar = resizeImageToCircle(avatar, avatarPositions[i][2], avatarPositions[i][2]);
            // 获取原始帧和延迟时间
            BufferedImage frame = decoder.getFrame(i);
            int delay = decoder.getDelay(i);

            // 合成头像与原始帧图像
            BufferedImage modifiedFrame = overlayAvatar(frame, avatar, avatarPositions[i][0] - 4, avatarPositions[i][1] - 4);
            encoder.setDelay(delay);
            encoder.addFrame(modifiedFrame);
        }

        // 编码完成，返回合成后的 gif 字节数组
        encoder.finish();
        return outputStream.toByteArray();
    }

    /**
     * 将头像叠加到 gif 帧图像中指定位置
     * @param frame gif 的一帧图像
     * @param avatar 已处理成圆形的头像图像
     * @param x x 坐标位置
     * @param y y 坐标位置
     * @return 合成后的图像
     */
    private BufferedImage overlayAvatar(BufferedImage frame, BufferedImage avatar, int x, int y) {
        // 创建与帧相同大小的透明图像
        BufferedImage combined = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // 在图像上绘制帧和头像
        Graphics2D g = combined.createGraphics();
        g.drawImage(frame, 0, 0, null);
        g.drawImage(avatar, x, y, null);
        g.dispose();
        return combined;
    }

    /**
     * 将输入图片缩放为指定大小并裁剪为圆形
     * @param original 原始图片
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 裁剪为圆形的图像
     */
    private BufferedImage resizeImageToCircle(BufferedImage original, int targetWidth, int targetHeight) {
        // 先缩放图片为目标大小
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();

        // 创建圆形缓冲图像并设置裁剪区域为圆形
        BufferedImage circleBuffer = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = circleBuffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置为透明背景
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, targetWidth, targetHeight);

        // 设置圆形裁剪区域
        g.setComposite(AlphaComposite.Src);
        g.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, targetWidth, targetHeight));

        // 绘制缩放后的图片到圆形区域
        g.drawImage(resized, 0, 0, null);
        g.dispose();

        return circleBuffer;
    }
}
