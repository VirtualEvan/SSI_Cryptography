import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SellarAlbergue {
  private Map<String, String> datos_albergue = new HashMap<String, String>();

  public static void mensajeAyuda() {
    System.out.println("Sellador de albergue");
    System.out.println("\tSintaxis:   java GenerarClaves [nombre albergue]");
    System.out.println();
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      mensajeAyuda();
      System.exit(1);
    }

    System.out.println();
    System.out.println( args[0].toUpperCase()+ " *************************************************");
    SellarAlbergue albergue = new SellarAlbergue();

    /*

    Scanner in = new Scanner(System.in);
    System.out.print("Numero : ");
    String nombre = in.nextLine();
    albergue.datos_albergue.put("numero", nombre);

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

    try {

    //Pruebas sin scanner
    albergue.datos_albergue = JSONUtils.json2map( Utils.leerJSON( args[0] ) );

    Paquete compostelaVirtual = PaqueteDAO.leerPaquete( "compostela.paquete" );


      byte[] firmaPeregrinoEncriptada = compostelaVirtual.getContenidoBloque( "Firma Digital" );
      byte[] firmaPeregrinoDesencriptada = Utils.desencriptarConPublica( firmaPeregrinoEncriptada, "peregrino" );

      String json = JSONUtils.map2json( albergue.datos_albergue );
      System.out.println("Datos del albergue: " + json);
      byte[] firmaAlbergue = Utils.generarFirma( json, firmaPeregrinoDesencriptada );
      byte[] firmaAlbergueEncriptada = Utils.encriptarConPrivada( firmaAlbergue, args[0] );

      compostelaVirtual.anadirBloque( new Bloque( args[0]+ "_Datos", json.getBytes() ) );
      compostelaVirtual.anadirBloque( new Bloque( args[0]+ "_Firma", firmaAlbergueEncriptada ) );

      PaqueteDAO.escribirPaquete( "compostela.paquete", compostelaVirtual );
    }
    catch(Exception e) {
      System.out.println("Error al generar la firma del albergue");
    }


  }
}
