public class CaminoSantiago {
  public static void main (String[] args){
    String[] argumentos;

    //Creación de las claves del peregrino y la oficina
    argumentos = new String[] {"oficina","peregrino","D:\\ESEI\\Cuarto\\SSI\\SSI_Cryptography", "D:\\ESEI\\Cuarto\\SSI\\SSI_Cryptography\\peregrino.privada", "D:\\ESEI\\Cuarto\\SSI\\SSI_Cryptography\\compostela"};
    try {
      GenerarClaves.main( new String[] {argumentos[0]} );
      GenerarClaves.main( new String[] {argumentos[1]} );
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
      DesempaquetarCompostela.main( new String[] {argumentos[4], argumentos[0]} );
    }
    catch (Exception e){
      System.out.println("Error desempaquetar compostela");
    }

  }
}