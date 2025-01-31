package com.amv.iestr.notas


class Nota(
    var titulo:String,
    var cuerpo:String,
    var fecha: String
) {

    private val colores = arrayOf("#fcf3cf","#d4efdf","#d6eaf8","#ebdef0", "#eafaf1",  "#e8f6f3", " #fce4ec", "#e8f5e9" )

    // Damos un color a la notaasignado aleatoriamente
    var color=colores.random()
}