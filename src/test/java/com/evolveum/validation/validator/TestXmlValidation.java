/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.SourceLocation;
import com.evolveum.concepts.ValidationLog;

import static org.testng.AssertJUnit.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import com.evolveum.validation.common.SupportedLanguage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by Dominik.
 */
public class TestXmlValidation {

    private static final Logger logger = Logger.getLogger(TestXmlValidation.class.getName());

    private ValidatorProvider<ValidationParams> validatorProvider;

    @BeforeMethod
    public void setUp() {
        this.validatorProvider = new ValidatorProviderImpl();
    }

    @Test
    public void mappingSnippetTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawXml = """
                <mapping>
                    <name>givenName-familyName-to-cn</name>
                    <source>
                        <path>$focus/givenName</path>
                    </source>
                    <source>
                        <path>$focus/familyName</path>
                    </source>
                    <expression>
                        <script>
                            <code>givenName + ' ' + familyName</code>
                        </script>
                    </expression>
                    <condition>
                        <script>
                            <code>givenName != null &amp;&amp; familyName != null</code>
                        </script>
                    </condition>
                    <target>
                        <path>$projection/attributes/cn</path>
                    </target>
                </mapping>
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 1, 1));

        rawXml = """
                <expression>
                    <script>
                        <code>givenName + ' ' + familyName</code>
                    </script>
                </expression>
                """;

        validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 1, 1));
    }

    @Test
    public void snippetWithoutItemDefinitionTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null)
        );

        String rawXml = """
                <attribute>
                    <ref>icfs:uid</ref>
                    <outbound>
                        <expression>
                            <script>
                                <code>title + ' ' + givenName</code>
                            </script>
                        </expression>
                    </outbound>
                </attribute>
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 1, 1));

        rawXml = """
                <objectType>
                <attribute>
                    <ref>icfs:uid</ref>
                    <outbound>
                        <expression>
                            <script>
                                <code>title + ' ' + givenName</code>
                            </script>
                        </expression>
                    </outbound>
                </attribute>
                </objectType>
                """;

        validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 1, 1));

        rawXml = """
                <schemaHandling>
                <objectType>
                <attribute>
                    <ref>icfs:uid</ref>
                    <outbound>
                        <expression>
                            <script>
                                <code>title + ' ' + givenName</code>
                            </script>
                        </expression>
                    </outbound>
                </attribute>
                </objectType>
                </schemaHandling>
                """;

        validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }

    @Test
    public void validationMetadataTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null)
        );

        String rawXml = """
                <user oid="f6ba0f28-8875-4b41-9915-1277ed561d11" version="42">
                    <_metadata>
                        <test>abc</test>
                    </_metadata>
                    <name>
                        <_value>alice</_value>
                    </name>
                    <additionalNames>
                        <_value>Jane</_value>
                        <_metadata>
                            <loa>medium</loa>
                        </_metadata>
                    </additionalNames>
                    <additionalNames>
                        <_value>Catherine</_value>
                        <_metadata>
                            <loa>low</loa>
                        </_metadata>
                    </additionalNames>
                    <description>Alice from the Wonderland</description>
                    <assignment id="1112">
                        <description>Assignment</description>
                        <accountConstruction>
                            <howto>Just do it</howto>
                            <when>2025-01-20T09:31:00.000Z</when>
                            <value>ABC</value>
                            <value>
                                <fullName>Nobody</fullName>
                            </value>
                            <_metadata>
                                <loa>low</loa>
                            </_metadata>
                        </accountConstruction>
                        <_metadata>
                            low
                        </_metadata>
                    </assignment>
                    <extension>
                        <singleStringType>
                            <_value>foobar</_value>
                            <_metadata>
                                <loa>low</loa>
                            </_metadata>
                        </singleStringType>
                    </extension>
                </user>
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);

        assertThat(validationLogs)
                .hasSize(4)
                .extracting(ValidationLog::location)
                .containsExactly(
                        SourceLocation.from("unknown", 34, 9),
                        SourceLocation.from("unknown", 8, 5),
                        SourceLocation.from("unknown", 23, 9),
                        SourceLocation.from("unknown", 3, 9)
                );
    }

    @Test
    public void validationContainerValueTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawXml = """
                <user xmlns="http://midpoint.evolveum.com/xml/ns/public/common/common-3">
                    <assignment>
                        Test
                    </assignment>
                </user>
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);

        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 2, 5));
    }

    @Test
    public void incorrectlyMappingTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawXml = """
                <resource xmlns="http://midpoint.evolveum.com/xml/ns/public/common/common-3"
                          xmlns:c="http://midpoint.evolveum.com/xml/ns/public/common/common-3"
                          xmlns:org="http://midpoint.evolveum.com/xml/ns/public/common/org-3"
                          xmlns:q="http://prism.evolveum.com/xml/ns/public/query-3"
                          xmlns:t="http://prism.evolveum.com/xml/ns/public/types-3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xmlns:icfc="http://midpoint.evolveum.com/xml/ns/public/connector/icf-1/connector-schema-3"
                          xmlns:ext="http://midpoint.evolveum.com/xml/ns/test/extension"
                          oid="e1b34373-f61e-4a5a-9fb1-81f931fcfc05" version="0">
                    <name>dms</name>

                    <connectorRef>
                        <filter>
                            <q:text>connectorType = "com.evolveum.polygon.connector.scripted.sql.ScriptedSQLConnector"</q:text>
                        </filter>
                    </connectorRef>

                    <c:connectorConfiguration>
                        <!-- Configuration specific for the ScriptedSQL connector -->
                        <icfc:configurationProperties
                                xmlns:icscscriptedsql="http://midpoint.evolveum.com/xml/ns/public/connector/icf-1/bundle/com.evolveum.polygon.connector-scripted-sql/com.evolveum.polygon.connector.scripted.sql.ScriptedSQLConnector">
                            <icscscriptedsql:createScriptFileName>CreateScript.groovy</icscscriptedsql:createScriptFileName>
                            <icscscriptedsql:updateScriptFileName>UpdateScript.groovy</icscscriptedsql:updateScriptFileName>
                            <icscscriptedsql:deleteScriptFileName>DeleteScript.groovy</icscscriptedsql:deleteScriptFileName>
                            <icscscriptedsql:schemaScriptFileName>SchemaScript.groovy</icscscriptedsql:schemaScriptFileName>
                            <icscscriptedsql:searchScriptFileName>SearchScript.groovy</icscscriptedsql:searchScriptFileName>
                            <icscscriptedsql:testScriptFileName>TestScript.groovy</icscscriptedsql:testScriptFileName>
                            <icscscriptedsql:scriptRoots>D:/mp-projects/issues/testing/associations-49-m5/dms/scripts</icscscriptedsql:scriptRoots>
                            <icscscriptedsql:classpath>.</icscscriptedsql:classpath>
                            <icscscriptedsql:scriptBaseClass>BaseScript</icscscriptedsql:scriptBaseClass>

                            <icscscriptedsql:recompileGroovySource>true</icscscriptedsql:recompileGroovySource>

                            <icscscriptedsql:user>midpoint</icscscriptedsql:user>
                            <icscscriptedsql:password>password</icscscriptedsql:password>
                            <icscscriptedsql:jdbcDriver>org.postgresql.Driver</icscscriptedsql:jdbcDriver>
                            <icscscriptedsql:jdbcUrlTemplate>jdbc:postgresql://localhost:5432/dms</icscscriptedsql:jdbcUrlTemplate>
                        </icfc:configurationProperties>
                    </c:connectorConfiguration>

                    <capabilities xmlns:cap="http://midpoint.evolveum.com/xml/ns/public/resource/capabilities-3">
                        <configured>
                           <cap:pagedSearch/>
                            <cap:countObjects/>
                            <cap:create>
                                <cap:enabled>true</cap:enabled>
                            </cap:create>
                            <cap:update>
                                <cap:enabled>true</cap:enabled>
                            </cap:update>
                            <cap:delete>
                                <cap:enabled>true</cap:enabled>
                            </cap:delete>
                        </configured>
                    </capabilities>
                    <schemaHandling>
                        <objectType>
                            <kind>account</kind>
                            <intent>default</intent>
                            <default>true</default>
                            <delineation>
                                <objectClass>user</objectClass>
                            </delineation>
                            <focus>
                                <type>UserType</type>
                            </focus>
                            <attribute>
                                <ref>name</ref>
                                <correlator/>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>name</path>
                                    </target>
                                </inbound>
                                <outbound>
                                    <strength>strong</strength>
                                    <source>
                                        <path>name</path>
                                    </source>
                                </outbound>
                            </attribute>
                            <!-- missing <attribute> element -->
                                <ref>fullname</ref>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>fullName</path>
                                    </target>
                                </inbound>
                                <outbound>
                                    <strength>strong</strength>
                                    <source>
                                        <path>fullName</path>
                                    </source>
                                </outbound>
                            <!-- /////////////////////////// -->
                            <attribute>
                                <ref>email</ref>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>emailAddress</path>
                                    </target>
                                </inbound>
                                <outbound>
                                    <strength>strong</strength>
                                    <source>
                                        <path>emailAddress</path>
                                    </source>
                                </outbound>
                            </attribute>
                            <synchronization>
                                <reaction>
                                    <situation>unmatched</situation>
                                    <actions>
                                        <addFocus/>
                                    </actions>
                                </reaction>
                                <reaction>
                                    <situation>unlinked</situation>
                                    <actions>
                                        <link/>
                                    </actions>
                                </reaction>
                                <reaction>
                                    <situation>linked</situation>
                                    <actions>
                                        <synchronize/>
                                    </actions>
                                </reaction>
                            </synchronization>
                        </objectType>
                        <objectType>
                            <kind>generic</kind>
                            <intent>documentStore</intent>
                            <delineation>
                                <objectClass>documentStore</objectClass>
                            </delineation>
                            <focus>
                                <type>ServiceType</type>
                                <archetypeRef oid="8c0b32f9-fadc-42cb-bc05-878ecfe001e8"/>
                            </focus>
                            <attribute>
                                <ref>name</ref>
                                <correlator/>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>name</path>
                                    </target>
                                </inbound>
                            </attribute>
                            <!--BUG empty container value-->
                            <attribute>
                                Primitive value for container
                            </attribute>
                            <attribute>
                                <ref>description</ref>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>displayName</path>
                                    </target>
                                </inbound>
                            </attribute>
                            <synchronization>
                                <reaction>
                                    <situation>unmatched</situation>
                                    <actions>
                                        <addFocus/>
                                    </actions>
                                </reaction>
                                <reaction>
                                    <situation>unlinked</situation>
                                    <actions>
                                        <link/>
                                    </actions>
                                </reaction>
                                <!-- missing <reaction> element -->
                                    <situation>linked</situation>
                                    <actions>
                                        <synchronize/>
                                    </actions>
                                <!-- /////////////////////////// -->
                            </synchronization>
                        </objectType>
                        <associationType>
                            <name>userDocumentStoreAccess</name>
                            <subject>
                                <objectType>
                                    <kind>account</kind>
                                    <intent>default</intent>
                                </objectType>
                                <association>
                                    <ref>access</ref>
                                    <outbound>
                                        <name>association-outbound</name>
                                        <expression>
                                            <associationConstruction>
                                                <attribute>
                                                    <ref>level</ref>
                                                    <mapping>
                                                        <source>
                                                            <path>targetRef</path>
                                                        </source>
                                                        <expression>
                                                            <script>
                                                                <code>targetRef?.relation?.localPart</code>
                                                            </script>
                                                        </expression>
                                                    </mapping>
                                                </attribute>
                                                <objectRef>
                                                    <ref>documentStore</ref>
                                                    <mapping>
                                                        <expression>
                                                            <associationFromLink/>
                                                        </expression>
                                                    </mapping>
                                                </objectRef>
                                            </associationConstruction>
                                        </expression>
                                    </outbound>
                                    <inbound>
                                        <name>association-inbound</name>
                                        <expression>
                                            <associationSynchronization>
                                                <objectRef>
                                                    <ref>documentStore</ref>
                                                    <mapping>
                                                        <source>
                                                            <path>$shadow/attributes/level</path>
                                                        </source>
                                                        <expression>
                                                            <shadowOwnerReferenceSearch>
                                                                <relationExpression>
                                                                    <path>$level</path>
                                                                </relationExpression>
                                                            </shadowOwnerReferenceSearch>
                                                        </expression>
                                                        <target>
                                                            <path>targetRef</path>
                                                        </target>
                                                         <target>
                                                        </target>
                                                    </mapping>
                                                </objectRef>
                                                <correlation>
                                                    <correlators>
                                                        <items>
                                                            <item>
                                                                <ref>targetRef</ref>
                                                            </item>
                                                        </items>
                                                    </correlators>
                                                </correlation>
                                                <synchronization>
                                                    <reaction>
                                                        <situation>unmatched</situation>
                                                        <actions>
                                                            <addFocusValue/>
                                                        </actions>
                                                    </reaction>
                                                    <reaction>
                                                        <situation>matched</situation>
                                                        <actions>
                                                            <synchronize/>
                                                        </actions>
                                                    </reaction>
                                                </synchronization>
                                            </associationSynchronization>
                                        </expression>
                                    </inbound>
                                </association>
                            </subject>
                        </associationType>
                    </schemaHandling>
                </resource>
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertThat(validationLogs)
                .hasSize(8)
                .extracting(ValidationLog::location)
                .containsExactly(
                        SourceLocation.from("unknown", 83, 17),
                        SourceLocation.from("unknown", 84, 17),
                        SourceLocation.from("unknown", 90, 17),
                        SourceLocation.from("unknown", 154, 13),
                        SourceLocation.from("unknown", 180, 21),
                        SourceLocation.from("unknown", 181, 21),
                        SourceLocation.from("unknown", 241, 41),
                        SourceLocation.from("unknown", 244, 42)
                );
    }

    @Test
    public void snippetWithItemPathInvalidTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(new ValidationParams("ResourceType", "schemaHandling/objectType/attribute"));

        String rawXml = """
        <attribute>
            <refa>name</refa>
            <correlator/>
            <inbound>
                <strength>strong</strength>
                <target>
                    <path>name</path>
                </target>
            </inbound>
            <outbound>
                <strength>strong</strength>
                <source>
                    <path>name</path>
                </source>
            </outbound>
        </attribute>
        """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(
                        SourceLocation.from("unknown", 2, 5)
                );
    }

    @Test
    public void snippetWithItemPathTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(new ValidationParams("ResourceType", "schemaHandling/objectType/attribute"));

        String rawXml = """
        <attribute>
            <ref>name</ref>
            <correlator/>
            <inbound>
                <strength>strong</strength>
                <target>
                    <path>name</path>
                </target>
            </inbound>
            <outbound>
                <strength>strong</strength>
                <source>
                    <path>name</path>
                </source>
            </outbound>
        </attribute>
        """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }

    @Test
    public void snippetWithComplexTypeDefParameterValidTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(new ValidationParams("ResourceAttributeDefinitionType", null));

        String rawXml = """
        <attribute>
            <ref>name</ref>
            <correlator/>
            <inbound>
                <strength>strong</strength>
                <target>
                    <path>name</path>
                </target>
            </inbound>
            <outbound>
                <strength>strong</strength>
                <source>
                    <path>name</path>
                </source>
            </outbound>
        </attribute>
        """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }

    @Test
    public void correctlyMappingTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawXml = """
                <resource xmlns="http://midpoint.evolveum.com/xml/ns/public/common/common-3"
                          xmlns:c="http://midpoint.evolveum.com/xml/ns/public/common/common-3"
                          xmlns:org="http://midpoint.evolveum.com/xml/ns/public/common/org-3"
                          xmlns:q="http://prism.evolveum.com/xml/ns/public/query-3"
                          xmlns:t="http://prism.evolveum.com/xml/ns/public/types-3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xmlns:icfc="http://midpoint.evolveum.com/xml/ns/public/connector/icf-1/connector-schema-3"
                          oid="e1b34373-f61e-4a5a-9fb1-81f931fcfc05" version="0">
                    <name>dms</name>

                    <connectorRef>
                        <filter>
                            <q:text>connectorType = "com.evolveum.polygon.connector.scripted.sql.ScriptedSQLConnector"</q:text>
                        </filter>
                    </connectorRef>

                    <c:connectorConfiguration>
                        <!-- Configuration specific for the ScriptedSQL connector -->
                        <icfc:configurationProperties
                                xmlns:icscscriptedsql="http://midpoint.evolveum.com/xml/ns/public/connector/icf-1/bundle/com.evolveum.polygon.connector-scripted-sql/com.evolveum.polygon.connector.scripted.sql.ScriptedSQLConnector">
                            <icscscriptedsql:createScriptFileName>CreateScript.groovy</icscscriptedsql:createScriptFileName>
                            <icscscriptedsql:updateScriptFileName>UpdateScript.groovy</icscscriptedsql:updateScriptFileName>
                            <icscscriptedsql:deleteScriptFileName>DeleteScript.groovy</icscscriptedsql:deleteScriptFileName>
                            <icscscriptedsql:schemaScriptFileName>SchemaScript.groovy</icscscriptedsql:schemaScriptFileName>
                            <icscscriptedsql:searchScriptFileName>SearchScript.groovy</icscscriptedsql:searchScriptFileName>
                            <icscscriptedsql:testScriptFileName>TestScript.groovy</icscscriptedsql:testScriptFileName>
                            <icscscriptedsql:scriptRoots>D:/mp-projects/issues/testing/associations-49-m5/dms/scripts</icscscriptedsql:scriptRoots>
                            <icscscriptedsql:classpath>.</icscscriptedsql:classpath>
                            <icscscriptedsql:scriptBaseClass>BaseScript</icscscriptedsql:scriptBaseClass>

                            <icscscriptedsql:recompileGroovySource>true</icscscriptedsql:recompileGroovySource>

                            <icscscriptedsql:user>midpoint</icscscriptedsql:user>
                            <icscscriptedsql:password>password</icscscriptedsql:password>
                            <icscscriptedsql:jdbcDriver>org.postgresql.Driver</icscscriptedsql:jdbcDriver>
                            <icscscriptedsql:jdbcUrlTemplate>jdbc:postgresql://localhost:5432/dms</icscscriptedsql:jdbcUrlTemplate>
                        </icfc:configurationProperties>
                    </c:connectorConfiguration>

                    <capabilities xmlns:cap="http://midpoint.evolveum.com/xml/ns/public/resource/capabilities-3">
                        <configured>
                           <cap:pagedSearch/>
                            <cap:countObjects/>
                            <cap:create>
                                <cap:enabled>true</cap:enabled>
                            </cap:create>
                            <cap:update>
                                <cap:enabled>true</cap:enabled>
                            </cap:update>
                            <cap:delete>
                                <cap:enabled>true</cap:enabled>
                            </cap:delete>
                        </configured>
                    </capabilities>

                    <schemaHandling>
                        <objectType>
                            <kind>account</kind>
                            <intent>default</intent>
                            <default>true</default>
                            <delineation>
                                <objectClass>user</objectClass>
                            </delineation>
                            <focus>
                                <type>UserType</type>
                            </focus>
                            <attribute>
                                <ref>name</ref>
                                <correlator/>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>name</path>
                                    </target>
                                </inbound>
                                <outbound>
                                    <strength>strong</strength>
                                    <source>
                                        <path>name</path>
                                    </source>
                                </outbound>
                            </attribute>
                            <attribute>
                                <ref>fullname</ref>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>fullName</path>
                                    </target>
                                </inbound>
                                <outbound>
                                    <strength>strong</strength>
                                    <source>
                                        <path>fullName</path>
                                    </source>
                                </outbound>
                            </attribute>
                            <attribute>
                                <ref>email</ref>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>emailAddress</path>
                                    </target>
                                </inbound>
                                <outbound>
                                    <strength>strong</strength>
                                    <source>
                                        <path>emailAddress</path>
                                    </source>
                                </outbound>
                            </attribute>
                            <synchronization>
                                <reaction>
                                    <situation>unmatched</situation>
                                    <actions>
                                        <addFocus/>
                                    </actions>
                                </reaction>
                                <reaction>
                                    <situation>unlinked</situation>
                                    <actions>
                                        <link/>
                                    </actions>
                                </reaction>
                                <reaction>
                                    <situation>linked</situation>
                                    <actions>
                                        <synchronize/>
                                    </actions>
                                </reaction>
                            </synchronization>
                        </objectType>
                        <objectType>
                            <kind>generic</kind>
                            <intent>documentStore</intent>
                            <delineation>
                                <objectClass>documentStore</objectClass>
                            </delineation>
                            <focus>
                                <type>ServiceType</type>
                                <archetypeRef oid="8c0b32f9-fadc-42cb-bc05-878ecfe001e8"/>
                            </focus>
                            <attribute>
                                <ref>name</ref>
                                <correlator/>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>name</path>
                                    </target>
                                </inbound>
                            </attribute>
                            <attribute>
                                <ref>description</ref>
                                <inbound>
                                    <strength>strong</strength>
                                    <target>
                                        <path>displayName</path>
                                    </target>
                                </inbound>
                            </attribute>
                            <synchronization>
                                <reaction>
                                    <situation>unmatched</situation>
                                    <actions>
                                        <addFocus/>
                                    </actions>
                                </reaction>
                                <reaction>
                                    <situation>unlinked</situation>
                                    <actions>
                                        <link/>
                                    </actions>
                                </reaction>
                                <reaction>
                                    <situation>linked</situation>
                                    <actions>
                                        <synchronize/>
                                    </actions>
                                </reaction>
                            </synchronization>
                        </objectType>
                        <associationType>
                            <name>userDocumentStoreAccess</name>
                            <subject>
                                <objectType>
                                    <kind>account</kind>
                                    <intent>default</intent>
                                </objectType>
                                <association>
                                    <ref>access</ref>
                                    <outbound>
                                        <name>association-outbound</name>
                                        <expression>
                                            <associationConstruction>
                                                <attribute>
                                                    <ref>level</ref>
                                                    <mapping>
                                                        <source>
                                                            <path>targetRef</path>
                                                        </source>
                                                        <expression>
                                                            <script>
                                                                <code>targetRef?.relation?.localPart</code>
                                                            </script>
                                                        </expression>
                                                    </mapping>
                                                </attribute>
                                                <objectRef>
                                                    <ref>documentStore</ref>
                                                    <mapping>
                                                        <expression>
                                                            <associationFromLink/>
                                                        </expression>
                                                    </mapping>
                                                </objectRef>
                                            </associationConstruction>
                                        </expression>
                                    </outbound>
                                    <inbound>
                                        <name>association-inbound</name>
                                        <expression>
                                            <associationSynchronization>
                                                <objectRef>
                                                    <ref>documentStore</ref>
                                                    <mapping>
                                                        <source>
                                                            <path>$shadow/attributes/level</path>
                                                        </source>
                                                        <expression>
                                                            <shadowOwnerReferenceSearch>
                                                                <relationExpression>
                                                                    <path>$level</path>
                                                                </relationExpression>
                                                            </shadowOwnerReferenceSearch>
                                                        </expression>
                                                        <target>
                                                            <path>targetRef</path>
                                                        </target>
                                                    </mapping>
                                                </objectRef>
                                                <correlation>
                                                    <correlators>
                                                        <items>
                                                            <item>
                                                                <ref>targetRef</ref>
                                                            </item>
                                                        </items>
                                                    </correlators>
                                                </correlation>
                                                <synchronization>
                                                    <reaction>
                                                        <situation>unmatched</situation>
                                                        <actions>
                                                            <addFocusValue/>
                                                        </actions>
                                                    </reaction>
                                                    <reaction>
                                                        <situation>matched</situation>
                                                        <actions>
                                                            <synchronize/>
                                                        </actions>
                                                    </reaction>
                                                </synchronization>
                                            </associationSynchronization>
                                        </expression>
                                    </inbound>
                                </association>
                            </subject>
                        </associationType>
                    </schemaHandling>
                </resource>
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }

    @Test
    public void snippetWithDefinitionTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(new ValidationParams("ResourceType", null));

        String rawXml = """
                        <schemaHandling>
                            <objectType>
                                <attribute>
                                    <ref>name</ref>
                                    <correlator/>
                                    <inbound>
                                        <strength>strong</strength>
                                        <target>
                                            <path>name</path>
                                        </target>
                                    </inbound>
                                    <outbound>
                                        <strength>strong</strength>
                                        <source>
                                            <path>name</path>
                                        </source>
                                    </outbound>
                                </attribute>
                            </objectType>
                        </schemaHandling>
                        """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }

    @Test
    public void snippetWithMappingTypeDefTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(new ValidationParams("MappingType", null));

        String rawXml = """
                <mapping>
                    <name>givenName-familyName-to-cn</name>
                    <source>
                        <path>$focus/givenName</path>
                    </source>
                    <source>
                        <path>$focus/familyName</path>
                    </source>
                    <expression>
                        <script>
                            <code>givenName + ' ' + familyName</code>
                        </script>
                    </expression>
                    <condition>
                        <script>
                            <code>givenName != null &amp;&amp; familyName != null</code>
                        </script>
                    </condition>
                    <target>
                        <path>$projection/attributes/cn</path>
                    </target>
                </mapping>
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }


    @Test
    public void snippetSchemaHandlingDefTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(new ValidationParams(null, null));

        String rawXml = """
        <schemaHandling>
            <objectType>
                <kind>account</kind>
                <intent>default</intent>
                <default>true</default>
                <delineation>
                    <objectClass>user</objectClass>
                </delineation>
                <focus>
                <type>UserType</type>
                </focus>
                <attribute>
                    <ref>name</ref>
                    <correlator/>
                    <inbound>
                    <strength>strong</strength>
                    <target>
                    <path>name</path>
                    </target>
                    </inbound>
                    <outbound>
                    <strength>strong</strength>
                    <source>
                        <path>name</path>
                    </source>
                    </outbound>
                </attribute>
            </objectType>
        </schemaHandling>
        """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawXml, SupportedLanguage.XML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }

    @Test(enabled = false, description = "Testing xml objects from Midpoint-Sample")
    public void samplesTest() throws IOException {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));
        String resourceFolder = "objects/";
        URL resource = getClass().getClassLoader().getResource(resourceFolder);
        if (resource == null) {
            throw new IllegalStateException("Could not find folder: " + resourceFolder + " on classpath");
        }

        if (!resource.getProtocol().equals("jar")) {
            System.out.println("Not loaded from a JAR. Protocol = " + resource.getProtocol());
            return;
        }

        String jarPath = resource.getPath();
        jarPath = jarPath.substring("file:".length(), jarPath.indexOf("!"));
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

        try (JarFile jarFile = new JarFile(jarPath)) {
            logger.log(new LogRecord(Level.INFO, "XML files found in midpoint-sample JAR under 'objects/':"));

            jarFile.stream()
                    .filter(e -> !e.isDirectory())
                    .filter(e -> e.getName().startsWith("objects/"))
                    .filter(e -> e.getName().endsWith(".xml"))
                    .forEach(entry -> {
                        try (InputStream is = getClass().getClassLoader().getResourceAsStream(entry.getName())) {
                            if (is != null) {
                                List<ValidationLog> validationLogs = new ArrayList<>();
                                validationLogs = codeValidator.validate(
                                        new String(is.readAllBytes(), StandardCharsets.UTF_8), SupportedLanguage.XML);
                                validationLogs.forEach(log -> {
                                    logger.log(new LogRecord(Level.WARNING, log.message()));
                                });
                            } else {
                                logger.log(new LogRecord(Level.WARNING, "Could not load stream for: " + entry.getName()));
                            }
                        } catch (Exception e) {
                            logger.log(new LogRecord(Level.WARNING, "Failed to parse: " + entry.getName() + " -> " + e.getMessage()));
                        }
                    });
        }
    }
}
