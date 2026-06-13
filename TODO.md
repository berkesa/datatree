# TODO — Modernize `datatree` (datatree-core) to 2.0.0

> **You are the per-project Claude Code instance for `datatree`.** This file is self-contained.
> Goal: migrate this project from Gradle/Java 8 to **Maven + JDK 21**, upgrade dependencies, get
> tests green on **JUnit 5**, clean out legacy files, and set the version to **2.0.0**.
> This is the **foundation** artifact — everything else depends on it, so it goes **first**.

## Coordinates & facts (don't change the coordinates)
- Maven: `com.github.berkesa:datatree-core`, packaging `jar`, license **Apache-2.0**.
- `name`: *DataTree Core API* · `inceptionYear`: 2017
- `url`: https://berkesa.github.io/datatree/ · `scm`: https://github.com/berkesa/datatree.git
- developer: `berkesa` / Andras Berkes / andras.berkes@programmer.net
- **Version → `2.0.0`** (use `2.0.0-SNAPSHOT` while working).
- No inter-project dependencies (this is the root of the graph).

## Target versions
| Dependency | Current | Target (confirm latest when you do it) | Scope |
|---|---|---|---|
| `org.mongodb:bson` | 4.11.1 | **5.x** (latest) | compile |
| `junit:junit` 4.12 | → `org.junit.jupiter:junit-jupiter` | **5.x** | test |
| Eclipse `ecj` 4.4.2 | — | **remove** (use javac) | — |
| Java | 1.8 | **21** | — |

## Steps
1. **Create `pom.xml`** (standalone, no parent). Port the metadata from `build.gradle`'s
   `modifyPom { … }` (already summarized above). Set `<maven.compiler.release>21</maven.compiler.release>`.
   Dependencies:
   ```xml
   <dependencies>
     <dependency>
       <groupId>org.mongodb</groupId><artifactId>bson</artifactId>
       <version>5.2.1</version>   <!-- confirm newest 5.x -->
     </dependency>
     <dependency>
       <groupId>org.junit.jupiter</groupId><artifactId>junit-jupiter</artifactId>
       <version>5.11.4</version><scope>test</scope>   <!-- confirm newest 5.x -->
     </dependency>
   </dependencies>
   ```
   Build plugins: `maven-compiler-plugin` 3.14.0, `maven-surefire-plugin` 3.5.3. Put
   sources/javadoc/gpg + `org.sonatype.central:central-publishing-maven-plugin:0.9.0`
   (`<extensions>true</extensions>`, `publishingServerId=central`) under a `release` profile.
2. **Remove ECJ.** Delete the `compileJava { options.fork … org.eclipse.jdt…Main }` block and the
   `ecj` config. Build with javac and fix any errors ECJ tolerated.
3. **bson 4 → 5.** Check `io.datatree.dom.converters.BsonConverterSet` and any `org.bson.*` usage
   (`BsonString`, `BsonInt32`, `BsonInt64`, `BsonNull`, `Decimal128`, `ObjectId`, …). The `org.bson`
   package is stable across 4→5, but verify no removed/relocated type breaks compilation.
4. **Tests → JUnit 5.** Migrate `io.datatree.*Test` from JUnit 4 to Jupiter (`@Test` from
   `org.junit.jupiter.api`, `Assertions.*`). `mvn test` must pass offline.
5. **Cleanup — delete:** `build.gradle`, `settings.gradle`, `gradlew`, `gradlew.bat`, `gradle/`,
   `.gradle/`, `.travis.yml`, `.codacy.yaml`, `.classpath`, `.project`, `.settings/`.
   Keep `.git/`, `LICENSE`, `README.md`, `CLAUDE.md`, `src/`.
6. **VSCode + .gitignore.** Add `.vscode/extensions.json` (java pack, redhat.java, maven) and
   `.vscode/settings.json` (`java.configuration.updateBuildConfiguration: automatic`,
   `files.exclude target`). No `launch.json` (pure library). Ensure `.gitignore` covers
   `target/`, `.gradle/`, `bin/`, `.classpath`, `.project`, `.settings/`.
7. **Build & install:** `mvn clean install` (so dependents resolve `datatree-core:2.0.0-SNAPSHOT`),
   then `mvn clean verify`.
8. **Update `CLAUDE.md`** build/test commands (`./gradlew …` → `mvn …`; note Maven/JDK 21, JUnit 5,
   no ECJ).

## Definition of done
- `mvn clean verify` green on JDK 21; ECJ/Gradle/Travis/Codacy/Eclipse files gone.
- `bson` on 5.x; JUnit 5; version `2.0.0(-SNAPSHOT)`; VSCode + .gitignore present.
- `datatree-core:2.0.0-SNAPSHOT` installed to local `~/.m2`.
- Publishing configured per the Central Portal method (sources+javadoc+gpg+central-publishing 0.9.0).

> The full effort's rules live in the workspace-root `CLAUDE.md` + `coordination/` (visible only
> from the workspace root). This file repeats everything you need here.
