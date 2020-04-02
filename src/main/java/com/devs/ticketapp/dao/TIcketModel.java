package com.devs.ticketapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TIcketModel extends CommonsDAO{

    /*
 *	Query para obtener todos los tickets para la tabla ticket.
 *
 * 
 * 	Query de JsonResponse de todos los tickets
 * 
 *  estructura de JsonResponse
 * 
 * 	[{'idticket': 1,'idcola': null, 'posicion':1, 'estado': 1, 'generaren': created_at, 'inicioturno': updated_at,'vencimiento':deleted_at, 'atendidoen'}]
 **/

    public String obtenerTotalTickets() {
        String result="";
        String sql = "select\n"+ 
                    " json_agg(\n"+
                        " json_build_object(\n"+
                                "'idticket', t2.id_ticket,\n"+
                                "'idcola', t2.id_cola,\n"+
                                "'posicion', t2.posicion, \n"+
                                "'estado',t2.estado, \n"+
                                "'generaren',t2.generado_en,\n"+
                                "'inicioturno',t2.inicio_turno,\n"+
                                "'vencimiento',t2.vencimiento,\n"+
                                "'atendidoen',t2.atendido_en,\n"+
                                "'ultimanotif',t2.ultima_notificacion \n"+
                            ")\n"+ 
                        ") as resultado\n"+
                    "FROM ticket t2";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
             Statement statement = conn.createStatement()) {
                System.err.println("El sql generado es : "+ sql);
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
 * El valor de un id de ticket 
 * 
 * Ejemplo
 [{'idticket':1,'idticket': 1,'idcola': null, 'posicion':1, 'estado': 1, 'generaren': created_at, 'inicioturno': updated_at,'vencimiento':deleted_at, 'atendidoen'}]
 * 
 * */
     
     public String obtenerTicketByID(String json) {
        String result="";
        String sql = "select\n"+
                    "    json_agg( \n"+
                    "        json_build_object(\n"+
                    "            'idticket', t2.id_ticket,\n"+
                    "            'idcola', t2.id_cola,\n"+
                    "            'posicion', t2.posicion,\n"+
                    "            'estado',t2.estado,\n"+
                    "            'generaren',t2.generado_en,\n"+
                    "            'inicioturno',t2.inicio_turno,\n"+
                    "            'vencimiento',t2.vencimiento,\n"+
                    "            'atendidoen',t2.atendido_en,\n"+
                    "            'ultimanotif',t2.ultima_notificacion \n"+
                    "        )\n"+
                    "    ) as resultado\n"+
                    "FROM ticket t2 \n"+
                    "where t2.id_ticket in (select  (json_array_elements_text('"+json+"')::jsonb->'idticket')::int4 as id)";

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
     
/* Query para insertar el ticket enviado como parametro en un arreglo Json 
	 * 
	 * Ejemplo de JsonRequest
	   [{"idusuario": null,"idcola": null, "posicion": 7, "estado": 6, "generaren":"2020/04/02","inicioturno":"2020/04/02","vencimiento":"2020/04/02","atendidoen":"2020/04/02","ultimanotif":"2020/04/02"}]
	 * */
     
     
     public String insertarTicket(String json) {
        String result="";
        String sql = "insert into ticket (id_usuario ,id_cola , posicion , estado , generado_en , inicio_turno ,vencimiento, atendido_en, ultima_notificacion)\n"+
                    "select idusuario::int4, idcola::int4, posicion::int2,estado::int4, generaren::timestamp,inicioturno::timestamp,vencimiento::timestamp,atendidoen::timestamp, ultimanotif::timestamp from json_to_recordset('"+json+"')\n"+
                    "as x(\"idusuario\" text, \"idcola\" text,\"posicion\" text,\"estado\" text,\"generaren\" text, \"inicioturno\" text,\"vencimiento\" text,\"atendidoen\" text,\"ultimanotif\" text);";

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
     
     
/* Query para modificar el ticket enviado como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idticket": 3,"idusuario": null,"idcola": null, "posicion": 7, "estado": 6, "generaren":"2020/04/02","inicioturno":"2020/04/02","vencimiento":"2020/04/02","atendidoen":"2020/04/02","ultimanotif":"2020/04/02"}]
 * 
 * */	
	
     
     
public String modificarTicket(String json) {
    String result="";
    String sql = "UPDATE ticket\n"+
                    "SET id_ticket = coalesce (usRecord.idticket::int8, ticket.id_ticket , usRecord.idticket::int8),\n"+
                        "id_usuario = coalesce (usRecord.idusuario::int4, ticket.id_usuario , usRecord.idusuario::int4), \n"+
                        "id_cola = coalesce (usRecord.idcola::int4 , ticket.id_cola , usRecord.idcola::int4),\n"+
                        "posicion = coalesce (usRecord.posicion::int2, ticket.posicion , usRecord.posicion::int2),\n"+
                        "estado = coalesce (usRecord.estado::int4, ticket.estado , usRecord.estado::int4),\n"+
                        "generado_en = coalesce (usRecord.generaren::timestamp, ticket.generado_en , usRecord.generaren::timestamp),\n"+
                        "inicio_turno = coalesce (usRecord.inicioturno::timestamp, ticket.inicio_turno , usRecord.inicioturno::timestamp),\n"+
                        "vencimiento = coalesce (usRecord.vencimiento::timestamp, ticket.vencimiento , usRecord.vencimiento::timestamp),\n"+
                        "atendido_en = coalesce (usRecord.atendidoen::timestamp, ticket.atendido_en , usRecord.atendidoen::timestamp),\n"+
                        "ultima_notificacion = coalesce (usRecord.ultimanotif::timestamp, ticket.ultima_notificacion , usRecord.ultimanotif::timestamp)\n"+
                    "FROM (\n"+
                    "select * from   json_to_recordset('"+json+"')\n"+
                            "as x(\"idticket\" text, \"idusuario\" text,\"idcola\"  text,\"posicion\" text,\"estado\" text,\"generaren\" text,\"inicioturno\" text,\"vencimiento\" text,\"atendidoen\" text,\"ultimanotif\" text)\n"+
                    ") AS usRecord\n"+
                    "WHERE  usRecord.idticket::int8 = ticket.id_ticket ";

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
/* Query para Eliminar el ticket enviado como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idticket": 6,"idusuario": null,"idcola": null, "posicion": 7, "estado": 6, "generaren":"2020/04/02","inicioturno":"2020/04/02","vencimiento":"2020/04/02","atendidoen":"2020/04/02","ultimanotif":"2020/04/02"}]
 * 
 * */	
     
     
     public String eliminarTicket(String json) {
        String result="";
        String sql = "   delete from ticket t \n"+
                        "where t.id_ticket in (\n"+
                        "select idticket::int8 from json_to_recordset('"+json+"')\n"+
                                "as x(\"idticket\" text,\"idusuario\" text,\"idcola\"  text,\"posicion\" text,\"estado\" text,\"generaren\" text,\"inicioturno\" text,\"vencimiento\" text,\"atendidoen\" text,\"ultimanotif\" text)\n"+
                        ") ";

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