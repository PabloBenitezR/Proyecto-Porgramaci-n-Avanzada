import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class filtrararchiv {

    public void hilosFilter(Map<String, Integer> filtros) {
        long inicioTiempo = System.currentTimeMillis(); 
        List<String> archivosGenerados = new ArrayList<>();
        int numOrigiCpus = Runtime.getRuntime().availableProcessors();
        int numCpus = numOrigiCpus * 4; // Multiplicamos por 4 el número de CPUs
    
  
        for (int i = 1; i <= numCpus; i++) { 
            archivosGenerados.add("parte_" + i + ".csv");
        }
    
        // Lista para almacenar los hilos
        List<Thread> hilos = new ArrayList<>();
    
        // Crear un hilo para cada archivo
        for (String archivo : archivosGenerados) {
            String archivoFiltrado = "filtrado_" + archivo; // Nombre del archivo filtrado
    

            Thread hilo = new Thread(() -> filtrarArchivo(archivo, archivoFiltrado, filtros));
            hilos.add(hilo);
            hilo.start(); // Inicia el hilo
        }
    
        // Maestro usa el patrón "Sleep" para monitorear los hilos
        boolean trabajosPendientes = true;
        while (trabajosPendientes) {
            trabajosPendientes = false;
    
            for (Thread hilo : hilos) {
                if (hilo.isAlive()) { // Si algún hilo aún está trabajando
                    trabajosPendientes = true;
                }
            }
    
            if (trabajosPendientes) {
                try {
                    Thread.sleep(100); // Maestro duerme por 100 ms antes de revisar nuevamente
                } catch (InterruptedException e) {
                    System.err.println("El maestro fue interrumpido: " + e.getMessage());
                }
            }
        }
        
        System.out.println("Filtrado de archivos completo.");
        long finTiempo = System.currentTimeMillis(); // Marca de tiempo final
        long tiempoEjecucion = finTiempo - inicioTiempo; // Diferencia en milisegundos
        System.out.println(tiempoEjecucion);
    }
    
    public static void filtrarArchivo(String archivoEntrada, String archivoSalida, Map<String, Integer> filtros) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada));
             BufferedWriter bw = new BufferedWriter(new FileWriter(archivoSalida))) {
    
            String cabecera = br.readLine(); // Leer la cabecera del archivo
            if (cabecera != null) {
                bw.write(cabecera); // Escribir la cabecera en el archivo filtrado
                bw.newLine();
            }
    
            String linea;
            while ((linea = br.readLine()) != null) {
                if (cumpleFiltro(linea, filtros, cabecera)) { // Aplica el filtro
                    bw.write(linea);
                    bw.newLine();
                }
            }
    
            System.out.println("Archivo " + archivoEntrada + " filtrado con éxito.");
    
        } catch (IOException e) {
            System.err.println("Error al filtrar el archivo " + archivoEntrada + ": " + e.getMessage());
        }
    }
    
    public static boolean cumpleFiltro(String linea, Map<String, Integer> filtros, String cabecera) {
        // Dividimos la línea en columnas
        String[] partes = linea.split(",");
        
        // Se obtiene el índice de cada columna a partir de la cabecera
        String[] columnas = cabecera.split(",");
        
        for (Map.Entry<String, Integer> entry : filtros.entrySet()) {
            String nombreColumna = entry.getKey();  // Nombre de la columna (por ejemplo, "columna1")
            int valorFiltro = entry.getValue();  // Valor del filtro (número)
            
            // Encontramos el índice de la columna correspondiente
            int indiceColumna = -1;
            for (int i = 0; i < columnas.length; i++) {
                if (columnas[i].trim().equals(nombreColumna.trim())) {
                    indiceColumna = i;
                    break;
                }
            }
            
            // Si no encontramos la columna en la cabecera, no pasa el filtro
            if (indiceColumna == -1) {
                return false;
            }

            // Ahora, comparamos el valor en la columna con el valor del filtro
            if (partes.length > indiceColumna) {
                String valorColumna = partes[indiceColumna].trim(); // Valor de la columna
                try {
                    int valorColumnaInt = Integer.parseInt(valorColumna);  // Convertimos el valor de la columna a entero
                    
                    if (valorColumnaInt != valorFiltro) {
                        return false;  // Si no coincide con el valor del filtro, no pasa el filtro
                    }
                } catch (NumberFormatException e) {
                    // Si la columna no tiene un número válido, no pasa el filtro
                    return false;
                }
            }
        }
        return true;  // Si todos los filtros se cumplen
    }
    
    
    
//p5_7_7
}


