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
 *: Archivo       : SinctacticoErrTest.java
 *: Autor         : Fernando Gil   
 *: 
 *: Fecha         : 25/Feb/2024
 *: Compilador    : Java JDK 17
 *: Descripción   : Casos de prueba con programas con error sintactico en 
 *:                 lenguajes SQLTec. Los errores que se prueban son al emparejar.   
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
public class SintacticoErrTest {
    Compilador        cmp       = new Compilador ();
    ArrayList<String> programas = new ArrayList<> ();
    
    //--------------------------------------------------------------------------
    
    public SintacticoErrTest() {
        // #1 - Error se esperaba end
        programas.add ( """
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
    public void emparejarTest () {
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* emparejarTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "emparejarTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "emparejarTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[emparejar]" ) );
        }
    }
  
    //--------------------------------------------------------------------------
    
    @Test
    public void simboloInicialTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 1, no se permite un programa en blanco
        programas.add ( "" );
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* siboloInicialTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "siboloInicialTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "siboloInicialTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[P]" ) );
        }
    }  
    
    //--------------------------------------------------------------------------
    
    @Test
    public void tipoDatoTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 1, se esperaba un tipo de dato
        programas.add ( 
      """

        """         
        );
        
        // #2 - Error en la linea 1, se esperaba un tipo de dato
        programas.add ( 
      """

        """         
        );        
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* tipoDatoTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "tipoDatoTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "tipoDatoTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[T]" ) );
        }
    }     
    
    //--------------------------------------------------------------------------
    
    @Test
    public void expresionTest () {
        ArrayList<String> programas = new ArrayList<> ();
        
        // #1 - Error en la linea 2, se esperaba una expresion
        programas.add ( 
      """

        """         
        );
        
        int i = 0;
        // Por cada uno de los programas de prueba...
        for ( String programa : programas ) { 
            i++;
            System.out.println ( "********* expresionTest #" + i + " *********" );
            
            // Ejecutar el Lexico y Sintactico 
            cmp.analizarLexico (programa );
            cmp.analizarSintaxis();

            // Debe contabilizar al menos 1 error y el primer error debe ser de [emparejar]
            assertTrue ( "expresionTest #" + i, 
                    cmp.getTotErrores ( Compilador.ERR_SINTACTICO ) > 0 );
            assertTrue ( "expresionTest #" + i, 
                cmp.getPrimerMensError ( Compilador.ERR_SINTACTICO )
                        .contains("[E]" ) );
        }
    }     

    //--------------------------------------------------------------------------    
}
