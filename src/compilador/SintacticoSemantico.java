/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *:-----------------------------------------------------------------------------
 */
package compilador;

import general.Linea_BE;
import general.Linea_TS;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;

public class SintacticoSemantico {

    //Declarar las constances VACIO y ERROR_TIPO
    private final String VACIO      = "vacio";
    private final String ERROR_TIPO = "error_tipo";
    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;
    private final String patron = "char\\(\\d+\\)";
    private final String patronCol = "columna(char\\(\\d+\\))";
    private final String patronColumna = "columna(\\d+)";
    private final String patronArray = "array\\( 1\\.\\.\\d+, char \\)";
    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //

    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica

    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
         ProgramaSQL(new Atributos());
        //PROGRAMA();
    }

    //--------------------------------------------------------------------------
    
//    private void PROGRAMA () {
//        if ( checarArchivo( "profes.db" ) ) {
//            System.out.println("Si existe profrs.db");
//        }
//    }

    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
 
    private void errorEmparejar(String _token, String _lexema, int numLinea ) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + ( _lexema.equals ( "$" )? "fin de archivo" : _lexema ) + 
                    ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico

    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
     private boolean checarArchivo ( String nomarchivo ) {
          FileReader     fr         = null;
          BufferedReader br         = null;
          String         linea      = null;
          String         columna    = null;
          String         tipo       = null;
          String         ambito     = null;
          boolean        existeArch = false;
          int            pos;
          
          try {
            // Intentar abrir el archivo con el dise�o de la tabla  
            fr = new FileReader ( nomarchivo );
            cmp.ts.anadeTipo(cmp.be.preAnalisis.getEntrada(), "tabla");
            br = new BufferedReader ( fr );
                
            // Leer linea x linea, cada linea es la especificacion de una columna
	        linea = br.readLine ();
	        while ( linea != null  ) {
  	        // Extraer nombre y tipo de dato de la columna
               try
               {
                   columna = linea.substring (  0, 24 ).trim ();
               }
               catch (Exception err)
               {
                   columna = "ERROR";
               }
               try
               {
                   tipo    = linea.substring ( 29     ).trim ();
               }
               catch (Exception err)
               {
                   tipo = "ERROR";
               }
               try
               {
                   ambito  = nomarchivo.substring( 0, nomarchivo.length ()- 3 );
               }
               catch(Exception err)
               {
                   ambito = "ERROR";
               }
               // Agregar a la tabla de simbolos
               Linea_TS lts = new Linea_TS ( "id", 
                                             columna, 
                                             "COLUMNA(" + tipo + ")", 
                                             ambito
                                            );
               // Checar si en la Tabla de Simbolos existe la entrada para un 
               // lexema y ambito iguales al de columna y ambito de la tabla .db
               if ( ( pos = cmp.ts.buscar ( columna, ambito ) ) > 0 ) {
                   // YA EXISTE: Si no tiene tipo asignarle el tipo columna(t) 
                   if ( cmp.ts.buscaTipo ( pos ).trim ().isEmpty () )
                       cmp.ts.anadeTipo  ( pos, tipo );
               } else {
                   // NO EXISTE: Buscar si en la T. de S. existe solo el lexema de la columna
                   if ( ( pos = cmp.ts.buscar ( columna ) ) > 0 ) {
                       // SI EXISTE: checar si el ambito esta en blanco
                       Linea_TS aux = cmp.ts.obt_elemento ( pos );
                       if ( aux.getAmbito ().trim ().isEmpty () ) {
                         // Ambito en blanco rellenar el tipo y el ambito  
                         cmp.ts.anadeTipo   ( pos, "COLUMNA("+ tipo+ ")"   );
                         cmp.ts.anadeAmbito ( pos, ambito );
                         
                       } else {
                         // Insertar un nuevo elemento a la tabla de simb.
                         cmp.ts.insertar ( lts );
                       }
                   } else {
                       // NO EXISTE: insertar un nuevo elemento a la tabla de simb.
                       cmp.ts.insertar ( lts );
                   }
                }
                   
                // Leer siguiente linea
	            linea   = br.readLine ();
	        }
            existeArch  = true;
          } catch ( IOException ex ) {
  	          System.out.println ( ex );
	      } finally {
              // Cierra los streams de texto si es que se crearon
              try {
                if ( br != null )
                    br.close ();
                if ( fr != null )
                    fr.close();
              } catch ( IOException ex ) {}
          }           
          return existeArch;
        }
	
	  /*----------------------------------------------------------------------------------------*/
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ProgramaSQL(Atributos ProgramaSQL) {
        Atributos Declaracion = new Atributos();
        Atributos Sentencias = new Atributos();
        if (preAnalisis.equals("declare") || preAnalisis.equals("end") || preAnalisis.equals("if") || preAnalisis.equals("while")
                || preAnalisis.equals("print") || preAnalisis.equals("assign") || preAnalisis.equals("select") || preAnalisis.equals("delete")
                || preAnalisis.equals("insert") || preAnalisis.equals("update") || preAnalisis.equals("create") || preAnalisis.equals("drop")
                || preAnalisis.equals("case")) {
        Declaracion(Declaracion);
	Sentencias(Sentencias);
	emparejar("end");
        //Accion Semantica 1
        if(analizarSemantica) {
            if(Declaracion.tipo.equals(VACIO) && Sentencias.tipo.equals(VACIO)) {
                ProgramaSQL.tipo = VACIO;
            } else {
                ProgramaSQL.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO, "[ProgramaSQL] Errores de tipos en el programa");

            }
        }
    } else {

            error("[ProgramaSQL] se esperaba una declaracion, sentencia o end");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Actregs(Atributos Actregs) {
        Atributos Igualacion = new Atributos();
        Atributos ExprCond = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if (preAnalisis.equals("update")) {
            // ACTREGS -> update id  set  IGUALACION   where EXPRCOND
            emparejar("update");
            id = cmp.be.preAnalisis;
            emparejar("id");
            //Accion Semantica 41
            if(analizarSemantica) {
                if(checarArchivo(id.lexema)) {
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    Actregs.id = VACIO;
                } else {
                    Actregs.id = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ActRegs] El identificador id.lexema no es una tabla");                                                             
                }
            }
            emparejar("set");
            Igualacion(Igualacion);
            //Accion Semantica 42
            if(analizarSemantica) {
                if(cmp.ts.buscaTipo(id.entrada).equals("tabla") && Igualacion.tipo.equals(VACIO)) {
                    Actregs.h = VACIO;
                } else {
                    Actregs.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ActRegs] El identificador no es una tabla");
                }
            }
            emparejar("where");
            ExprCond(ExprCond);
            //Accion Semantica 43 pendiente
            if(analizarSemantica) {
                if(Actregs.h.equals(VACIO) && ExprCond.tipo.equals("boolean")) {
                    Actregs.tipo = VACIO;
                } else {
                    Actregs.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ActRegs] Error en la condicion de actualizacion");                                                             
                }
            }
        } else {
            error("[Actregs] se esperaba update");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Columnas( Atributos Columnas ) {
        
        Linea_BE id             = new Linea_BE ();
        Atributos ColumnasPrima = new Atributos ();
        
        if(preAnalisis.equals("id")) {
            
            id = cmp.be.preAnalisis;
            
            //Columnas -> id ColumnasPrima
            emparejar("id");
            ColumnasPrima( ColumnasPrima );
            
            // Acción Semántica 76
            if ( analizarSemantica ) {
                if ( ( cmp.ts.buscaTipo( id.entrada ).matches( patronColumna ) ) &&
                     ColumnasPrima.tipo.equals( VACIO ) ) {
                    Columnas.tipo = VACIO;
                }
            }
            
        } else {
            error("[Columnas] se esperaba id");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ColumnasPrima( Atributos ColumnasPrima ) {
        
        Atributos Columnas = new Atributos ();
        
        if(preAnalisis.equals(",")) {
            //ColumnasPrima -> , Columnas
            emparejar(",");
            Columnas( Columnas );
            
            // Acción semántica 77
            if ( analizarSemantica ) {
                ColumnasPrima.tipo = Columnas.tipo;
            }
            
        } else {
            //ColumnasPrima -> empty
            
            // Acción semántica 78
            if ( analizarSemantica ) {
                ColumnasPrima.tipo = VACIO;
            }
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Declaracion(Atributos Declaracion) {
        Atributos Tipo = new Atributos();
        Atributos Declaracion1 = new Atributos(); 
        Linea_BE idvar = new Linea_BE();        
        if(preAnalisis.equals("declare")) {
            //Declaracion -> declare idvar Tipo Declaracion
            emparejar("declare");
            idvar = cmp.be.preAnalisis; 
            emparejar("idvar");
            Tipo(Tipo);
            //Accion Semantica 2
            if(analizarSemantica) {
                if(cmp.ts.buscaTipo(idvar.entrada) == null || cmp.ts.buscaTipo(idvar.entrada).length() == 0) {
                    cmp.ts.anadeTipo(idvar.entrada, Tipo.tipo);
                    Declaracion1.h = VACIO;
                } else {
                    Declaracion1.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Declaracion1] Redeclaración del idvar");

                }
            }
            Declaracion(Declaracion1);
            //Accion Semantica 3
            if(analizarSemantica) {
                if(Declaracion1.h.equals(VACIO) && Declaracion1.tipo.equals(VACIO)) {
                    Declaracion.tipo = VACIO;
                } else {
                    Declaracion.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Declaracion] Errores de tipos en la declaración de variables");
                }
            }
        } else {
            //Declaracoin -> Empty
            //Accion Semantica 4
            if(analizarSemantica) {
                Declaracion.tipo = VACIO;
            }
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Despliegue(Atributos Despliegue) {
        Atributos Exparit = new Atributos();
        if(preAnalisis.equals("print")) {
            //Despliegue -> print Exprarit
            emparejar("print");
            Exparit (Exparit);
            //Accion Semantica 30
            if(analizarSemantica) {
                if(!Exparit.tipo.equals(ERROR_TIPO)) {
                    Despliegue.tipo = VACIO;
                } else {
                    Despliegue.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Despliegue] Error de tipos en el despliegue");                                         
                }
            }
        } else {
            error("[Despliegue] se esperaba print");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void DelReg(Atributos DelReg) {
        Atributos ExprCond = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if(preAnalisis.equals("delete")) {
            //DelReg -> delete from id where ExprCond
            emparejar("delete");
            emparejar("from");
            id = cmp.be.preAnalisis;            
            emparejar("id");
            //Accion Semantica 39
            if(analizarSemantica) {
                if(checarArchivo(id.lexema)) {
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    DelReg.h = VACIO;
                } else {
                    DelReg.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DelReg] El id no es una tabla");                                                             
                }
            }
            emparejar("where");
            ExprCond(ExprCond);
            //Accion Semantica 40 Pendiente
            if(analizarSemantica) {
                if(DelReg.h.equals(VACIO) && ExprCond.tipo.equals("boolean")) {
                    DelReg.tipo = VACIO;
                } else {
                    if(DelReg.h.equals(ERROR_TIPO)) {
                        DelReg.tipo = VACIO;
                    } else {
                        DelReg.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[DelReg] La expresion condicional no es booleana");                                                                 
                    }
                }
            }
        } else {
            error("[DelReg] se esperaba delete");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Expresiones( Atributos Expresiones ) {
        
        Atributos Exparit           = new Atributos ();
        Atributos ExpresionesPrima  = new Atributos ();
        
        if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
            //Expresiones -> Exparit ExpresionesPrima
            Exparit( Exparit );
            ExpresionesPrima( ExpresionesPrima );
            
            // Acción semántica 79
            if ( analizarSemantica ) {
                if ( !Exparit.tipo.equals( ERROR_TIPO ) && ExpresionesPrima.tipo.equals( VACIO ) ) {
                    Expresiones.tipo = VACIO;
                } else {
                    Expresiones.tipo = ERROR_TIPO;
                    cmp.me.error( Compilador.ERR_SEMANTICO, "[Expresiones] Error de tipos en las expresiones" );
                }
            }
            
        } else {
            error("[Expresiones] se esperaba num, num.num, idvar, literal o id");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ExpresionesPrima( Atributos ExpresionesPrima ) {
        
        Atributos Expresiones = new Atributos ();
        
        if(preAnalisis.equals(",")) {
            //ExpresionesPrima -> , Expresiones
            emparejar(",");
            Expresiones( Expresiones );
            
            // Acción semántica 80
            if ( analizarSemantica ) {
                ExpresionesPrima.tipo = Expresiones.tipo;
            }
            
        } else {
            //ExpresionesPrima -> empty
            
            // Acción semántica 81
            if ( analizarSemantica) {
                ExpresionesPrima.tipo = VACIO;
            }
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Exparit(Atributos Exparit) {
        Atributos Operando = new Atributos();
        Atributos ExparitPrima = new Atributos();
        if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
            //Exparit -> Operando ExparitPrima
            Operando(Operando);
            //Accion Semantica 58
            if(analizarSemantica) {
                ExparitPrima.h = Operando.tipo;
                Exparit.lexema = Operando.lexema;
            }
            ExparitPrima(ExparitPrima);
            //Accion Semantica 59
            if(analizarSemantica) {
                Exparit.tipo = ExparitPrima.tipo;
            }
        } else if(preAnalisis.equals("(")) {
            //Exparit -> (Exparit) ExparitPrima
            emparejar("(");
            Exparit(Exparit);
            emparejar(")");
            //Accion Semantica 60
            if(analizarSemantica) {
                ExparitPrima.h = Exparit.tipo;
            }
            ExparitPrima(ExparitPrima);
            //Accion Semantica 61
            if(analizarSemantica) {
                Exparit.tipo = ExparitPrima.tipo;
            }
            ExparitPrima(ExparitPrima);
        } else {
            error("[Exparit] se esperaba una expresion aritmetica");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ExparitPrima ( Atributos ExparitPrima ) {
        
        Atributos Exparit = new Atributos ();       
        
        if(preAnalisis.equals("opsuma")) {
            //ExparitPrima -> opsuma Exparit
            emparejar("opsuma");
            Exparit( Exparit );
            
            // Acción semántica 67 es 62
            if ( analizarSemantica ) {
                // Checar cuando es char(n)
                if ( ExparitPrima.h.equals( Exparit.tipo ) &&
                    !ExparitPrima.h.equals( "tabla" ) ) {
                     ExparitPrima.tipo = ExparitPrima.h;
                } else if ( ( ExparitPrima.h.equals( "int ")            && Exparit.tipo.equals( "float" ) )             ||
                            ( ExparitPrima.h.equals( "float" )          && Exparit.tipo.equals( "int" ) )               ||
                            ( ExparitPrima.h.equals( "float" )          && Exparit.tipo.equals( "columna(int)" ) )      ||
                            ( ExparitPrima.h.equals( "float" )          && Exparit.tipo.equals( "columnas(float)" ) )   ||
                            ( ExparitPrima.h.equals( "columna(int)" )   && Exparit.tipo.equals( "float" ) )             ||
                            ( ExparitPrima.h.equals( "columna(float)" ) && Exparit.tipo.equals( "int" ) )               ||
                            ( ExparitPrima.h.equals( "columna(float)" ) && Exparit.tipo.equals( "float" ) ) ) {
                    ExparitPrima.tipo = "float";
                } else if ( ( ExparitPrima.h.equals( "int" )            && Exparit.tipo.matches( patronArray ) ) ||
                            ( ExparitPrima.h.equals( "float" )          && Exparit.tipo.matches( patronArray ) ) ||
                            ( ExparitPrima.h.matches( patronArray )          && Exparit.tipo.equals( "int" ) )   ||
                            ( ExparitPrima.h.matches( patronArray )          && Exparit.tipo.equals( "float" ) ) ||
                            ( ExparitPrima.h.equals( "columna(int)" )   && Exparit.tipo.matches( patronArray ) ) ||
                            ( ExparitPrima.h.equals( "columna(float)" ) && Exparit.tipo.matches( patronArray ) ) ||
                            ( ExparitPrima.h.matches( patronCol )       && Exparit.tipo.equals( "int" ) )   ||
                            ( ExparitPrima.h.matches( patronCol )       && Exparit.tipo.equals( "float" ) ) ) {
                    if(ExparitPrima.lexema != null) {
                    ExparitPrima.lexemaInt = ExparitPrima.lexema.length() + Exparit.lexema.length();
                    ExparitPrima.lexema += Exparit.lexema;                        
                    }
                    ExparitPrima.tipo = "array( 1.." + ExparitPrima.lexemaInt + ", char )";                    
                } else if ( ( ExparitPrima.h.equals( "int" )            && Exparit.tipo.equals( "columna(int)" ) )  ||
                            ( ExparitPrima.h.equals( "columna(int)" )   && Exparit.tipo.equals( "int" ) ) ) {
                    ExparitPrima.tipo = "int";
                } else if ( ( ExparitPrima.h.matches( patronArray ) && Exparit.tipo.matches( patronArray ) ) ||
                            ( ExparitPrima.h.matches( patronArray ) && Exparit.tipo.matches( patronCol ) ) || 
                            ( ExparitPrima.h.matches( patronCol) && Exparit.tipo.matches( patronArray ) ) ) {
                    if(ExparitPrima.lexema != null) {
                    ExparitPrima.lexemaInt = ExparitPrima.lexema.length() + Exparit.lexema.length();
                    ExparitPrima.lexema += Exparit.lexema;
                    }
                    ExparitPrima.tipo = "array( 1.." + ExparitPrima.lexemaInt + ", char )";                    
                } else {
                    ExparitPrima.tipo = ERROR_TIPO;
                    cmp.me.error( Compilador.ERR_SEMANTICO , "[Exparit'] Error de tipos en la expersión aritmetica" );
                }
            }
            
        } else if(preAnalisis.equals("opmult")) {
            //ExparitPrima -> opmult Exparit
            emparejar("opmult");
            Exparit( Exparit );
            
            // Acción semántica 68
            if ( analizarSemantica ) {
                if ( ( ExparitPrima.h.equals( Exparit.tipo ) ) &&
                    !( ExparitPrima.h.matches( patron ) ) &&
                    !( Exparit.tipo.matches( patron ) ) &&
                    !( ExparitPrima.h.matches( patronCol ) ) &&
                    !( Exparit.tipo.matches( patronCol ) ) ) {
                    ExparitPrima.tipo = ExparitPrima.h;
                } else if ( ( ExparitPrima.h.equals( "int" )            && Exparit.tipo.equals( "float" ) ) ||
                            ( ExparitPrima.h.equals( "float" )          && Exparit.tipo.equals( "int" ) )   ||
                            ( ExparitPrima.h.equals( "columna(int)" )   && Exparit.tipo.equals( "float" ) ) ||
                            ( ExparitPrima.h.equals( "columna(float)" ) && Exparit.tipo.equals( "int" ) )   ||
                            ( ExparitPrima.h.equals( "columna(float)" ) && Exparit.tipo.equals( "float" ) ) ) {
                    ExparitPrima.tipo = "float";
                } else if ( ( ExparitPrima.h.equals( "columna(int)" )   && Exparit.tipo.equals( "int" ) ) || 
                            ( ExparitPrima.h.equals( "int" )            && Exparit.tipo.equals( "columna(int)" ) ) ) {
                    ExparitPrima.tipo = "int";
                } else {
                    ExparitPrima.tipo = ERROR_TIPO;
                    cmp.me.error( Compilador.ERR_SEMANTICO, "[ExparitPrima] Error de tipos en la expresión aritmetica");
                }
            }
            
        } else {
            //ExparitPrima -> empty
            
            // Acción semántica 69
            if ( analizarSemantica ) {
                ExparitPrima.tipo = ExparitPrima.h;
            }
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ExprCond(Atributos ExprCond) {
        Atributos Exprrel = new Atributos();
        Atributos ExprLog = new Atributos();
         if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
             //ExpCond -> Exprrel
             Exprrel(Exprrel);
             Exprlog(ExprLog);
             //Accion Semantica 25
             if(analizarSemantica) {
                 if(Exprrel.tipo.equals("boolean") && !ExprLog.tipo.equals(ERROR_TIPO)) {
                     ExprCond.tipo = "boolean";
                 } else {
                     ExprCond.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ExprCond] Error de tipos en la expresión condicional");                     
                 }
             }
         } else {
             error("[ExprCond] se esperaba una expresion aritmetica");
         }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Exprrel(Atributos Exprrel) {
        Atributos Exparit = new Atributos();
        Atributos Exparit1 = new Atributos();
         if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
             //Exprrel -> Exparit oprel Exparit
             Exparit(Exparit);
             emparejar("oprel");
             Exparit(Exparit1);
             //Accion Semantica 26
             if(analizarSemantica) {
                 if(Exparit.tipo.equals(Exparit1)) {
                     Exprrel.tipo = "boolean";
                 } else if(Exparit.tipo.equals("int") && Exparit1.tipo.equals("float")) {
                     Exprrel.tipo = "boolean";
                 } else if(Exparit.tipo.equals("float") && Exparit1.tipo.equals("int")) {
                     Exprrel.tipo = "boolean";
                 } else if(Exparit.tipo.matches(patron) && Exparit1.tipo.matches(patron)) {
                     Exprrel.tipo = "boolean";
                 } else {
                     Exprrel.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Exprrel] Tipos incompatibles en la comparación");                                          
                 }
             }
         } else {
             error("[Exprrel] se esperaba una expresion aritmetica");
         }
    }
    
    // Autor: Arturo Fernandez Alvarez
    private void Exprlog(Atributos Exprlog) {
        Atributos Exprrel = new Atributos();
        if (preAnalisis.equals("and")) {
            //EXPRLOG -> and EXPRREL
            emparejar("and");
            Exprrel(Exprrel);
            //Accion Semantica 27
            if(analizarSemantica) {
                Exprlog.tipo = Exprrel.tipo;
            }
        } else if(preAnalisis.equals("or")) {
            emparejar("or");
            Exprrel(Exprrel);
            //Accion Semantica 28
            if(analizarSemantica) {
                Exprlog.tipo = Exprrel.tipo;
            }
        }  else {
            // EXPRLOG -> empty
            //Accion Semantica 29
            if(analizarSemantica) {
                Exprlog.tipo = VACIO;
            }
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void ElimTab(Atributos ElimTab) {
        Linea_BE id = new Linea_BE();
        
        if (preAnalisis.equals("drop")) {
            // ELIMTAB -> drop table id
            emparejar("drop");
            emparejar("table");
            id = cmp.be.preAnalisis;            
            emparejar("id");
            //Accion Semantica 38
            if(analizarSemantica) {
                if(cmp.ts.buscaTipo(id.entrada).equals("tabla")) {
                    ElimTab.tipo = VACIO;
                } else {
                    ElimTab.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[ElimTab] Incompatibilidad de tipos de asignación");                                         

                }
            }
        } else {
            error("[ELIMTAB] El Tipo de Dato es Incorrecto."
                    + "Se esperaba drop, table o id."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void IfElse(Atributos IfElse) {
        Atributos ExprCond = new Atributos();
        Atributos Sentencias = new Atributos();
        Atributos IfElsePrima = new Atributos();
        
        if (preAnalisis.equals("if")) {
            // IFELSE -> if EXPRCOND begin SENTENCIAS end IFELSE'
            emparejar("if");
            ExprCond(ExprCond);
            emparejar("begin");
            Sentencias(Sentencias);
            emparejar("end");
            IfElsePrima(IfElsePrima);
            //Accion Semantica 21
            if(analizarSemantica) {
                if(ExprCond.tipo.equals("boolean") && Sentencias.tipo.equals(VACIO)) {
                    IfElse.tipo = VACIO;
                } else {
                    IfElse.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[IfElse] Error de tipos en la comprobación del if-else");
                    
                }
            }
        } else {
            error("[IFELSE] El Tipo de Dato es Incorrecto."
                    + "Se esperaba if, begin o end"
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void IfElsePrima(Atributos IfElsePrima) {
        Atributos Sentencias = new Atributos();
        if (preAnalisis.equals("else")) {
            // IFELSE' -> else begin SENTENCIAS end
            emparejar("else");
            emparejar("begin");
            Sentencias(Sentencias);
            emparejar("end");
            //Accion Semantica 22
            if(analizarSemantica) {
                IfElsePrima.tipo = Sentencias.tipo;
            }
        } else {
            // IFELSE' -> empty
            //Accion Semantica 23
            if(analizarSemantica) {
                IfElsePrima.tipo = VACIO;
            }
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Igualacion(Atributos Igualacion) {
        Atributos Exparit = new Atributos();
        Atributos IgualacionPrima = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if (preAnalisis.equals("id")) {
            // IGUALACION -> id opasig EXPRARIT IGUALACIONprima
            id = cmp.be.preAnalisis;            
            emparejar("id");
            emparejar("opasig");
            Exparit( Exparit );
            //Accion Semantica 44
            if(analizarSemantica) {
                if(cmp.ts.buscaTipo(id.entrada).equals(Exparit.tipo)) {
                    Igualacion.h = VACIO;
                } else if(cmp.ts.buscaTipo(id.entrada).matches(patron) && Exparit.tipo.matches(patron)) {
                    Igualacion.h = VACIO;
                } else if(cmp.ts.buscaTipo(id.entrada).equals("float") && Exparit.tipo.equals("int")) {
                    Igualacion.h = VACIO;
                } else if (cmp.ts.buscaTipo(id.entrada) == null) {
                    Igualacion.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Igualacion] Variable id.lexema no declarada");                                                             
                } else {
                    Igualacion.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Igualacion] Incompatibilidad de tipos de asignación");                                                             
                }
            }
            IgualacionPrima( IgualacionPrima );
            //Accion Semantica 45
            if(analizarSemantica) {
                if(Igualacion.h.equals(VACIO) && IgualacionPrima.tipo.equals(VACIO)) {
                    Igualacion.tipo = VACIO;
                } else {
                    Igualacion.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Igualacion] Incompatibilidad en la igualacion de variables");                                                             
                }
            }
        } else {
            error("[IGUALACION] El Tipo de Dato es Incorrecto."
                    + "Se esperaba id u opasig."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void IgualacionPrima(Atributos IgualacionPrima) {
        Atributos Igualacion = new Atributos();
        if (preAnalisis.equals(",")) {
            emparejar(",");
            Igualacion(Igualacion);
            //Accion Semantica 46
            if(analizarSemantica) {
                IgualacionPrima.tipo = Igualacion.tipo;
            }
        } else {
            // IGUALACIONprima -> empty
            //Accion Semantica 47
            if(analizarSemantica) {
                IgualacionPrima.tipo = VACIO;
            }
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Insercion(Atributos Insercion) {
        Atributos Columnas = new Atributos();
        Atributos Expresiones = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if (preAnalisis.equals("insert")) {
            emparejar("insert");
            emparejar("into");
            id = cmp.be.preAnalisis;            
            emparejar("id");
            //Accion Semantica 55
            if(analizarSemantica) {
                if(checarArchivo(id.lexema)) {
                    Insercion.h = VACIO;
                } else {
                    Insercion.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Insercion] El identificador no es una tabla");                                                             
                }
            }
            emparejar("(");
            Columnas(Columnas);
            emparejar(")");
            //Accion Semantica 56
            if(analizarSemantica) {
                if(Insercion.h.equals(VACIO) && Columnas.tipo.equals("boolean")) {
                    Insercion.tipo = VACIO;
                } else {
                    Insercion.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Insercion] Error en la expresion condicional");                                                             
                }
            }
            emparejar("values");
            emparejar("(");
            Expresiones(Expresiones);
            emparejar(")");
            //Accion Semantica 57
            if(analizarSemantica) {
                if(Insercion.h.equals(VACIO) && Expresiones.tipo.equals("boolean")) {
                    Insercion.tipo = VACIO;
                } else {
                    Insercion.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Insercion] Error en la expresion condicional");                                                             
                }
            }
        } else {
            error("[Insercion] se esperaba un insert");
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void ListaIds() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            emparejar("id");
            ListaIds();
        } else {
            // LISTAIDs -> empty
        }
    }

    // Autor: Arturo Fernandez Alvarez**
    private void Nulo( Atributos Nulo ) {                
        
        if (preAnalisis.equals("null")) {
            emparejar("null");
            
            // Acción semántica 85
            if ( analizarSemantica ) {
                Nulo.tipo = "null";
            }
            
        } else if (preAnalisis.equals("not")) {
            emparejar("not");
            emparejar("null");
            
            // Acción semántica 86
            if ( analizarSemantica ) {
                Nulo.tipo = "not null";
            }
            
            
        } else {
            // NULO -> empty
            
            // Acción semántica 87
            if ( analizarSemantica ) {
                Nulo.tipo = VACIO;
            }
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Operando( Atributos Operando ) {
        
        Linea_BE idvar      = new Linea_BE();
        Linea_BE literal    = new Linea_BE();
        Linea_BE id         = new Linea_BE();
        Linea_BE num         = new Linea_BE();
        Linea_BE numnum         = new Linea_BE();
        
        if (preAnalisis.equals("num")) {
            num = cmp.be.preAnalisis;
            emparejar("num");
            
            // Acción semántica 62
            if ( analizarSemantica ) {
                Operando.tipo = "int";
                Operando.lexema = num.lexema;
            }
            
        } else if (preAnalisis.equals("num.num")) {
            numnum = cmp.be.preAnalisis;
            emparejar("num.num");
            
            // Acción semántica 63
            if ( analizarSemantica ) {
                Operando.tipo = "float";
                Operando.lexema = numnum.lexema;
            }
            
        } else if (preAnalisis.equals("idvar")) {
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            
            // Acción semántica 64
            if ( analizarSemantica ) {
                Operando.tipo = cmp.ts.buscaTipo( idvar.entrada );
            }
            
        } else if (preAnalisis.equals("literal")) {
            literal = cmp.be.preAnalisis;
            emparejar("literal");
            
            // Acción semántica 65
            if ( analizarSemantica ) {
                Operando.tipo = "array( 1.." + literal.lexema.length() + ", char )";
                Operando.lexema = literal.lexema;
            }
            
        } else if (preAnalisis.equals("id")) {
            id = cmp.be.preAnalisis;
            emparejar("id");
            
            // Acción semántica 66
            if ( analizarSemantica ) {
                Operando.tipo = cmp.ts.buscaTipo( id.entrada );
            }
            
        } else {
            error("[OPERANDO] El Tipo de Dato es Incorrecto."
                    + "Se esperaba se esperaba una expresion aritmetica."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Sentencias(Atributos Sentencias) {
        Atributos Sentencia = new Atributos();
        if (preAnalisis.equals("if")|| preAnalisis.equals("while")
                || preAnalisis.equals("print") || preAnalisis.equals("assign") || preAnalisis.equals("select") || preAnalisis.equals("delete")
                || preAnalisis.equals("insert") || preAnalisis.equals("update") || preAnalisis.equals("create") || preAnalisis.equals("drop")
                || preAnalisis.equals("case")) {
            Sentencia(Sentencia);
            Sentencias(Sentencias);
            //Accion Semantica 8 pendiente
            if(analizarSemantica) {
                if(Sentencia.tipo.equals(VACIO) && Sentencias.tipo.equals(VACIO)) {
                    Sentencias.tipo = VACIO;
                } else {
                    Sentencias.tipo = ERROR_TIPO;
                cmp.me.error(Compilador.ERR_SEMANTICO, "[Sentencias] Error de tipo Sentencia");
                }
            }
        } else {
            // SENTENCIAS -> empty
            //Accion Semantica 9
            if(analizarSemantica) {
                Sentencias.tipo = VACIO;
            }
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Sentencia(Atributos Sentencia) {
        Atributos IfElse = new Atributos();
        Atributos SenRep = new Atributos();
        Atributos Despliegue = new Atributos();
        Atributos SentAsig = new Atributos();
        Atributos SentSelect = new Atributos();
        Atributos DelReg = new Atributos();
        Atributos Insercion = new Atributos();
        Atributos Actregs = new Atributos();
        Atributos Tabla = new Atributos();
        Atributos ElimTab = new Atributos();
        Atributos Selectiva = new Atributos();
        
        if (preAnalisis.equals("if")) {
            IfElse(IfElse);
            //Accion Semantica 10
            if(analizarSemantica) {
                Sentencia.tipo = IfElse.tipo;
            }
        } else if (preAnalisis.equals("while")) {
            SenRep(SenRep);
            //Accion Semantica 11
            if(analizarSemantica) {
                Sentencia.tipo = SenRep.tipo;
            }            
        } else if (preAnalisis.equals("print")) {
            Despliegue(Despliegue);
            //Accion Semantica 12
            if(analizarSemantica) {
                Sentencia.tipo = Despliegue.tipo;                
            }            
        } else if (preAnalisis.equals("assign")) {
            SentAsig(SentAsig);
            //Accion Semantica 13
            if(analizarSemantica) {
                Sentencia.tipo = SentAsig.tipo;                
            }            
        } else if (preAnalisis.equals("select")) {
            SentSelect(SentSelect);
            //Accion Semantica 14
            if(analizarSemantica) {
                Sentencia.tipo = SentSelect.tipo;                
            }            
        } else if (preAnalisis.equals("delete")) {
            DelReg(DelReg);
            //Accion Semantica 15
            if(analizarSemantica) {
                Sentencia.tipo = DelReg.tipo;                
            }            
        } else if (preAnalisis.equals("insert")) {
            Insercion(Insercion);
            //Accion Semantica 16
            if(analizarSemantica) {
                Sentencia.tipo = Insercion.tipo;                
            }            
        } else if (preAnalisis.equals("update")) {
            Actregs(Actregs);
            //Accion Semantica 17
            if(analizarSemantica) {
                Sentencia.tipo = Actregs.tipo;
            }            
        } else if (preAnalisis.equals("create")) {
            Tabla(Tabla);
            //Accion Semantica 18
            if(analizarSemantica) {
                Sentencia.tipo = Tabla.tipo;
            }            
        } else if (preAnalisis.equals("drop")) {
            ElimTab(ElimTab);
            //Accion Semantica 19
            if(analizarSemantica) {
                Sentencia.tipo = ElimTab.tipo;                
            }            
        } else if (preAnalisis.equals("case")) {
            Selectiva(Selectiva);
            //Accion Semantica 20
            if(analizarSemantica) {
                Sentencia.tipo = Selectiva.tipo;                
            }            
        } else {
            error("[SENTENCIA] El Tipo de Dato es Incorrecto."
                    + "Se esperaba id u opasig."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Selectiva(Atributos Selectiva) {
        Atributos SelWhen = new Atributos();
        Atributos SelElse = new Atributos();
        if (preAnalisis.equals("case")) {
            emparejar("case");
            SelWhen(SelWhen);
            SelElse(SelElse);
            emparejar("end");
            //Accion Semantica 54
            if(analizarSemantica) {
                if(SelWhen.tipo.equals(VACIO) && SelElse.tipo.equals(VACIO)) {
                    Selectiva.tipo = VACIO;
                } else {
                    Selectiva.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Selectiva] Error de sintaxis en la clausula CASE");                                                             
                }
            }
        } else {
            error("[SELECTIVA] El Tipo de Dato es Incorrecto."
                    + "Se esperaba id u opasig."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    // Autor: Ivanovicx Nuñez -----------------------------------------------------
    private void SelWhen (Atributos SelWhen) {
        Atributos ExprCond = new Atributos();
        Atributos Sentencia = new Atributos();
        Atributos SelWhenPrima = new Atributos();
        if ( preAnalisis.equals( "when" ) ) {
            // SELWHEN -> when EXPRCOND then SENTENCIA SELWHEN_PRIMA()
            emparejar ( "when" );
            ExprCond (ExprCond);
            //Accion Semantica 50
            if(analizarSemantica) {
                if(ExprCond.tipo.equals("boolean")) {
                    SelWhen.h = VACIO;
                } else {
                    SelWhen.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SelWhen] Expresion condicional invalida");                                                             
                }
            }
            emparejar ( "then" );
            Sentencia (Sentencia);
            SelWhenPrima (SelWhenPrima);
            //Accion Semantica 51
            if(analizarSemantica) {
                if(Sentencia.tipo.equals(VACIO) && SelWhenPrima.tipo.equals(VACIO) && SelWhen.tipo.equals(VACIO)) {
                    SelWhen.tipo = VACIO;
                } else {
                    SelWhen.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SelWhen] La sentencia no es valida");                                                             
                }
            }
        } else {
            error ( "[SelWhen] -> Se esperaba la palabra reservada 'when'");
        }
    }
    
    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SelWhenPrima (Atributos SelWhenPrima) {
        Atributos SelWhen = new Atributos();
        if ( preAnalisis.equals( "when" ) ) {
            // SELWEN_PRIMA -> SELWHEN
            SelWhen (SelWhen);
            //Accion Semantica 48
            if(analizarSemantica) {
                SelWhenPrima.tipo = SelWhen.tipo;
            }
        } else {
            // SELWEN_PRIMA -> empty
            //Accion Semantica 49
            if(analizarSemantica) {
                SelWhenPrima.tipo = VACIO;
            }
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SelElse (Atributos SelElse) {
        Atributos Sentencia = new Atributos();
        if ( preAnalisis.equals( "else" ) ) {
            // SELELSE -> else SENTENCIA
            emparejar ("else");
            Sentencia (Sentencia);   
            //Accion Semantica 52
            if(analizarSemantica) {
                SelElse.tipo = Sentencia.tipo;
            }
        } else {
            // SELELSE -> else SENTENCIA
            //Accion Semantica 53
            if(analizarSemantica) {
                SelElse.tipo = VACIO;
            }
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SenRep (Atributos SenRep) {
        Atributos ExprCond = new Atributos();
        Atributos Sentencias = new Atributos();
        if ( preAnalisis.equals( "while" ) ) {
            // SENREP -> while EXPRCOND begin SENTENCIAS end
            emparejar( "while" );
            ExprCond(ExprCond);
            emparejar( "begin" );
            Sentencias(Sentencias);
            emparejar( "end" );
            //Accion Semantica 24
            if(analizarSemantica) {
                if(ExprCond.tipo.equals("boolean") && Sentencias.tipo.equals(VACIO)) {
                    SenRep.tipo = VACIO;
                } else {
                    SenRep.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SenRep] Error de tipos en la comprobación del while");
                }
            }
        } else {
            error ( "[SenRep] -> Se esperaba la palabra reservada 'while'");
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SentAsig (Atributos SentAsig) {
        Atributos Exparit = new Atributos();
        Linea_BE idvar = new Linea_BE();
        if ( preAnalisis.equals( "assign" ) ) {
            // SENTASIG -> assign idvar opasig EXPRARIT
            emparejar ( "assign" );
            idvar = cmp.be.preAnalisis;
            
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            Exparit(Exparit);
            //Accion Semantica 31 pendiente
            if(analizarSemantica) {
                if(cmp.ts.buscaTipo(idvar.entrada).equals(Exparit.tipo)) {
                    SentAsig.tipo = VACIO;
                } else if(cmp.ts.buscaTipo(idvar.entrada).matches(patronArray) && Exparit.tipo.matches(patronArray)) {
                    SentAsig.tipo = VACIO;
                } else if(cmp.ts.buscaTipo(idvar.entrada).equals("float") && Exparit.tipo.equals("int")) {
                    SentAsig.tipo = VACIO;
                } else if(cmp.ts.buscaTipo(idvar.entrada) == null) {
                    SentAsig.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SentAsig] Variable no declarada: idvar.lexema");                                         
                } else {
                    SentAsig.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[SentAsig] Incompatibilidad de tipos de asignación");                                         
                }
            }
        } else {
            error ( "[SentAsig] -> Se esperaba la palbra reservada 'assign'");
        }
    }
    
    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SentSelect ( Atributos SentSelect ) {
        
        Linea_BE idvar          = new Linea_BE ();
        Linea_BE id             = new Linea_BE ();
        Atributos ExprCond      = new Atributos ();
        Atributos SentSelectC   = new Atributos ();
        ArrayList < Tupla > array_select = new ArrayList<> ();
        
        if ( preAnalisis.equals( "select" ) ) {
            
            idvar = cmp.be.preAnalisis;
            id = cmp.be.preAnalisis;
            
            // SENTSELECT -> select idvar opasig id SENTSELECTC from id where EXPRCOND
            emparejar ( "select" );
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            emparejar ( "id" );
            
            // Acción semántica 70
            if ( analizarSemantica ) {
                Tupla tupla = new Tupla( cmp.ts.buscaTipo( idvar.entrada ), id.entrada );
                array_select.add( tupla );
            }
            
            SentSelectC( SentSelectC );
            emparejar ( "from" );
            emparejar ( "id" );
            
            // Acción semántica 71
            if ( analizarSemantica ) {
                if ( checarArchivo( id.lexema ) ) {
                    cmp.ts.anadeTipo( id.entrada , "tabla");
                    SentSelect.tipo = VACIO;
                } else {
                    SentSelect.tipo = ERROR_TIPO;
                    cmp.me.error( Compilador.ERR_SINTACTICO, "El identificador " + id.lexema + " no es una tabla" );
                }
            }
            
            emparejar ( "where" );
            ExprCond ( ExprCond );
            
            // Acción semántica 72
            if ( analizarSemantica ) {
                if ( SentSelect.h.equals( VACIO ) && ExprCond.tipo.equals( "boolean" ) ) {
                    for ( int i = 0; i < array_select.size(); i++ ) {
                        for ( int j = 0; j < array_select.size(); j++ ) {
                            if ( ( array_select.get(j).getTipo() .equals( cmp.ts.buscaTipo( id.entrada ) ) ) &&
                                 ( cmp.ts.buscaTipo( id.entrada ).equals( patronColumna ) ) ) {
                                if ( array_select.get(i).getTipo().equals( cmp.ts.buscaTipo( id.entrada ) ) ) {
                                    SentSelect.tipo = VACIO;
                                } else if ( array_select.get(i).getTipo().matches( patron ) &&
                                            cmp.ts.buscaTipo( id.entrada ).matches( patron ) ) {
                                    SentSelect.tipo = VACIO;
                                } else if ( array_select.get(i).getTipo().equals( "float" ) &&
                                            cmp.ts.buscaTipo( id.entrada ).equals( "int" ) ) {
                                    SentSelect.tipo = VACIO;
                                } else if ( array_select.get(i).getTipo() == null ) {
                                    SentSelect.tipo = ERROR_TIPO;
                                    cmp.me.error( Compilador.ERR_SEMANTICO, "[SentSelect] Variable " + id.lexema + " no declarada.");                                    
                                } else {
                                    SentSelect.tipo = ERROR_TIPO;
                                    cmp.me.error( Compilador.ERR_SEMANTICO, "[SentSelect] Incompatibilidad de tipos en la asignación.");                                    
                                }
                            }
                        }
                    }
                }
            }
            
        } else {
            error ( "[SentSelect] -> Se esperaba la palabra reservada 'select'");
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SentSelectC ( Atributos SentSelectC ) {
        
        Linea_BE idvar          = new Linea_BE ();
        Linea_BE id             = new Linea_BE ();
        Atributos SentSelectC1  = new Atributos ();
        ArrayList < Tupla > array_select = new ArrayList<> ();
        
        if ( preAnalisis.equals( "," ) ) {
            
            idvar = cmp.be.preAnalisis;
            id = cmp.be.preAnalisis;
            
            // SENTSELECTC -> , idvar opasig id SENTSELECTC
            emparejar ( "," );
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            emparejar ( "id" );
            
            // Acción semántica 73
            if ( analizarSemantica ) {
                array_select.add( new Tupla( cmp.ts.buscaTipo( idvar.entrada ), id.entrada ) );
            }
            
            SentSelectC ( SentSelectC1 );
            
            // Acción semántica 74 
            if ( analizarSemantica ) {
                SentSelectC.tipo = VACIO;
            }
            
            
        } else {
            // SENTSELECTC -> empty
            
            // Acción semántica 75
            if ( analizarSemantica ) {
                SentSelectC.tipo = VACIO;
            }
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void Tipo (Atributos Tipo) {
        Linea_BE num = new Linea_BE();

        if ( preAnalisis.equals( "int" ) ) {
            // TIPO -> int
            emparejar ( "int" );
            //Accion Semantica 5
            if(analizarSemantica) {
                Tipo.tipo = "int";
            }
        } else if ( preAnalisis.equals( "float" ) ) {
            // TIPO -> float
            emparejar ( "float" );
            //Accion Semantica 6
            if(analizarSemantica) {
                Tipo.tipo = "float";
            }
        } else if ( preAnalisis.equals( "char" ) ) {
            // TIPO -> char (num)
            emparejar ( "char" );
            emparejar ( "(" );
            num = cmp.be.preAnalisis;            
            emparejar ( "num" );
            emparejar ( ")" );
            //Accion Semantica 7 pendiente
            if(analizarSemantica) {
                Tipo.tipo = "array( 1.." + num.lexema + ", char )";
            }
        } else {
            error ( "[Tipo] -> Se esperaba un tipo de dato válido" );
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void Tabla (Atributos Tabla) {
        Atributos TabColumnas = new Atributos();
        Linea_BE id = new Linea_BE();
        
        if ( preAnalisis.equals( "create" ) ) {
            // TABLA -> create table id ( TABCOLUMAS )
            emparejar ( "create" );
            emparejar ( "table" );
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            //Accion Semantica 32
            if(analizarSemantica) {
                if(cmp.ts.buscaTipo(id.entrada) == null) {
                    cmp.ts.anadeTipo(id.entrada, "tabla");
                    Tabla.h = VACIO;
                } else {
                    Tabla.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Tabla] Identificador de tabla ya ha sido declarado");                                                             
                }
            }
            emparejar ( "(" );
            TabColumnas (TabColumnas);
            emparejar ( ")" );
            //Accion Semantica 33
            if(analizarSemantica) {
                if(Tabla.h.equals(VACIO) && TabColumnas.h.equals(VACIO)) {
                    Tabla.tipo = VACIO;
                } else {
                    Tabla.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[Tabla] Error de tipos en sentencia CREATE TABLE");                                                             
                }
            }
        } else {
            error ( "[Tabla] -> Se esperaba la palabra reservada 'create'" );
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void TabColumnas (Atributos TabColumnas) {        
        Atributos Tipo = new Atributos();
        Atributos Nulo = new Atributos();
        Atributos TabColumnasPrima = new Atributos();
        Linea_BE id = new Linea_BE();        
        if ( preAnalisis.equals ( "id" ) ) {
            // TABCOLUMNAS -> id TIPO NULO TABCOLUMNAS_PRIMA
            id = cmp.be.preAnalisis;            
            emparejar ( "id" );
            Tipo (Tipo);
            //Accion Semantica 34
            if(analizarSemantica) {
                if(cmp.ts.buscaTipo(id.entrada) == null) {
                    cmp.ts.anadeTipo(id.entrada, "columna(Tipo.tipo)");
                    TabColumnas.h = VACIO;
                } else {
                    TabColumnas.h = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[TabColumnas] Identificador de columnas ya declarado");                                                             
                }
            }
            Nulo (Nulo);
            TabColumnasPrima (TabColumnasPrima);
            //Accion Semantica 35
            if(analizarSemantica) {
                if(TabColumnas.h.equals(VACIO) && TabColumnasPrima.tipo.equals(VACIO)) {
                    TabColumnas.tipo = VACIO;
                } else {
                    TabColumnas.tipo = ERROR_TIPO;
                    cmp.me.error(Compilador.ERR_SEMANTICO, "[TabColumnas] Error de TIPOS");                                                             
                }
            }
        } else {
            error ( "[TabColumnas] -> Se esperaba un nombre de columna");
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void TabColumnasPrima (Atributos TabColumnasPrima) {
        Atributos TabColumnas = new Atributos();
        if ( preAnalisis.equals( "," ) ) {
            // TABCOLUMNAS_PRIMA -> , TABCOLUMNAS
            emparejar ( "," );
            TabColumnas (TabColumnas);
            //Accion Semantica 36
            if(analizarSemantica) {
                TabColumnasPrima.tipo = TabColumnas.tipo;
            }
        } else {
            // TABCOLUMNAS_PRIMA -> empty
            //Accion Semantica 37
            if(analizarSemantica) {
                TabColumnasPrima.tipo = VACIO;
            }
        }
    }
}
    
//------------------------------------------------------------------------------
//::