package app.utils;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * Gson TypeAdapter for Joda DateTime type
 * Created by panduka.desilva on 24/11/2016.
 */
public class DateTimeTypeAdapter extends TypeAdapter<DateTime> {

    public final DateTimeFormatter formatter;
    public final DateTimeFormatter altFormatter;

    public DateTimeTypeAdapter() {
        formatter = ISODateTimeFormat.dateTimeNoMillis();
        altFormatter = ISODateTimeFormat.dateTime();
    }

    public DateTimeTypeAdapter(String formatString) {
        formatter = DateTimeFormat.forPattern(formatString);
        altFormatter = formatter;
    }

    @Override
    public void write(JsonWriter out,DateTime date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            out.value(formatter.print(date));
        }
    }


    @Override
    public DateTime read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            default:
                String date = in.nextString();
                DateTime dateTime;
                try {
                    dateTime = formatter.parseDateTime(date);
                } catch (IllegalArgumentException e) {
                    dateTime = altFormatter.parseDateTime(date);
                }
                return dateTime;
        }
    }
}
