import java.io.*;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.List;


public class diviArch {
    private String ruta;
    //static int tamanoArchivo;

    
    public String  getarchivo() {
        return ruta;
    }

    public boolean checkArchivo(String ruta) {
        this.ruta = ruta;
        File objetofile =new File(ruta);
        if (objetofile.exists()){
            System.out.println("el archivo si existe");
            return true;
        } else{
            System.out.println("no existe el archivo revisa el nombre o verifica si esta en la carpeta"); 
            return false ;
        }
    }


    public void divArchivo(String ruta){
        long inicioTiempo = System.currentTimeMillis(); // Marca de tiempo inicial
        // se crean las intancias File y se ve el numero de cpus que tiene
        File archivo =new File(ruta);
        int numOrigiCpus = Runtime.getRuntime().availableProcessors();
        int numCpus=numOrigiCpus*4; //multiplicamos x4 el numero de CPUs

        long tamanoArchivo = archivo.length();
        long lineasPorHilo = tamanoArchivo / (numCpus); //definimos cuantas lineas por hilo va leer y escriir 


        List<Thread> hilosLectores = new ArrayList<>(); //Hilos lectores 
        List<List<String>> fragmentos = new ArrayList<>(numCpus); //fragmentos por hilo CPUs 

        // Inicializa listas vacías para cada fragmento
        for (int i = 0; i < numCpus; i++) {
            fragmentos.add(new ArrayList<>());
        }

        // Creamos los hilos para leer cada fragmento
        for (int i = 0; i < numCpus; i++) {
            int idHilo = i;
            long inicio = i * lineasPorHilo;
            long fin = (i == numCpus - 1) ? tamanoArchivo : inicio + lineasPorHilo;

            Thread lector = new Thread(() -> { //empezamos a leer 
                try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                    raf.seek(inicio); 

                    // Ajustamos para eviar particiones mal 
                    if (inicio != 0) {
                        raf.readLine(); // Ignora la línea parcial al inicio
                    }

                    String linea;
                    while (raf.getFilePointer() < fin && (linea = raf.readLine()) != null) {
                        fragmentos.get(idHilo).add(linea); //agregamos las lineas por hilo hasta llegar al limite de cada archivo
                        synchronized (fragmentos.get(idHilo)) {
                            fragmentos.get(idHilo).add(linea); // Agrega la línea de forma segura
                            //System.out.println("Hilo " + idHilo + " terminado.");
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error al leer el archivo en el hilo " + idHilo + ": " + e.getMessage());
                }
            });

            hilosLectores.add(lector); //agregamos los hilos a la lista de hilos
            lector.start();
        }
        //Esperar a que terminen los hilos lectores
        for (Thread hilo : hilosLectores) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                System.err.println("Error al esperar un hilo lector: " + e.getMessage());
            }
        }

        System.out.println("Lectura completa. Iniciando la escritura en archivos.");
        // ahora creamos los hilos que escribiran 
        List<Thread> hilosEscritores = new ArrayList<>();
        for (int i = 0; i < numCpus; i++) {
            int idHilo = i + 1;
            List<String> fragmento = fragmentos.get(i); //obtenemos los fragmentos 

            Thread escritor = new Thread(new ecribirCSV(fragmento, "parte_" + idHilo + ".csv"));//y escribimos para esto esta el metodo escribiRCsv
            hilosEscritores.add(escritor);
            escritor.start();
        }

        // Esperar a que terminen los hilos escritores
        for (Thread hilo : hilosEscritores) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                System.err.println("Error al esperar un hilo escritor: " + e.getMessage());
            }
        }

        System.out.println("División completa. Regresando al menú principal...");
        long finTiempo = System.currentTimeMillis(); // Marca de tiempo final
        long tiempoEjecucion = finTiempo - inicioTiempo; // Diferencia en milisegundos
        System.out.println(tiempoEjecucion);
    }

    static class ecribirCSV implements Runnable { //implementamos la interfaz Runnable para poder crear los archivoscon los hilos
        private List<String> datos;
        private String nombreArchivo;

        public ecribirCSV(List<String> datos, String nombreArchivo) {
            this.datos = datos;
            this.nombreArchivo = nombreArchivo;
        }

        @Override
        public void run() {
            synchronized (datos) {  // Sincroniza el acceso a los datos
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
                    for (String linea : datos) {
                        bw.write(linea);
                        bw.newLine();
                    }
                    System.out.println("Archivo " + nombreArchivo + " escrito con éxito.");
                } catch (IOException e) {
                    System.err.println("Error al escribir en el archivo " + nombreArchivo + ": " + e.getMessage());
                }
            }
        }
    }
    
    public void diviArchivSinHil(String ruta,String cabecera){
        long inicioTiempo = System.currentTimeMillis(); // Marca de tiempo inicial
        File archivo =new File(ruta);
        int numOrigiCpus = Runtime.getRuntime().availableProcessors();
        int numCpus=numOrigiCpus*4; //multiplicamos x4 el numero de CPUs
        
        // Leer el archivo completo en memoria como en clase
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }
        int lineasint=lineas.size();
        int totalLineas=lineasint/numCpus;
        int resto=lineasint%numCpus;

        int inicio = 0;
        for (int i = 0; i < numCpus; i++) {
            int fin = inicio + totalLineas + (resto > 0 ? 1 : 0); // Agregar una línea extra si hay resto
            resto--;

            // Escribir las líneas correspondientes a este fragmento en un archivo nuevo
            String nombreArchivo = "parte_" + (i + 1) + ".csv";
            escribirArchivo(lineas.subList(inicio, fin), nombreArchivo, cabecera);

            inicio = fin; // Actualiza el inicio para la siguiente parte
        }

        System.out.println("Archivo dividido exitosamente.");
        long finTiempo = System.currentTimeMillis(); // Marca de tiempo final
        long tiempoEjecucion = finTiempo - inicioTiempo; // Diferencia en milisegundos
        System.out.println(tiempoEjecucion);
    }

    private static void escribirArchivo(List<String> datos, String nombreArchivo,String cabecera) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {

            if (cabecera != null) {
                bw.write(cabecera);  // Escribe la cabecera en el archivo de salida
                bw.newLine();  // Añade una nueva línea después de la cabecera
            }
            for (String linea : datos) {
                //bw.write(cabecera);
                bw.write(linea);
                bw.newLine();
            }
            System.out.println("Archivo " + nombreArchivo + " creado con éxito.");
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }

    public String leerCabecera(String rutaArchivo) {
        String cabecera = null;
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            cabecera = br.readLine(); // Leer la primera línea del archivo
        } catch (IOException e) {
            System.err.println("Error al leer la cabecera del archivo: " + e.getMessage());
        }
        return cabecera;
    }



}
        
    



    

