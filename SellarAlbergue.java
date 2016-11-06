import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SellarAlbergue {
  private Map<String, String> datos_albergue = new HashMap<String, String>();

  public static void mensajeAyuda() {
    System.out.println("Sellador de albergue");
    System.out.println("\tSintaxis:   java GenerarClaves [nombre paquete] [identificador albergue] [clave publica peregrino] [clave privada albergue]");
    System.out.println();
  }

  public static void main(String[] args) {
    if (args.length != 4) {
      mensajeAyuda();
      System.exit(1);
    }

    System.out.println();
    System.out.println( args[1].toUpperCase()+ " *************************************************");
    SellarAlbergue albergue = new SellarAlbergue();

    try {

    //Pruebas sin scanner
    albergue.datos_albergue = JSONUtils.json2map( Utils.leerJSON( args[1] ) );


    /*
    Scanner in = new Scanner(System.in);
    System.out.print("Numero : ");
    String numero = in.nextLine();
    albergue.datos_albergue.put("numero", numero);

    System.out.print("Nombre  : ");
    String nombre = in.nextLine();
    albergue.datos_albergue.put("nombre", nombre);

    System.out.print("Lugar  : ");
    String lugar = in.nextLine();
    albergue.datos_albergue.put("lugar", lugar);

    System.out.print("Fecha  : ");
    String fecha = in.nextLine();
    albergue.datos_albergue.put("fecha", fecha);
    in.close();
    */


    Paquete compostelaVirtual = PaqueteDAO.leerPaquete( args[0] + ".paquete" );


      byte[] firmaPeregrinoEncriptada = compostelaVirtual.getContenidoBloque( "Firma Digital" );
      byte[] firmaPeregrinoDesencriptada = Utils.desencriptarConPublica( firmaPeregrinoEncriptada, args[2] );

      String json = JSONUtils.map2json( albergue.datos_albergue );
      System.out.println("Datos del albergue: " + json);
      byte[] firmaAlbergue = Utils.generarFirma( json, firmaPeregrinoDesencriptada );
      byte[] firmaAlbergueEncriptada = Utils.encriptarConPrivada( firmaAlbergue, args[3] );

      compostelaVirtual.anadirBloque( new Bloque( args[1]+ "_Datos", json.getBytes() ) );
      compostelaVirtual.anadirBloque( new Bloque( args[1]+ "_Firma", firmaAlbergueEncriptada ) );

      PaqueteDAO.escribirPaquete( "compostela.paquete", compostelaVirtual );
    }
    catch(Exception e) {
      System.out.println("Error al generar la firma del albergue");
    }


  }
}
