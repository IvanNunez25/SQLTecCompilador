/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import general.Linea_BE;
import java.util.ArrayList;

/**
 *
 * @author ivan2
 */
public class Tupla {
    
    private Linea_BE idvar;
    private Linea_BE id;
    
    

//    public Tupla(String idvar, int id) {
//        this.idvar = idvar;
//        this.id = id;
//    }
//
//    public String getidvar() {
//        return idvar;
//    }
//
//    public void setidvar(String idvar) {
//        this.idvar = idvar;
//    }
//
//    public int getid() {
//        return id;
//    }
//
//    public void setid(int id) {
//        this.id = id;

    public Tupla(Linea_BE idvar, Linea_BE id) {
        this.idvar = idvar;
        this.id = id;
    }

    public Linea_BE getIdvar() {
        return idvar;
    }

    public void setIdvar(Linea_BE idvar) {
        this.idvar = idvar;
    }

    public Linea_BE getId() {
        return id;
    }

    public void setId(Linea_BE id) {
        this.id = id;
    }
    
    
    
}
