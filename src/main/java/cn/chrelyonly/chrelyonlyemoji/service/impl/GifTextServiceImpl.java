package cn.chrelyonly.chrelyonlyemoji.service.impl;

import cn.chrelyonly.chrelyonlyemoji.service.GifTextService;
import cn.chrelyonly.chrelyonlyemoji.util.R;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author 11725
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GifTextServiceImpl implements GifTextService {

    private final ResourceLoader resourceLoader;
    private final HttpServletResponse response;


}
