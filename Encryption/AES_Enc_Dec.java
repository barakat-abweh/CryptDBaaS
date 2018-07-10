package Encryption;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author theblackdevil
 */
public class AES_Enc_Dec {
    private String plainText;
    private String cipherText;
    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }
    public String getPlainText() {
        return this.plainText;
    }
    public String getCipherText() {
        return this.cipherText;
    }
    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }
    public void encryptAES() {
        try {
            byte[] plainTextBytes = plainText.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(Byte_Hex_Transform.hexStringToByteArray("67e14dc17eb9f0c9fe4896467c77922b"), "AES");
            Cipher encAEScipher = Cipher.getInstance("AES");
            encAEScipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] cipherTextBytes = encAEScipher.doFinal(plainTextBytes);
            this.cipherText=Byte_Hex_Transform.byteArrayToHexString(cipherTextBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
            System.out.println("don "+e);
        }
    }
    public void decryptAES()
    {
        try {
            byte [] cipherBytes =Byte_Hex_Transform.hexStringToByteArray(cipherText);
            Key aesKey = new SecretKeySpec(Byte_Hex_Transform.hexStringToByteArray("67e14dc17eb9f0c9fe4896467c77922b"), "AES");
            Cipher encAEScipher = Cipher.getInstance("AES");
            encAEScipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] plainTextBytes = encAEScipher.doFinal(cipherBytes);
            System.out.println("The plainText is : "+new String(plainTextBytes));
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println("don: " + e);
        }
    }
}