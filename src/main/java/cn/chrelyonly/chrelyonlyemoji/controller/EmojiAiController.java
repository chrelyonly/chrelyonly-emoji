package cn.chrelyonly.chrelyonlyemoji.controller;

import cn.chrelyonly.chrelyonlyemoji.service.GifTextService;
import cn.chrelyonly.chrelyonlyemoji.util.MyEmojiUtil;
import cn.chrelyonly.chrelyonlyemoji.util.R;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chrelyonly
 * 图片表情包生成api
 */
@Slf4j
@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/emoji")
public class EmojiAiController {

    private final MyEmojiUtil emojiUtil;
    public static String emojiToken = "";
    private final GifTextService gifTextService;

    @SneakyThrows
    @RequestMapping("/gif")
    public void gif(HttpServletResponse response) {
        String gifPath = "D:\\dev\\dev\\project\\chrelyonly-emoji\\resource\\1.gif";
        String avatarPath = "D:\\dev\\dev\\project\\chrelyonly-emoji\\resource\\4.jpg";
        byte[] result = gifTextService.replaceGifFace(
                Files.readAllBytes(Paths.get(gifPath)),
                Files.readAllBytes(Paths.get(avatarPath))
        );
        response.setContentType("image/gif");
        response.getOutputStream().write(result);
        response.getOutputStream().flush();
//        return R.ok();
    }

    @SneakyThrows
    @RequestMapping("/uploadEmoji")
    public R uploadEmoji(HttpServletResponse response,@RequestBody Map<String, String> body) {
        byte[] gifBytes = ResourceUtil.readBytes("static/resource/1.gif");
        String base64 = body.get("base64");
        // 去掉前缀：data:image/png;base64,
        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }
        // ✅ 这是正确的处理方式
        byte[] avatar = Base64.getDecoder().decode(base64);
        byte[] result = gifTextService.replaceGifFace(
                gifBytes,
                avatar);
//        response.setContentType("image/gif");
//        response.getOutputStream().write(result);
//        response.getOutputStream().flush();

        // 转成base64字符串
        String gifBase64 = Base64.getEncoder().encodeToString(result);
        return R.data(gifBase64);
    }

}
