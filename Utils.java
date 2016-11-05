import java.io.*;

import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.Arrays;

public class Utils  {

  public static PublicKey leerClavePublica(String nombreClave) throws Exception{
    KeyFactory keyFactoryRSA = KeyFactory.getInstance("RSA", "BC");

    File clavePublicaArchivo = new File(nombreClave + ".publica");
    FileInputStream in = new FileInputStream(clavePublicaArchivo);
    int length = (int) clavePublicaArchivo.length();
    byte[] bufferPub = new byte[length];

    in.read(bufferPub, 0, length);
    in.close();

    //Recuperar clave publica desde datos codificados en formato X509
    X509EncodedKeySpec clavePublicaSpec = new X509EncodedKeySpec(bufferPub);
    PublicKey clavePublica = keyFactoryRSA.generatePublic(clavePublicaSpec);

    return clavePublica;
  }

  public static PrivateKey leerClavePrivada(String nombreClave) throws Exception{
    KeyFactory keyFactoryRSA = KeyFactory.getInstance("RSA", "BC");

    File clavePrivadaArchivo = new File(nombreClave + ".privada");
    FileInputStream in = new FileInputStream(clavePrivadaArchivo);
    int length = (int) clavePrivadaArchivo.length();
    byte[] bufferPriv = new byte[length];

    in.read(bufferPriv, 0, length);
    in.close();

    //Recuperar clave publica desde datos codificados en formato X509
    PKCS8EncodedKeySpec clavePrivadaSpec = new PKCS8EncodedKeySpec(bufferPriv);
    PrivateKey clavePrivada = keyFactoryRSA.generatePrivate(clavePrivadaSpec);

    return clavePrivada;
  }

  public static byte[] encriptarConPrivada( byte[] datos, String nombreClave ) throws Exception{
    Security.addProvider( new BouncyCastleProvider() );

    //********************************************************//
    //Cifrado de firma digital del peregrino con la clave privada RSA
    PrivateKey  clavePrivada = Utils.leerClavePrivada( nombreClave );

    // Creacion de cifrador
    Cipher cifradorRSA = Cipher.getInstance("RSA", "BC");
    cifradorRSA.init(Cipher.ENCRYPT_MODE, clavePrivada);

    // Cifrado de contrasena con la clave pública
    byte[] datosEncriptados = cifradorRSA.doFinal(datos);

    return datosEncriptados;
  }

  public static byte[] desencriptarConPrivada(byte[] encriptado, String nombreClave) throws Exception{
    Security.addProvider(new BouncyCastleProvider());  // Cargar el provider BC

    PrivateKey  clavePrivada = Utils.leerClavePrivada( nombreClave );

    //Crear el cifrador
    Cipher cifrador = Cipher.getInstance("RSA", "BC");

    //Modo descifrado (clave privada)
    cifrador.init(Cipher.DECRYPT_MODE, clavePrivada); // Descrifra con la clave privada

    //Descifrado con clave privada
    byte[] desencriptado = cifrador.doFinal(encriptado);

    return desencriptado;
  }

  public static byte[] encriptarConPublica(byte[] datos, String nombreClave) throws Exception{
    Security.addProvider( new BouncyCastleProvider() );

    PublicKey clavePublica = Utils.leerClavePublica( nombreClave );

    // Creacion de cifrador
    Cipher cifradorRSA = Cipher.getInstance("RSA", "BC");
    cifradorRSA.init(Cipher.ENCRYPT_MODE, clavePublica);

    // Cifrado de con la clave pública
    byte[] encriptado = cifradorRSA.doFinal(datos);

    return encriptado;
  }

  public static byte[] desencriptarConPublica(byte[] encriptado, String nombreClave) throws Exception{
    Security.addProvider( new BouncyCastleProvider() );

    PublicKey clavePublica = Utils.leerClavePublica( nombreClave );

    // Creacion de cifrador
    Cipher cifradorRSA = Cipher.getInstance("RSA", "BC");
    cifradorRSA.init(Cipher.DECRYPT_MODE, clavePublica);

    // Descifrado de con la clave pública
    byte[] desencriptado = cifradorRSA.doFinal(encriptado);

    return desencriptado;
  }


  public static byte[] generarFirma( String datos ) throws Exception{
    Security.addProvider( new BouncyCastleProvider() );

    //Crear función resumen
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    //Crear resumen
    byte[] firma = messageDigest.digest( datos.getBytes() );

    return firma;
  }

  public static byte[] generarFirma( String datos, byte[] firmaOriginal ) throws Exception{
    Security.addProvider( new BouncyCastleProvider() );

    //Crear función resumen
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");

    //Crear resumen
    messageDigest.update( firmaOriginal );
    messageDigest.update( datos.getBytes() );
    byte[] firma = messageDigest.digest();

    return firma;
  }

  public static void escribirJSON(String datos, String nombre) throws Exception{
    BufferedWriter writer = new BufferedWriter( new FileWriter( nombre + ".json" ) );
    writer.write( datos );
    writer.close();
  }

  public static String leerJSON(String nombreJSON) throws Exception{
    File json = new File(nombreJSON + ".json");
    FileInputStream in = new FileInputStream(json);
    int length = (int) json.length();
    byte[] buffer = new byte[length];

    in.read(buffer, 0, length);
    in.close();
    String toret = new String(buffer, "UTF-8");
    return toret;
  }

  public static void mostrarBytes(byte[] buffer) {
    System.out.write(buffer, 0, buffer.length);
  }


}