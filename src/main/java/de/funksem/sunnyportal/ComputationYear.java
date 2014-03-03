package de.funksem.sunnyportal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComputationYear extends ComputationGlobal
{

    Map<Integer, Double> monatErtrag = new HashMap<>();

    public ComputationYear()
    {
    }

    public ComputationYear(Map<Date, Double> dataCsv)
    {
        super(dataCsv);
    }

    @Override
    public void startComputation()
    {
        super.startComputation();
        for (Integer month : ComputationUtils.getMonth(dataCsv))
        {
            Map<Date, Double> filteredData = ComputationUtils.filterMonth(dataCsv, month);

            Double gesamtertagMonat = 0.0;
            for (Map.Entry<Date, Double> entry : filteredData.entrySet())
            {
                gesamtertagMonat += entry.getValue();
            }
            monatErtrag.put(month, gesamtertagMonat);
        }
    }

    public Map<Integer, Double> getMonatsErtraege()
    {
        return monatErtrag;
    }
}
