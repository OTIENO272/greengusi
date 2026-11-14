package com.example.myapplication

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

object GusiiLocations {

    val locationHierarchy = mapOf(
        "Kisii" to mapOf(
            "Kitutu Chache South" to listOf("Bogusero", "Nyatieko", "Kitutu Central", "Nyakoe", "Bogeka"),
            "Kitutu Chache North" to listOf("Marani", "Sensi", "Kegogi", "Monyerero"),
            "Kisii Central" to listOf("Kisii Central", "Keumbu", "Kiogoro", "Birongo", "Bobaracho"),
            "Nyaribari Chache" to listOf("Kiogoro", "Bobaracho", "Kisii Township", "Ibeno"),
            "Nyaribari Masaba" to listOf("Gesusu", "Masimba", "Kiamokama", "Magenche", "Ichuni"),
            "Bobasi" to listOf("Sameta", "Nyacheki", "Bassi Central", "Masige West", "Bobasi Chache"),
            "Bomachoge Borabu" to listOf("Boochi Borabu", "Magenchea", "Bokimonge", "Bombaba"),
            "Bomachoge Chache" to listOf("Majoge Basi", "Boochi/Tendere", "Bosoti/Sengera"),
            "South Mugirango" to listOf("Tabaka", "Boikanga", "Bogetenga", "Borabu/Chitago", "Moticho"),
            "Bonchari" to listOf("Bomorenda", "Riana", "Bogiakumu", "Nyakoe")
        ),
        "Nyamira" to mapOf(
            "West Mugirango" to listOf("Bonyamatuta", "Bogichora", "Bosamaro", "Nyamaiya", "Township"),
            "North Mugirango" to listOf("Itibo", "Bomwagamo", "Bokeira", "Magwagwa", "Ekerenyo"),
            "Kitutu Masaba" to listOf("Rigoma", "Magombo", "Gachuba", "Kemera", "Manga", "Gesima"),
            "Borabu" to listOf("Mekenene", "Kiabonyoru", "Nyansiongo", "Esise"),
            "Manga" to listOf("Manga", "Kemera", "Magombo")
        )
    )

    fun setupCascadingSpinners(
        context: Context,
        countySpinner: Spinner,
        subCountySpinner: Spinner,
        wardSpinner: Spinner
    ) {
        val counties = locationHierarchy.keys.toList()
        countySpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, counties)

        countySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCounty = counties[position]
                val subCounties = locationHierarchy[selectedCounty]?.keys?.toList() ?: emptyList()
                subCountySpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, subCounties)

                subCountySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, i: Long) {
                        val selectedSubCounty = subCounties.getOrNull(pos) ?: return
                        val wards = locationHierarchy[selectedCounty]?.get(selectedSubCounty) ?: emptyList()
                        wardSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, wards)
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}
