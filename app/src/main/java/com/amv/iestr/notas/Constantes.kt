package com.amv.iestr.notas

import java.text.SimpleDateFormat

// CLAVES PARA PASAR COMO PARAMETROS
const val TITULO = "titulo";
const val CUERPO = "cuerpo";
const val FECHA = "fecha";

object almacenamiento{
    // Nuestra lista de notas
    val listaNotas = ArrayList<Nota>();
    // Nuestra papelera de notas
    val papelera = ArrayList<Nota>();
}