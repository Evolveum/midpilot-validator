/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */

package com.evolveum.validation.convertor;

import com.evolveum.validation.converter.Converter;
import com.evolveum.validation.common.SupportedLanguage;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Dominik.
 */
public class ConvertorTest {

    @Test()
    public void testXmlToJson() throws JSONException {

        Converter convertor = new Converter("json");
        String xml = """
                <attribute>
                    <ref>givenName</ref>
                    <outbound>
                        <expression>
                            <script>
                                <code>title + ' ' + givenName</code>
                            </script>
                        </expression>
                    </outbound>
                </attribute>
                """;

        String jsonString = convertor.convert(xml, SupportedLanguage.XML);
        new JSONObject(jsonString);

        Assert.assertTrue(true);
    }

    @Test()
    public void testXmlToYaml() {
        Converter convertor = new Converter("yaml");
        String xml = """
                <attribute>
                    <ref>givenName</ref>
                    <outbound>
                        <expression>
                            <script>
                                <code>title + ' ' + givenName</code>
                            </script>
                        </expression>
                    </outbound>
                </attribute>
                """;

        String yamlString = convertor.convert(xml, SupportedLanguage.XML);

        Assert.assertNotNull(yamlString);
    }

    @Test()
    public void testJsonToXml() throws ParserConfigurationException, IOException, SAXException {
        Converter convertor = new Converter("xml");
        String json = """
                {
                  "attribute": {
                    "ref": "givenName",
                    "outbound": {
                      "expression": {
                        "script": {
                          "code": "title + ' ' + givenName"
                        }
                      }
                    }
                  }
                }
                """;

        String xmlString = convertor.convert(json, SupportedLanguage.JSON);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource is = new InputSource(new StringReader(xmlString));
        var xmlObj = builder.parse(is);

        Assert.assertNotNull(xmlObj);
    }
    
}
