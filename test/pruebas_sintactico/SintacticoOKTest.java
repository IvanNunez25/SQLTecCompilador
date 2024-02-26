/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:        # Casos de prueba JUnit  para el Analizador Sintactico                 
 *:                           
 *: Archivo       : SinctacticoOKTest.java
 *: Autor         : Fernando Gil   
 *: 
 *: Fecha         : 25/Feb/2024
 *: Compilador    : Java JDK 17
 *: Descripción   : Casos de prueba con programas correctamente escritos en 
 *:                 lenguajes SQLTec.   
 *:           	      
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 25/Feb/2024 FGil               -Creacion de la estructura de la prueba.  
 *:-----------------------------------------------------------------------------
 */

package pruebas_sintactico;

import compilador.Compilador;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author FGIL.0
 */
public class SintacticoOKTest {
    Compilador        cmp       = new Compilador ();
    ArrayList<String> programas = new ArrayList<> ();

    //--------------------------------------------------------------------------
    
    public SintacticoOKTest() {
        
        // #1 - Programa minimo valido
        programas.add ( """
            end 
        """ );
               
    }
    
    //--------------------------------------------------------------------------
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    //--------------------------------------------------------------------------
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    //--------------------------------------------------------------------------
    
    @Before
    public void setUp() {
    }
    
    //--------------------------------------------------------------------------
    
    @After
    public void tearDown() {
    }

    //--------------------------------------------------------------------------
    
    @Test
    public void programasOKTest () {

        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* programasOKTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar 0 errores y el primer error registrado debe ser vacio
            assertEquals ( "programasOKTest #" + i, 
                    0, cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) );
            assertEquals ( "programasOKTest #" + i, 
                    "", cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO ) );
        }
    }  
    
    //--------------------------------------------------------------------------
  
}
