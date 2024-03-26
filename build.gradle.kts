import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version System.getProperty("spring_version")
	id("io.spring.dependency-management") version System.getProperty("spring_dm_version")
	kotlin("jvm") version System.getProperty("kotlin_version")
	kotlin("plugin.spring") version System.getProperty("kotlin_version")

	id("com.github.bjornvester.xjc") version "1.8.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")

	implementation("io.github.oshai:kotlin-logging-jvm:" + System.getProperty("kotlin_logging_version"))

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jar {
	enabled = false
}

xjc {
	xsdDir.set(layout.projectDirectory.dir("src/main/xsd"))
//	bindingFiles = project.files(xsdDir.file("bindings.xjb"))
}
