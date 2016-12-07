package org.abcmap.core.utils;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.ByteArrayType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Allow to persist image in database. Used to improve render computing time, and to keep distant resources in database.
 */
public class BufferedImagePersister extends ByteArrayType {

    private static final BufferedImagePersister singleTon = new BufferedImagePersister();

    private BufferedImagePersister() {
        super(SqlType.BYTE_ARRAY, new Class<?>[]{BufferedImage.class});
    }

    public static BufferedImagePersister getSingleton() {
        return singleTon;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        BufferedImage image = (BufferedImage) javaObject;
        if (image == null) {
            return null;
        } else {
            return imageToByte(image);
        }
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return bytesToImage((byte[]) sqlArg);
    }

    /**
     * Return a byte array or null if an error occur
     *
     * @param img
     * @return
     */
    public static byte[] imageToByte(BufferedImage img) {

        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(img, "png", out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Return a buffered image generated from byte array or null if an error occur
     *
     * @param bytes
     * @return
     */
    public static BufferedImage bytesToImage(byte[] bytes) {

        try {
            try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
                return ImageIO.read(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
