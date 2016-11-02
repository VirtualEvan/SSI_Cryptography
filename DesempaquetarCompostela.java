import java.io.*;

import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.HashMap;
import java.util.Map;

public class DesempaquetarCompostela {

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

    byte[] bufferPlano = cifrador.doFinal(datosCifrados);
    String datos = new String(bufferPlano);

    return datos;
  }

  public static void mensajeAyuda() {
    System.out.println("Generador de Compostela");
    System.out.println("\tSintaxis:   java GenerarClaves [ruta de la compostela] [clave privada]");
    System.out.println();
  }

  public static final void main(String args[]){
    if (args.length != 2) {
      mensajeAyuda();
      System.exit(1);
    }

    Paquete compostelaVirtual = PaqueteDAO.leerPaquete( args[0]+".paquete" );

    try {
      byte[] claveEncriptada = compostelaVirtual.getContenidoBloque( "Clave Encriptada" );
      byte[] datosCifrados = compostelaVirtual.getContenidoBloque( "Datos Cifrados" );
      byte[] firmaEncriptada = compostelaVirtual.getContenidoBloque( "Firma Digital" );
      PrivateKey clavePrivada = Utils.leerClavePrivada( "oficina" );
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