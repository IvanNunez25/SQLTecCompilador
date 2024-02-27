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
            Exprarir();
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
    
}
//------------------------------------------------------------------------------
//::