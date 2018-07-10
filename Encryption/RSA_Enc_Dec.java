package Encryption;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author theblackdevil
 */
public class RSA_Enc_Dec {
    private String plainText;
    private String cipherText;
    final private String keyStore="KeyStore.jks";
    final private String keyName="mykey";
    final private String password="password";
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
    public void encryptRSA() {
        try {
            PublicKey publicKey = getPubKey(this.keyStore,this.keyName);
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] plainTextBytes = this.plainText.getBytes();
            // Generate a symmetric key to encrypt the data and initiate the AES Cipher Object
            KeyGenerator sKenGen = KeyGenerator.getInstance("AES");
            Key aesKey = sKenGen.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            // Encrypt the plaintext with the AES key
            byte[] cipherTextBytes = aesCipher.doFinal(plainTextBytes);
            // Encrypt the symmetric AES key with the public RSA key
            byte[] encodedKey = rsaCipher.doFinal(aesKey.getEncoded());
            //Write the encrypted AES key and Ciphertext to the file.
            this.cipherText=Byte_Hex_Transform.byteArrayToHexString(encodedKey)+Byte_Hex_Transform.byteArrayToHexString(cipherTextBytes);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println("Don: "+e);
        }
    }
    /*
    * Decrypting files encrypted by RSA
    */
    public void decryptRSA()
    {
        File keyStoreFile = new File(this.keyStore);
        try {
            PrivateKey privateKey = getPriKey(keyStoreFile, this.keyName);
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] keys = rsaCipher.doFinal(Byte_Hex_Transform.hexStringToByteArray(this.cipherText.substring(0, 512)));
            //initiate Cipher
            Key aesKey = new SecretKeySpec(keys, "AES");
            Cipher encAEScipher = Cipher.getInstance("AES");
            encAEScipher.init(Cipher.DECRYPT_MODE, aesKey);
            //find the aes cipher
            byte[] cipher = Byte_Hex_Transform.hexStringToByteArray(this.cipherText.substring(512, this.cipherText.length()));
            //decrypt
            byte[] plainTextBytes = encAEScipher.doFinal(cipher);
            //print
            this.plainText=new String(plainTextBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println("Don: " + e);
        }
    }
    private PublicKey getPubKey(String keyStoreFile, String keyName) {
        PublicKey publicKey = null;
        try {
            // Load the keyStore
            KeyStore myKeyStore = KeyStore.getInstance("JKS");
            FileInputStream inStream = new FileInputStream(keyStoreFile);
            //Get the keyStore password, using Console lets us ask the password
            myKeyStore.load(inStream,"password".toCharArray());
            Certificate cert = myKeyStore.getCertificate(keyName);
            publicKey = cert.getPublicKey();
        }
        catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            System.out.println("Don: "+e);
        }
        return publicKey;
    }
    private PrivateKey getPriKey(File keyStoreFile, String keyName) {
        PrivateKey privateKey = null;
        try {
            KeyStore myKeyStore = KeyStore.getInstance("JKS");
            FileInputStream inStream = new FileInputStream(keyStoreFile);
            myKeyStore.load(inStream, password.toCharArray());
            //get the privatekey
            privateKey = (PrivateKey) myKeyStore.getKey(keyName, password.toCharArray());
            
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException e) {
            System.out.println("Don: " + e);
        }
        return privateKey;
    }
}
