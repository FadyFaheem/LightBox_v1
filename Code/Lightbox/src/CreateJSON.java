import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;

public class CreateJSON {
    public static void main(String[] args) {
        JSONObject obj = new JSONObject();
        obj.put("sensorName", "DLM");
        JSONArray array = new JSONArray();
        array.add(new LightSetPoints(100,5000));
        array.add(new LightSetPoints(1000, 6000));
        obj.put("Light Points", array);



        try (FileWriter file = new FileWriter("Data.json")){
            file.write(obj.toString());
            file.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(obj);
    }
}
