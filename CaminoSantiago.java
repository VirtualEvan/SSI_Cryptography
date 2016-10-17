public class CaminoSantiago {
  public static void main (String[] args){
    String[] argumentos;

    //Creación de las claves del peregrino
    argumentos = new String[] {"peregrino","D:\\ESEI\\Cuarto\\SSI\\SSI_Cryptography"};
    try {
      GenerarClaves.main( new String[] {argumentos[0]} );
    }
    catch (Exception e){
      System.out.println("Error al generar las claves");
    }

    try {
      GenerarCompostela.main( argumentos );
    }
    catch (Exception e){
      System.out.println("Error al generar las claves");
    }

  }
}