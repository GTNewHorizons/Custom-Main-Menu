package lumien.custommainmenu.util.apngloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

public class APNGLoader {

    public static APNGImage load(File toLoad) throws IOException {
        byte[] length;
        int success;
        long startTime = System.currentTimeMillis();
        FileInputStream inputStream = new FileInputStream(toLoad);
        if (!APNGLoader.isPNG(inputStream)) {
            System.out.println("NOT A PNG FILE");
            inputStream.close();
            return null;
        }
        APNGImage loadedImage = new APNGImage();
        boolean finished = false;
        ArrayList<Frame> frames = new ArrayList<Frame>();
        Frame currentFrame = null;
        ByteArrayOutputStream currentImageData = new ByteArrayOutputStream();
        int currentSequenceNumber = -1;
        while ((success = inputStream.read(length = new byte[4])) != -1) {
            int chunkLength = ByteBuffer.wrap(length).getInt();
            byte[] type = new byte[4];
            inputStream.read(type);
            String typeString = new String(type, "US-ASCII");
            System.out.println("Chunk-Type: " + typeString);
            if (typeString.equals("IEND")) break;
            boolean critical = Character.isUpperCase(typeString.charAt(0));
            boolean isPublic = Character.isUpperCase(typeString.charAt(1));
            boolean conform = Character.isUpperCase(typeString.charAt(2));
            boolean saveToCopy = Character.isUpperCase(typeString.charAt(3));
            byte[] chunkData = new byte[chunkLength];
            inputStream.read(chunkData);
            byte[] crc = new byte[4];
            inputStream.read(crc);
            if (typeString.equals("IHDR")) {
                int width = Ints
                        .fromBytes((byte) chunkData[0], (byte) chunkData[1], (byte) chunkData[2], (byte) chunkData[3]);
                int height = Ints
                        .fromBytes((byte) chunkData[4], (byte) chunkData[5], (byte) chunkData[6], (byte) chunkData[7]);
                byte bitDepth = chunkData[8];
                byte colorType = chunkData[9];
                byte compression = chunkData[10];
                byte filter = chunkData[11];
                byte interlace = chunkData[12];
                loadedImage.setWidth(width);
                loadedImage.setHeight(height);
                loadedImage.setBitDepth(bitDepth);
                loadedImage.setColorType(colorType);
                loadedImage.setCompression(compression);
                loadedImage.setFilter(filter);
                loadedImage.setInterlace(interlace);
                System.out.println("Width: " + width);
                System.out.println("Height: " + height);
                System.out.println("BitDepth: " + bitDepth);
                System.out.println("ColorType: " + colorType);
                System.out.println("Compression: " + compression);
                System.out.println("Filter: " + filter);
                continue;
            }
            if (typeString.equals("PLTE")) {
                Triple[] palette = new Triple[chunkData.length / 3];
                boolean index = false;
                for (int i = 0; i < chunkData.length; i += 3) {
                    int red = chunkData[i] & 0xFF;
                    int green = chunkData[i + 1] & 0xFF;
                    int blue = chunkData[i + 2] & 0xFF;
                    palette[i / 3] = Triple.of((Object) red, (Object) green, (Object) blue);
                }
                loadedImage.setPalette(palette);
                continue;
            }
            if (typeString.equals("IDAT")) {
                if (currentFrame == null) continue;
                currentImageData.write(chunkData);
                continue;
            }
            if (typeString.equals("acTL")) {
                loadedImage.setNumberOfFrames(
                        Ints.fromBytes(
                                (byte) chunkData[0],
                                (byte) chunkData[1],
                                (byte) chunkData[2],
                                (byte) chunkData[3]));
                loadedImage.setNumberOfLoops(
                        Ints.fromBytes(
                                (byte) chunkData[4],
                                (byte) chunkData[5],
                                (byte) chunkData[6],
                                (byte) chunkData[7]));
                System.out.println("Frames : " + loadedImage.numberOfFrames);
                continue;
            }
            if (typeString.equals("fcTL")) {
                if (currentFrame != null) {
                    currentFrame.textureID = APNGLoader
                            .loadImageData(loadedImage, currentFrame, currentImageData.toByteArray());
                    frames.add(currentFrame);
                }
                currentImageData = new ByteArrayOutputStream();
                currentFrame = new Frame();
                ByteArrayInputStream byteStream = new ByteArrayInputStream(chunkData);
                currentFrame.sequenceNumber = Ints.fromBytes(
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()));
                currentFrame.width = Ints.fromBytes(
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()));
                currentFrame.height = Ints.fromBytes(
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()));
                currentFrame.offX = Ints.fromBytes(
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()));
                currentFrame.offY = Ints.fromBytes(
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()),
                        (byte) ((byte) byteStream.read()));
                currentFrame.delayNum = Shorts
                        .fromBytes((byte) ((byte) byteStream.read()), (byte) ((byte) byteStream.read()));
                currentFrame.delayDen = Shorts
                        .fromBytes((byte) ((byte) byteStream.read()), (byte) ((byte) byteStream.read()));
                currentFrame.disposeOP = byteStream.read();
                currentFrame.blendOP = byteStream.read();
                System.out.println("New Frame: " + currentFrame.sequenceNumber);
                System.out.println("Width: " + currentFrame.width);
                System.out.println("Height: " + currentFrame.height);
                continue;
            }
            if (!typeString.equals("fdAT")) continue;
            ByteArrayInputStream byteStream = new ByteArrayInputStream(chunkData);
            int sequenceNumber = Ints.fromBytes(
                    (byte) ((byte) byteStream.read()),
                    (byte) ((byte) byteStream.read()),
                    (byte) ((byte) byteStream.read()),
                    (byte) ((byte) byteStream.read()));
            byte[] data = new byte[byteStream.available()];
            byteStream.read(data);
            currentImageData.write(data);
        }
        if (currentFrame != null) {
            currentFrame.textureID = APNGLoader
                    .loadImageData(loadedImage, currentFrame, currentImageData.toByteArray());
            frames.add(currentFrame);
        }
        System.out.println(loadedImage.frames.length + ":" + frames.size());
        loadedImage.frames = frames.toArray(loadedImage.frames);
        inputStream.close();
        System.out.println("Loading took " + (System.currentTimeMillis() - startTime) + "ms");
        return loadedImage;
    }

    private static ByteBuffer getPixelData(APNGImage pngImage, Frame frame, byte[] imageData) {
        int i;
        int samples = 3;
        switch (pngImage.getColorType()) {
            case 2: {
                samples = 3;
                break;
            }
            case 3: {
                samples = 1;
                break;
            }
            case 6: {
                samples = 4;
            }
        }
        int colorAmount = 3;
        if (pngImage.getColorType() == 6) {
            colorAmount = 4;
        }
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(
                (int) (frame.width * frame.height * colorAmount * pngImage.bitDepth / 8 + frame.height));
        Inflater inflater = new Inflater();
        inflater.setInput(imageData);
        byte[][] inflated = new byte[frame.height][frame.width * samples * pngImage.bitDepth / 8 + 1];
        for (int y = 0; y < frame.height; ++y) {
            try {
                inflater.inflate(inflated[y]);
                continue;
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
        }
        for (int line = 0; line < inflated.length; ++line) {
            byte filter = inflated[line][0];
            block16: for (int t = 1; t < inflated[line].length; ++t) {
                int data = inflated[line][t] & 0xFF;
                int a = 0;
                if (t > samples * pngImage.bitDepth / 8) {
                    a = inflated[line][t - samples * pngImage.bitDepth / 8] & 0xFF;
                }
                int b = 0;
                if (line > 0) {
                    b = inflated[line - 1][t] & 0xFF;
                }
                int c = 0;
                if (t > samples * pngImage.bitDepth / 8 && line > 0) {
                    c = inflated[line - 1][t - samples * pngImage.bitDepth / 8] & 0xFF;
                }
                switch (filter) {
                    case 0: {
                        inflated[line][t] = (byte) data;
                        continue block16;
                    }
                    case 1: {
                        inflated[line][t] = (byte) (a + data);
                        continue block16;
                    }
                    case 2: {
                        inflated[line][t] = (byte) (b + data);
                        continue block16;
                    }
                    case 3: {
                        inflated[line][t] = (byte) (data + APNGLoader.getMean(a, b));
                        continue block16;
                    }
                    case 4: {
                        int path = APNGLoader.paethPredictor(a, b, c);
                        inflated[line][t] = (byte) (data + path);
                        continue block16;
                    }
                    default: {
                        System.out.println(filter);
                    }
                }
            }
        }
        if (pngImage.getColorType() == 2 || pngImage.getColorType() == 6) {
            for (i = 0; i < inflated.length; ++i) {
                for (int a = 1; a < inflated[i].length; ++a) {
                    byteBuffer.put(inflated[i][a]);
                }
            }
        } else if (pngImage.getColorType() == 3) {
            for (i = 0; i < inflated.length; ++i) {
                for (int a = 1; a < inflated[i].length; ++a) {
                    Triple triple = pngImage.palette[inflated[i][a] & 0xFF];
                    byteBuffer.put(((Integer) triple.getLeft()).byteValue());
                    byteBuffer.put(((Integer) triple.getMiddle()).byteValue());
                    byteBuffer.put(((Integer) triple.getRight()).byteValue());
                }
            }
        }
        byteBuffer.rewind();
        return byteBuffer;
    }

    private static int getMean(int... numberList) {
        int total = 0;
        for (int i : numberList) {
            total += i;
        }
        return total / numberList.length;
    }

    private static int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc) {
            return a;
        }
        if (pb <= pc) {
            return b;
        }
        return c;
    }

    private static int loadImageData(APNGImage apngImage, Frame frame, byte[] imageData) {
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture((int) GL11.GL_TEXTURE_2D, (int) textureID);
        GL11.glTexParameteri((int) GL11.GL_TEXTURE_2D, (int) GL11.GL_TEXTURE_WRAP_S, (int) GL11.GL_REPEAT);
        GL11.glTexParameteri((int) GL11.GL_TEXTURE_2D, (int) GL11.GL_TEXTURE_WRAP_T, (int) GL11.GL_REPEAT);
        GL11.glTexParameteri((int) GL11.GL_TEXTURE_2D, (int) GL11.GL_TEXTURE_MAG_FILTER, (int) GL11.GL_NEAREST);
        GL11.glTexParameteri((int) GL11.GL_TEXTURE_2D, (int) GL11.GL_TEXTURE_MIN_FILTER, (int) GL11.GL_NEAREST);
        if (apngImage.getColorType() == 6) {
            GL11.glTexImage2D(
                    (int) 3553,
                    (int) 0,
                    (int) 6408,
                    (int) frame.width,
                    (int) frame.height,
                    (int) 0,
                    (int) 6408,
                    (int) 5121,
                    (ByteBuffer) APNGLoader.getPixelData(apngImage, frame, imageData));
        } else {
            GL11.glTexImage2D(
                    (int) 3553,
                    (int) 0,
                    (int) 6407,
                    (int) frame.width,
                    (int) frame.height,
                    (int) 0,
                    (int) 6407,
                    (int) 5121,
                    (ByteBuffer) APNGLoader.getPixelData(apngImage, frame, imageData));
        }
        return textureID;
    }

    private static boolean isPNG(FileInputStream inputStream) throws IOException {
        byte[] magic = new byte[8];
        inputStream.read(magic);
        boolean isPNG = true;
        block10: for (int byteIndex = 0; byteIndex < magic.length && isPNG; ++byteIndex) {
            byte b = magic[byteIndex];
            switch (byteIndex) {
                case 0: {
                    if (b == -119) continue block10;
                    isPNG = false;
                    continue block10;
                }
                case 1: {
                    if (b == 80) continue block10;
                    isPNG = false;
                    continue block10;
                }
                case 2: {
                    if (b == 78) continue block10;
                    isPNG = false;
                    continue block10;
                }
                case 3: {
                    if (b == 71) continue block10;
                    isPNG = false;
                    continue block10;
                }
                case 4: {
                    if (b == 13) continue block10;
                    isPNG = false;
                    continue block10;
                }
                case 5: {
                    if (b == 10) continue block10;
                    isPNG = false;
                    continue block10;
                }
                case 6: {
                    if (b == 26) continue block10;
                    isPNG = false;
                    continue block10;
                }
                case 7: {
                    if (b == 10) continue block10;
                    isPNG = false;
                }
            }
        }
        return isPNG;
    }
}
