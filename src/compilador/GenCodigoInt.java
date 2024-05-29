/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:        SEMESTRE: ______________            HORA: ______________ HRS
 *:                                   
 *:               
 *:    # Clase con la funcionalidad del Generador de COdigo Intermedio
 *                 
 *:                           
 *: Archivo       : GenCodigoInt.java
 *: Autor         : Fernando Gil  
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   :  
 *:                  
 *:           	     
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */


package compilador;

import general.Linea_BE;
import general.Linea_TS;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;


public class GenCodigoInt {
 
    private Compilador cmp;
    private String preAnalisis;
    private boolean analizarSemantica = false;
    
    
    private static final String PATRON_COLUMNA_FLOAT = "COLUMNA\\(float\\)"; // COLUMNA(float)
    private static final String PATRON_COLUMNA_INT = "COLUMNA\\(int\\)";    // COLUMNA(int)
    private static final String PATRON_COLUMNA_1 = "COLUMNA\\(char\\(\\d+\\)\\)"; // columna(char(n))
    private static final String PATRON_ARRAY_1 = "array\\(\\s*1\\s*\\.\\.\\s*\\d+\\s*,\\s*char\\s*\\)"; // array( 1..n, char )    

    
    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
	public GenCodigoInt ( Compilador c ) {
        cmp = c;
    }
    // Fin del Constructor
    //--------------------------------------------------------------------------
	
    public void generar () {
        ProgramaSQL( new Atributos() );
    }    
    
