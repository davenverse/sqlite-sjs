import sbtcrossproject.CrossProject
ThisBuild / tlBaseVersion := "0.0" // your current series x.y

ThisBuild / organization := "io.chrisdavenport"
ThisBuild / organizationName := "Christopher Davenport"
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("christopherdavenport", "Christopher Davenport")
)
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / tlSonatypeUseLegacyHost := true


val Scala213 = "2.13.7"

ThisBuild / crossScalaVersions := Seq(Scala213, "3.1.1")
ThisBuild / scalaVersion := Scala213

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

val catsV = "2.7.0"
val catsEffectV = "3.3.14"
val fs2V = "3.3.0"
val http4sV = "0.23.16"
val circeV = "0.14.3"
val doobieV = "1.0.0-RC2"
val munitCatsEffectV = "2.0.0-M3"


// Projects
lazy val `sqlite-cross` = tlCrossRootProject
  .aggregate(sqliteSjs, sqliteNative, examples, cross, `examples-cross`)

lazy val sqliteSjs = project.in(file("sqlite-sjs"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    name := "sqlite-sjs",

    Compile / npmDependencies ++= Seq(
      "sqlite" -> "4.1.1",
      "sqlite3" -> "5.0.9"
    ),
    mimaPreviousArtifacts := Set(),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},

    libraryDependencies ++= Seq(
      "org.typelevel"               %%% "cats-core"                  % catsV,
      "org.typelevel"               %%% "cats-effect"                % catsEffectV,

      "io.circe"                    %%% "circe-core"                 % circeV,
      "io.circe"                    %%% "circe-scalajs"              % circeV,

      "io.circe"                    %%% "circe-parser"               % circeV % Test,

      "org.typelevel"               %%% "munit-cats-effect"        % munitCatsEffectV         % Test,
    )
  )

lazy val sqliteNative = project.in(file("sqlite-native"))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.david-bouyssie" %%% "sqlite4s" % "0.4.1",
      "org.typelevel"               %%% "cats-effect"                % catsEffectV,
    )
  )

lazy val examples = project
  .in(file("examples"))
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .dependsOn(sqliteSjs)
  .settings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
    scalaJSUseMainModuleInitializer := true,
  )

lazy val cross = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("cross"))
  .settings(
    name := "sqlite-cross",
    mimaPreviousArtifacts := Set(),
    libraryDependencies ++= Seq(
      "io.circe"                    %%% "circe-core"                 % circeV,
    )
  ).jsSettings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
  ).jsConfigure { project => project.dependsOn(sqliteSjs) }
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.tpolecat"  %% "doobie-core" % doobieV,
      "org.xerial"    %  "sqlite-jdbc" % "3.36.0.3",
    )
  ).nativeConfigure{ project => project.dependsOn(sqliteNative)}

lazy val `examples-cross` = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("examples-cross"))
  .enablePlugins(NoPublishPlugin)
  .settings(
    libraryDependencies ++= Seq (
      "io.circe"                    %%% "circe-generic"                 % circeV,
      "co.fs2" %%% "fs2-io"  % fs2V,
    )
  )
  .dependsOn(cross)
  .jsConfigure(project => project.enablePlugins(ScalaJSBundlerPlugin))
  .jsSettings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
    scalaJSUseMainModuleInitializer := true,
  )


// lazy val shims = project
//   .in(file("shims"))
//   .enablePlugins(NoPublishPlugin)
//   .enablePlugins(ScalaJSPlugin)
//   .enablePlugins(ScalablyTypedConverterPlugin)
//   .settings(

//     Compile / npmDependencies ++= Seq(
//       "sqlite" -> "4.1.1",
//       "sqlite3" -> "5.0.9"
//     ),
//     stIgnore := List(
//       "sqlite3"
//     )
//   )
  

lazy val site = project.in(file("site"))
  .enablePlugins(TypelevelSitePlugin)
  // .dependsOn(core)
