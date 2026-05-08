# Setup Code Quality Tooling — detekt, jacoco, ktlint, CI

## TL;DR

> **Quick Summary**: Setup Code Quality Tooling: detekt, jacoco, ktlint, and GitHub Actions CI configuration.
>
> **Deliverables**:
> - Updated `build.gradle.kts` with detekt, jacoco, ktlint plugins + task configs
> - `config/detekt/detekt.yml` (full config)
> - `config/detekt/baseline.xml` (empty/fresh baseline)
> - `.editorconfig` (with ktlint settings)
> - `.github/workflows/ci.yml` (CI without performance-test job)
>
> **Estimated Effort**: Quick
> **Parallel Execution**: NO — sequential, each file depends on the previous context
> **Critical Path**: build.gradle.kts → detekt config files → .editorconfig → CI workflow

---

## Context

### Original Request
- detekt configuration
- jacoco configuration
- ktlint configuration
- GitHub Workflows `ci.yml` — WITHOUT the `performance-test` step/job

### Key Facts Discovered

**estaparking current `build.gradle.kts`:**
- Spring Boot `3.5.14`, Java `21`, Kotlin `2.3.10`
- Does NOT have detekt, jacoco, ktlint yet
- Uses tabs for indentation
- Group: `com.charlesluxinger.estaparking`
- Already has: `kotlin("jvm")`, `kotlin("plugin.spring")`, `kotlin("plugin.jpa")`, `org.springframework.boot`, `io.spring.dependency-management`

**versions:**
- ktlint plugin: `org.jlleitschuh.gradle.ktlint` version `13.1.0`
- detekt plugin: `dev.detekt` version `2.0.0-alpha.2`
- jacoco: built-in Gradle plugin (no external version needed)
- Kotlin version conflict workaround for detekt: force `org.jetbrains.kotlin` to `2.3.0` inside `matching { it.name.startsWith("detekt") }` configurations block

**estaparking existing `.github/ci.yml`:** Already has a CI file at `.github/ci.yml` (NOT inside `.github/workflows/`). This must be MOVED to `.github/workflows/ci.yml` (create the `workflows/` folder).

---

## Work Objectives

### Core Objective
Wire up detekt, jacoco, and ktlint into estaparking's `build.gradle.kts`, create the required config files, `.editorconfig`, and a proper `ci.yml` inside `.github/workflows/`.

### Concrete Deliverables
1. `C:\Users\charl\Projetos\estaparking\build.gradle.kts` — updated
2. `C:\Users\charl\Projetos\estaparking\config\detekt\detekt.yml` — new file
3. `C:\Users\charl\Projetos\estaparking\config\detekt\baseline.xml` — new file (empty baseline)
4. `C:\Users\charl\Projetos\estaparking\.editorconfig` — new file
5. `C:\Users\charl\Projetos\estaparking\.github\workflows\ci.yml` — new file
6. Delete `C:\Users\charl\Projetos\estaparking\.github\ci.yml` — the old misplaced file

### Must NOT Have (Guardrails)
- Do NOT include the `performance-test` job in ci.yml
- Do NOT change the Spring Boot version, Java version, or any existing dependencies
- Do NOT remove the existing `allOpen`, `kotlin { compilerOptions }`, or `tasks.withType<Test>` blocks
- Do NOT change indentation style of existing code — keep tabs where they already exist, but new blocks should follow the style of the surrounding file

---

## TODOs

