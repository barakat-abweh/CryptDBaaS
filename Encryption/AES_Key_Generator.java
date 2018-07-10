package Encryption;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;

/**
 *
 * @author theblackdevil
 */
public class AES_Key_Generator {
    
    /**
     * @param args the command line arguments
     * @throws java.security.NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // TODO code application logic here
        //new AES key
        KeyGenerator aesKeyGen = KeyGenerator.getInstance("AES");
        Key aesKey = aesKeyGen.generateKey();
        byte[] keyBytes = aesKey.getEncoded();
        System.out.println("Key as hex: "+Byte_Hex_Transform.byteArrayToHexString(keyBytes));
    }
}