package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    public static final String BASE_FILE_LOCATION = "resources/";
    public static final String FILE_TYPE = ".json";
    public static final String SERVER_INSERT = "serverInsert";
    public static void main(String[] args) {
	    String jSon =
            "{\"itemList\": " +
                "{\"items\": [" +
                    "{\"id\": \"item1\"}," +
                    "{\"id\": \"item2\", \"label\": \"Item 2\"}," +
                    "{\"id\": \"item3\"}," +
                    "{\"id\": \"item4\"}," +
                    "{\"id\": \"item5\"}," +
                    "{\"id\": \"subItem1\"," +
                        "\"subItems\": [" +
                            "{\"id\": \"subItem1Item1\", \"label\": \"SubItem 1\"}," +
                            "{\"id\": \"subItem1Item2\", \"label\": \"SubItem 2\"}," +
                            "{\"id\": \"subItem1Item3\", \"label\": \"SubItem 3\"}," +
                        "]" +
                    "}," +
                    "{\"id\": \"item6\"}," +
                    "{\"id\": \"item7\", \"label\": \"Item 7\"}," +
                    "{\"id\": \"subItem2\"," +
                        "\"subItems\": [" +
                            "{\"id\": \"item1\", \"label\": \"SubItem 2 item1\"}" +
                        "]" +
                    "}" +
                "]}" +
            "}";

        System.out.println("1. Get full path on an element");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Element to Find:");
        String input = scanner.next();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jSon);

        String path = findPath(element, input);
        System.out.println("Path: " + path);

        System.out.println("2. Read in the main.json and when you find a \"serverInsert\" element replace the serverInsert element with the corresponding json data.");
        String replace = objectReplace();
        System.out.println("Replaced JSON: " + replace);

        System.out.println("3. Given the following JSON Array, write a method to return the value for a given path, element, and id.");
        System.out.println("Enter Path to Follow:");
        String pathToFollow = scanner.next();
        System.out.println("Enter Element to Find:");
        String elementToFind = scanner.next();
        System.out.println("Enter ID to Find:");
        String idToFind = scanner.next();

        String requestedValue = requestedValue(pathToFollow, elementToFind, idToFind);
        System.out.println("Requested Value: " + requestedValue);

    }

    public static String findPath(JsonElement json, String lookupValue) {
        if (json.isJsonObject()) {
            Set<Entry<String, JsonElement>> ens = ((JsonObject) json).entrySet();
            if (ens != null) {
                for (Entry<String, JsonElement> en : ens) {
                    String foundPath = findPath(en.getValue(), lookupValue);
                    if (foundPath != null) {
                        return (en.getValue().isJsonArray() ? foundPath : ("\\" + en.getKey() + foundPath));
                    }
                }
            }
        }

        // Check whether jsonElement is Arrary or not
        else if (json.isJsonArray()) {
            JsonArray jarr = json.getAsJsonArray();
            // Iterate JSON Array to JSON Elements
            for (int i = 0; i < jarr.size(); i++) {
                JsonElement je = jarr.get(i);
                String foundPath = findPath(je, lookupValue);
                if (foundPath != null) {
                    return "[" + i + "]" + foundPath;
                }
            }
        }

        // Check whether jsonElement is NULL or not
        else if (json.isJsonNull()) {
            return null;
        }
        // Check whether jsonElement is Primitive or not
        else if (json.isJsonPrimitive()) {
            // print value as String
            if (lookupValue.equals(json.getAsString())) {
                return "";
            } else {
                return null;
            }
        }

        return null;
    }

    public static String objectReplace() {
        JsonParser parser = new JsonParser();
        try {
            File mainJsonFile = new File(BASE_FILE_LOCATION + "main" + FILE_TYPE);
            mainJsonFile.createNewFile();

            JsonElement jsonElement = parser.parse(new FileReader(mainJsonFile));
            JsonObject itemListObject = jsonElement.getAsJsonObject();
            JsonObject itemsArrayObject = (JsonObject) itemListObject.get("itemList");
            JsonArray itemsArray = (JsonArray) itemsArrayObject.get("items");

            for (int i = 0; i < itemsArray.size(); i++) {
                JsonElement je = itemsArray.get(i);
                if (je.isJsonObject()) {
                    JsonObject jo = je.getAsJsonObject();
                    if (jo.has(SERVER_INSERT)) {
                        File newJsonFile = new File(BASE_FILE_LOCATION + jo.get(SERVER_INSERT).getAsString() + FILE_TYPE);
                        newJsonFile.createNewFile();

                        JsonElement newJsonElement = parser.parse(new FileReader(newJsonFile));

                        itemsArray.set(i, newJsonElement);
                    }
                }
            }

            return jsonElement.toString();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }

        catch (IOException io) {
            System.out.println("IOException: " + io);
        }
        return null;
    }

    public static String requestedValue(String pathToFollow, String element, String id) {
        String returnValue = "Can't Find Value";

        String jsonArrayString =
                "{\"itemList\":" +
                        "{\"items\": {" +
                        "\"subItems\": [" +
                        "{\"id\": \"subItem1Item1\",\"label\": \"SubItem 1 label\"}," +
                        "{\"id\": \"subItem1Item2\",\"label\": \"SubItem 2 label\"}," +
                        "{\"id\": \"subItem1Item3\",\"label\": \"SubItem 3 label\"}" +
                        "]" +
                        "}" +
                        "}}";

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonArrayString);

        String[] paths = pathToFollow.split("\\.");
        for (String path : paths) {
            jsonElement = jsonElement.getAsJsonObject().get(path);
        }
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement subItem : jsonArray) {
                if (subItem.isJsonObject()) {
                    JsonObject subItemObject = subItem.getAsJsonObject();
                    if (subItemObject.has("id")
                            && id.equals(subItemObject.get("id").getAsString())
                            && subItemObject.has(element)) {
                        returnValue = subItemObject.get(element).getAsString();
                    }
                }
            }
        }

        return returnValue;
    }
}
