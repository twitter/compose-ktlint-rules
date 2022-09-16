When using the [Detekt Gradle Plugin](https://detekt.dev/docs/gettingstarted/gradle), you can specify the dependency on this set of rules by using `detektPlugins`.

```groovy
dependencies {
    detektPlugins "com.twitter.compose.rules:detekt:<VERSION>"
}
```

### Using with detekt CLI

The [releases](https://github.com/twitter/compose-rules/releases) page contains an [uber jar](https://stackoverflow.com/questions/11947037/what-is-an-uber-jar) for each version release that can be used to run with the [CLI version of detekt](https://detekt.dev/docs/gettingstarted/cli).

```shell
detekt -p detekt-<VERSION>-all.jar -c your/config/detekt.yml
```

### Enabling rules

For the rules to be picked up, you will need to enable them in your `detekt.yml` configuration file.

```yaml
TwitterCompose:
  ContentEmitterReturningValues:
    active: true
  ModifierComposable:
    active: true
  ModifierMissing:
    active: true
  ModifierReused:
    active: true
  ModifierWithoutDefault:
    active: true
  MultipleEmitters:
    active: true
  MutableParams:
    active: true
  ComposableNaming:
    active: true
  ComposableParamOrder:
    active: true
  PreviewPublic:
    active: true
  RememberMissing:
    active: true
  ViewModelForwarding:
    active: true
  ViewModelInjection:
    active: true
```

### Disabling a specific rule

To disable a rule you have to follow the [instructions from the Detekt documentation](https://detekt.dev/docs/introduction/suppressing-rules), and use the id of the rule you want to disable.

For example, to disable `ComposableNaming`:

```kotlin
@Suppress("ComposableNaming")
@Composable
fun myNameIsWrong() { }
```
