# 📝 Notas App (Android)

## 📌 Introducción
Esta aplicación de Android, desarrollada en **Kotlin con Android Studio**, permite gestionar notas de texto con funcionalidades adicionales como papelera y acceso al calendario del dispositivo.

![image](https://github.com/user-attachments/assets/3ac4e1ca-7dcf-493c-b5cc-691bdcc7313b)

## 🚀 Funcionalidades Principales
✅ **Crear y editar** notas de texto.  
✅ **Eliminar** notas con opción de restauración.  
✅ **Personalización de notas** con colores aleatorios.  
✅ **Acceso rápido** a la app de calendario del dispositivo.

## 📱 Pantallas y Flujo

### 1️⃣ **MainNotaActivity** (Pantalla principal)
📌 Muestra la lista de notas creadas.  
📌 Permite crear una nueva nota o acceder a la papelera.  
📌 Acceso rápido al calendario.  
📌 Mantener pulsado sobre una nota permite eliminarla.  
📌 Botón `Back`: debe presionarse dos veces para cerrar la aplicación.  

### 2️⃣ **EditorNotas** (Edición y creación de notas)
📌 Interfaz para crear o modificar una nota existente.  
📌 Muestra un contador de caracteres.  
📌 `btnGuardar`: Solo se muestra si la nota tiene título y contenido.  
📌 `btnVolver`: Deshabilita el botón `Back`, solo se puede salir mediante este botón.  

### 3️⃣ **PapeleraNotas** (Notas eliminadas)
📌 Muestra las notas eliminadas.  
📌 Opción para restaurarlas a la lista principal.  
📌 `btnNotas`: Regresa a la pantalla principal sin crear una nueva instancia.  

## 📂 Estructura del Código
📌 **Clase `Nota`** → Maneja los atributos de cada nota (título, cuerpo, fecha y color).  
📌 **Archivo `Constantes.kt`** → Guarda claves globales para la transferencia de datos entre actividades.  
📌 **`mostrarListadoNotas()`** → Método encargado de cargar las notas en las vistas dinámicamente.  

## 🛠️ Tecnologías y Componentes Usados
🔹 **Lenguaje**: Kotlin  
🔹 **IDE**: Android Studio  
🔹 **Layouts**: ConstraintLayout y LinearLayout  
🔹 **Componentes UI**: EditText, Button, ScrollView, SnackBar, Toast, AlertDialog  
🔹 **Intents**: Explícitos e implícitos para navegación y comunicación entre pantallas  

## 🎛️ Extras y Configuración
✅ **Pausa y restauración de datos**  
✅ **Doble pulsación en `Back` para evitar cierres accidentales**  
✅ **Diferentes layouts para portrait y landscape**  
✅ **Soporte para múltiples idiomas (Español e Inglés)**  

## 📜 Recursos y Bibliografía
📌 [Documentación oficial de Kotlin](https://kotlinlang.org/docs/home.html)  
📌 [Guía de Android Studio](https://developer.android.com/studio)  
📌 [Uso de Intents en Android](https://developer.android.com/guide/components/intents-filters)  
