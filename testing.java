public class testing {
  public static void main(String args[]){
    if ("(123) 456-7890".matches( "[(][0-9]{3}[)][ ][0-9]{3}[-][0-9]{4}" )){
      System.out.println("done");
    }
    System.out.println("none");

  }
}