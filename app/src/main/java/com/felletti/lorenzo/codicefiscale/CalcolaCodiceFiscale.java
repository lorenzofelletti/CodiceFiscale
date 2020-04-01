package com.felletti.lorenzo.codicefiscale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CalcolaCodiceFiscale extends AppCompatActivity {
    TextView cf;
    Spinner sesso;
    EditText nome, cognome, data, luogo, provincia;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcola_codice_fiscale);

        nome = (EditText) findViewById(R.id.Nome);
        cognome = (EditText) findViewById(R.id.Cognome);
        data = (EditText) findViewById(R.id.DataNascita);
        sesso = (Spinner) findViewById(R.id.sex);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sesso.setAdapter(adapter);
        luogo = (EditText) findViewById(R.id.LuogoNascita);
        provincia = (EditText) findViewById(R.id.Provincia);
        cf = (TextView) findViewById(R.id.CodiceFiscale);
        button = (Button) findViewById(R.id.CalcolaCF);
    }

    public void onClick(View v) {
        String name = nome.getText().toString().trim();
        String surname = cognome.getText().toString().trim();
        String dateStr = data.getText().toString().trim();
        String luogoNascita = luogo.getText().toString().trim();
        String prov = provincia.getText().toString().trim();
        String sex = sesso.getSelectedItem().toString().trim();;
        int giorno, mese, anno;
        try {
            if( name == null || name.isEmpty() || surname == null || surname.isEmpty()
                    || dateStr == null || dateStr.isEmpty() || luogoNascita == null
                    || luogoNascita.isEmpty() || prov == null || prov.isEmpty()
                    || sex == null || sex.isEmpty() )
                throw new IllegalArgumentException("Mancano dei dati.");

            if (!dateStr.isEmpty() && dateStr.length() == 10 ) {
                String gg = dateStr.substring(0, 2);
                String mm = dateStr.substring(3, 5);
                String aaaa = dateStr.substring(6);
                giorno = Integer.parseInt(gg);
                mese = Integer.parseInt(mm);
                anno = Integer.parseInt(aaaa);
            } else {
                throw new IllegalArgumentException("Data di nascita non valida");
            }

            CodiceFiscale codF = new CodiceFiscale(name, surname, sex, luogoNascita, prov, giorno, mese, anno, this.getApplicationContext());
            cf.setText(codF.getCodiceFiscale());

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(CalcolaCodiceFiscale.this).create();
            alertDialog.setTitle("Errore");
            alertDialog.setMessage(e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }
}
