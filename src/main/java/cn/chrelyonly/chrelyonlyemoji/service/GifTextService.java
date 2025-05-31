package cn.chrelyonly.chrelyonlyemoji.service;

import cn.chrelyonly.chrelyonlyemoji.util.R;
import com.alibaba.fastjson2.JSONObject;

import java.io.IOException;

/**
 * @author 11725
 */
public interface GifTextService {

    byte[] replaceGifFace(byte[] gifBytes, byte[] avatarBytes) throws IOException;
}
