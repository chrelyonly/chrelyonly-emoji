package cn.chrelyonly.chrelyonlyemoji.service.impl;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

public class GifSequenceWriter {
    private ImageWriter writer;
    private ImageOutputStream output;
    private ImageWriteParam param;

    public GifSequenceWriter(ImageOutputStream output, boolean loop) throws IOException {
        this.output = output;

        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("gif");
        if (!writers.hasNext()) {
            throw new IllegalArgumentException("No GIF writer found");
        }

        writer = writers.next();
        param = writer.getDefaultWriteParam();
        writer.setOutput(output);

        if (loop) {
            writer.prepareWriteSequence(null);
        }
    }

    public void writeToSequence(BufferedImage img) throws IOException {
        writer.writeToSequence(new IIOImage(img, null, null), param);
    }

    public void close() throws IOException {
        writer.endWriteSequence();
        output.close();
    }
}
