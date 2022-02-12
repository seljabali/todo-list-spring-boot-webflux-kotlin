<div id="top"></div>

<h2 align="center">Todo List</h2>
<br>

## Built With
* [Kotlin](https://kotlinlang.org/)
* [PostgreSQL](https://www.postgresql.org)
* [Spring Boot 2.6.3](https://spring.io)

## Prerequisites
* [Java JDK Oracle (SE) 17](https://www.oracle.com/java/technologies/downloads/#java17)
* [IntelliJ IDEA CE](https://www.jetbrains.com/idea/download/)

## Dependencies
* [Spring Reactive Webflux](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-webflux)
* [Spring Data R2DBC SQL](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-r2dbc)
* [Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)
* [PostgreSQL Driver SQL](https://mvnrepository.com/artifact/org.postgresql/postgresql)

## Troubleshooting
<!-- Ensure Java Home variable is set -->
<details>
<summary>Ensure Java Home variable is set</summary>

```bash
echo $JAVA_HOME
```
Should see
```
/Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home
```
If not, you either don't have it downloaded, or Java Home variable is not set. <br>
</details>

<!-- Ensure Java version is downloaded -->
<details>
<summary>Ensure Java version is downloaded</summary>

```bash
/usr/libexec/java_home -V
```
Should see installed JDKs:
```
17.0.1 (x86_64) "Oracle Corporation" - "Java SE 17.0.1" /Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home
16.0.1 (x86_64) "Oracle Corporation" - "OpenJDK 16.0.1" /Users/user/Library/Java/JavaVirtualMachines/openjdk-16.0.1/Contents/Home
11.0.13 (x86_64) "Oracle Corporation" - "Java SE 11.0.13" /Library/Java/JavaVirtualMachines/jdk-11.0.13.jdk/Contents/Home
...
```
If not found, have not downloaded the JDK properly.<br>
</details>

<!-- Ensure correct v17 is showing -->
<details>
<summary>Ensure correct v17 is showing</summary>

```bash
/usr/libexec/java_home -v 17
```
Should see
```
/Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home
```
If you see a non-Oracle JDK, like open JDK, you need to uninstall it first. <br>
</details>

<!-- Set Java Home variable -->
<details>
<summary>Set Java Home variable</summary>

Find out what shell version you're using:
```bash
echo $SHELL
```
If you're using [ZSH](https://ohmyz.sh): you ought to be updating `~/.zshrc` else `~/.bash_profile` for the following steps. <br>

Update your profile:
```bash
[emacs/vim/atom] ~/.zshrc
```
Add the following line:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```
Update shell profile
```bash
source ~/.zshrc
```
</details>

## Build & Run Project
<!-- Clean Project -->
<details>
<summary>Clean Project</summary>

```bash

```
</details>

<!-- Build Project -->
<details>
<summary>Build Project</summary>

```bash

```
</details>

<!-- Run Project -->
<details>
<summary>Run Project</summary>

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```
</details>
