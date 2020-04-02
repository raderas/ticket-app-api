package com.devs.ticketapp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.devs.ticketapp.dao.EstablecimientoModel;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class EstablecimientoController {
    @RequestMapping(value="/establecimiento/list", method=RequestMethod.GET)
    public String obtenerTotalColas(){
        EstablecimientoModel model= new EstablecimientoModel();
        String respuesta = model.obtenerTotalEstablecimientos();
        return respuesta;
    }
    

    
    @RequestMapping(value="/establecimiento", method=RequestMethod.POST)
    public String obtenerColaById(@RequestBody String jSonRequest){
        EstablecimientoModel model= new EstablecimientoModel();
        String respuesta = model.obtenerEstablecimientoID(jSonRequest);
        return respuesta;
    }
     
    @RequestMapping(value="/establecimiento/add", method=RequestMethod.POST)
    public String insertarCola(@RequestBody String jSonRequest){
        EstablecimientoModel model= new EstablecimientoModel();
        String respuesta = model.insertarEstablecimiento(jSonRequest);
        return respuesta;
    }
    
    
    @RequestMapping(value="/establecimiento/edit", method=RequestMethod.PUT)
    public String modificarCola(@RequestBody String jSonRequest){
        EstablecimientoModel model= new EstablecimientoModel();
        String respuesta = model.modificarEstablecimiento(jSonRequest);
        return respuesta;
    }
    
    
    @RequestMapping(value="/establecimiento/delete", method=RequestMethod.DELETE)
    public String eliminarCola(@RequestBody String jSonRequest){
        EstablecimientoModel model= new EstablecimientoModel();
        String respuesta = model.eliminarEstablecimiento(jSonRequest);
        return respuesta;
    }
}