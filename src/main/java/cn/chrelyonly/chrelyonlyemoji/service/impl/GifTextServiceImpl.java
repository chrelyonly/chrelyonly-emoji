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
        avatar = resizeImage(avatar, 150, 150); // 设定头像大小

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.start(outputStream);
        encoder.setRepeat(decoder.getLoopCount());

//        头像帧
        int[][] avatarPositions = {
                {237, 50}, {229, 50}, {222, 50}, {214, 50}, {207, 50}, {199, 50}, {192, 50},
                {184, 50}, {177, 50}, {169, 50}, {162, 50}, {154, 50}, {147, 50}, {139, 50},
                {132, 50}, {124, 50}, {117, 50}, {109, 50}, {102, 50}, {94, 50}, {87, 50},
                {79, 50}, {72, 50}, {64, 50}, {57, 50}, {49, 50}, {42, 50}, {34, 50}, {27, 50},
                {50, 50}, {58, 50}, {67, 50}, {75, 50}, {84, 50}, {92, 50}, {101, 50},
                {109, 50}, {118, 50}, {126, 50}, {135, 50}, {143, 50}, {152, 50}, {160, 50},
                {169, 50}, {177, 50}, {186, 50}, {194, 50}, {203, 50}, {211, 50}, {220, 50},
                {228, 50}, {237, 50}, {237, 50}, {237, 50}, {237, 50}, {237, 50}, {237, 50}
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

    private BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resized;
    }
}
