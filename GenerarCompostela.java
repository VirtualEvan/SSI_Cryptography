import java.io.*;

import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.HashMap;
import java.util.Map;


public class GenerarCompostela {
  private Map<String, String> datos_peregrino = new HashMap<String, String>();

  //public GenerarCompostela(){}

    private static SecretKey generarClaveDES() throws Exception{
      Security.addProvider( new BouncyCastleProvider() );

      //*************************************************************//
      //Cifrado de datos con algoritmo DES y generación de su clave
      KeyGenerator generadorDES = KeyGenerator.getInstance("DES")

	    generadorDES.init(56); // clave de 56 bits
	    SecretKey contrasena = generadorDES.generateKey();

      return contrasena;
    }

    private static byte[] cifrarDatos(String datos, SecretKey contrasena) throws Exception{
      Security.addProvider( new BouncyCastleProvider() );

      //Crear cifrador
  		Cipher cifradorDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

      //Modo Cifrado
  		cifradorDES.init(Cipher.ENCRYPT_MODE, contrasena);

  		//Leer fichero de 1k en 1k y pasar fragmentos leidos al cifrador
  		byte[] bufferDatos = datos.getBytes();
  		byte[] bufferDatosCifrado;

      //Cifrar datos con algoritmo DES
  		bufferDatosCifrado = cifradorDES.doFinal(bufferDatos);

      return bufferDatosCifrado;
    }

    private static byte[] encriptarClave(SecretKey contrasena, String nombreClave) throws Exception{
      Security.addProvider( new BouncyCastleProvider() );

      //********************************************************//
      //Cifrado de clave del algoritmo DES con la clave pública RSA
      PublicKey clavePublica = Utils.leerClavePublica( nombreClave );

      // Creacion de cifrador
      Cipher cifradorRSA = Cipher.getInstance("RSA", "BC");
      cifradorRSA.init(Cipher.ENCRYPT_MODE, clavePublica);

      // Cifrado de contrasena con la clave pública
      byte[] claveEncriptada = cifradorRSA.doFinal(contrasena.getEncoded());

      return claveEncriptada;
    }

    public static void mensajeAyuda() {
      System.out.println("Generador de Compostela");
      System.out.println("\tSintaxis:   java GenerarClaves [clave publica oficina] [clave publica peregrino] [ruta donde se almacenará la compostela]");
      System.out.println();
    }

  public static final void main(String[] args) {
      if (args.length != 3) {
        mensajeAyuda();
        System.exit(1);
      }
      GenerarCompostela compostela = new GenerarCompostela();

      System.out.println("* Crear una cadena en formato JSON simplificado");

      compostela.datos_peregrino.put("nombre", "Viktor Va Arriba");
      compostela.datos_peregrino.put("dni", "86152445K");
      compostela.datos_peregrino.put("domicilio", "Su casa");
      compostela.datos_peregrino.put("fecha", "28/09/16");
      compostela.datos_peregrino.put("lugar", "Ourense");
      compostela.datos_peregrino.put("motivacion", "Ayer comencé a vivir una vida loca, super loca, requete loca, llegué a a la disco y me pasé de copas, entré con una, y salí con otra.");

      /*
      Scanner in = new Scanner(System.in);
      System.out.print("Nombre : ");
      String nombre = in.nextLine();
      datos.put("nombre", nombre);

      System.out.print("Fecha  : ");
      String fecha = in.nextLine();
      datos.put("fecha", fecha);

      System.out.print("Lugar  : ");
      String lugar = in.nextLine();
      datos.put("lugar", lugar);
      in.close();
      */

      String json = JSONUtils.map2json(compostela.datos_peregrino);
      System.out.println("Datos a enviar: " + json);
      try{
        SecretKey claveDES = generarClaveDES();
        byte[] datosCifrados = cifrarDatos( json, claveDES );
        byte[] claveEncriptada = encriptarClave( claveDES, args[0] );
        byte[] firma = Utils.generarFirma( json );
        byte[] firmaEncriptada = Utils.encriptarUsandoClavePrivada( firma, args[1] );

        Paquete compostelaVirtual = new Paquete();
        compostelaVirtual.anadirBloque( new Bloque( "Datos Cifrados", datosCifrados ) );
        compostelaVirtual.anadirBloque( new Bloque( "Clave Encriptada", claveEncriptada ) );
        compostelaVirtual.anadirBloque( new Bloque( "Firma Digital" , firmaEncriptada ) );

        PaqueteDAO.escribirPaquete( args[2]+"\\compostela.paquete", compostelaVirtual );
      }
      catch(Exception e){
        System.out.println(e);
      }


  }
}

