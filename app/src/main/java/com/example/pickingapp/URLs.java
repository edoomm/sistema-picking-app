package com.example.pickingapp;

public class URLs {
    private static final String IP = "192.168.1.69"; // cambiará dependiendo de la IP que tenga su computadora, cuando se modifique no añadan cambios en el git pls
    private static final String ROOT_URL = "http://" + IP + "/adoo/app/";

    public static final String URL_INSERT = ROOT_URL + "insert.php";
    public static final String URL_QUERY = ROOT_URL + "query.php";
}