package com.example.prediales

import android.app.Activity
import android.net.ipsec.ike.IkeSessionConfiguration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Month
import java.util.Calendar.FEBRUARY
import java.util.Calendar.JANUARY


class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding

    var predios=ArrayList<Property>()
    var sexo:String=""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val js = resources.openRawResource(R.raw.zonas)
        val jsonText =
            js.use { js ->
                js.bufferedReader().use(BufferedReader::readText)
            }
        val gson = Gson()
        val tipoZona = object : TypeToken<List<Zona>>() {}.type
        var zonas = gson.fromJson<List<Zona>>(jsonText, tipoZona)
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, zonas
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spZonas.setSelection(0, true)
        binding.spZonas.setAdapter(adapter)

        binding.btnCalcular.setEnabled(false)
        binding.btnAgregarPredio.setOnClickListener({
            val zona: Zona = binding.spZonas.selectedItem as Zona
            val predio = Property(zone= zona,
                extension = binding.etExtensionPredio.text.toString().toDouble())
            predios.add(predio)
            Toast.makeText(this, "Predio agregado", Toast.LENGTH_LONG).show()
            binding.etExtensionPredio.setText("0")
            binding.etExtensionPredio.requestFocus()

            binding.predios.setText(predios.toList().toString())
            binding.btnCalcular.setEnabled(true)
            //binding.btnCalcular.setOnClickListener {
            val person = Person(
                fullName = binding.etNombre.text.toString(),
                birthDate = LocalDate.parse(binding.etFechaNacimiento.text.toString()),
                genre = sexo,
                singleMother = binding.chkMadreSoltera.isChecked
            )
            val tax = Tax.Builder(folio = 1, paymentDate = LocalDate.now(), owner = person)
                .addAllProperties(predios)
                .build()

            val decimalFormat = DecimalFormat("#,###.00")

            Toast.makeText(
                this,
                "El impuesto a pagar de ${person.fullName}" +
                        "es de: $${decimalFormat.format(tax.totalTax())}", Toast.LENGTH_LONG
            ).show()
            predios.clear()
        })
    }
}



