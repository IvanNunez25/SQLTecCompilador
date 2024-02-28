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

import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean    analizarSemantica = false;
    private String     preAnalisis;
    
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
        
    }

    //--------------------------------------------------------------------------

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
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ProgramaSQL() {
        if (preAnalisis.equals("end")) {
        Declaracion();
	Sentencias();
	emparejar("end");
    } else {
            error("[ProgramaSQL]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Actregs() {
        if (preAnalisis.equals("id")) {
            // ACTREGS -> update id  set  IGUALACION   where EXPRCOND
            emparejar("update");
            emparejar("id");
            emparejar("set");
            Igualacion();
            emparejar("where");
            ExprCond();
        } else {
            error("[Actregs]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Columnas() {
        if(preAnalisis.equals("id")) {
            //Columnas -> id ColumnasPrima
            emparejar("id");
            ColumnasPrima();
        } else {
            error("[Columnas]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ColumnasPrima() {
        if(preAnalisis.equals(",")) {
            //ColumnasPrima -> , Columnas
            emparejar(",");
            Columnas();
        } else {
            error("[ColumnasPrima]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Declaracion() {
        if(preAnalisis.equals("declare")) {
            //Declaracion -> declare idvar Tipo Declaracion
            emparejar("declare");
            emparejar("idvar");
            Tipo();
            Declaracion();
        } else {
            //Declaracoin -> Empty
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Despliegue() {
        if(preAnalisis.equals("print")) {
            //Despliegue -> print Exprarit
            emparejar("print");
            Exparit ();
        } else {
            error("[Despliegue]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void DelReg() {
        if(preAnalisis.equals("delete")) {
            //DelReg -> delete from id where ExprCond
            emparejar("delete");
            emparejar("from");
            emparejar("id");
            emparejar("where");
            ExprCond();
        } else {
            error("[DelReg]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Expresiones() {
        if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
            //Expresiones -> Exparit ExpresionesPrima
            Exparit();
            ExpresionesPrima();
        } else {
            error("[Expresiones]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ExpresionesPrima() {
        if(preAnalisis.equals(",")) {
            //ExpresionesPrima -> , Expresiones
            emparejar(",");
            Expresiones();
        } else {
            //ExpresionesPrima -> empty
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Exparit() {
        if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
            //Exparit -> Operando ExparitPrima
            Operando();
            ExparitPrima();
        } else if(preAnalisis.equals("(")) {
            //Exparit -> (Exparit) ExparitPrima
            emparejar("(");
            Exparit();
            emparejar(")");
            ExparitPrima();
        } else {
            error("[Exparit]");
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ExparitPrima() {
        if(preAnalisis.equals("opsuma")) {
            //ExparitPrima -> opsuma Exparit
            emparejar("opsuma");
            Exparit();
        } else if(preAnalisis.equals("opmult")) {
            //ExparitPrima -> opmult Exparit
            emparejar("opmult");
            Exparit();
        } else {
            //ExparitPrima -> empty
        }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void ExprCond() {
         if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
             //ExpCond -> Exprrel
             Exprrel();
             Exprlog();
         } else {
             error("[ExprCond]");
         }
    }
    //-------------------------------------------------------------
    //Autor: Daniel Vargas Hernandez
    private void Exprrel() {
         if(preAnalisis.equals("num") || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar") || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {
             //Exprrel -> Exparit oprel Exparit
             Exparit();
             emparejar("oprel");
             Exparit();
         } else {
             error("[Exprrel]");
         }
    }
    
    // Autor: Arturo Fernandez Alvarez
    private void Exprlog() {
        if (preAnalisis.equals("and")) {
            //EXPRLOG -> and EXPRREL
            emparejar("and");
            Exprrel();
        } else {
            // EXPRLOG -> empty
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void ElimTab() {
        if (preAnalisis.equals("drop")) {
            // ELIMTAB -> drop table id
            emparejar("drop");
            emparejar("table");
            emparejar("id");
        } else {
            error("[ELIMTAB] El Tipo de Dato es Incorrecto."
                    + "Se esperaba drop, table o id."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void IfElse() {
        if (preAnalisis.equals("if")) {
            // IFELSE -> if EXPRCOND begin SENTENCIAS end IFELSE'
            emparejar("if");
            ExprCond();
            emparejar("begin");
            Sentencias();
            emparejar("end");
            IfElsePrima();
        } else {
            error("[IFELSE] El Tipo de Dato es Incorrecto."
                    + "Se esperaba if, begin o end"
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void IfElsePrima() {
        if (preAnalisis.equals("else")) {
            // IFELSE' -> else begin SENTENCIAS end
            emparejar("else");
            emparejar("begin");
            Sentencias();
            emparejar("end");
        } else {
            // IFELSE' -> empty
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Igualacion() {
        if (preAnalisis.equals("id")) {
            // IGUALACION -> id opasig EXPRARIT IGUALACIONprima
            emparejar("id");
            emparejar("opasig");
            Exparit();
            IgualacionPrima();
        } else {
            error("[IGUALACION] El Tipo de Dato es Incorrecto."
                    + "Se esperaba id u opasig."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void IgualacionPrima() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            Igualacion();
        } else {
            // IGUALACIONprima -> empty
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Insercion() {
        if (preAnalisis.equals("insert")) {
            emparejar("insert");
            emparejar("into");
            emparejar("id");
            emparejar("(");
            Columnas();
            emparejar(")");
            emparejar("values");
            emparejar("(");
            Expresiones();
            emparejar(")");
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

    // Autor: Arturo Fernandez Alvarez
    private void Nulo() {
        if (preAnalisis.equals("null")) {
            emparejar("null");
        } else if (preAnalisis.equals("not null")) {
            emparejar("not null");
        } else {
            // NULO -> empty
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Operando() {
        if (preAnalisis.equals("num")) {
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
        } else if (preAnalisis.equals("idvar")) {
            emparejar("idvar");
        } else if (preAnalisis.equals("literal")) {
            emparejar("literal");
        } else if (preAnalisis.equals("id")) {
            emparejar("id");
        } else {
            error("[OPERANDO] El Tipo de Dato es Incorrecto."
                    + "Se esperaba id u opasig."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Sentencias() {
        if (preAnalisis.equals("if")) {
            Sentencia();
            Sentencias();
        } else {
            // SENTENCIAS -> empty
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Sentencia() {
        if (preAnalisis.equals("if")) {
            IfElse();
        } else if (preAnalisis.equals("while")) {
            SenRep();
        } else if (preAnalisis.equals("print")) {
            Despliegue();
        } else if (preAnalisis.equals("assign")) {
            SentAsig();
        } else if (preAnalisis.equals("select")) {
            SentSelect();
        } else if (preAnalisis.equals("delete")) {
            DelReg();
        } else if (preAnalisis.equals("id")) {
            Insercion();
        } else if (preAnalisis.equals("update")) {
            Actregs();
        } else if (preAnalisis.equals("create")) {
            Tabla();
        } else if (preAnalisis.equals("drop")) {
            ElimTab();
        } else if (preAnalisis.equals("case")) {
            Selectiva();
        } else {
            error("[SENTENCIA] El Tipo de Dato es Incorrecto."
                    + "Se esperaba id u opasig."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

    // Autor: Arturo Fernandez Alvarez
    private void Selectiva() {
        if (preAnalisis.equals("case")) {
            emparejar("case");
            SelWhen();
            SelElse();
            emparejar("end");
        } else {
            error("[SELECTIVA] El Tipo de Dato es Incorrecto."
                    + "Se esperaba id u opasig."
                    + "No. Linea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
    
    // Autor: Ivanovicx Nuñez -----------------------------------------------------
    private void SelWhen () {
        if ( preAnalisis.equals( "when" ) ) {
            // SELWHEN -> when EXPRCOND then SENTENCIA SELWHEN_PRIMA()
            emparejar ( "when" );
            ExprCond ();
            emparejar ( "then" );
            Sentencia ();
            SelWhenPrima ();
        } else {
            error ( "[SelWhen] -> Se esperaba la palabra reservada 'when'");
        }
    }
    
    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SelWhenPrima () {
        if ( preAnalisis.equals( "when" ) ) {
            // SELWEN_PRIMA -> SELWHEN
            SelWhen ();
        } else {
            // SELWEN_PRIMA -> empty
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SelElse () {
        if ( preAnalisis.equals( "else" ) ) {
            // SELELSE -> else SENTENCIA
            emparejar ( "else" );
            Sentencia ();            
        } else {
            // SELELSE -> else SENTENCIA
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SenRep () {
        if ( preAnalisis.equals( "while" ) ) {
            // SENREP -> while EXPRCOND begin SENTENCIAS end
            emparejar ( "while" );
            ExprCond ();
            emparejar( "begin" );
            Sentencias ();
            emparejar( "end" );
        } else {
            error ( "[SenRep] -> Se esperaba la palabra reservada 'while'");
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SentAsig () {
        if ( preAnalisis.equals( "assign" ) ) {
            // SENTASIG -> assign idvar opasig EXPRARIT
            emparejar ( "assign" );
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            Exparit();
        } else {
            error ( "[SentAsig] -> Se esperaba la palbra reservada 'assign'");
        }
    }
    
    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SentSelect () {
        if ( preAnalisis.equals( "select" ) ) {
            // SENTSELECT -> select idvar opasig id SENTSELECTC from id where EXPRCOND
            emparejar ( "select" );
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            emparejar ( "id" );
            SentSelectC();
            emparejar ( "from" );
            emparejar ( "id" );
            emparejar ( "where" );
            ExprCond ();
        } else {
            error ( "[SentSelect] -> Se esperaba la palabra reservada 'select'");
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void SentSelectC () {
        if ( preAnalisis.equals( "," ) ) {
            // SENTSELECTC -> , idvar opasig id SENTSELECTC
            emparejar ( "," );
            emparejar ( "idvar" );
            emparejar ( "opasig" );
            emparejar ( "id" );
            SentSelect ();
        } else {
            // SENTSELECTC -> empty
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void Tipo () {
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
            emparejar ( "num" );
            emparejar ( ")" );
        } else {
            error ( "[Tipo] -> Se esperaba un tipo de dato válido" );
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void Tabla () {
        if ( preAnalisis.equals( "create" ) ) {
            // TABLA -> create table id ( TABCOLUMAS )
            emparejar ( "create" );
            emparejar ( "table" );
            emparejar ( "id" );
            emparejar ( "(" );
            TabColumnas ();
            emparejar ( ")" );
        } else {
            error ( "[Tabla] -> Se esperaba la palabra reservada 'create'" );
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void TabColumnas () {
        if ( preAnalisis.equals ( "id" ) ) {
            // TABCOLUMNAS -> id TIPO NULO TABCOLUMNAS_PRIMA
            emparejar ( "id" );
            Tipo ();
            Nulo ();
            TabColumnasPrima ();
        } else {
            error ( "[TabColumnas] -> Se esperaba un nombre de columna");
        }
    }

    // Autor: Ivanovicx Nuñez -----------------------------------------------------

    private void TabColumnasPrima () {
        if ( preAnalisis.equals( "," ) ) {
            // TABCOLUMNAS_PRIMA -> , TABCOLUMNAS
            emparejar ( "," );
            TabColumnas ();
        } else {
            // TABCOLUMNAS_PRIMA -> empty
        }
    }
}
    
//------------------------------------------------------------------------------
//::