- [ ] 1. Update `build.gradle.kts` — add plugins and task config blocks

  **What to do**:

  Edit `C:\Users\charl\Projetos\estaparking\build.gradle.kts`.

  **Step A — Add plugins** (insert the 3 new plugin lines after the existing `id("io.spring.dependency-management")` line):
  ```
  	id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
  	id("dev.detekt") version "2.0.0-alpha.2"
  	jacoco
  ```

  **Step B — Add `configurations` block** (insert after the existing `repositories { mavenCentral() }` block, before `dependencies`):
  ```kotlin
  configurations {
  	matching { it.name.startsWith("detekt") }.configureEach {
  		resolutionStrategy.eachDependency {
  			if (requested.group == "org.jetbrains.kotlin") {
  				useVersion("2.3.0")
  			}
  		}
  	}
  }
  ```

  **Step C — Add `detekt` configuration block** (insert after the `allOpen { ... }` block):
  ```kotlin
  detekt {
  	toolVersion = "2.0.0-alpha.2"
  	buildUponDefaultConfig = true
  	config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
  	baseline = file("$rootDir/config/detekt/baseline.xml")
  	ignoreFailures = false
  }
  ```

  **Step D — Update `tasks.withType<Test>`** — add `finalizedBy(tasks.jacocoTestReport)` inside the existing `tasks.withType<Test> { ... }` block. The existing block looks like:
  ```kotlin
  tasks.withType<Test> {
  	useJUnitPlatform()
  }
  ```
  It should become:
  ```kotlin
  tasks.withType<Test> {
  	useJUnitPlatform()
  	finalizedBy(tasks.jacocoTestReport)
  }
  ```

  **Step E — Add jacoco report and coverage verification tasks** (insert at the END of the file, after `tasks.withType<Test>`):
  ```kotlin
  tasks.jacocoTestReport {
  	dependsOn(tasks.test)
  	reports {
  		xml.required.set(true)
  		csv.required.set(false)
  		html.required.set(true)
  	}
  }

  tasks.jacocoTestCoverageVerification {
  	violationRules {
  		rule {
  			limit {
  				minimum = "0.85".toBigDecimal()
  			}
  		}
  	}
  }
  ```

  **Must NOT do**:
  - Do NOT add `mockitoAgent` configuration (not needed in estaparking)
  - Do NOT change `java-version: '21'` or any Spring Boot version
  - Do NOT add any new dependencies
  - Do NOT remove existing blocks (`allOpen`, `kotlin { compilerOptions }`)

  **References**:
  - `C:\Users\charl\Projetos\estaparking\build.gradle.kts` — current state of the target file (read before editing)

  **Acceptance Criteria**:
  - [ ] `build.gradle.kts` plugins block includes `ktlint`, `detekt`, and `jacoco`
  - [ ] `configurations { matching { ... } }` block is present for detekt Kotlin version pin
  - [ ] `detekt { ... }` block is present pointing to `config/detekt/detekt.yml` and `config/detekt/baseline.xml`
  - [ ] `tasks.withType<Test>` block includes `finalizedBy(tasks.jacocoTestReport)`
  - [ ] `tasks.jacocoTestReport { ... }` block is present with xml/html output
  - [ ] `tasks.jacocoTestCoverageVerification { ... }` block is present with 0.85 minimum

---

