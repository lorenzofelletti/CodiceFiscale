package com.felletti.lorenzo.codicefiscale;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;


public class CodiceFiscale {
    private String codiceFiscale;

    public CodiceFiscale(String nome, String cognome, String sesso, String luogoNascita,
                         String provincia, int giorno, int mese, int anno, Context myContext)
            throws Exception{
        String codiceIncompleto = consonantiCognome(cognome) + lettereNome(nome) +
                dataNascita(giorno, mese, anno, sesso) +
                siglaLuogoDiNascita(luogoNascita, provincia, myContext);
        this.codiceFiscale = codiceIncompleto + letteraDiControllo(codiceIncompleto);
    }

    public String getCodiceFiscale() {
        return this.codiceFiscale;
    }

    private static String consonantiCognome(String cognome) {
        String vocals = "AEIOU";
        String result = "";

        cognome = cognome.toUpperCase();
        if (cognome.length() < 3) {
            return (cognome + 'X');
        }

        for(int i = 0; i < cognome.length() && result.length() < 3; i++) {
            if(vocals.indexOf(cognome.charAt(i)) < 0) {
                result = (result + cognome.charAt(i));
            }
        }

        if(result.length() < 3) {
            for(int i = 0; i < cognome.length() && result.length() < 3; i++) {
                if(vocals.indexOf(cognome.charAt(i)) >= 0)
                    result = (result + cognome.charAt(i));
            }
        }

        return result;
    }

    private static String lettereNome(String nome) {
        String vocals = "AEIOU";
        String consonanti = "";
        String result = "";
        nome = nome.toUpperCase().trim();

        if(nome.length() < 3)
            return (nome + "X");

        for(int i = 0; i < nome.length() && consonanti.length() < 4; i++) {
            if(vocals.indexOf(nome.charAt(i)) == -1) {
                consonanti += nome.charAt(i);
            }
        }

        if(consonanti.length() < 3) {
            for(int i = 0; i < consonanti.length(); i++) {
                result += consonanti.charAt(i);
            }
            for(int i = 0; i < nome.length() && result.length() < 3; i++) {
                if(vocals.indexOf(nome.charAt(i)) >= 0) {
                    result += nome.charAt(i);
                }
            }
            return  result;
        }

        if(consonanti.length() == 3)
            return consonanti;

        for(int i = 0; i < consonanti.length() && result.length() < 3; i++) {
            if(i != 1) {
                result = result + consonanti.charAt(i);
            }
        }

        return result;
    }

    private static String dataNascita(int giorno, int mese, int anno, String sesso) throws Exception {
        String annoStr = String.valueOf(anno);
        String result = annoStr.substring(2);
        String lettereMesi = "ABCDEHLMPRST";
        sesso = sesso.toUpperCase();

        if(mese > 12)
            throw new Exception("Mese di nascita non valido.");
        result = result + lettereMesi.charAt(mese-1);

        switch (sesso) {
            case "M":
                return (result + giorno);
            case "F":
                return (result + (giorno + 40));
            default:
                throw new Exception("Sesso non valido.");
        }
    }


    private static String siglaLuogoDiNascita(String luogoNascita, String provincia, Context myContext) throws Exception {
        if( luogoNascita.isEmpty() || provincia.isEmpty() )
            throw new Exception("Informazioni mancanti.");
        AssetManager assetManager = myContext.getAssets();
        InputStream is = assetManager.open("listacomuni.txt");
        Reader reader = new InputStreamReader(is);

        luogoNascita = luogoNascita.toUpperCase().trim();
        provincia = provincia.toUpperCase().trim();
        String comune;
        String codice;
        String prov;
        String line;
        StringTokenizer stk;
        try (BufferedReader br = new BufferedReader(reader)) {
            while ((line = br.readLine()) != null) {
                stk = new StringTokenizer(line, ";");

                comune = stk.nextToken().trim().toUpperCase();
                prov = stk.nextToken().trim().toUpperCase();
                codice = stk.nextToken().trim().toUpperCase();
                if (comune.equals(luogoNascita) && prov.equals(provincia)) {
                    return codice;
                }
            }
        } catch (Exception e) {
            throw new Exception("Qualcosa è andato storto nel calcolo della sigla di nascita." +
                "\nControlla i dati immessi e riprova.");
        }
        throw new Exception("Comune di nascita non trovato");
    }

    private static String letteraDiControllo(String codiceIncompleto) {
        String letteraFinale = "";
        String cifre = "0123456789";
        String pari = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String dispari = "BAKPLCQDREVOSFTGUHMINJWZYX";
        int sum = 0;
        codiceIncompleto = codiceIncompleto.toUpperCase();

        for (int i = 0; i < codiceIncompleto.length(); i++) {
            for(int j = 0; j < cifre.length(); j++) {
                if(codiceIncompleto.charAt(i) == (cifre.charAt(j)))
                    codiceIncompleto = codiceIncompleto.replace(codiceIncompleto.charAt(i), pari.charAt(j));
            }
        }

        for(int i = 0; i < codiceIncompleto.length(); i++) {
            if((i+1)%2 == 0)
                sum += pari.indexOf(codiceIncompleto.charAt(i));
            else
                sum += dispari.indexOf(codiceIncompleto.charAt(i));
        }

        letteraFinale = letteraFinale + pari.charAt(sum%26);
        return letteraFinale;
    }
}
