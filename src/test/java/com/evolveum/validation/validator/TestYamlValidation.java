/*
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 */
package com.evolveum.validation.validator;

import com.evolveum.concepts.SourceLocation;
import com.evolveum.concepts.ValidationLog;
import com.evolveum.validation.common.SupportedLanguage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by Dominik.
 */
public class TestYamlValidation {

    private ValidatorProvider<ValidationParams> validatorProvider;

    @BeforeMethod
    public void setUp() {
        this.validatorProvider = new ValidatorProviderImpl();
    }

    @Test
    public void mappingSnippetTest() {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawYaml = """
                mapping:
                  name: givenName-familyName-to-cn
                  source:
                    - path: "$focus/givenName"
                    - path: "$focus/familyName"
                  expression:
                    script:
                      code: "givenName + ' ' + familyName"
                  condition:
                    script:
                      code: "givenName != null && familyName != null"
                  target:
                    path: "$projection/attributes/cn"
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 2, 3),
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

        rawYaml = """
                expression:
                  script:
                    code: "givenName + ' ' + familyName"
                """;

        validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 2, 3),
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

        String rawYaml = """
                attribute:
                  ref: icfs:uid
                  outbound:
                    expression:
                      script:
                        code: "title + ' ' + givenName"
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 2, 3),
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

        rawYaml = """
            objectType:
              attribute:
                - ref: icfs:uid
                  outbound:
                    expression:
                      script:
                        code: "title + ' ' + givenName"
            """;

        validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 2, 3),
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

        rawYaml = """
                schemaHandling:
                  objectType:
                    - attribute:
                        - ref: icfs:uid
                          outbound:
                            expression:
                              script:
                                code: "title + ' ' + givenName"
                """;

        validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 2, 3),
                                "Cannot parse object from element schemaHandling, there is no definition for that element",
                                "Cannot parse object from element schemaHandling, there is no definition for that element can you clarify the definition based on the expected definitions from list: " +
                                        "[CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}ResourceType)]")
                );
    }

    @Test
    public void validationContainerValueTest() {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawYaml = """
                user:
                  "@xmlns": "http://midpoint.evolveum.com/xml/ns/public/common/common-3"
                  assignment: "Test"
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(2)
                .extracting(
                        ValidationLog::location,
                        ValidationLog::message,
                        log -> log.technicalMessage().message().formatted(log.technicalMessage().arguments()))
                .containsExactly(
                        tuple(SourceLocation.from(null, 2, 73),
                                "Item @xmlns has no definition (in value UserType) while parsing MapXNodeImpl",
                                "Item @xmlns has no definition (in value CTD ({http://midpoint.evolveum.com/xml/ns/public/common/common-3}UserType)) while parsing (\n" +
                                        "  @xmlns => \n" +
                                        "    parser JsonValueParser(JSON value: \"http://midpoint.evolveum.com/xml/ns/public/common/common-3\")\n" +
                                        "  assignment => \n" +
                                        "    parser JsonValueParser(JSON value: \"Test\")\n" +
                                        ") can you clarify the definition based on the expected definitions from list: []"),
                        tuple(SourceLocation.from(null, 3, 21),
                                "Cannot parse container value from (non-empty) ",
                                "Cannot parse container value from (non-empty) XNode(primitive:parser JsonValueParser(JSON value: \"Test\"))")
                );
    }

    @Test
    public void correctlyMappingTest() {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawYaml = """
                resource:
                  oid: e1b34373-f61e-4a5a-9fb1-81f931fcfc05
                  version: "0"
                  name: dms
                  schemaHandling:
                    objectType:
                      - kind: account
                        intent: default
                        default: "true"
                        delineation:
                          objectClass: user
                        focus:
                          type: UserType
                        attribute:
                          - ref: name
                            correlator: {}
                            inbound:
                              strength: strong
                              target:
                                path: name
                            outbound:
                              strength: strong
                              source:
                                path: name
                          - ref: fullname
                            inbound:
                              strength: strong
                              target:
                                path: fullName
                            outbound:
                              strength: strong
                              source:
                                path: fullName
                          - ref: email
                            inbound:
                              strength: strong
                              target:
                                path: emailAddress
                            outbound:
                              strength: strong
                              source:
                                path: emailAddress
                        synchronization:
                          reaction:
                            - situation: unmatched
                              actions:
                                addFocus: {}
                            - situation: unlinked
                              actions:
                                link: {}
                            - situation: linked
                              actions:
                                synchronize: {}
                      - kind: generic
                        intent: documentStore
                        delineation:
                          objectClass: documentStore
                        focus:
                          type: ServiceType
                          archetypeRef:
                            oid: 8c0b32f9-fadc-42cb-bc05-878ecfe001e8
                        attribute:
                          - ref: name
                            correlator: {}
                            inbound:
                              strength: strong
                              target:
                                path: name
                          - ref: description
                            inbound:
                              strength: strong
                              target:
                                path: displayName
                        synchronization:
                          reaction:
                            - situation: unmatched
                              actions:
                                addFocus: {}
                            - situation: unlinked
                              actions:
                                link: {}
                            - situation: linked
                              actions:
                                synchronize: {}
                    associationType:
                      name: userDocumentStoreAccess
                      subject:
                        objectType:
                          kind: account
                          intent: default
                        association:
                          ref: access
                          outbound:
                            name: association-outbound
                            expression:
                              associationConstruction:
                                attribute:
                                  ref: level
                                  mapping:
                                    source:
                                      path: targetRef
                                    expression:
                                      script:
                                        code: targetRef?.relation?.localPart
                                objectRef:
                                  ref: documentStore
                                  mapping:
                                    expression:
                                      associationFromLink: {}
                          inbound:
                            name: association-inbound
                            expression:
                              associationSynchronization:
                                objectRef:
                                  ref: documentStore
                                  mapping:
                                    source:
                                      path: $shadow/attributes/level
                                    expression:
                                      shadowOwnerReferenceSearch:
                                        relationExpression:
                                          path: $level
                                    target:
                                      path: targetRef
                                correlation:
                                  correlators:
                                    items:
                                      item:
                                        ref: targetRef
                                synchronization:
                                  reaction:
                                    - situation: unmatched
                                      actions:
                                        addFocusValue: {}
                                    - situation: matched
                                      actions:
                                        synchronize: {}
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }
}
