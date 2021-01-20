package code.common;

import android.os.Build;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MCrypt {

    static char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private static String iv = "117ab263fe4fc695";//Dummy iv (CHANGE IT!)
    private static IvParameterSpec ivspec;
    private static SecretKeySpec keyspec;
    private static Cipher cipher;

    private static String SecretKey = "67eb8b0439aeba175dbb637ead185031";//Dummy secretKey (CHANGE IT!)

    public MCrypt()
    {
        ivspec = new IvParameterSpec(iv.getBytes());

        keyspec = new SecretKeySpec(SecretKey.getBytes(), "rijndael-128");
        //keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch tableName
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch tableName
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(String text) throws Exception
    {
        new MCrypt();

        if(text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(text.trim().getBytes());
        } catch (Exception e)
        {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public static byte[] decrypt(String code) throws Exception
    {
        if(code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
            //Remove trailing zeroes
            if( decrypted.length > 0)
            {
                int trim = 0;
                for( int i = decrypted.length - 1; i >= 0; i-- ) if( decrypted[i] == 0 ) trim++;

                if( trim > 0 )
                {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e)
        {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }

    public static String bytesToHex(byte[] bytes)
    {
        StringBuffer hexString = new StringBuffer();
        for (byte b : bytes) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }


    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {

            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);

            }
            return buffer;
        }
    }



    private static String padString(String source)
    {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;
        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }
        return source;
    }

    // convert UTF-8 to internal Java String format
    public static String convertUTF8ToString(String s) {
        String out = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            out = new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return out;
    }

    // convert internal Java String format to UTF-8
    public static String convertStringToUTF8(String s) {
        String out = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            out = new String(s.trim().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        return out.trim();
    }


    /**
     * @param value data to encrypt
     * @param key a secret key used for encryption
     * @return String result of encryption
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String encryptNew(String value)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] value_bytes = value.getBytes("UTF-8");
        byte[] key_bytes = getKeyBytes(SecretKey);
        return Base64.encodeToString(encrypt(value_bytes, key_bytes, key_bytes), 0);
    }

    public static byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        // setup AES cipher in CBC mode with PKCS #5 padding
        Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // encrypt
        localCipher.init(1, new SecretKeySpec(paramArrayOfByte2, "rijndael-128"), new IvParameterSpec(paramArrayOfByte3));
        return localCipher.doFinal(paramArrayOfByte1);
    }


    /**
     *
     * @param value data to decrypt
     * @param key a secret key used for encryption
     * @return String result after decryption
     * @throws KeyException
     * @throws GeneralSecurityException
     * @throws GeneralSecurityException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public static String decryptNew(String value)
            throws GeneralSecurityException, IOException
    {
        byte[] value_bytes = Base64.decode(value, 0);
        byte[] key_bytes = getKeyBytes(SecretKey);
        return new String(decrypt(value_bytes, key_bytes, key_bytes), "UTF-8");
    }

    public static byte[] decrypt(byte[] ArrayOfByte1, byte[] ArrayOfByte2, byte[] ArrayOfByte3)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        // setup AES cipher in CBC mode with PKCS #5 padding
        Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // decrypt
        localCipher.init(2, new SecretKeySpec(ArrayOfByte2, "rijndael-128"), new IvParameterSpec(ArrayOfByte3));
        return localCipher.doFinal(ArrayOfByte1);
    }

    private static byte[] getKeyBytes(String paramString)
            throws UnsupportedEncodingException
    {
        byte[] arrayOfByte1 = new byte[16];
        byte[] arrayOfByte2 = paramString.getBytes("UTF-8");
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, Math.min(arrayOfByte2.length, arrayOfByte1.length));
        return arrayOfByte1;
    }


}
