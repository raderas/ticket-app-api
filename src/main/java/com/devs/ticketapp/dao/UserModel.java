/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.devs.ticketapp.dao;

import com.devs.ticketapp.dto.HeaderMsg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author azus
 */
public class UserModel extends CommonsDAO{
    
    
/* Query para obtener todos los usuarios de la tabla 
 * 
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idusuario" : 20000, "nombre" : "prueba2", "telefono" : "1111111 ", "notificacion" : "cel"}, {"idusuario" : 10000, "nombre" : "prueba", "telefono" : "7777777 ", "notificacion" : null}]
 * 
 * */   
    
     public String obtenerTotalUsuarios() {
        String result="";
        String sql = "select\n" +
            "	json_agg(\n" +
            "    json_build_object(\n" +
            "        'idusuario', u.id_usuario ,\n" +
            "        'nombre', u.nombre ,\n" +
            "        'telefono', u.telefono, \n" +
            "        'notificacion', u.tipo_notif \n" +
            "    )\n" +
            "    ) as resultado\n" +
            "FROM usuario u";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
             Statement statement = conn.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result= resultSet.getString("resultado");
            }
            if(result == null){
                result= "{\"header\":" + obtenerCabecera(CODE_NOT_RECORDS, MSG_NOT_RECORDS) + ",\"data\":"+result+"}";
            }
            else{
                result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":"+result+"}";
            }
            resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
        
     
/* Query para obtener todos los usuarios en base al arreglo de usuarios JSON que espera como parametro 
 * 
 * Ejemplo de JsonRequest
 * [{"idusuario": 10000, "nombre":"Prueba","telefono":null},{"idusuario": 20000, "nombre":null,"telefono":null}]
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idusuario" : 20000, "nombre" : "prueba2", "telefono" : "1111111 ", "notificacion" : "cel"}, {"idusuario" : 10000, "nombre" : "prueba", "telefono" : "7777777 ", "notificacion" : ""}]
 * 
 * */    
     
     public String obtenerUsuariosByID(String json) {
        String result="";
        String sql = "select\n" +
            "	json_agg(\n" +
            "    json_build_object(\n" +
            "        'idUsuario', u.id_usuario ,\n" +
            "        'nombre', u.nombre ,\n" +
            "        'telefono', u.telefono,\n" +
            "        'notificacion', u.tipo_notif \n" +
            "    )\n" +
            "    ) as resultado\n" +
            "FROM usuario u\n" +
            "where u.id_usuario  in (select  (json_array_elements_text('"+json+"')::jsonb->'idUsuario')::int4 as id)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            //System.err.println("El sql generado es : "+ sql);

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                result= resultSet.getString("resultado");
            }
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":"+result+"}";
            resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
     
    /* Query para insertar el usuario enviado como parametro en un arreglo Json 
     * 
     * Ejemplo de JsonRequest
     * [{"nombre":"Prueba","telefono":"11111111", "notificacion":"SMS"}]
     * 
     * */
     
     
     public String insertarUsuario(String json) {
        String result="";
        String sql = "insert into usuario (nombre , telefono , tipo_notif )\n" +
                    "select trim(nombre) as nombre, trim(telefono) as telefono, trim(notificacion) as notificacion from   json_to_recordset('"+json +"')\n" +
                    "		as x(\"nombre\" text, \"telefono\" text, \"notificacion\" text)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            //System.err.println("El sql generado es : "+ sql);

            ps.executeUpdate();
            
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":["+result+"]}";
            //resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
     
     
/* Query para modificar el usuario enviado como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idusuario" : 20000,"nombre":"Prueba","telefono":null, "notificacion":"SMS"}]
 * 
 * */
     
     
     public String modificarUsuario(String json) {
        String result="";
        String sql = "UPDATE usuario\n" +
                "SET nombre = coalesce (usRecord.nombre, usuario.nombre, usRecord.nombre), \n" +
                "    telefono = coalesce (usRecord.telefono, usuario.telefono, usRecord.telefono),\n" +
                "    tipo_notif =coalesce (usRecord.notificacion, usuario.tipo_notif, usRecord.notificacion)\n" +
                "FROM (\n" +
                "select * from   json_to_recordset('"+json+"')\n" +
                "		as x(\"idusuario\" text, \"nombre\" text, \"telefono\" text, \"notificacion\" text)\n" +
                ") AS usRecord\n" +
                "WHERE \n" +
                "    usRecord.idusuario::int4 = usuario.id_usuario";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            //System.err.println("El sql generado es : "+ sql);

            ps.executeUpdate();
            
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":["+result+"]}";
            //resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
  
/* Query para eliminar el usuario enviado como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idusuario" : 20000,"nombre":"Prueba","telefono":null, "notificacion":"SMS"}]
 * 
 * */
     
     
     public String eliminarUsuario(String json) {
        String result="";
        String sql = "   delete from usuario u \n" +
                "   where u.id_usuario in (\n" +
                "	select idusuario::int4 from   json_to_recordset('"+json+"')\n" +
                "			as x(\"idusuario\" text, \"nombre\" text, \"telefono\" text, \"notificacion\" text)\n" +
                "	)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.err.println("El sql generado es : "+ sql);

            ps.executeUpdate();
            
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":["+result+"]}";
            //resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }     
     
     
}
