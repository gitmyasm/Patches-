# Patches-

Morphe patch bundle. Built from [morphe-patches-template](https://github.com/MorpheApp/morphe-patches-template).

## Usage

Add this repository as a patch source in Morphe Manager:

```
github.com/gitmyasm/Patches-
```

The bundle is private, so Morphe Manager needs a token with read access, or download
the `.mpp` artifact from the latest GitHub Actions run and import it manually.

## Development

```bash
./gradlew :patches:buildAndroid
```

Output: `patches/build/libs/patches-*.mpp`

Apply it to an APK with Morphe Desktop:

```bash
java -jar morphe-desktop-*-all.jar patch -p patches/build/libs/patches-*.mpp app.apk
```

CI builds every push to `main` and uploads the bundle as an artifact.

## Layout

```
patches/src/main/kotlin/app/template/patches/   # patch sources
extensions/extension/                           # extension code bundled into patches
patches/build.gradle.kts                        # bundle metadata
```

## License

GPL-3.0. See [LICENSE](LICENSE) and [NOTICE](NOTICE).
