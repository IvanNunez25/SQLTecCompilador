/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: AGO-DIC/2013     HORA: 12-13 HRS
 *:                                   
 *:               
 *:    # Clase que representa una localidad del Buffer de Entrada
 *                 
 *:                           
 *: Archivo       : Linea_BE.java
 *: Autor         : Fernando Gil  
 *:
 *: Fecha         : 03/Octubre/2013
 *: Compilador    : Java JDK 7
 *: Descripción   : Clase en la que se definiran el contructor con los parametros necesarios para 
 *:                 nuestra linea del buffer de entrada asi como se definiran tambien las propiedades 
 *:           	    que se utilizaran para la obtencion de lexema, Complex y Entrada.  
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *:-----------------------------------------------------------------------------
 */


package compilador;

public class Linea_BE{
/*------------------------------------------------------------------------------------------------------------*/
//Declaracion de variables 
    
        protected String complex;
        protected String lexema;
        protected int entrada;    
         
/*------------------------------------------------------------------------------------------------------------*/
//Constructor de Linea_BE que no recibe ningun argumento     
        
        public Linea_BE( )
        {
            complex = "";
            lexema = "";
            entrada = 0;           
        }
/*------------------------------------------------------------------------------------------------------------*/
//Constructor de Linea_BE con los argumentos necesarios para ser utilizados en nuestra clase BufferEntrada
        
        public Linea_BE(String compleX, String lexemA, int entradA)
        {
            
            complex = compleX;
            lexema = lexemA;
            entrada = entradA;
            
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo set Complex 
        
        public void setComplex(String Compleex)
        {
            
            complex=Compleex;
            
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo GetComplex 
        
        public String getComplex()
        {
            
            return complex;
            
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo setLexema 
        
        public void setLexema(String Lexemaa)
        {
            
            lexema = Lexemaa;
            
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo getLexema
        
        public String getLexema()
        {
            
            return lexema;
            
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo setEntrada
        
        public void setEntrada(int Entradaa)
        {
            
            entrada = Entradaa;
            
        }
/*------------------------------------------------------------------------------------------------------------*/
//Metodo GetEntrada 
        
        public int getEntrada()
        {
            
            return entrada;
            
        }
/*------------------------------------------------------------------------------------------------------------*/
      

}
