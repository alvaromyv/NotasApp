package com.amv.iestr.notas

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.CATEGORY_APP_CALENDAR
import android.graphics.Color
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amv.iestr.notas.almacenamiento.listaNotas
import com.amv.iestr.notas.databinding.ActivityMainBinding
import com.amv.iestr.notas.databinding.VistaMinNotaBinding
import java.text.SimpleDateFormat
import java.util.Date

class MainNotaActivity : AppCompatActivity() {
    //private val listaNotas = ArrayList<Nota>()
    private val enlace: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Nota seleccionada al elegir para editar una
    private var indiceNota = 0;

    // Controlar doble pulsacion para abandonar el programa
    var doblePulsacion = false;

    // Creamos una view para la nota y la añadimos al nuestro layout (areaNotas)
    lateinit var enlaceNotas: VistaMinNotaBinding;
    // Registramos el lanzador dpara editar notas existentes antes de utilizarlo
    lateinit var lanzadorEditor: ActivityResultLauncher<Intent>
    // Registramos el lanzador para agregar nuevas notas antes de utilizarlo
    lateinit var lanzadorAgregar: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Comprobamos si ha vuelto una nota restaurada
        // Restauramos la información de la nota al abrir la pantalla
        val extras=intent.extras
        extras?.let{
            val nota = Nota(extras.getString(TITULO).toString(),
                extras.getString(CUERPO).toString(),
                darFechaActual())
            // Si algún parametro es nulo la aplicacion no se agrega una nueva nota
            if(nota.titulo.isNotEmpty() || nota.cuerpo.isNotEmpty()){
                listaNotas.add(nota)
            }
        } ?: run {
            refrescarVistaNotas()
        }
        setContentView(enlace.root)
        ViewCompat.setOnApplyWindowInsetsListener(enlace.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        /*// Comprobamos si ha vuelto una nota restaurada
        // Restauramos la información de la nota al abrir la pantalla si estamos editando una nota
        val extras=intent.extras
        extras?.let{
            val nota = Nota(extras.getString(TITULO).toString(),
                extras.getString(CUERPO).toString(),
                darFechaActual())
            // Si algún parametro es nulo la aplicacion no se agrega una nueva nota
            if(nota.titulo.isNotEmpty() || nota.cuerp o.isNotEmpty()){
                listaNotas.add(nota)
            }
        } ?: run {
            refrescarVistaNotas()
        }
        refrescarVistaNotas()*/

        // Configuramos para que al pulsar dos veces al back cerramos la aplicacion
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (doblePulsacion){
                    finishAffinity()
                } else {
                    doblePulsacion = true
                    Toast.makeText(this@MainNotaActivity, getString(R.string.press_twice), Toast.LENGTH_SHORT).show()

                    window.decorView.postDelayed({
                        doblePulsacion = false
                    }, 1000) // Si en 1 segundo no se ha pulsado otra vez reiniciamos el boton back
                }}})

        // Navega hacia EditorNota para EDITAR una NOTA EXISTENTE
        lanzadorEditor = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { recibido ->
            // Al regresar a la pantalla principal:
            val datos = recibido.data;
            if(recibido.resultCode == RESULT_OK) {
                // Sobreescribimos la nota con la informacion nueva
                listaNotas[indiceNota].titulo = datos?.getStringExtra(TITULO).toString();
                listaNotas[indiceNota].cuerpo = datos?.getStringExtra(CUERPO).toString();
                listaNotas[indiceNota].fecha = datos?.getStringExtra(FECHA).toString();
                Toast.makeText(this@MainNotaActivity,getString(R.string.note_modified), Toast.LENGTH_SHORT).show()
            }

        }

        // Navega hacia EditorNota para AGREGAR una NUEVA NOTA
        lanzadorAgregar = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { recibido ->
            val datos = recibido.data;
            if(recibido.resultCode == RESULT_OK){
                val titulo: String = datos?.getStringExtra(TITULO).toString()
                val cuerpo: String = datos?.getStringExtra(CUERPO).toString()
                val fecha: String = datos?.getStringExtra(FECHA).toString()
                // Agregamos nuestra nota al ArrayList
                listaNotas.add(Nota(titulo,cuerpo,fecha));
                Toast.makeText(this@MainNotaActivity,getString(R.string.note_saved), Toast.LENGTH_SHORT).show()
            }
        }

        // Boton para abrir el calendario
        enlace.btnVerCalendario.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build()
            )
            startActivity(intent)
        }

        // Botón para agregar una nueva actividad
        enlace.btnAgregar.setOnClickListener {
            val intencion = Intent(this, EditorNota::class.java);
            lanzadorAgregar.launch(intencion);
        }

        // Boton para ver la papelera
        enlace.btnPapelera.setOnClickListener{
            val intento = Intent(this, PapeleraNotas::class.java);
            intento.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intento);
        }

        refrescarVistaNotas();
    }

    override fun onResume() {
        super.onResume()
        // Siempre que volvamos a la pantalla refrescamos
        refrescarVistaNotas();
    }

    private fun mostrarListadoNotas(){
        for((index, nota) in listaNotas.withIndex()){
            // Inflamos el layout en enlace notas
            enlaceNotas = VistaMinNotaBinding.inflate(layoutInflater, enlace.areaNotas, false);
            // Le asignamos a los campos del layout sus respectivos valores
            if(nota.titulo.isEmpty()){ // Si no hay titulo, el cuerpo es el titulo y el cuerpo se encuentra vacio
                enlaceNotas.txtTitulo.text = nota.cuerpo;
                enlaceNotas.txtCuerpo.text = getString(R.string.without_body)
            }else if (nota.cuerpo.isEmpty()){ // Si el cuerpo esta vacio, se encuentra vacio
                enlaceNotas.txtTitulo.text = nota.titulo;
                enlaceNotas.txtCuerpo.text = getString(R.string.without_body)
            }else{
                enlaceNotas.txtTitulo.text = nota.titulo;
                enlaceNotas.txtCuerpo.text = nota.cuerpo;
            }
            enlaceNotas.txtFecha.text = nota.fecha;
            // Le damos un color al fondo
            enlaceNotas.root.setBackgroundColor(Color.parseColor(nota.color));
            // Añadimos la vista al areaNotas
            enlace.areaNotas.addView(enlaceNotas.root);
            ////////////////////////////////////////////////////////////////////////////////////////////
            // Si pulsan sobre la nota, navegamos a la vista de EditorNota pasando la nota completa
            enlaceNotas.root.setOnClickListener {
                // Copiamos la nota seleccionada para luego buscarla en el ArrayList
                indiceNota = index;
                // Creamos un bundle para pasar los datos de la nota
                val bundle = Bundle().apply {
                    putString(TITULO, nota.titulo);
                    putString(CUERPO, nota.cuerpo);
                    putString(FECHA, nota.fecha);
                }
                // Navegamos a EditorNota pasando el bundle
                val intencion = Intent(this, EditorNota::class.java);
                intencion.putExtras(bundle);
                lanzadorEditor.launch(intencion);
            }

            enlaceNotas.root.setOnLongClickListener{
                eliminarNota(index)
            }
        }
    }

    private fun refrescarVistaNotas(){
        // Limpiamos las notas antiguas para evitar duplicados
        enlace.areaNotas.removeAllViews()
        if (listaNotas.isNotEmpty()) { // Si tenemos notas guardadas, recorremos el bucle y mostramos las notas
            mostrarListadoNotas();
        }
    }

    private fun eliminarNota(indice : Int): Boolean{
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(getString(R.string.delete_note_title))
            .setMessage(getString(R.string.delete_note_message))
            .setPositiveButton(getString(R.string.note_yes)) { dialog, which ->
                mandarNotaAPapelera(indice);
                // Eliminamos la nota del listado de notas
                listaNotas.removeAt(indice);
                refrescarVistaNotas();
                Toast.makeText(this@MainNotaActivity,getString(R.string.note_deleted), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.note_no)) { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show();

        return true;
    }

    private fun mandarNotaAPapelera(indice : Int){
        // Copiamos la nota elegida para eliminar
        val nota: Nota = listaNotas.get(indice);
        // Empaquetamos la nota en un Bundle
        val bundle = Bundle().apply {
            putString(TITULO, nota.titulo);
            putString(CUERPO, nota.cuerpo);  // La fecha en la que hemos eliminado la nota la daremos en PapeleraNotas
        }

        // Navegamos a PapeleraNota pasando el bundle
        val intento = Intent(this, PapeleraNotas::class.java);
        //intento.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intento.putExtras(bundle);
        startActivity(intento);
    }

    private fun darFechaActual(): String{
        val formato = SimpleDateFormat("d 'de' MMMM HH:mm")
        return formato.format(Date()); // Fecha en este mismo instante formateada
    }
}