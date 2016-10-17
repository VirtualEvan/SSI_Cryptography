import java.io.*;

import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Base64;

public class DesempaquetarCompostela {

  public static PrivateKey leerClavePrivada(String ruta) throws Exception{
    // Cargar el provider BC
    Security.addProvider(new BouncyCastleProvider());

    //Provider
    KeyFactory keyFactoryRSA = KeyFactory.getInstance("RSA", "BC");

    File ficheroClavePrivada = new File(ruta + ".privada");
    int tamanoFicheroClavePrivada = (int) ficheroClavePrivada.length();
    byte[] bufferPriv = new byte[tamanoFicheroClavePrivada];
    FileInputStream in = new FileInputStream(ficheroClavePrivada);
    in.read(bufferPriv, 0, tamanoFicheroClavePrivada);
    in.close();

    PKCS8EncodedKeySpec clavePrivadaSpec = new PKCS8EncodedKeySpec(bufferPriv);
		PrivateKey clavePrivada = keyFactoryRSA.generatePrivate(clavePrivadaSpec);

    return clavePrivada;
  }

  public static SecretKey descifrarRSA(byte[] claveEncriptada, PrivateKey clavePrivada) throws Exception{
    Security.addProvider(new BouncyCastleProvider());  // Cargar el provider BC
    //Crear el cifrador
    Cipher cifrador = Cipher.getInstance("RSA", "BC");

    //Crear SKF (Transformacion de SecretKeys)
    SecretKeyFactory secretKeyFactoryDES = SecretKeyFactory.getInstance("DES");

    //Modo descifrado (clave privada)
    cifrador.init(Cipher.DECRYPT_MODE, clavePrivada); // Descrifra con la clave privada

    //Descifrar clave encriptada
    byte[] bufferClave = cifrador.doFinal(claveEncriptada);

    DESKeySpec DESspec = new DESKeySpec(bufferClave);
		SecretKey claveDES = secretKeyFactoryDES.generateSecret(DESspec);

    return claveDES;
  }

  public static String descifrarDatos(SecretKey claveDES, byte[] datosCifrados) throws Exception{
    // Cargar el provider BC
    Security.addProvider(new BouncyCastleProvider());

    //Crear el cifrador
    Cipher cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");

    //Cifrador en modo descifrado
    cifrador.init(Cipher.DECRYPT_MODE, claveDES);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    byte[] bufferPlano = cifrador.doFinal(Base64.getEncoder().encode(datosCifrados));                             //
    mostrarBytes(bufferPlano);                                                                                    //
    String datos = new String(Arrays.toString(bufferPlano));                                                      //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    return datos;
  }

  public static void mostrarBytes(byte [] buffer) {
    System.out.write(buffer, 0, buffer.length);
  }

  public static void mensajeAyuda() {
    System.out.println("Generador de Compostela");
    System.out.println("\tSintaxis:   java GenerarClaves [clave publica] [ruta donde se almacenará la compostela]");
    System.out.println();
  }

  public static final void main(String args[]){
    /*if (args.length != 2) {
      mensajeAyuda();
      System.exit(1);
    }*/

    Paquete compostelaVirtual = PaqueteDAO.leerPaquete("D:\\ESEI\\Cuarto\\SSI\\SSI_Cryptography\\compostela.paquete");//args[0]);

    try {
      byte[] claveEncriptada = compostelaVirtual.getContenidoBloque( "Clave Encriptada" );
      byte[] datosCifrados = compostelaVirtual.getContenidoBloque( "Datos Cifrados" );
      PrivateKey clavePrivada = leerClavePrivada( "peregrino" );
      SecretKey claveDES = descifrarRSA( claveEncriptada, clavePrivada );
      String datos = descifrarDatos( claveDES, datosCifrados );

      System.out.println(datos);
    }
    catch(Exception e){
      System.out.println(e);
    }


    /*
    for (String nombre : compostelaVirtual.getNombresBloque()){
      System.out.println(nombre);
    }
    */
  }

}