- [ ] 2. Create `config/detekt/detekt.yml`

  **What to do**:
  Create the directory `C:\Users\charl\Projetos\estaparking\config\detekt\` and write the following file **verbatim** as `detekt.yml`:

  ```yaml
  config:
    validation: true
    warningsAsErrors: false
    checkExhaustiveness: false
    # when writing own rules with new properties, exclude the property path e.g.: ['my_rule_set', '.*>.*>[my_property]']
    excludes: []

  processors:
    active: true
    exclude:
      - 'DetektProgressListener'
    # - 'KtFileCountProcessor'
    # - 'PackageCountProcessor'
    # - 'ClassCountProcessor'
    # - 'FunctionCountProcessor'
    # - 'PropertyCountProcessor'
    # - 'ProjectComplexityProcessor'
    # - 'ProjectCognitiveComplexityProcessor'
    # - 'ProjectLLOCProcessor'
    # - 'ProjectCLOCProcessor'
    # - 'ProjectLOCProcessor'
    # - 'ProjectSLOCProcessor'
    # - 'LicenseHeaderLoaderExtension'

  console-reports:
    active: true
    exclude:
       - 'ProjectStatisticsReport'
       - 'ComplexityReport'
       - 'NotificationReport'
       - 'IssuesReport'
       - 'FileBasedIssuesReport'
    #  - 'LiteIssuesReport'

  comments:
    active: true
    AbsentOrWrongFileLicense:
      active: false
      licenseTemplateIsRegex: false
      licenseTemplate: ''
    DeprecatedBlockTag:
      active: false
    DocumentationOverPrivateFunction:
      active: false
    DocumentationOverPrivateProperty:
      active: false
    EndOfSentenceFormat:
      active: false
      endOfSentenceFormat: '([.?!][ \t\n\r\f<])|([.?!:]$)'
    KDocReferencesNonPublicProperty:
      active: false
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
    OutdatedDocumentation:
      active: false
      matchTypeParameters: true
      matchDeclarationsOrder: true
      allowParamOnConstructorProperties: false
    UndocumentedPublicClass:
      active: false
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      searchInNestedClass: true
      searchInInnerClass: true
      searchInInnerObject: true
      searchInInnerInterface: true
      searchInProtectedClass: false
      ignoreDefaultCompanionObject: false
    UndocumentedPublicFunction:
      active: false
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      searchProtectedFunction: false
    UndocumentedPublicProperty:
      active: false
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      searchProtectedProperty: false
      ignoreEnumEntries: false

  complexity:
    active: true
    CognitiveComplexMethod:
      active: false
      allowedComplexity: 15
    ComplexCondition:
      active: true
      allowedConditions: 3
    ComplexInterface:
      active: false
      allowedDefinitions: 10
      includeStaticDeclarations: false
      includePrivateDeclarations: false
      ignoreOverloaded: false
    CyclomaticComplexMethod:
      active: true
      allowedComplexity: 14
      ignoreSingleWhenExpression: false
      ignoreSimpleWhenEntries: false
      ignoreNestingFunctions: false
      ignoreLocalFunctions: false
      nestingFunctions:
        - 'also'
        - 'apply'
        - 'forEach'
        - 'isNotNull'
        - 'ifNull'
        - 'let'
        - 'run'
        - 'use'
        - 'with'
    LabeledExpression:
      active: false
      ignoredLabels: []
    LargeClass:
      active: true
      allowedLines: 600
    LongMethod:
      active: true
      allowedLines: 60
    LongParameterList:
      active: true
      allowedFunctionParameters: 5
      allowedConstructorParameters: 6
      ignoreDefaultParameters: false
      ignoreDataClasses: true
      ignoreAnnotatedParameter: []
    MethodOverloading:
      active: false
      allowedOverloads: 6
    NamedArguments:
      active: false
      allowedArguments: 3
      ignoreMethods: []
      ignoreArgumentsMatchingNames: false
    NestedBlockDepth:
      active: true
      allowedDepth: 4
    NestedScopeFunctions:
      active: false
      allowedDepth: 1
      functions:
        - 'kotlin.apply'
        - 'kotlin.run'
        - 'kotlin.with'
        - 'kotlin.let'
        - 'kotlin.also'
    ReplaceSafeCallChainWithRun:
      active: false
    StringLiteralDuplication:
      active: false
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      allowedDuplications: 2
      ignoreAnnotation: true
      allowedWithLengthLessThan: 5
      ignoreStringsRegex: '$^'
    TooManyFunctions:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      allowedFunctionsPerFile: 12
      allowedFunctionsPerClass: 12
      allowedFunctionsPerInterface: 11
      allowedFunctionsPerObject: 11
      allowedFunctionsPerEnum: 11
      ignoreDeprecated: false
      ignorePrivate: false
      ignoreInternal: false
      ignoreOverridden: false
      ignoreAnnotatedFunctions: []

  coroutines:
    active: true
    CoroutineLaunchedInTestWithoutRunTest:
      active: false
    GlobalCoroutineUsage:
      active: false
    InjectDispatcher:
      active: true
      dispatcherNames:
        - 'IO'
        - 'Default'
        - 'Unconfined'
    RedundantSuspendModifier:
      active: true
    SleepInsteadOfDelay:
      active: true
    SuspendFunInFinallySection:
      active: false
    SuspendFunSwallowedCancellation:
      active: false
    SuspendFunWithCoroutineScopeReceiver:
      active: false
      aliases: ['SuspendFunctionOnCoroutineScope']
    SuspendFunWithFlowReturnType:
      active: true

  empty-blocks:
    active: true
    EmptyCatchBlock:
      active: true
      allowedExceptionNameRegex: '_|(ignore|expected).*'
    EmptyClassBlock:
      active: true
    EmptyDefaultConstructor:
      active: true
    EmptyDoWhileBlock:
      active: true
    EmptyElseBlock:
      active: true
    EmptyFinallyBlock:
      active: true
    EmptyForBlock:
      active: true
    EmptyFunctionBlock:
      active: true
      ignoreOverridden: false
    EmptyIfBlock:
      active: true
    EmptyInitBlock:
      active: true
    EmptyKotlinFile:
      active: true
    EmptySecondaryConstructor:
      active: true
    EmptyTryBlock:
      active: true
    EmptyWhenBlock:
      active: true
    EmptyWhileBlock:
      active: true

  exceptions:
    active: true
    ErrorUsageWithThrowable:
      active: false
    ExceptionRaisedInUnexpectedLocation:
      active: true
      methodNames:
        - 'equals'
        - 'finalize'
        - 'hashCode'
        - 'toString'
    InstanceOfCheckForException:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
    NotImplementedDeclaration:
      active: false
    ObjectExtendsThrowable:
      active: false
    PrintStackTrace:
      active: true
    RethrowCaughtException:
      active: true
    ReturnFromFinally:
      active: true
      ignoreLabeled: false
    SwallowedException:
      active: true
      ignoredExceptionTypes:
        - 'InterruptedException'
        - 'MalformedURLException'
        - 'NumberFormatException'
        - 'ParseException'
      allowedExceptionNameRegex: '_|(ignore|expected).*'
    ThrowingExceptionFromFinally:
      active: true
    ThrowingExceptionInMain:
      active: false
    ThrowingExceptionsWithoutMessageOrCause:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      exceptions:
        - 'ArrayIndexOutOfBoundsException'
        - 'Exception'
        - 'IllegalArgumentException'
        - 'IllegalMonitorStateException'
        - 'IllegalStateException'
        - 'IndexOutOfBoundsException'
        - 'NullPointerException'
        - 'RuntimeException'
        - 'Throwable'
    ThrowingNewInstanceOfSameException:
      active: true
    TooGenericExceptionCaught:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      exceptionNames:
        - 'ArrayIndexOutOfBoundsException'
        - 'Error'
        - 'Exception'
        - 'IllegalMonitorStateException'
        - 'IndexOutOfBoundsException'
        - 'NullPointerException'
        - 'RuntimeException'
        - 'Throwable'
      allowedExceptionNameRegex: '_|(ignore|expected).*'
    TooGenericExceptionThrown:
      active: true
      exceptionNames:
        - 'Error'
        - 'Exception'
        - 'RuntimeException'
        - 'Throwable'

  naming:
    active: true
    BooleanPropertyNaming:
      active: false
      allowedPattern: '^(is|has|are)'
    ClassNaming:
      active: true
      aliases: ['ClassName']
      classPattern: '[A-Z][a-zA-Z0-9]*'
    ConstructorParameterNaming:
      active: true
      parameterPattern: '[a-z][A-Za-z0-9]*'
      privateParameterPattern: '[a-z][A-Za-z0-9]*'
      excludeClassPattern: '$^'
    EnumNaming:
      active: true
      aliases: ['EnumEntryName']
      enumEntryPattern: '[A-Z][_a-zA-Z0-9]*'
    ForbiddenClassName:
      active: false
      forbiddenName: []
    FunctionNameMaxLength:
      active: false
      aliases: ['FunctionMaxNameLength']
      maximumFunctionNameLength: 30
    FunctionNameMinLength:
      active: false
      aliases: ['FunctionMinNameLength']
      minimumFunctionNameLength: 3
    FunctionNaming:
      active: true
      aliases: ['FunctionName']
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      functionPattern: '[a-z][a-zA-Z0-9]*'
      excludeClassPattern: '$^'
    FunctionParameterNaming:
      active: true
      parameterPattern: '[a-z][A-Za-z0-9]*'
      excludeClassPattern: '$^'
    InvalidPackageDeclaration:
      active: true
      aliases: ['PackageDirectoryMismatch']
      rootPackage: ''
      requireRootInDeclaration: false
    LambdaParameterNaming:
      active: false
      parameterPattern: '[a-z][A-Za-z0-9]*|_'
    MatchingDeclarationName:
      active: true
      mustBeFirst: true
      multiplatformTargets:
        - 'ios'
        - 'android'
        - 'js'
        - 'jvm'
        - 'native'
        - 'iosArm64'
        - 'iosX64'
        - 'macosX64'
        - 'mingwX64'
        - 'linuxX64'
    MemberNameEqualsClassName:
      active: true
      ignoreOverridden: true
    NoNameShadowing:
      active: true
    NonBooleanPropertyPrefixedWithIs:
      active: false
    ObjectPropertyNaming:
      active: true
      aliases: ['ObjectPropertyName']
      constantPattern: '[A-Za-z][_A-Za-z0-9]*'
      propertyPattern: '[A-Za-z][_A-Za-z0-9]*'
      privatePropertyPattern: '(_)?[A-Za-z][_A-Za-z0-9]*'
    PackageNaming:
      active: true
      aliases: ['PackageName']
      packagePattern: '[a-z_]+(\.[a-z_][A-Za-z0-9_]*)*'
    TopLevelPropertyNaming:
      active: true
      constantPattern: '[A-Z][_A-Z0-9]*'
      propertyPattern: '[A-Za-z][_A-Za-z0-9]*'
      privatePropertyPattern: '_?[A-Za-z][_A-Za-z0-9]*'
    VariableMaxLength:
      active: false
      maximumVariableNameLength: 64
    VariableMinLength:
      active: false
      minimumVariableNameLength: 1
    VariableNaming:
      active: true
      aliases: ['PropertyName']
      variablePattern: '[a-z][A-Za-z0-9]*'
      privateVariablePattern: '(_)?[a-z][A-Za-z0-9]*'
      excludeClassPattern: '$^'

  performance:
    active: true
    ArrayPrimitive:
      active: true
    CouldBeSequence:
      active: false
      allowedOperations: 2
    ForEachOnRange:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
    SpreadOperator:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
    UnnecessaryInitOnArray:
      active: false
    UnnecessaryPartOfBinaryExpression:
      active: false
    UnnecessaryTemporaryInstantiation:
      active: true
    UnnecessaryTypeCasting:
      active: false

  potential-bugs:
    active: true
    AvoidReferentialEquality:
      active: true
      forbiddenTypePatterns:
        - 'kotlin.String'
    CastNullableToNonNullableType:
      active: false
      ignorePlatformTypes: true
    CastToNullableType:
      active: false
    CharArrayToStringCall:
      active: false
    Deprecation:
      active: false
      aliases: ['DEPRECATION']
      excludeImportStatements: false
    DontDowncastCollectionTypes:
      active: false
    DoubleMutabilityForCollection:
      active: true
      aliases: ['DoubleMutability']
      mutableTypes:
        - 'kotlin.collections.MutableList'
        - 'kotlin.collections.MutableMap'
        - 'kotlin.collections.MutableSet'
        - 'java.util.ArrayList'
        - 'java.util.LinkedHashSet'
        - 'java.util.HashSet'
        - 'java.util.LinkedHashMap'
        - 'java.util.HashMap'
    ElseCaseInsteadOfExhaustiveWhen:
      active: false
      ignoredSubjectTypes: []
    EqualsAlwaysReturnsTrueOrFalse:
      active: true
    EqualsWithHashCodeExist:
      active: true
    ExitOutsideMain:
      active: false
    ExplicitGarbageCollectionCall:
      active: true
    HasPlatformType:
      active: true
    IgnoredReturnValue:
      active: true
      restrictToConfig: true
      returnValueAnnotations:
        - 'CheckResult'
        - '*.CheckResult'
        - 'CheckReturnValue'
        - '*.CheckReturnValue'
      ignoreReturnValueAnnotations:
        - 'CanIgnoreReturnValue'
        - '*.CanIgnoreReturnValue'
      returnValueTypes:
        - 'kotlin.Function*'
        - 'kotlin.sequences.Sequence'
        - 'kotlinx.coroutines.flow.*Flow'
        - 'java.util.stream.*Stream'
      ignoreFunctionCall: []
    ImplicitDefaultLocale:
      active: true
    ImplicitUnitReturnType:
      active: false
      allowExplicitReturnType: true
    InvalidRange:
      active: true
    IteratorHasNextCallsNextMethod:
      active: true
    IteratorNotThrowingNoSuchElementException:
      active: true
    LateinitUsage:
      active: false
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
      ignoreOnClassesPattern: ''
    MapGetWithNotNullAssertionOperator:
      active: true
    MissingPackageDeclaration:
      active: false
      excludes: ['**/*.kts']
    MissingSuperCall:
      active: false
      mustInvokeSuperAnnotations:
        - 'androidx.annotation.CallSuper'
        - 'javax.annotation.OverridingMethodsMustInvokeSuper'
    MissingUseCall:
      active: false
    NullCheckOnMutableProperty:
      active: false
    NullableToStringCall:
      active: false
    PropertyUsedBeforeDeclaration:
      active: false
    UnconditionalJumpStatementInLoop:
      active: false
    UnnamedParameterUse:
      active: false
      allowAdjacentDifferentTypeParams: true
      allowSingleParamUse: true
      ignoreArgumentsMatchingNames: true
      ignoreFunctionCall: []
    UnnecessaryNotNullCheck:
      active: false
    UnnecessaryNotNullOperator:
      active: true
    UnnecessarySafeCall:
      active: true
    UnreachableCatchBlock:
      active: true
    UnreachableCode:
      active: true
    UnsafeCallOnNullableType:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**']
    UnsafeCast:
      active: true
      aliases: ['UNCHECKED_CAST']
    UnusedUnaryOperator:
      active: true
    UselessPostfixExpression:
      active: true
    WrongEqualsTypeParameter:
      active: true

  style:
    active: true
    AbstractClassCanBeConcreteClass:
      active: true
    AbstractClassCanBeInterface:
      active: true
    AlsoCouldBeApply:
      active: false
    BracesOnIfStatements:
      active: false
      singleLine: 'never'
      multiLine: 'always'
    BracesOnWhenStatements:
      active: false
      singleLine: 'necessary'
      multiLine: 'consistent'
    CanBeNonNullable:
      active: false
    CascadingCallWrapping:
      active: false
      includeElvis: true
    ClassOrdering:
      active: false
    CollapsibleIfStatements:
      active: false
    DataClassContainsFunctions:
      active: false
      conversionFunctionPrefix:
        - 'to'
      allowOperators: false
    DataClassShouldBeImmutable:
      active: false
    DestructuringDeclarationWithTooManyEntries:
      active: true
      maxDestructuringEntries: 3
    DoubleNegativeExpression:
      active: false
    DoubleNegativeLambda:
      active: false
      negativeFunctions:
        - reason: 'Use `takeIf` instead.'
          value: 'takeUnless'
        - reason: 'Use `all` instead.'
          value: 'none'
      negativeFunctionNameParts:
        - 'not'
        - 'non'
    EqualsNullCall:
      active: true
    EqualsOnSignatureLine:
      active: false
    ExplicitCollectionElementAccessMethod:
      active: false
    ExplicitItLambdaMultipleParameters:
      active: true
    ExplicitItLambdaParameter:
      active: true
    ExpressionBodySyntax:
      active: false
      includeLineWrapping: false
    ForbiddenAnnotation:
      active: false
      annotations:
        - reason: 'it is a java annotation. Use `Suppress` instead.'
          value: 'java.lang.SuppressWarnings'
        - reason: 'it is a java annotation. Use `kotlin.Deprecated` instead.'
          value: 'java.lang.Deprecated'
        - reason: 'it is a java annotation. Use `kotlin.annotation.MustBeDocumented` instead.'
          value: 'java.lang.annotation.Documented'
        - reason: 'it is a java annotation. Use `kotlin.annotation.Target` instead.'
          value: 'java.lang.annotation.Target'
        - reason: 'it is a java annotation. Use `kotlin.annotation.Retention` instead.'
          value: 'java.lang.annotation.Retention'
        - reason: 'it is a java annotation. Use `kotlin.annotation.Repeatable` instead.'
          value: 'java.lang.annotation.Repeatable'
        - reason: 'Kotlin does not support @Inherited annotation, see https://youtrack.jetbrains.com/issue/KT-22265'
          value: 'java.lang.annotation.Inherited'
    ForbiddenComment:
      active: true
      comments:
        - reason: 'Forbidden FIXME todo marker in comment, please fix the problem.'
          value: 'FIXME:'
        - reason: 'Forbidden STOPSHIP todo marker in comment, please address the problem before shipping the code.'
          value: 'STOPSHIP:'
        - reason: 'Forbidden TODO todo marker in comment, please do the changes.'
          value: 'TODO:'
      allowedPatterns: ''
    ForbiddenImport:
      active: false
      forbiddenImports: []
      allowedImports: []
    ForbiddenMethodCall:
      active: false
      methods:
        - reason: 'print does not allow you to configure the output stream. Use a logger instead.'
          value: 'kotlin.io.print'
        - reason: 'println does not allow you to configure the output stream. Use a logger instead.'
          value: 'kotlin.io.println'
        - reason: 'using `BigDecimal(Double)` can result in unexpected floating point precision behavior. Use `BigDecimal.valueOf(Double)` or `String.toBigDecimalOrNull()` instead.'
          value: 'java.math.BigDecimal.<init>(kotlin.Double)'
        - reason: 'using `BigDecimal(String)` can result in a `NumberFormatException`. Use `String.toBigDecimalOrNull()`'
          value: 'java.math.BigDecimal.<init>(kotlin.String)'
        - reason: 'It is marked as obsolete. Use `kotlin.time.measureTime` instead.'
          value: 'kotlin.system.measureTimeMillis'
    ForbiddenNamedParam:
      active: false
      methods: []
    ForbiddenOptIn:
      active: false
      markerClasses: []
    ForbiddenSuppress:
      active: false
      rules: []
    ForbiddenVoid:
      active: true
      ignoreOverridden: false
      ignoreUsageInGenerics: false
    FunctionOnlyReturningConstant:
      active: true
      ignoreOverridableFunction: true
      ignoreActualFunction: true
      excludedFunctions: []
    LoopWithTooManyJumpStatements:
      active: true
      maxJumpCount: 1
    MagicNumber:
      active: true
      excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**', '**/*.kts']
      ignoreNumbers:
        - '-1'
        - '0'
        - '1'
        - '2'
      ignoreHashCodeFunction: true
      ignorePropertyDeclaration: false
      ignoreLocalVariableDeclaration: false
      ignoreConstantDeclaration: true
      ignoreCompanionObjectPropertyDeclaration: true
      ignoreAnnotation: false
      ignoreNamedArgument: true
      ignoreEnums: false
      ignoreRanges: false
      ignoreExtensionFunctions: true
    MandatoryBracesLoops:
      active: false
    MaxChainedCallsOnSameLine:
      active: false
      maxChainedCalls: 5
    MaxLineLength:
      active: true
      maxLineLength: 120
      excludes:
        - '**/test/**'
        - '**/androidTest/**'
        - '**/commonTest/**'
        - '**/jvmTest/**'
        - '**/androidUnitTest/**'
        - '**/androidInstrumentedTest/**'
        - '**/jsTest/**'
        - '**/iosTest/**'
      excludePackageStatements: true
      excludeImportStatements: true
      excludeCommentStatements: false
      excludeRawStrings: true
    MayBeConstant:
      active: true
    ModifierOrder:
      active: true
    MultilineLambdaItParameter:
      active: false
    MultilineRawStringIndentation:
      active: false
      indentSize: 4
      trimmingMethods:
        - 'trimIndent'
        - 'trimMargin'
    NestedClassesVisibility:
      active: true
    NewLineAtEndOfFile:
      active: true
    NoTabs:
      active: false
    NullableBooleanCheck:
      active: false
    ObjectLiteralToLambda:
      active: true
    OptionalAbstractKeyword:
      active: true
    OptionalUnit:
      active: false
    ProtectedMemberInFinalClass:
      active: true
    RangeUntilInsteadOfRangeTo:
      active: false
    RedundantConstructorKeyword:
      active: false
    RedundantExplicitType:
      active: false
    RedundantHigherOrderMapUsage:
      active: true
    RedundantVisibilityModifier:
      active: false
    ReturnCount:
      active: true
      max: 2
      excludedFunctions:
        - 'equals'
      excludeLabeled: false
      excludeReturnFromLambda: true
      excludeGuardClauses: false
    SafeCast:
      active: true
    SerialVersionUIDInSerializableClass:
      active: true
    SpacingAfterPackageDeclaration:
      active: false
    StringShouldBeRawString:
      active: false
      maxEscapedCharacterCount: 2
      ignoredCharacters: []
    ThrowsCount:
      active: true
      max: 2
      excludeGuardClauses: false
    TrailingWhitespace:
      active: false
    TrimMultilineRawString:
      active: false
      trimmingMethods:
        - 'trimIndent'
        - 'trimMargin'
    UnderscoresInNumericLiterals:
      active: false
      acceptableLength: 4
      allowNonStandardGrouping: false
    UnnecessaryAny:
      active: false
    UnnecessaryApply:
      active: true
    UnnecessaryBackticks:
      active: false
    UnnecessaryBracesAroundTrailingLambda:
      active: false
    UnnecessaryFilter:
      active: true
    UnnecessaryFullyQualifiedName:
      active: false
    UnnecessaryInheritance:
      active: true
    UnnecessaryInnerClass:
      active: false
    UnnecessaryLet:
      active: false
    UnnecessaryParentheses:
      active: false
      allowForUnclearPrecedence: false
    UnnecessaryReversed:
      active: false
    UnusedImport:
      active: false
      additionalOperatorSet: []
    UnusedParameter:
      active: true
      aliases: ['UNUSED_PARAMETER', 'unused']
      allowedNames: 'ignored|expected'
    UnusedPrivateClass:
      active: true
      aliases: ['unused']
    UnusedPrivateFunction:
      active: true
      aliases: ['unused']
      allowedNames: ''
    UnusedPrivateProperty:
      active: true
      aliases: ['unused']
      allowedNames: 'ignored|expected|serialVersionUID'
    UnusedVariable:
      active: true
      aliases: ['UNUSED_VARIABLE', 'unused']
      allowedNames: 'ignored|_'
    UseAnyOrNoneInsteadOfFind:
      active: true
    UseArrayLiteralsInAnnotations:
      active: true
    UseCheckNotNull:
      active: true
    UseCheckOrError:
      active: true
    UseDataClass:
      active: false
      allowVars: false
    UseEmptyCounterpart:
      active: false
    UseIfEmptyOrIfBlank:
      active: false
    UseIfInsteadOfWhen:
      active: false
      ignoreWhenContainingVariableDeclaration: false
    UseIsNullOrEmpty:
      active: true
    UseLet:
      active: false
    UseOrEmpty:
      active: true
    UseRequire:
      active: true
    UseRequireNotNull:
      active: true
    UseSumOfInsteadOfFlatMapSize:
      active: false
    UselessCallOnNotNull:
      active: true
    UtilityClassWithPublicConstructor:
      active: true
    VarCouldBeVal:
      active: true
      aliases: ['CanBeVal']
      ignoreLateinitVar: false
    WildcardImport:
      active: true
      excludeImports:
        - 'java.util.*'
  ```

  **Must NOT do**:
  - Do NOT modify the content — write it exactly as shown above

  **Acceptance Criteria**:
  - [ ] File exists at `C:\Users\charl\Projetos\estaparking\config\detekt\detekt.yml`
  - [ ] File is valid YAML (no syntax errors)

---

- [ ] 3. Create `config/detekt/baseline.xml`

  **What to do**:
  Create the file `C:\Users\charl\Projetos\estaparking\config\detekt\baseline.xml` with a **fresh/empty** baseline (no existing issues, since estaparking is a new project):

  ```xml
  <?xml version="1.0" ?>
  <SmellBaseline>
    <ManuallySuppressedIssues/>
    <CurrentIssues/>
  </SmellBaseline>
  ```

  **Must NOT do**:
  - This is a fresh project — the baseline starts empty

  **Acceptance Criteria**:
  - [ ] File exists at `C:\Users\charl\Projetos\estaparking\config\detekt\baseline.xml`
  - [ ] `<CurrentIssues/>` is empty (self-closing tag)

---

- [ ] 4. Create `.editorconfig`

  **What to do**:
  Create the file `C:\Users\charl\Projetos\estaparking\.editorconfig` with these exact contents:

  ```
  root = true

  [*]
  charset = utf-8
  end_of_line = lf
  insert_final_newline = true
  indent_style = space
  indent_size = 4
  trim_trailing_whitespace = true

  [*.{kt,kts}]
  ktlint_code_style = ktlint_official
  ktlint_standard_package-name = disabled
  max_line_length = 120
  ```

  **Acceptance Criteria**:
  - [ ] File exists at `C:\Users\charl\Projetos\estaparking\.editorconfig`
  - [ ] Contains `ktlint_code_style = ktlint_official`
  - [ ] `max_line_length = 120`

---

- [ ] 5. Create `.github/workflows/ci.yml` and remove old `.github/ci.yml`

  **What to do**:

  **Step A** — Create the directory `.github/workflows/` inside `C:\Users\charl\Projetos\estaparking\`.

  ```yaml
  name: CI

  on:
    pull_request:
      branches: [main]
    push:
      branches: [main]

  permissions:
    contents: read
    pull-requests: write
    checks: write

  concurrency:
    group: ci-${{ github.workflow }}-${{ github.ref }}
    cancel-in-progress: true

  jobs:
    build-and-test:
      name: Lint and tests
      runs-on: ubuntu-latest
      timeout-minutes: 30

      steps:
        - name: Checkout
          uses: actions/checkout@v4

        - name: Set up JDK 21
          uses: actions/setup-java@v4
          with:
            distribution: temurin
            java-version: '21'
            cache: gradle

        - name: Make Gradle wrapper executable
          run: chmod +x gradlew

        - name: Verify formatting and unused imports
          run: ./gradlew --no-daemon ktlintMainSourceSetCheck ktlintTestSourceSetCheck

        - name: Run detekt static analysis
          run: ./gradlew --no-daemon detekt

        - name: Run tests with coverage
          run: ./gradlew --no-daemon test jacocoTestReport

        - name: Add coverage to PR summary
          if: always()
          uses: madrapps/jacoco-report@v1.7.1
          with:
            paths: ${{ github.workspace }}/build/reports/jacoco/test/jacocoTestReport.xml
            token: ${{ github.token }}
            min-coverage-overall: 85
            min-coverage-changed-files: 90

        - name: Upload coverage report
          if: always()
          uses: actions/upload-artifact@v4
          with:
            name: jacoco-report
            path: build/reports/jacoco/test/html/
            retention-days: 7
  ```

  **Step C** — Delete the old misplaced file `C:\Users\charl\Projetos\estaparking\.github\ci.yml`.

  **Must NOT do**:
  - Do NOT include the `performance-test` job
  - Do NOT add branches like `develop`, `staging`, `release/**` — keep only `main` as in the existing estaparking CI

  **Acceptance Criteria**:
  - [ ] File exists at `C:\Users\charl\Projetos\estaparking\.github\workflows\ci.yml`
  - [ ] The `performance-test` job is NOT present anywhere in the file
  - [ ] `java-version: '21'` is used
  - [ ] Old file `C:\Users\charl\Projetos\estaparking\.github\ci.yml` is deleted

---

## Success Criteria

```bash
# File existence checks
Test-Path "estaparking\config\detekt\detekt.yml"         # True
Test-Path "estaparking\config\detekt\baseline.xml"       # True
Test-Path "estaparking\.editorconfig"                    # True
Test-Path "estaparking\.github\workflows\ci.yml"         # True
Test-Path "estaparking\.github\ci.yml"                   # False (deleted)

# Plugin presence in build.gradle.kts
Select-String "ktlint" "estaparking\build.gradle.kts"   # Found
Select-String "detekt" "estaparking\build.gradle.kts"   # Found
Select-String "jacoco" "estaparking\build.gradle.kts"   # Found

# No performance-test in CI
Select-String "performance-test" "estaparking\.github\workflows\ci.yml"  # NOT found
```
