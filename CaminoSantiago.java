import java.util.HashMap;
import java.util.Map;
public class CaminoSantiago {
  public static void main (String[] args){
    String[] argumentos;

    //Creación de las claves del peregrino y la oficina
    argumentos = new String[] {"compostela", "oficina", "peregrino", "albergue1", "albergue2"};
    try {
      GenerarClaves.main( new String[] {argumentos[1]} );
      GenerarClaves.main( new String[] {argumentos[2]} );
      GenerarClaves.main( new String[] {argumentos[3]} );
      GenerarClaves.main( new String[] {argumentos[4]} );
    }
    catch (Exception e){
      System.out.println("Error al generar las claves");
    }

    try {
      GenerarCompostela.main( new String[] {argumentos[0], argumentos[1], argumentos[2]} );
    }
    catch (Exception e){
      System.out.println("Error al empaquetar compostela");
    }

    try {
      //ALBERGUE 1
      Map<String, String> datos_albergue = new HashMap<String, String>();

      datos_albergue.put("numero", "1");
      datos_albergue.put("nombre", "El rincon del peregrino");
      datos_albergue.put("lugar", "Albergueria");
      datos_albergue.put("fecha", "29/09/16");

      String json = JSONUtils.map2json(datos_albergue);
      Utils.escribirJSON(json, "albergue1");

      SellarAlbergue.main( new String[] {argumentos[0], argumentos[3], argumentos[2], argumentos[3]} );

    }
    catch (Exception e){
      System.out.println("Error sellar en el albergue 1");
    }

    try {
      //ALBERGUE 2
      Map<String, String> datos_albergue = new HashMap<String, String>();

      datos_albergue.put("numero", "2");
      datos_albergue.put("nombre", "Albergue de peregrinos de Vilar de Barrio");
      datos_albergue.put("lugar", "Vilar de Barrio");
      datos_albergue.put("fecha", "30/09/16");

      String json = JSONUtils.map2json(datos_albergue);
      Utils.escribirJSON(json, "albergue2");

      SellarAlbergue.main( new String[] {argumentos[0], argumentos[4], argumentos[2], argumentos[4]} );

    }
    catch (Exception e){
      System.out.println("Error sellar en el albergue 2");
    }

    try {
      DesempaquetarCompostela.main( new String[] {argumentos[0], argumentos[1], argumentos[2]} );
    }
    catch (Exception e){
      System.out.println("Error desempaquetar compostela");
    }

  }
}