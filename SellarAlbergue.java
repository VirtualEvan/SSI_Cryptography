import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SellarAlbergue {
  private Map<String, String> datos_albergue = new HashMap<String, String>();

  public static void main(String[] args) {
    System.out.println();
    System.out.println( args[1].toUpperCase()+ " *************************************************");
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
    albergue.datos_albergue = JSONUtils.json2map( Utils.leerJSON( args[1] ) );

    Paquete compostelaVirtual = PaqueteDAO.leerPaquete( args[0]+".paquete" );


      byte[] firmaPeregrinoEncriptada = compostelaVirtual.getContenidoBloque( "Firma Digital" );
      byte[] firmaPeregrinoDesencriptada = Utils.desencriptarConPublica( firmaPeregrinoEncriptada, "peregrino" );

      String json = JSONUtils.map2json( albergue.datos_albergue );
      System.out.println("Datos del albergue: " + json);
      byte[] firmaAlbergue = Utils.generarFirma( json, firmaPeregrinoDesencriptada );

      compostelaVirtual.anadirBloque( new Bloque( args[1]+ "_Datos", json.getBytes() ) );
      compostelaVirtual.anadirBloque( new Bloque( args[1]+ "_Firma", firmaAlbergue ) );

      PaqueteDAO.escribirPaquete( args[2]+"\\compostela.paquete", compostelaVirtual );
    }
    catch(Exception e) {
      System.out.println("Error al generar la firma del albergue");
    }


  }
}
