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
        avatar = resizeImageToCircle(avatar, 155, 155);

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.start(outputStream);
        encoder.setRepeat(decoder.getLoopCount());

//        头像帧
        int[][] avatarPositions = {
                {225, 20}, {227, 20}, {227, 18}, {227, 18}, {218, 18}, {210, 18}, {198, 20},
                {185, 25}, {179, 23}, {170, 21}, {160, 25}, {152, 25}, {145, 24}, {138, 28},
                {122, 18}, {117, 22}, {108, 25}, {100, 22}, {91, 25}, {89, 22}, {80, 22},
                {78, 15}, {66, 15}, {65, 18}, {50, 18}, {46, 20}, {52, 20}, {39, 20},
                {41, 20}, {46, 24}, {49, 20}, {49, 25}, {49, 22}, {52, 20}, {52, 23},
                {74, 18}, {79, 18}, {106, 20}, {108, 17}, {120 ,20}, {127 ,20}, {149, 18},
                {160, 20}, {177, 23}, { 184, 25}, {199, 23}, { 200, 22}, {209, 22},
                { 215, 22}, {225, 20}, { 222, 20}, { 224, 20}, {226, 20}, { 226, 20},
                {227, 20}, {224, 20}, { 227, 20},
        };

//        循环素材帧
        for (int i = 0; i < decoder.getFrameCount(); i++) {
            BufferedImage frame = decoder.getFrame(i);
            int delay = decoder.getDelay(i);

            BufferedImage modifiedFrame = overlayAvatar(frame, avatar, avatarPositions[i][0] - 4, avatarPositions[i][1] - 4);
            encoder.setDelay(delay);
            encoder.addFrame(modifiedFrame);
        }

        encoder.finish();
        return outputStream.toByteArray();
    }

    private BufferedImage overlayAvatar(BufferedImage frame, BufferedImage avatar, int x, int y) {
//        BufferedImage combined = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage combined = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

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
