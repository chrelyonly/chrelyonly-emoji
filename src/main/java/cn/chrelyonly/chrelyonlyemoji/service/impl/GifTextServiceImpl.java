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
        avatar = resizeImage(avatar, 80, 80); // 设定头像大小

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.start(outputStream);
        encoder.setRepeat(decoder.getLoopCount());

        for (int i = 0; i < decoder.getFrameCount(); i++) {
            BufferedImage frame = decoder.getFrame(i);
            int delay = decoder.getDelay(i);

            BufferedImage modifiedFrame = overlayAvatar(frame, avatar, 250, 40); // 坐标需你自己调试
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

    private BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resized;
    }
}
