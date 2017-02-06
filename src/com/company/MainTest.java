package com.company;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by coatsmi on 2/4/17.
 */
public class MainTest {

    private String testJSON ="{\"itemList\": " +
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
    JsonElement testElement;

    @Before
    public void setUp() throws Exception {
        JsonParser parser = new JsonParser();
        testElement= parser.parse(testJSON);
    }

    @Test
    public void testFindPath() throws Exception {
        String item = "item2";
        String expectedPath = "\\itemList[1]\\id";

        String actual = Main.findPath(testElement, item);

        assertEquals(expectedPath, actual);
    }

    @Test
    public void testObjectReplace() throws Exception {
        String expected = "{\"itemList\":{\"items\":[" +
            "{\"id\":\"item1\"}," +
            "{\"id\":\"item2\",\"label\":\"Item 2\"}," +
            "{\"id\":\"item3\"}," +
            "{\"id\":\"subItem1Itemssn\",\"label\":\"ssn\"}," +
            "{\"id\":\"item4\"}," +
            "{\"id\":\"item5\"}," +
            "{\"id\":\"subItem1\"," +
            "\"subItems\":[" +
                "{\"id\":\"subItem1Item1\",\"label\":\"SubItem 1\"}," +
                "{\"id\":\"subItem1Item2\",\"label\":\"SubItem 2\"}," +
                "{\"id\":\"subItem1Item3\",\"label\":\"SubItem 3\"}" +
            "]" +
            "}," +
            "{\"id\":\"subItem1Itemphone\",\"label\":\"phone\"}," +
            "{\"id\":\"item7\",\"label\":\"Item 7\"}," +
            "{\"id\":\"subItem2\"," +
            "\"subItems\":{\"id\":\"item1\",\"label\":\"SubItem 2 item1\"}" +
            "}" +
            "]}}";

        String actual = Main.objectReplace();

        assertEquals(expected, actual);
    }

    @Test
    public void testRequestedValue() {
        String pathToFollow = "itemList.items.subItems";
        String elementToFind = "label";
        String idToFind = "subItem1Item2";
        String expected = "SubItem 2 label";
        String actual = Main.requestedValue(pathToFollow, elementToFind, idToFind);

        assertEquals(expected, actual);
    }
}