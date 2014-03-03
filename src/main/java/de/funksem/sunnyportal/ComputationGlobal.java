package de.funksem.sunnyportal;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

// Zeitraum Von - Bis
// Anzahl der Ertragstage
// Anzahl der Tage ohne Ertrag
// Gesamtertrag
// Höchster Ertag mit Datum
// Niedrigster Ertrag mit Datum
// Gesamtdurchschnitt
// Durchschnitt pro Jahr
// Durchschnitt pro Monat
public class ComputationGlobal
{
    Map<Date, Double> dataCsv;

    int tageMitErtrag = 0;
    int tageOhneErtrag = 0;
    int gesamtTage = 0;
    Double gesamtertag = 0.0;
    Pair<Date, Double> hoechsterErtrag = Pair.of(null, 0.0);
    Pair<Date, Double> niedrigsterErtrag = Pair.of(null, 10000.0);
    Date anfangsDatum = null;
    Date endeDatum = null;

    public ComputationGlobal()
    {
    }

    public ComputationGlobal(Map<Date, Double> dataCsv)
    {
        this.dataCsv = dataCsv;
    }

    public void startComputation()
    {
        for (Map.Entry<Date, Double> entry : dataCsv.entrySet())
        {
            final Date date = entry.getKey();
            final Double power = entry.getValue();

            if ((power == null) || (power <= 0.0))
            {
                tageOhneErtrag++;
                continue;
            }

            tageMitErtrag++;
            gesamtertag += power;

            if (hoechsterErtrag.getValue() < power)
            {
                hoechsterErtrag = Pair.of(date, power);
            }
            if (niedrigsterErtrag.getValue() > power)
            {
                niedrigsterErtrag = Pair.of(date, power);
            }

            if ((anfangsDatum == null) || (date.compareTo(anfangsDatum) < 0))
            {
                anfangsDatum = date;
            }
            if ((endeDatum == null) || (date.compareTo(endeDatum) > 0))
            {
                endeDatum = date;
            }

        }

        gesamtTage = tageMitErtrag + tageOhneErtrag;
    }

    public int getTageMitErtrag()
    {
        return tageMitErtrag;
    }

    public int getTageOhneErtrag()
    {
        return tageOhneErtrag;
    }

    public int getGesamtTage()
    {
        return gesamtTage;
    }

    public Double getGesamtertag()
    {
        return gesamtertag;
    }

    public Pair<Date, Double> getHoechsterErtrag()
    {
        return hoechsterErtrag;
    }

    public Pair<Date, Double> getNiedrigsterErtrag()
    {
        return niedrigsterErtrag;
    }

    public Date getAnfangsDatum()
    {
        return anfangsDatum;
    }

    public Date getEndeDatum()
    {
        return endeDatum;
    }
}
