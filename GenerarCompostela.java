import java.io.*;

import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class GenerarCompostela {
  private Map<String, String> datos_peregrino = new HashMap<String, String>();

  //public GenerarCompostela(){}

    private static SecretKey generarClaveDES() throws Exception{
      Security.addProvider( new BouncyCastleProvider() );

      //*************************************************************//
      //Cifrado de datos con algoritmo DES y generación de su clave
      KeyGenerator generadorDES = KeyGenerator.getInstance("DES");

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
      //********************************************************//
      //Cifrado de clave del algoritmo DES con la clave pública RSA
      byte[] claveEncriptada = Utils.encriptarConPublica(contrasena.getEncoded(), nombreClave);

      return claveEncriptada;
    }

    public static void mensajeAyuda() {
      System.out.println("Generador de Compostela");
      System.out.println("\tSintaxis:   java GenerarClaves [nombre compostela] [ nombre clave publica oficina] [nombre clave privada peregrino]");
      System.out.println();
    }

  public static final void main(String[] args) {
      if (args.length != 3) {
        mensajeAyuda();
        System.exit(1);
      }
      System.out.println();
      System.out.println("GENERAR COMPOSTELA *************************************************");

      GenerarCompostela compostela = new GenerarCompostela();

      System.out.println("* Crear una cadena en formato JSON simplificado");

      compostela.datos_peregrino.put("nombre", "Viktor Va Arriba");
      compostela.datos_peregrino.put("dni", "86152445K");
      compostela.datos_peregrino.put("domicilio", "Su casa");
      compostela.datos_peregrino.put("fecha", "28/09/16");
      compostela.datos_peregrino.put("lugar", "Ourense");
      compostela.datos_peregrino.put("motivacion", "Ayer comence a vivir una vida loca, super loca, requete loca, llegue a a la disco y me pase de copas, entre con una, y sali con otra.");

      /*
      Scanner in = new Scanner(System.in);
      System.out.print("Nombre : ");
      String nombre = in.nextLine();
      compostela.datos_peregrino.put("nombre", nombre);

      System.out.print("DNI  : ");
      String dni = in.nextLine();
      compostela.datos_peregrino.put("dni", dni);

      System.out.print("Domicilio  : ");
      String domicilio = in.nextLine();
      compostela.datos_peregrino.put("domicilio", domicilio);

      System.out.print("Fecha  : ");
      String fecha = in.nextLine();
      compostela.datos_peregrino.put("fecha", fecha);

      System.out.print("Lugar  : ");
      String lugar = in.nextLine();
      compostela.datos_peregrino.put("lugar", lugar);

      System.out.print("Motivacion  : ");
      String motivacion = in.nextLine();
      compostela.datos_peregrino.put("motivacion", motivacion);
      in.close();
      */

      String json = JSONUtils.map2json(compostela.datos_peregrino);
      System.out.println("Datos a enviar: " + json);
      try{
        SecretKey claveDES = generarClaveDES();
        byte[] datosCifrados = cifrarDatos( json, claveDES );
        byte[] claveEncriptada = encriptarClave( claveDES, args[1] );
        byte[] firma = Utils.generarFirma( json );
        byte[] firmaEncriptada = Utils.encriptarConPrivada( firma, args[2] );

        Paquete compostelaVirtual = new Paquete();
        compostelaVirtual.anadirBloque( new Bloque( "Datos Cifrados", datosCifrados ) );
        compostelaVirtual.anadirBloque( new Bloque( "Clave Encriptada", claveEncriptada ) );
        compostelaVirtual.anadirBloque( new Bloque( "Firma Digital" , firmaEncriptada ) );

        PaqueteDAO.escribirPaquete( args[0]+".paquete", compostelaVirtual );
      }
      catch(Exception e){
        System.out.println(e);
      }


  }
}

