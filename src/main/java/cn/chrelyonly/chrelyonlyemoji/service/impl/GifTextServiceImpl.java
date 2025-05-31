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
 * @author 11725
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GifTextServiceImpl implements GifTextService {

    private final ResourceLoader resourceLoader;
    private final HttpServletResponse response;

    @Override
    public byte[] replaceGifFace(byte[] gifBytes, byte[] avatarBytes) throws IOException {
        GifDecoder decoder = new GifDecoder();
        decoder.read(new ByteArrayInputStream(gifBytes));

        BufferedImage avatar = ImageIO.read(new ByteArrayInputStream(avatarBytes));
        // 设定头像大小
        avatar = resizeImageToCircle(avatar, 150, 150);

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.start(outputStream);
        encoder.setRepeat(decoder.getLoopCount());

//        头像帧
        int[][] avatarPositions = {
                {237, 25}, {229, 28}, {221, 23}, {213, 27}, {205, 22}, {197, 26}, {189, 21},
                {181, 29}, {173, 24}, {165, 30}, {157, 22}, {149, 26}, {141, 21}, {133, 28},
                {125, 23}, {117, 29}, {109, 24}, {101, 30}, {93, 22},  {85, 26}, {77, 21},
                {69, 29}, {61, 24},  {53, 30},  {50, 22}, {50, 27}, {50, 23}, {50, 29},
                {50, 25}, // 停留开始

                {50, 25}, {50, 30}, {50, 24}, {50, 20}, {50, 28}, // 停留5帧

                {50, 25}, {58, 29}, {66, 24}, {74, 30},       // 保留4组
                {90, 26}, {98, 21}, {106, 28}, {114, 23},    // 去掉 {82,22}
                {130, 24}, {138, 30}, {146, 22}, {154, 26},  // 去掉 {122,29}
                {170, 29}, {178, 23}, {186, 28}, {194, 24},  // 去掉 {162,21}
                {210, 22}, {218, 27}, {226, 23}, {234, 29},   // 去掉 {202,30} 和 {237,25}（稍微集中删）


                {237, 30}, {237, 30}, {237, 24}, {237, 30}, {237, 28}, // 停留5帧
        };



//        循环素材帧
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            BufferedImage frame = decoder.getFrame(i);
            int delay = decoder.getDelay(i);

            BufferedImage modifiedFrame = overlayAvatar(frame, avatar, avatarPositions[i][0], avatarPositions[i][1]);
            encoder.setDelay(delay);
            encoder.addFrame(modifiedFrame);
        }

        encoder.finish();
        return outputStream.toByteArray();
    }

    private BufferedImage overlayAvatar(BufferedImage frame, BufferedImage avatar, int x, int y) {
        BufferedImage combined = new BufferedImage(
                frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = combined.createGraphics();
        g.drawImage(frame, 0, 0, null);
        g.drawImage(avatar, x, y, null); // 替换头像的位置
        g.dispose();
        return combined;
    }

    private BufferedImage resizeImageToCircle(BufferedImage original, int targetWidth, int targetHeight) {
        // 先缩放图片到目标大小
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();

        // 创建一个圆形裁剪后的图片
        BufferedImage circleBuffer = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = circleBuffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 清空画布（透明）
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, targetWidth, targetHeight);

        // 画圆形剪裁区域
        g.setComposite(AlphaComposite.Src);
        g.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, targetWidth, targetHeight));

        // 将缩放后的图片绘制到圆形区域内
        g.drawImage(resized, 0, 0, null);
        g.dispose();

        return circleBuffer;
    }

}
