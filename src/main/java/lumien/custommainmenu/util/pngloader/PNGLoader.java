package lumien.custommainmenu.util.pngloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.primitives.Ints;

public class PNGLoader {

    public static PNGImage load(File toLoad) throws IOException {
        int success;
        byte[] length;
        long startTime = System.currentTimeMillis();
        FileInputStream inputStream = new FileInputStream(toLoad);
        if (!PNGLoader.isPNG(inputStream)) {
            System.out.println("NOT A PNG FILE");
            inputStream.close();
            return null;
        }
        PNGImage loadedImage = new PNGImage();
        boolean finished = false;
        ByteArrayOutputStream imageDataOutput = new ByteArrayOutputStream();
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
                int width = Ints.fromBytes(chunkData[0], chunkData[1], chunkData[2], chunkData[3]);
                int height = Ints.fromBytes(chunkData[4], chunkData[5], chunkData[6], chunkData[7]);
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
                System.out.println("Interlace: " + interlace);
                continue;
            }
            if (typeString.equals("IDAT")) {
                imageDataOutput.write(chunkData);
                continue;
            }
            if (!typeString.equals("PLTE")) continue;
            Triple[] palette = new Triple[chunkData.length / 3];
            boolean index = false;
            for (int i = 0; i < chunkData.length; i += 3) {
                int red = chunkData[i] & 0xFF;
                int green = chunkData[i + 1] & 0xFF;
                int blue = chunkData[i + 2] & 0xFF;
                palette[i / 3] = Triple.of((Object) red, (Object) green, (Object) blue);
            }
            loadedImage.setPalette(palette);
        }
        PNGLoader.loadImageData(loadedImage, imageDataOutput.toByteArray());
        inputStream.close();
        System.out.println("Loading took " + (System.currentTimeMillis() - startTime) + "ms");
        return loadedImage;
    }

    private static ByteBuffer getPixelData(PNGImage pngImage, byte[] imageData) {
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
                pngImage.width * pngImage.height * colorAmount * pngImage.bitDepth / 8 + pngImage.height);
        Inflater inflater = new Inflater();
        inflater.setInput(imageData);
        byte[][] inflated = new byte[pngImage.height][pngImage.width * samples * pngImage.bitDepth / 8 + 1];
        for (int y = 0; y < pngImage.height; ++y) {
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
                        continue;
                    }
                    case 1: {
                        inflated[line][t] = (byte) (a + data);
                        continue;
                    }
                    case 2: {
                        inflated[line][t] = (byte) (b + data);
                        continue;
                    }
                    case 3: {
                        inflated[line][t] = (byte) (data + PNGLoader.getMean(a, b));
                        continue;
                    }
                    case 4: {
                        int path = PNGLoader.paethPredictor(a, b, c);
                        inflated[line][t] = (byte) (data + path);
                        continue;
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

    private static ByteBuffer getPixelData2(PNGImage pngImage, byte[] imageData) {
        int samples = 3;
        Inflater inflater = new Inflater();
        inflater.setInput(imageData, 0, imageData.length);
        byte[] uncompressedImageData = null;
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
        uncompressedImageData = new byte[pngImage.width * pngImage.height * samples * pngImage.bitDepth / 8
                + pngImage.height];
        try {
            System.out.println(
                    "Decompressed " + inflater.inflate(uncompressedImageData)
                            + " from "
                            + uncompressedImageData.length
                            + " bytes");
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        int colorAmount = 3;
        if (pngImage.getColorType() == 6) {
            colorAmount = 4;
        }
        ByteBuffer byteBuffer = BufferUtils
                .createByteBuffer(pngImage.width * pngImage.height * colorAmount * pngImage.bitDepth / 8);
        System.out.println("Samples: " + samples);
        block12: for (int scanLine = 0; scanLine < pngImage.height; ++scanLine) {
            switch (pngImage.getColorType()) {
                case 2: {
                    int bytesPerScanLine = pngImage.width * samples * pngImage.bitDepth / 8 + 1;
                    byteBuffer.put(uncompressedImageData, scanLine * bytesPerScanLine + 1, bytesPerScanLine - 1);
                    continue;
                }
                case 3: {
                    int bytesPerScanLine = pngImage.width + 1;
                    Triple[] palette = pngImage.getPalette();
                    for (int i = 1; i < bytesPerScanLine; ++i) {
                        Triple color = palette[uncompressedImageData[scanLine * bytesPerScanLine + i] & 0xFF];
                        byteBuffer.put(((Integer) color.getLeft()).byteValue());
                        byteBuffer.put(((Integer) color.getMiddle()).byteValue());
                        byteBuffer.put(((Integer) color.getRight()).byteValue());
                    }
                    continue;
                }
                case 6: {
                    int bytesPerScanLine = pngImage.width * samples * pngImage.bitDepth / 8 + 1;
                    byteBuffer.put(uncompressedImageData, scanLine * bytesPerScanLine + 1, bytesPerScanLine - 1);
                }
            }
        }
        byteBuffer.rewind();
        return byteBuffer;
    }

    private static void loadImageData(PNGImage pngImage, byte[] imageData) {
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        pngImage.setTextureID(textureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        if (pngImage.getColorType() == 6) {
            GL11.glTexImage2D(
                    3553,
                    0,
                    6408,
                    pngImage.getWidth(),
                    pngImage.getHeight(),
                    0,
                    6408,
                    5121,
                    PNGLoader.getPixelData(pngImage, imageData));
        } else {
            GL11.glTexImage2D(
                    3553,
                    0,
                    6407,
                    pngImage.getWidth(),
                    pngImage.getHeight(),
                    0,
                    6407,
                    5121,
                    PNGLoader.getPixelData(pngImage, imageData));
        }
    }

    private static boolean isPNG(FileInputStream inputStream) throws IOException {
        byte[] magic = new byte[8];
        inputStream.read(magic);
        boolean isPNG = true;
        block10: for (int byteIndex = 0; byteIndex < magic.length && isPNG; ++byteIndex) {
            byte b = magic[byteIndex];
            switch (byteIndex) {
                case 0: {
                    if (b == -119) continue;
                    isPNG = false;
                    continue;
                }
                case 1: {
                    if (b == 80) continue;
                    isPNG = false;
                    continue;
                }
                case 2: {
                    if (b == 78) continue;
                    isPNG = false;
                    continue;
                }
                case 3: {
                    if (b == 71) continue;
                    isPNG = false;
                    continue;
                }
                case 4: {
                    if (b == 13) continue;
                    isPNG = false;
                    continue;
                }
                case 5: {
                    if (b == 10) continue;
                    isPNG = false;
                    continue;
                }
                case 6: {
                    if (b == 26) continue;
                    isPNG = false;
                    continue;
                }
                case 7: {
                    if (b == 10) continue;
                    isPNG = false;
                }
            }
        }
        return isPNG;
    }
}
