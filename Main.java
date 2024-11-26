import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int opcion;
        //int numCPus;
        Scanner scanner = new Scanner(System.in);

        do {

            System.out.println("=== MENÚ ===");
            System.out.println("1. dividir un archivo.");
            System.out.println("2. filtrar archivos.");
            System.out.println("3. mostrar resultados.");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");

            opcion = scanner.nextInt();


            switch (opcion) {
                case 1:
                    scanner.nextLine(); 
                    //Scanner scanner = new Scanner(System.in);
                    System.out.println("dividir un archivo.");
                    diviArch divider = new diviArch();
                    System.out.print("Por favor, ingresa la ruta del archivo: ");
                    String rutaArchivo = scanner.nextLine(); 
                    //scanner.nextLine();
                    if (divider.checkArchivo(rutaArchivo)){
                        System.out.print("Eligue dividir 1. con hilos     2.sin hilos : ");
                        int choSChil = scanner.nextInt();  
                        if (choSChil==1){
                            String ruta= divider.getarchivo();
                            divider.divArchivo(ruta);
                        }else if (choSChil==2){
                            String ruta= divider.getarchivo();
                            String cabecera=divider.leerCabecera(ruta);
                            divider.diviArchivSinHil(ruta,cabecera);
                        }else{
                            System.out.println("opcion incorrecta solo es 1 o 2");
                        }
                        //divider.checkArchivo("/Users/mariopablolopezgranciano/Desktop/proyectoprogra/tviaje.csv");
                    }
                    break;
                case 2:
                    System.out.println("filtrar archivos.");
                    
                    scanner.nextLine();

                    diviArch divider2 = new diviArch();
                    filtrararchiv filterarch = new filtrararchiv();

                    System.out.print("cual es la ruta de tu dataset original");
                    String rutacabec = scanner.nextLine(); // Leer valor
                    
                    if (divider2.checkArchivo(rutacabec)){

                        String cabecera=divider2.leerCabecera(rutacabec);
                        Map<String, Integer> filtros = new HashMap<>();
    
                        System.out.println("Introduce las variables. Escribe 'salir' como nombre de columna para terminar.");
    
                        while (true) {
                            System.out.print("Nombre de la columna (o 'salir' para terminar): ");
                            String columna = scanner.nextLine(); // Leer columna
                            if (cabecera.contains(columna)) {
    
                                System.out.print("Valor a filtrar en la columna '" + columna + "': ");
                                String valor = scanner.nextLine(); // Leer valor
                                
                                
                                filtros.put(columna, Integer.parseInt(valor));  // Guardar columna y valor como un par en el mapa
                                
    
                            }else{System.out.println("nombre no valido revisar nombre de la columna ");}
    
                            if (columna.equalsIgnoreCase("salir")) { // Terminar si el usuario escribe 'salir'
                                break;
                            }
    
    
                        }
                        filterarch.hilosFilter(filtros);
                        //filterarch.cumpleFiltro();
                        

                    } else{ System.out.print("ruta incorrecta vuelve a intentar (revisa nombre del archivo)");}
 

                    break;
                case 3:

                    System.out.println("mostrar resultados en Resultados.");
                    scanner.nextLine();
                    resultCheck checkcapr = new resultCheck();
                    diviArch divider3 = new diviArch();

                    System.out.print("cual es la ruta donde se guardaran los resultados");

                    String rutaResult = scanner.nextLine(); // Leer valor

                    if (divider3.checkArchivo(rutaResult)){
                        rutaResult=rutaResult+"/RESULTADOS";

                        if (checkcapr.checkCreacarp(rutaResult)){
                            System.out.println("ya esta la carpeta");
                        }else{
                            checkcapr.ACtResult(rutaResult);}
                    
                        
                        
                    }else{
                        System.out.println("verifica la carpeta donde pondras los resultados");
                    }
                    

                        
                    //}

                    
                    break;
                case 4:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        } while (opcion != 4); // Repetir hasta que elija salir

        scanner.close();
    }
}
