package com.example.yidnek.amguesthouse;

/**
 * Created by Yidnek on 7/2/2018.
 */

public class History {
    public long TotalGebi,TotalWechi;
   public String date;

    public History() {
    }

    public History(long TotalGebi, long TotalWechi, String date) {
        this.TotalGebi = TotalGebi;
        this.TotalWechi = TotalWechi;
        this.date = date;
    }

    public long getWechi() {
        return TotalWechi;
    }

    public long getGebi() {
        return TotalGebi;
    }

    public String getHistoryDate() { return date; }


}
