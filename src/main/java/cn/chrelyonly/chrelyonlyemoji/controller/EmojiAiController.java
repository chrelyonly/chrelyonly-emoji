package cn.chrelyonly.chrelyonlyemoji.controller;

import cn.chrelyonly.chrelyonlyemoji.service.GifTextService;
import cn.chrelyonly.chrelyonlyemoji.util.MyEmojiUtil;
import cn.chrelyonly.chrelyonlyemoji.util.R;
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
    public R gif(HttpServletResponse response) {
        String gifPath = "D:\\dev\\dev\\project\\chrelyonly-emoji\\resource\\1.gif";
        String avatarPath = "D:\\dev\\dev\\project\\chrelyonly-emoji\\resource\\3.jpg";
        byte[] result = gifTextService.replaceGifFace(
                Files.readAllBytes(Paths.get(gifPath)),
                Files.readAllBytes(Paths.get(avatarPath))
        );
        response.setContentType("image/gif");
        response.getOutputStream().write(result);
        return R.ok();
    }

}