    public void emite ( String c3d ) {
        cmp.iuListener.mostrarCodInt( c3d );
    }
    
    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }
    
    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;            
        } else {
            errorEmparejar( t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea );
        }
    }
    
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
            fr = new FileReader ( nomarchivo + ".db" );
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

    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ProgramaSQL( Atributos ProgramaSQL ) {
        
        Atributos Declaracion = new Atributos();
        Atributos Sentencias = new Atributos();
        if (preAnalisis.equals("declare") || preAnalisis.equals("end") || preAnalisis.equals("if") || preAnalisis.equals("while")
                || preAnalisis.equals("print") || preAnalisis.equals("assign") || preAnalisis.equals("select") || preAnalisis.equals("delete")
                || preAnalisis.equals("insert") || preAnalisis.equals("update") || preAnalisis.equals("create") || preAnalisis.equals("drop")
                || preAnalisis.equals("case")) {
        Declaracion(Declaracion);
	Sentencias(Sentencias);
	emparejar("end");
        
    } else {

            cmp.me.error( Compilador.ERR_CODINT, "[ProgramaSQL] se esperaba una declaracion, sentencia o end");
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
            
            emparejar("set");
            Igualacion(Igualacion);
            
            
            emparejar("where");
            ExprCond(ExprCond);
            
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
            
            
        } else {
            //ColumnasPrima -> empty
            
            
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
            
            Declaracion(Declaracion1);
            
        } else {
            //Declaracoin -> Empty
            
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
            
            emparejar("where");
            ExprCond(ExprCond);
            
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
            
            
        } else {
            //ExpresionesPrima -> empty
            
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
            
            ExparitPrima(ExparitPrima);
            
        } else if(preAnalisis.equals("(")) {
            //Exparit -> (Exparit) ExparitPrima
            emparejar("(");
            Exparit(Exparit);
            emparejar(")");
            
            ExparitPrima(ExparitPrima);
            
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
            
            
        } else if(preAnalisis.equals("opmult")) {
            //ExparitPrima -> opmult Exparit
            emparejar("opmult");
            Exparit( Exparit );
            
            
            
        } else {
            //ExparitPrima -> empty
            
            
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
             
         } else {
             error("[ExprCond] se esperaba una expresion aritmetica");
         }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Exprrel( Atributos Exprrel ) {
        
        Atributos Exparit = new Atributos();
        Atributos Exparit1 = new Atributos();
        
         if(preAnalisis.equals("num")       || 
            preAnalisis.equals("num.num")   || 
            preAnalisis.equals("idvar")     || 
            preAnalisis.equals("literal")   || 
            preAnalisis.equals("id")) {
             
             //Exprrel -> Exparit oprel Exparit
             
             Exparit( Exparit );
             emparejar("oprel");
             Exparit( Exparit1 );
             
             
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
            
        } else if(preAnalisis.equals("or")) {
            emparejar("or");
            Exprrel(Exprrel);
            
        }  else {
            // EXPRLOG -> empty
            
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
            
        } else {
            // IFELSE' -> empty
            
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
            
            IgualacionPrima( IgualacionPrima );
            
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
            
        } else {
            // IGUALACIONprima -> empty
            
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
            
            emparejar("(");
            Columnas(Columnas);
            emparejar(")");
            
            emparejar("values");
            emparejar("(");
            Expresiones(Expresiones);
            emparejar(")");
            
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
            
            
        } else if (preAnalisis.equals("not")) {
            emparejar("not");
            emparejar("null");
            
            
            
            
        } else {
            // NULO -> empty
            
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
            
            
        } else if (preAnalisis.equals("num.num")) {
            numnum = cmp.be.preAnalisis;
            emparejar("num.num");
            
            
            
        } else if (preAnalisis.equals("idvar")) {
            idvar = cmp.be.preAnalisis;
            emparejar("idvar");
            
            
        } else if (preAnalisis.equals("literal")) {
            literal = cmp.be.preAnalisis;
            emparejar("literal");
            
            
            
        } else if (preAnalisis.equals("id")) {
            id = cmp.be.preAnalisis;
            emparejar("id");
            
            
            
        } else {
            error("[OPERANDO] El Tipo de Dato es Incorrecto."
                    + "Se esperaba se esperaba una expresion aritmetica."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Sentencias(Atributos Sentencias) {
        
        Atributos Sentencia = new Atributos();
        Atributos Sentencias1 = new Atributos();
        
        if (preAnalisis.equals("if")|| preAnalisis.equals("while")
                || preAnalisis.equals("print") || preAnalisis.equals("assign") || preAnalisis.equals("select") || preAnalisis.equals("delete")
                || preAnalisis.equals("insert") || preAnalisis.equals("update") || preAnalisis.equals("create") || preAnalisis.equals("drop")
                || preAnalisis.equals("case")) {
            Sentencia( Sentencia );
            Sentencias( Sentencias1 );
            
        } else {
            // SENTENCIAS -> empty
            
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Sentencia( Atributos Sentencia ) {
        Atributos IfElse        = new Atributos();
        Atributos SenRep        = new Atributos();
        Atributos Despliegue    = new Atributos();
        Atributos SentAsig      = new Atributos();
        Atributos SentSelect    = new Atributos();
        Atributos DelReg        = new Atributos();
        Atributos Insercion     = new Atributos();
        Atributos Actregs       = new Atributos();
        Atributos Tabla         = new Atributos();
        Atributos ElimTab       = new Atributos();
        Atributos Selectiva     = new Atributos();
        
        if (preAnalisis.equals("if")) {
            IfElse(IfElse);
            
        } else if (preAnalisis.equals("while")) {
            SenRep(SenRep);
            
        } else if (preAnalisis.equals("print")) {
            Despliegue(Despliegue);
            
        } else if (preAnalisis.equals("assign")) {
            SentAsig(SentAsig);
            
        } else if (preAnalisis.equals("select")) {
            SentSelect(SentSelect);
            
        } else if (preAnalisis.equals("delete")) {
            DelReg(DelReg);
            
        } else if (preAnalisis.equals("insert")) {
            Insercion(Insercion);
            
        } else if (preAnalisis.equals("update")) {
            Actregs(Actregs);
            
        } else if (preAnalisis.equals("create")) {
            Tabla(Tabla);
            
        } else if (preAnalisis.equals("drop")) {
            ElimTab(ElimTab);
            
        } else if (preAnalisis.equals("case")) {
            Selectiva(Selectiva);
            
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
            
            emparejar ( "then" );
            Sentencia (Sentencia);
            SelWhenPrima (SelWhenPrima);
            
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
            
        } else {
            // SELWEN_PRIMA -> empty
            
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SelElse (Atributos SelElse) {
        Atributos Sentencia = new Atributos();
        if ( preAnalisis.equals( "else" ) ) {
            // SELELSE -> else SENTENCIA
            emparejar ("else");
            Sentencia (Sentencia);   
            
        } else {
            // SELELSE -> else SENTENCIA
            
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
        
        if ( preAnalisis.equals( "select" ) ) {
            
            
            
            
            // SENTSELECT -> select idvar opasig id SENTSELECTC from id where EXPRCOND
            emparejar ( "select" );
            
            idvar = cmp.be.preAnalisis;
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            
            
            
            SentSelectC( SentSelectC );
            emparejar ( "from" );
            
            id = cmp.be.preAnalisis;
            
            emparejar ( "id" );
            
            
            emparejar ( "where" );
            ExprCond ( ExprCond );
            
            
        } else {
            error ( "[SentSelect] -> Se esperaba la palabra reservada 'select'");
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SentSelectC ( Atributos SentSelectC ) {
        
        Linea_BE idvar          = new Linea_BE ();
        Linea_BE id             = new Linea_BE ();
        Atributos SentSelectC1  = new Atributos ();
        
        if ( preAnalisis.equals( "," ) ) {
            
            
            
            // SENTSELECTC -> , idvar opasig id SENTSELECTC
            emparejar ( "," );
            
            idvar = cmp.be.preAnalisis;            
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            
            id = cmp.be.preAnalisis;
            emparejar ( "id" );
            
            
            SentSelectC ( SentSelectC1 );
            
            
            
            
        } else {
            // SENTSELECTC -> empty
            
            
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void Tipo (Atributos Tipo) {
        Linea_BE num = new Linea_BE();

        if ( preAnalisis.equals( "int" ) ) {
            // TIPO -> int
            emparejar ( "int" );
            
        } else if ( preAnalisis.equals( "float" ) ) {
            // TIPO -> float
            emparejar ( "float" );
            
        } else if ( preAnalisis.equals( "char" ) ) {
            // TIPO -> char (num)
            emparejar ( "char" );
            emparejar ( "(" );
            num = cmp.be.preAnalisis;            
            emparejar ( "num" );
            emparejar ( ")" );
            
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
            
            emparejar ( "(" );
            TabColumnas (TabColumnas);
            emparejar ( ")" );
            
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
            
            Nulo (Nulo);
            TabColumnasPrima (TabColumnasPrima);
            
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
            
        } else {
            // TABCOLUMNAS_PRIMA -> empty
            
        }
    }
}
