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

public class DesempaquetarCompostela {

  public static SecretKey descifrarRSA(byte[] claveEncriptada, String nombreClave) throws Exception{
    Security.addProvider(new BouncyCastleProvider());  // Cargar el provider BC

    //Descifrar clave encriptada
    byte[] bufferClave = Utils.desencriptarConPrivada( claveEncriptada, nombreClave);

    //Crear SKF (Transformacion de SecretKeys)
    SecretKeyFactory secretKeyFactoryDES = SecretKeyFactory.getInstance("DES");

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
    System.out.println();
    System.out.println("DESEMPAQUETAR COMPOSTELA *************************************************");
    Paquete compostelaVirtual = PaqueteDAO.leerPaquete( args[0]+".paquete" );

    try {
      byte[] claveEncriptada = compostelaVirtual.getContenidoBloque( "CLAVE_ENCRIPTADA" );
      byte[] datosCifrados = compostelaVirtual.getContenidoBloque( "DATOS_CIFRADOS" );
      byte[] firmaEncriptada = compostelaVirtual.getContenidoBloque( "FIRMA_DIGITAL" );
      compostelaVirtual.eliminarBloque( "CLAVE_ENCRIPTADA" );
      compostelaVirtual.eliminarBloque( "DATOS_CIFRADOS" );
      compostelaVirtual.eliminarBloque( "FIRMA_DIGITAL" );
      SecretKey claveDES = descifrarRSA( claveEncriptada, "oficina" );
      String datosPeregrino = descifrarDatos( claveDES, datosCifrados );
      byte[] firmaPeregrinoDesencriptada = Utils.desencriptarConPublica( firmaEncriptada, "peregrino" );
      byte[] firmaPeregrinoGenerada = Utils.generarFirma( datosPeregrino );
      System.out.println( "***COMPORBANDO PEREGRINO***" );

      System.out.println(datosPeregrino);

      if( Arrays.equals(firmaPeregrinoDesencriptada,firmaPeregrinoGenerada) ) {
        System.out.println( "LOS DATOS NO HAN SIDO MODIFICADOS" );
      }
      else {
        System.out.println( "ERROR: DATOS MODIFICADOS" );
      }

      System.out.println( "***COMPORBANDO ALBERGUES***" );

      int count = 0;
      while( compostelaVirtual.getNombresBloque().size()!=count ){
        System.out.println( "Comprobando Actualmente: " +compostelaVirtual.getNombresBloque().get(count) );

        String datos = new String( compostelaVirtual.getContenidoBloque( compostelaVirtual.getNombresBloque().get(count) ) );
        byte[] firmaAlbergue = compostelaVirtual.getContenidoBloque( compostelaVirtual.getNombresBloque().get(count + 1) );
        //compostelaVirtual.eliminarBloque( compostelaVirtual.getNombresBloque().get(count) );
        //compostelaVirtual.eliminarBloque( compostelaVirtual.getNombresBloque().get(count + 1) );
        count+=2;
        if( Arrays.equals( firmaAlbergue, Utils.generarFirma( datos, firmaPeregrinoDesencriptada ) ) ) {
          System.out.println( "LOS DATOS NO HAN SIDO MODIFICADOS" );
          System.out.println( datos );
        }
        else {
          System.out.println( "ERROR: DATOS MODIFICADOS" );
        }
      }
      /*
      for (String nombre : compostelaVirtual.getNombresBloque()){
        System.out.println( nombre );
      }*/
    }
    catch(Exception e){
      System.out.println(e);
    }


    /*

    */
  }

}