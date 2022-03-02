package br.com.thalesnunes.cotacao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import org.json.JSONObject
import java.lang.reflect.Array
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result = findViewById<TextView>(R.id.txt_result)

        val buttonConverter = findViewById<Button>(R.id.btn_converter)

        buttonConverter.setOnClickListener{
            converter()
        }
    }

    private fun converter(){
        val selectedCurrency = findViewById<RadioGroup>(R.id.radio_group)
        val checked = selectedCurrency.checkedRadioButtonId

        val currency = when (checked){
            R.id.radio_usd -> {
                "USD"
            }
            R.id.radio_eur -> {
                "EUR"
            }
            else -> {
                "GBP"
            }
        }

        val editField = findViewById<EditText>(R.id.edit_field)
        val value = editField.text.toString()

        if(value.isEmpty()){
            return
        }

        Thread{

            // url da api concatenada via "template string" com a variavel da moeda escolhida
            val url = URL("https://exchange-rates.abstractapi.com/v1/live/?api_key=9e00bc0bb43041ada24f62d607a463e7&base=BRL&target=${currency}")

            // abre a conexão utilizando url da api
            val conn = url.openConnection() as HttpsURLConnection

            try {

                // captura os dados obtidos
                val data = conn.inputStream.bufferedReader().readText()

                // armezenando os dados como objeto json
                val obj = JSONObject(data)

                runOnUiThread{
                    // obtem o fator de diferença da moeda escolhida
                    val rates = obj.getJSONObject("exchange_rates")[currency]

                    // divide o valor desejado pelo fator da moeda
                    val res = value.toDouble() / rates.toString().toDouble()

                    // insere o valor no label
                    result.text = "R$ ${"%.2f".format(res)}"

                    // torna o label visível
                    result.visibility = View.VISIBLE
                }

            } finally {
                // encerra a conexão
                conn.disconnect()
            }
        }.start()
    }
}