/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */
package com.evolveum.validation.validator;

import com.evolveum.concepts.SourceLocation;
import com.evolveum.concepts.ValidationLog;

import static org.assertj.core.api.Assertions.tuple;
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
    public void mappingSnippetTest() {
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
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 1, 1),
                                "Cannot parse object from element mapping, there is no definition for that element",
                                "Cannot parse object from element mapping, there is no definition for that element can you clarify the definition based on the expected definitions from list: " +
                                        "[CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MappingEvaluationTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ObjectTemplateType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MappingsType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ObjectTemplateItemDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MetadataHandlingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MetadataItemDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}FocalAutoassignSpecificationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AttributeInboundMappingsDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AttributeOutboundMappingsDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MappingEvaluationRequestType)]")
                );

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
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 1, 1),
                                "Cannot parse object from element expression, there is no definition for that element",
                                "Cannot parse object from element expression, there is no definition for that element can you clarify the definition based on the expected definitions from list: " +
                                        "[CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MappingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MetadataMappingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AbstractMappingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}BeforeItemConditionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ItemSearchConfidenceDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}InboundMappingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}StatePolicyConstraintType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ModificationPolicyConstraintType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AssignmentModificationPolicyConstraintType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CustomPolicyConstraintType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CustomNotifierType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ObjectTemplateMappingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CustomNormalizationStepType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CompositeCorrelatorType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CorrelationConfidenceDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CompositeSubCorrelatorType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ShadowTagSpecificationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}SystemConfigurationAuditEventRecordingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}SystemConfigurationAuditEventRecordingPropertyType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AfterItemConditionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}GuiFlexibleLabelType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}DirectionElementsType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}GuiActionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ExpressionParameterType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ObjectSynchronizationSorterType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AutoassignMappingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}SimulationObjectPredicateType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}LegacyCustomTransportConfigurationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CustomTransportConfigurationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}DashboardWidgetDataFieldType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}SubreportParameterType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CheckExpressionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ItemReportingConditionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}FormItemServerValidationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}PopulateItemType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MappingTimeDeclarationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/model/scripting-3}ScriptingVariableDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/model/scripting-3}EvaluateExpressionActionExpressionType)]")
                );
    }

    @Test
    public void snippetWithoutItemDefinitionTest() {
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
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 1, 1),
                                "Cannot parse object from element attribute, there is no definition for that element",
                                "Cannot parse object from element attribute, there is no definition for that element can you clarify the definition based on the expected definitions from list: " +
                                        "[CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ConstructionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ResourceObjectTypeDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AssociatedResourceObjectTypeDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AssociationSynchronizationExpressionEvaluatorType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AssociationConstructionExpressionEvaluatorType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/resource/capabilities-3}ActivationStatusCapabilityType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/resource/capabilities-3}ActivationLockoutStatusCapabilityType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/resource/capabilities-3}LastLoginTimestampCapabilityType)]")
                );

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
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 1, 1),
                                "Cannot parse object from element objectType, there is no definition for that element",
                                "Cannot parse object from element objectType, there is no definition for that element can you clarify the definition based on the expected definitions from list: " +
                                        "[CTD ({http://prism.evolveum.com/xml/ns/public/types-3}ObjectDeltaType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}RepositoryGetObjectTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}OperationExecutionRecordRealOwnerType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}MappingSpecificationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}RepositoryGetTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ShadowAssociationTypeParticipantDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AccessCertificationAssignmentReviewScopeType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ShadowAssociationTypeObjectDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}RepositoryModifyTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}CacheObjectTypeSettingsType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}AccessCertificationObjectBasedScopeType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}RepositoryGetVersionTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}FullTextSearchIndexedItemsConfigurationType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}SchemaHandlingType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ShadowAssociationTypeSubjectDefinitionType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ObjectActionsExecutedEntryType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}RepositorySearchObjectsTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}RepositorySearchTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}RepositoryDeleteTraceType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/model/model-3}SearchObjectsType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/model/model-3}GetObjectType), " +
                                        "CTD ({http://midpoint.evolveum.com/xml/ns/public/model/extension-3}TaskExtensionType)]")
                );

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
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 1, 1),
                                "Cannot parse object from element schemaHandling, there is no definition for that element",
                                "Cannot parse object from element schemaHandling, there is no definition for that element can you clarify the definition based on the expected definitions from list: " +
                                        "[CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ResourceType)]")
                );
    }

    @Test
    public void validationMetadataTest() {
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
                .hasSize(1)
                .extracting(ValidationLog::location, ValidationLog::message)
                .containsExactly(
                        tuple(SourceLocation.from(null, 21, 5),
                                "Metadata is not of Map type: ")
                );
    }

    @Test
    public void validationContainerValueTest() {
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
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 2, 5),
                                "Cannot parse container value from (non-empty) ",
                                "Cannot parse container value from (non-empty) XNode(primitive:parser ValueParser(DOM-less, \n" +
                                        "        Test\n" +
                                        "    , namespace declarations))")
                );
    }

    @Test()
    public void incorrectlyMappingTest() {
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
                .hasSize(7)
                .extracting(ValidationLog::location, ValidationLog::message)
                .containsExactly(
                    tuple(SourceLocation.from(null, 83, 26), "Item ref has no definition (in value ResourceObjectTypeDefinitionType) while parsing MapXNodeImpl"),
                        tuple(SourceLocation.from(null, 84, 17), "Item inbound has no definition (in value ResourceObjectTypeDefinitionType) while parsing MapXNodeImpl"),
                        tuple(SourceLocation.from(null, 90, 25), "Item outbound has no definition (in value ResourceObjectTypeDefinitionType) while parsing MapXNodeImpl"),
                        tuple(SourceLocation.from(null, 154, 25), "Cannot parse container value from (non-empty) "),
                        tuple(SourceLocation.from(null, 180, 25), "Item situation has no definition (in value SynchronizationReactionsType) while parsing MapXNodeImpl"),
                        tuple(SourceLocation.from(null, 181, 30), "Item actions has no definition (in value SynchronizationReactionsType) while parsing MapXNodeImpl"),
                        tuple(SourceLocation.unknown(), "Attempt to store multiple values in single-valued property target")
                );
    }

    @Test()
    public void correctlyMappingTest() {
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
