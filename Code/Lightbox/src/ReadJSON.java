import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadJSON {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("Data.json"));
            JSONObject jsonObject = (JSONObject) obj;
            String sensorName = (String) jsonObject.get("sensorName");


        } catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        catch (ParseException e) {e.printStackTrace();}
        catch (Exception e) {e.printStackTrace();}
    }
}
