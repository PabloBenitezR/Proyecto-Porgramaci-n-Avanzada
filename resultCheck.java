import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class resultCheck {
    public Boolean checkCreacarp(String ruta){
        File CARPETr = new File(ruta);
        if (CARPETr.exists() && CARPETr.isDirectory()) {
            System.out.println("La carpeta existe.");
            return true;
        } else {
            System.out.println("La carpeta no existe creando.......");

            if (CARPETr.mkdirs()) {
                System.out.println("La carpeta fue creada exitosamente.");
            } else {
                System.out.println("No se pudo crear la carpeta.");
            }

            return false;
        }

    }

    public void ACtResult(String rutaArchivoCombinado){
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        String fechaFormateada = fechaActual.format(formateador);

        rutaArchivoCombinado=rutaArchivoCombinado+"/"+fechaFormateada+".csv";
        List<String> archivosCombin = new ArrayList<>();
        int numOrigiCpus = Runtime.getRuntime().availableProcessors();
        int numCpus = numOrigiCpus * 4; // Multiplicamos por 4 el número de CPUs
    
  
        for (int i = 1; i <= numCpus; i++) { 
            archivosCombin.add("filtrado_parte_" + i + ".csv");
        }

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaArchivoCombinado))) {
            boolean primeraCabecera = true; // Bandera para manejar la cabecera
            for (String archivo : archivosCombin) {
                try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    boolean esPrimeraLinea = true;
                    while ((linea = lector.readLine()) != null) {
                        // Si es la primera línea y no es el primer archivo, omitir la cabecera
                        if (esPrimeraLinea && !primeraCabecera) {
                            esPrimeraLinea = false;
                            continue;
                        }
                        // Escribir la línea en el archivo combinado
                        escritor.write(linea);
                        escritor.newLine();
                        esPrimeraLinea = false;
                    }
                } catch (IOException e) {
                    System.err.println("Error al leer el archivo " + archivo + ": " + e.getMessage());
                }
                // Después de procesar el primer archivo, desactivar la escritura de cabeceras
                primeraCabecera = false;
            }
            System.out.println("Archivos combinados exitosamente en: " + rutaArchivoCombinado);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo combinado: " + e.getMessage());
        }
    }
    
    
}
