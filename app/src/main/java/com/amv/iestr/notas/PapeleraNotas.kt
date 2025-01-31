package com.amv.iestr.notas

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amv.iestr.notas.almacenamiento.papelera
import com.amv.iestr.notas.databinding.ActivityPapeleraNotasBinding
import com.amv.iestr.notas.databinding.VistaMinNotaBinding
import java.text.SimpleDateFormat
import java.util.Date

class PapeleraNotas : AppCompatActivity() {
    // Notas eliminadas
    //private val papelera = ArrayList<Nota>();

    lateinit var enlaceNotas: VistaMinNotaBinding;

    private val enlace: ActivityPapeleraNotasBinding by lazy {
        ActivityPapeleraNotasBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recuperamos la nota y la agregamos al Array de notas eliminadas
        val extras = intent.extras
        extras?.let {
            val titulo: String = extras.getString(TITULO).toString();
            val cuerpo = extras.getString(CUERPO).toString();
            papelera.add(Nota(titulo, cuerpo, darFechaActual()));
        }
        refrescarVistaNotas()
        enableEdgeToEdge()
        setContentView(enlace.root)
        ViewCompat.setOnApplyWindowInsetsListener(enlace.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        refrescarVistaNotas();

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                btnNotasPulsado(); // La misma funcion que btnNotas, regresar a NotaActivity
            }
        })

        enlace.btnNotas.setOnClickListener {
            btnNotasPulsado(); // La misma funcion que btnNotas, regresar a NotaActivity
        }

    }

    override fun onResume() {
        super.onResume()
        refrescarVistaNotas()
    }
    private fun btnNotasPulsado(){
        val intento = Intent(this, MainNotaActivity::class.java)
        intento.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intento);
    }

    private fun mostrarListadoNotas() {
        for ((index, nota) in papelera.withIndex()) {
            // Inflamos el layout en enlace notas
            enlaceNotas = VistaMinNotaBinding.inflate(layoutInflater, enlace.areaNotas, false);
            // Le asignamos a los campos del layout sus respectivos valores
            if (nota.titulo.isEmpty()) { // Si no hay titulo, el cuerpo es el titulo y el cuerpo se encuentra vacio
                enlaceNotas.txtTitulo.text = nota.cuerpo;
                enlaceNotas.txtCuerpo.text = getString(R.string.without_body)
            } else if (nota.cuerpo.isEmpty()) { // Si el cuerpo esta vacio, se encuentra vacio
                enlaceNotas.txtTitulo.text = nota.titulo;
                enlaceNotas.txtCuerpo.text = getString(R.string.without_body)
            } else {
                enlaceNotas.txtTitulo.text = nota.titulo;
                enlaceNotas.txtCuerpo.text = nota.cuerpo;
            }
            enlaceNotas.txtFecha.text = nota.fecha;
            // Le damos un color rojo al fondo
            enlaceNotas.root.setBackgroundColor(Color.parseColor(getString(R.color.nota_roja)));
            // Añadimos la vista al areaNotas
            enlace.areaNotas.addView(enlaceNotas.root);
            ////////////////////////////////////////////////////////////////////////////////////////////
            enlaceNotas.root.setOnLongClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder
                    .setTitle(getString(R.string.restored_note_title))
                    .setMessage(getString(R.string.restored_note_message))
                    .setPositiveButton(getString(R.string.note_yes)) { dialogo, opcion ->
                        // Restauramos las notas
                        restaurarNota(index);
                        Toast.makeText(this, getString(R.string.note_restored), Toast.LENGTH_SHORT)
                            .show()
                    }

                    .setNegativeButton(getString(R.string.note_no)) { dialog, which ->
                        dialog.dismiss()
                    }

                val dialogo: AlertDialog = builder.create()
                dialogo.show()
                true
            }
        }
    }

    private fun refrescarVistaNotas() {
        // Limpiamos las notas antiguas para evitar duplicados
        enlace.areaNotas.removeAllViews()
        if (papelera.isNotEmpty()) { // Si tenemos notas guardadas, recorremos el bucle y mostramos las notas
            mostrarListadoNotas();
        }
    }

    private fun darFechaActual(): String {
        val formato = SimpleDateFormat("'${getString(R.string.deleted_date_message)}' d MMMM ' ${getString(R.string.deleted_date_message2)} 'HH:mm")
        return formato.format(Date()); // Fecha en este mismo instante formateada
    }

    private fun restaurarNota(indice: Int) {
        var paraRestaurar: Nota = papelera[indice];
        val bundle = Bundle().apply {
            putString(TITULO, paraRestaurar.titulo);
            putString(CUERPO, paraRestaurar.cuerpo); // La fecha en la que hemos eliminado la nota la daremos en PapeleraNotas
        }
        // Borramos el indice de la papelera
        papelera.removeAt(indice);

        // Navegamos a EditorNota pasando el bundle
        val intento = Intent(this, MainNotaActivity::class.java);
        intento.putExtras(bundle);
        startActivity(intento);
        finish()
    }
}
