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
public class ColaModel extends CommonsDAO{
    
    
/* Query para obtener todos las colas de la tabla 
 * 
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * */
    
     public String obtenerTotalColas() {
        String result="";
        String sql = "select\n" +
                "	json_agg(\n" +
                "    json_build_object(\n" +
                "        'idcola', c.id_cola ,\n" +
                "        'descripcion', c.descripcion ,\n" +
                "        'idestablecimiento', c.id_establecimiento ,\n" +
                "        'cupos', c.cupos ,\n" +
                "        'ultimoatendido', c.ultimo_atendido \n" +
                "    )\n" +
                "    ) as resultado\n" +
                "FROM cola c	";

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
        
     
/* Query para obtener todas las colas en base al arreglo de usuarios JSON que espera como parametro 
 * 
 * Ejemplo de JsonRequest
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * */   
     
     public String obtenerColaByID(String json) {
        String result="";
        String sql = "select\n" +
            "	json_agg(\n" +
            "    json_build_object(\n" +
            "        'idcola', c.id_cola ,\n" +
            "        'descripcion', c.descripcion ,\n" +
            "        'idestablecimiento', c.id_establecimiento ,\n" +
            "        'cupos', c.cupos ,\n" +
            "        'ultimoatendido', c.ultimo_atendido \n" +
            "    )\n" +
            "    ) as resultado\n" +
            "FROM cola c\n" +
            "where c.id_cola  in (select  (json_array_elements_text('"+json+"')::jsonb->'idcola')::int4 as id)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.err.println("El sql generado es : "+ sql);

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
     
/* Query para insertar la cola enviada como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * */
     
     public String insertarCola(String json) {
        String result="";
        String sql = "insert into cola (descripcion , id_establecimiento , cupos , ultimo_atendido )\n" +
            "select trim(descripcion) as descripcion, idestablecimiento::int4 as idestablecimiento, cupos::int2 as cupos, ultimoatendido::int8 as ultimoatendido from   json_to_recordset('"+json+"')\n" +
            "		as x(\"idcola\" text, \"descripcion\" text, \"idestablecimiento\" text, \"cupos\" text, \"ultimoatendido\" text)";

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
     
     
/* Query para modificar la cola enviada como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * */	
     
     
     public String modificarCola(String json) {
        String result="";
        String sql = "UPDATE cola\n" +
                "SET descripcion = coalesce (usRecord.descripcion, cola.descripcion , usRecord.descripcion), \n" +
                "    id_establecimiento = coalesce (usRecord.idestablecimiento::int4, cola.id_establecimiento, usRecord.idestablecimiento::int4),\n" +
                "    cupos =coalesce (usRecord.cupos::int2, cola.cupos , usRecord.cupos::int2),\n" +
                "    ultimo_atendido =coalesce (usRecord.ultimoatendido::int8, cola.ultimo_atendido , usRecord.ultimoatendido::int8)\n" +
                "FROM (\n" +
                "select * from   json_to_recordset('"+json+"')\n" +
                "		as x(\"idcola\" text, \"descripcion\" text, \"idestablecimiento\" text, \"cupos\" text, \"ultimoatendido\" text)\n" +
                ") AS usRecord\n" +
                "WHERE \n" +
                "    usRecord.idcola::int4 = cola.id_cola ";

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
  
 /* Query para eliminar la cola enviada como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * */   
     
     
     public String eliminarCola(String json) {
        String result="";
        String sql = "   delete from cola c \n" +
                "   where c.id_cola in (\n" +
                "	select idcola::int4 from   json_to_recordset('"+json+"')\n" +
                "			as x(\"idcola\" text, \"descripcion\" text, \"idestablecimiento\" text, \"cupos\" text, \"ultimoatendido\" text)\n" +
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
