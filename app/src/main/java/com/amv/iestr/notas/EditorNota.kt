package com.amv.iestr.notas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.amv.iestr.notas.databinding.ActivityEditarNotaBinding
import java.text.SimpleDateFormat
import java.util.Date


class EditorNota : AppCompatActivity() {
    private val enlace: ActivityEditarNotaBinding by lazy {
        ActivityEditarNotaBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restauramos la información de la nota al abrir la pantalla si estamos editando una nota
        val extras=intent.extras
        extras?.let{
            enlace.txtTitulo.setText(extras.getString(TITULO));
            enlace.txtCuerpo.setText(extras.getString(CUERPO));
            enlace.lblFecha.text = extras.getString(FECHA)
        } ?: run { // Si es nulo, mostramos la fecha y el contador
            enlace.lblFecha.text = mostrarFechaActual();
        }

        enlace.lblContador.text = mostrarContadorActual();

        /*val bundle = intent.extras // Recuperamos el bundle con los datos de la nota
        enlace.txtTitulo.setText(bundle?.getString(TITULO));
        enlace.txtCuerpo.setText(bundle?.getString(CUERPO));
        actualizarInformacion(Date().toString());*/
        /////////////////////////////////////////////////////////////
        enableEdgeToEdge()
        setContentView(enlace.root)
        ViewCompat.setOnApplyWindowInsetsListener(enlace.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        enlace.txtTitulo.addTextChangedListener{
            if(enlace.txtTitulo.text.isEmpty() && enlace.txtCuerpo.text.isBlank()){
                enlace.btnGuardar.visibility = View.INVISIBLE
            }else{
                enlace.btnGuardar.visibility = View.VISIBLE
            }
        }

        enlace.txtCuerpo.addTextChangedListener{
            if(enlace.txtCuerpo.text.isEmpty() && enlace.txtTitulo.text.isEmpty() || enlace.txtCuerpo.text.isBlank()){
                enlace.btnGuardar.visibility = View.INVISIBLE
            }else{
                enlace.btnGuardar.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()

        enlace.txtTitulo.addTextChangedListener{
            enlace.btnGuardar.visibility = View.VISIBLE;
            actualizarInformacion();
        }

        enlace.txtCuerpo.addTextChangedListener{
            enlace.btnGuardar.visibility = View.VISIBLE;
            actualizarInformacion();
        }

        enlace.btnGuardar.setOnClickListener(){
            val intencion=Intent()
            val extras=intent.extras
            intencion.putExtra(TITULO,enlace.txtTitulo.text.toString());
            intencion.putExtra(CUERPO,enlace.txtCuerpo.text.toString());
            extras?.let{
                intencion.putExtra(FECHA,enlace.lblFecha.text);
            } ?: run { // Si estamos creando una nueva nota, pasamos todos los parametros y la fecha actual
                intencion.putExtra(FECHA,mostrarFechaActual());
            }
            setResult(Activity.RESULT_OK, intencion);
            finish();
        }

        enlace.btnVolver.setOnClickListener(){
            Toast.makeText(this@EditorNota,getString(R.string.note_doesnt_save), Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Toast.makeText(this@EditorNota,getString(R.string.press_button_to_exit), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarInformacion(){
        // Construimos una cadena con la fecha formateada y otra con el nº de caracteres de la nota
        enlace.lblFecha.text = mostrarFechaActual()
        enlace.lblContador.text = mostrarContadorActual()
    }

    private fun mostrarContadorActual(): String {
        val caracterTraduccion: String;
        if(enlace.txtCuerpo.text.length==1){
            caracterTraduccion = getString(R.string.character)
        }else{
            caracterTraduccion = getString(R.string.characters)
        }

        return "\t|\t${enlace.txtCuerpo.text.length} $caracterTraduccion";
    }

    private fun mostrarFechaActual(): String{
        val formato = SimpleDateFormat("d 'de' MMMM HH:mm")
        return formato.format(Date()); // Fecha en este mismo instante formateada
    }
}