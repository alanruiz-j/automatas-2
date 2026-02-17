/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gerardo
 */
public class Archivos
{
/**
 *
 * @author David de la Luz
 */

    public static void guardarArchivo(String texto) {
        FileWriter fichero = null;
        PrintWriter pw = null;

        try {
            // Ruta relativa para la carpeta Datos
            File folder = new File("Datos");
            if (!folder.exists()) {
                folder.mkdir(); // Crear la carpeta si no existe
            }

            fichero = new FileWriter(new File(folder, "archivo.txt"));
            pw = new PrintWriter(fichero);
            pw.println(texto);

        } catch (IOException ex) {
            Logger.getLogger(Archivos.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            if (fichero != null) {
                try {
                    fichero.close();
                } catch (IOException ex) {
                    Logger.getLogger(Archivos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static String cargarArchivo() {
        String cadena = "";
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        // Ruta relativa para la carpeta Datos
        archivo = new File("Datos/archivo.txt");
        try {
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String linea;

            while ((linea = br.readLine()) != null) {
                cadena = cadena + linea + "\n";
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Archivos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Archivos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(Archivos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return cadena;
    }
}

